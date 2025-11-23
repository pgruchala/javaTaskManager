package org.taskmanager.repository;

import org.taskmanager.model.Category;

import java.util.List;

public interface CategoryDAO {
    void save(Category category);
    List<Category> findAll();
    void update(Category category);
    void deletebyId(Long id);
}
