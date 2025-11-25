package org.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.model.Category;
import org.taskmanager.model.Task;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository,CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public Page<TaskDTO> getTasksPage(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @Transactional
    public void createTask(TaskDTO taskDTO) {
        Category category = categoryRepository.findById(taskDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Task task = new Task(
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getDueDate(),
                category,
                taskDTO.getStatus()
        );
        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Category category = categoryRepository.findById(taskDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setCategory(category);
        task.setStatus(taskDTO.getStatus());

        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO mapToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getStatus()
        );
    }

}
