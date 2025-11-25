//package org.taskmanager.repository;
//
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Repository;
//import org.taskmanager.model.Category;
//import org.taskmanager.model.Task;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//@Repository
//public class JdbcCategoryDAO implements CategoryDAO {
//
//    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    public JdbcCategoryDAO(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
//        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//    }
//
//    private static class TaskRowMapper implements RowMapper<Category> {
//        @Override
//        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Category category = new Category();
//            category.setId(rs.getLong("id"));
//            category.setColor(rs.getString("color"));
//            category.setName(rs.getString("name"));
//            return category;
//        }
//    }
//
//    @Override
//    public void save(Category category) {
//        String sql = "INSERT INTO categories (name, color) VALUES (:name, :color)";
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("name", category.getName())
//                .addValue("color", category.getColor());
//        namedParameterJdbcTemplate.update(sql,params);
//    }
//
//    @Override
//    public List<Category> findAll() {
//        String sql = "SELECT * FROM categories";
//        return namedParameterJdbcTemplate.query(sql,new TaskRowMapper());
//    }
//
//    @Override
//    public void update(Category category) {
//        String sql = "UPDATE categories SET name = :name, color = :color WHERE id = :id";
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("id", category.getId())
//                .addValue("name",category.getName())
//                .addValue("color",category.getColor());
//        namedParameterJdbcTemplate.update(sql,params);
//    }
//
//    @Override
//    public void deletebyId(Long id) {
//        String sql = "DELETE FROM categories WHERE id = :id";
//        MapSqlParameterSource params = new MapSqlParameterSource("id",id);
//        namedParameterJdbcTemplate.update(sql,params);
//    }
//}
