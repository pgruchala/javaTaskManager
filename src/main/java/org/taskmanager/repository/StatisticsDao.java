package org.taskmanager.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatisticsDao {
    private final JdbcTemplate jdbcTemplate;

    public StatsDTO getStatistics() {
        String countSql = "SELECT COUNT(*) FROM tasks";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);
        int totalTasks = (total != null) ? total : 0;

        String groupSql = "SELECT status, COUNT(*) as count FROM tasks GROUP BY status";

        List<StatusCount> statusList = jdbcTemplate.query(groupSql, new StatusCountRowMapper());

        Map<String, Integer> statusCounts = new HashMap<>();
        for (StatusCount sc : statusList) {
            statusCounts.put(sc.getStatus(), sc.getCount());
        }

        jdbcTemplate.query(groupSql, (rs, rowNum) -> {
            String status = rs.getString("status");
            int count = rs.getInt("count");
            statusCounts.put(status, count);
            return null;
        });

        int todo = statusCounts.getOrDefault(Status.TODO.name(), 0);
        int inProgress = statusCounts.getOrDefault(Status.IN_PROGRESS.name(), 0);
        int done = statusCounts.getOrDefault(Status.DONE.name(), 0);

        double percentage = (totalTasks > 0) ? ((double) done / totalTasks) * 100 : 0.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        return new StatsDTO(totalTasks, todo, inProgress, done, percentage);
    }
    @Getter
    @AllArgsConstructor
    private static class StatusCount {
        private String status;
        private int count;
    }

    private static class StatusCountRowMapper implements RowMapper<StatusCount> {
        @Override
        public StatusCount mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new StatusCount(
                    rs.getString("status"),
                    rs.getInt("count")
            );
        }
    }
}
