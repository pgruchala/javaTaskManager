package org.taskmanager.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.exceptions.ResourceNotFoundException;
import org.taskmanager.model.Category;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Nie znaleziono zalogowanego użytkownika"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByUser(getCurrentUser());
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findByIdAndUser(id,getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        User currentUser = getCurrentUser();
        if (categoryRepository.existsByNameAndUser(categoryDTO.getName(),currentUser)) {
            throw new RuntimeException("Category with this name already exists");
        }
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setColor(categoryDTO.getColor());
        category.setUser(currentUser);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = getCategoryById(id);
        User currentUser = getCurrentUser();

        if (!category.getName().equals(categoryDTO.getName()) &&
                categoryRepository.existsByNameAndUser(categoryDTO.getName(), currentUser)) {
            throw new RuntimeException("Category name already exists");
        }

        category.setName(categoryDTO.getName());
        category.setColor(categoryDTO.getColor());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        List<Task> tasks = category.getTasks();

        for (Task task : tasks) {
            task.setCategory(null);
            taskRepository.save(task);
        }

        categoryRepository.deleteById(id);
    }
}
