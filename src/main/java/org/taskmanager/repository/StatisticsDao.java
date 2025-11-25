package org.taskmanager.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.model.Status;

import java.util.HashMap;
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

        Map<String, Integer> statusCounts = new HashMap<>();


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
}
