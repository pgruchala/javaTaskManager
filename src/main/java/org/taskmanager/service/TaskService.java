package org.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.exceptions.ResourceNotFoundException;
import org.taskmanager.model.Category;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository,CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasks(String searchTitle, Status status, Long categoryId, Pageable pageable) {
        if (searchTitle != null && !searchTitle.isBlank()) {
            return taskRepository.searchByTitle(searchTitle, pageable);
        } else if (status != null && categoryId != null) {
            return taskRepository.findByStatusAndCategoryId(status, categoryId, pageable);
        } else if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        } else if (categoryId != null) {
            return taskRepository.findByCategoryId(categoryId, pageable);
        }

        return taskRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public Task createTask(TaskDTO taskDTO) {
        Category category = categoryRepository.findById(taskDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Task task = new Task(
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getDueDate(),
                category,
                taskDTO.getStatus()
        );
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Category category = categoryRepository.findById(taskDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setCategory(category);
        task.setStatus(taskDTO.getStatus());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }



}
