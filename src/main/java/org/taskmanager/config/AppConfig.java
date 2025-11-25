package org.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.service.CategoryService;
import org.taskmanager.service.TaskService;

@Configuration
public class AppConfig {
    @Bean
    public TaskService taskService(TaskRepository taskRepository, CategoryRepository categoryRepository){
        return new TaskService(taskRepository, categoryRepository);
    }
    @Bean
    public CategoryService categoryService(CategoryRepository categoryRepository){
        return new CategoryService(categoryRepository);
    }
}
