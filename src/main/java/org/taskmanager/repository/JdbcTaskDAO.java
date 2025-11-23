package org.taskmanager.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTaskDAO implements TaskDAO {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    public JdbcTaskDAO(NamedParameterJdbcTemplate namedJdbcTemplate){
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    private static class TaskRowMapper implements RowMapper<Task> {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException{
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setTitle(rs.getString("title"));
            task.setDescription(rs.getString("description"));
            task.setDueDate(rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null);
            task.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime().toLocalDate() : null);
            task.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime().toLocalDate() : null);
            task.setCategoryid(rs.getObject("category_id") != null ? rs.getLong("category_id") : null);

            String statusStr = rs.getString("status");
            if (statusStr != null) {
                task.setStatus(Status.valueOf(statusStr));
            }
            return task;
        }
    }
    @Override
    public void save(Task task) {
        String sql = """
            INSERT INTO tasks (title, description, status, dueDate, created_at, updated_at, category_id) 
            VALUES (:title, :description, :status, :dueDate, :createdAt, :updatedAt, :categoryId)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", task.getTitle())
                .addValue("description", task.getDescription())
                .addValue("status", task.getStatus() != null ? task.getStatus().name() : Status.TODO.name())
                .addValue("dueDate", task.getDueDate())
                .addValue("createdAt", task.getCreatedAt())
                .addValue("updatedAt", task.getUpdatedAt())
                .addValue("categoryId", task.getCategoryid());

        namedJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks";
        return namedJdbcTemplate.query(sql, new TaskRowMapper());
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = :id";
        try {
            Task task = namedJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), new TaskRowMapper());
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Task task) {
        String sql = """
            UPDATE tasks SET 
                title = :title, 
                description = :description, 
                status = :status, 
                dueDate = :dueDate, 
                updated_at = :updatedAt, 
                category_id = :categoryId 
            WHERE id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", task.getId())
                .addValue("title", task.getTitle())
                .addValue("description", task.getDescription())
                .addValue("status", task.getStatus().name())
                .addValue("dueDate", task.getDueDate())
                .addValue("updatedAt", task.getUpdatedAt())
                .addValue("categoryId", task.getCategoryid());

        namedJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM tasks WHERE id = :id";
        namedJdbcTemplate.update(sql, new MapSqlParameterSource("id",id));
    }
}
