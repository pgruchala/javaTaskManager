package org.taskmanager.service;

import org.springframework.stereotype.Service;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.model.Task;
import org.taskmanager.repository.TaskDAO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskDAO taskDAO;

    public TaskService(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public List<TaskDTO> getAllTasks() {
        return taskDAO.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long id) {
        return taskDAO.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public void createTask(TaskDTO taskDTO) {
        Task task = new Task(
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getDueDate(),
                taskDTO.getCategoryId(),
                taskDTO.getStatus()
        );
        taskDAO.save(task);
    }

    public void updateTask(Long id, TaskDTO taskDTO) {
        Optional<Task> existingTaskOpt = taskDAO.findById(id);
        if (existingTaskOpt.isPresent()) {
            Task task = existingTaskOpt.get();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setDueDate(taskDTO.getDueDate());
            task.setCategoryid(taskDTO.getCategoryId());
            task.setStatus(taskDTO.getStatus());
            task.setUpdatedAt(LocalDate.now());

            taskDAO.update(task);
        } else {
            throw new RuntimeException("Task not found with id: " + id);
        }
    }

    public void deleteTask(Long id) {
        taskDAO.deleteById(id);
    }

    private TaskDTO mapToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getCategoryid(),
                task.getStatus()
        );
    }

}
