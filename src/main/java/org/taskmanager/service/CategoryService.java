package org.taskmanager.service;

import org.springframework.stereotype.Service;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.model.Category;
import org.taskmanager.repository.CategoryDAO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryDAO categoryDAO;
    public CategoryService(CategoryDAO categoryDAO){
        this.categoryDAO = categoryDAO;
    }
    public List<CategoryDTO> getAllCategories() {
        return categoryDAO.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category(categoryDTO.getName(), categoryDTO.getColor());
        categoryDAO.save(category);
    }

    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = new Category(categoryDTO.getName(), categoryDTO.getColor());
        category.setId(id);
        categoryDAO.update(category);
    }

    public void deleteCategory(Long id) {
        categoryDAO.deletebyId(id);
    }

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO(category.getName(), category.getColor());
        dto.setId(category.getId());
        return dto;
    }
}
