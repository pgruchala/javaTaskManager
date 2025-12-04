package org.taskmanager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.model.Status;
import org.taskmanager.model.User;
import org.taskmanager.repository.StatisticsDao;
import org.taskmanager.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Nie znaleziono zalogowanego użytkownika"));
    }
    public StatsDTO getStatistics() {
        User currentUser = getCurrentUser();

        String countSql = "SELECT COUNT(*) FROM tasks WHERE user_id = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, currentUser.getId());
        int totalTasks = (total != null) ? total : 0;

        String groupSql = "SELECT status, COUNT(*) as count FROM tasks WHERE user_id = ? GROUP BY status";

        List<StatusCount> statusList = jdbcTemplate.query(groupSql,
                new StatusCountRowMapper(), currentUser.getId());

        Map<String, Integer> statusCounts = new HashMap<>();
        for (StatusCount sc : statusList) {
            statusCounts.put(sc.getStatus(), sc.getCount());
        }

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

