//package org.taskmanager.repository;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class StatisticsRepository {
//    private final JdbcTemplate jdbcTemplate;
//
//    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//    public List<TaskStatsDTO> getTaskCountByCategory() {
//        String sql = """
//            SELECT c.name as category_name, COUNT(t.id) as task_count
//            FROM categories c
//            LEFT JOIN tasks t ON c.id = t.category_id
//            GROUP BY c.name
//        """;
//
//        RowMapper<TaskStatsDTO> rowMapper = (rs, rowNum) -> new TaskStatsDTO(
//                rs.getString("category_name"),
//                rs.getInt("task_count")
//        );
//
//        return jdbcTemplate.query(sql, rowMapper);
//    }
//}
