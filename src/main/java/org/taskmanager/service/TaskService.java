package org.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.exceptions.ResourceNotFoundException;
import org.taskmanager.model.Category;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.repository.UserRepository;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public TaskService(TaskRepository taskRepository,CategoryRepository categoryRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Nie znaleziono zalogowanego użytkownika"));
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasks(String searchTitle, Status status, Long categoryId, Pageable pageable) {
        User currentUser = getCurrentUser();

        if (searchTitle != null && !searchTitle.isBlank()) {
            return taskRepository.searchByTitleAndUser(searchTitle,currentUser, pageable);
        } else if (status != null && categoryId != null) {
            return taskRepository.findByStatusAndCategoryIdAndUser(status, categoryId,currentUser, pageable);
        } else if (status != null) {
            return taskRepository.findByStatusAndUser(status, currentUser,pageable);
        } else if (categoryId != null) {
            return taskRepository.findByCategoryIdAndUser(categoryId, currentUser,pageable);
        }

        return taskRepository.findByUser(currentUser,pageable);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        User currentUser = getCurrentUser();
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));
    }

    @Transactional
    public Task createTask(TaskDTO taskDTO) {
        Category category = null;
        if(taskDTO.getCategoryId() != null) {
            category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        User currentUser = getCurrentUser();

        Task task = new Task(
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getDueDate(),
                category,
                taskDTO.getStatus(),
                currentUser
        );
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, TaskDTO taskDTO) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));

        if(taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setStatus(taskDTO.getStatus());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public void exportTasksToCsv(Writer writer) throws IOException {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByUser(currentUser,Pageable.unpaged()).getContent();
        writer.write("ID,Title,Description,Status,Due Date,Category,Created At,Updated At\n");

        for (Task task : tasks) {
            writer.write(String.format("%d,\"%s\",\"%s\",%s,%s,\"%s\",%s,%s\n",
                    task.getId(),
                    escapeCsv(task.getTitle()),
                    escapeCsv(task.getDescription()),
                    task.getStatus(),
                    task.getDueDate() != null ? task.getDueDate().toString() : "",
                    task.getCategory() != null ? escapeCsv(task.getCategory().getName()) : "No Category",
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            ));
        }

        writer.flush();
    }
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }

    @Transactional
    public Task uploadAttachment(Long id, MultipartFile file) {
        Task task = getTaskById(id);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Plik nie może być pusty");
        }

        if (task.getAttachmentFilename() != null) {
            fileStorageService.deleteFile(task.getAttachmentFilename());
        }

        String filename = fileStorageService.storeFile(file, task.getTitle());
        task.setAttachmentFilename(filename);

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Path getAttachment(Long id) {
        Task task = getTaskById(id);

        if (task.getAttachmentFilename() == null) {
            throw new ResourceNotFoundException("Zadanie nie ma załącznika");
        }

        return fileStorageService.loadFile(task.getAttachmentFilename());
    }

    @Transactional
    public void deleteAttachment(Long id) {
        Task task = getTaskById(id);

        if (task.getAttachmentFilename() != null) {
            fileStorageService.deleteFile(task.getAttachmentFilename());
            task.setAttachmentFilename(null);
            taskRepository.save(task);
        }
    }

}
