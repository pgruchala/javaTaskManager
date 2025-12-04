package org.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.repository.UserRepository;
import org.taskmanager.service.CategoryService;
import org.taskmanager.service.FileStorageService;
import org.taskmanager.service.TaskService;

@Configuration
public class AppConfig {
    @Bean
    public TaskService taskService(TaskRepository taskRepository, CategoryRepository categoryRepository, UserRepository userRepository, FileStorageService fileStorageService){
        return new TaskService(taskRepository, categoryRepository, userRepository, fileStorageService);
    }
    @Bean
    public CategoryService categoryService(CategoryRepository categoryRepository, TaskRepository taskRepository, UserRepository userRepository){
        return new CategoryService(categoryRepository, taskRepository,userRepository);
    }
}
