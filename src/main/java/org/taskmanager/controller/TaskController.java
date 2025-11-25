package org.taskmanager.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }
    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(
            @Parameter(description = "Szukaj po tytule") @RequestParam(required = false) String title,
            @Parameter(description = "Filtruj po statusie") @RequestParam(required = false) Status status,
            @Parameter(description = "ID kategorii") @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<Task> tasks = taskService.getTasks(title, status, categoryId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono zadanie"),
            @ApiResponse(responseCode = "404", description = "Zadanie nie istnieje")
    })
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Zadanie utworzone pomyślnie")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDTO taskDto) {
        Task createdTask = taskService.createTask(taskDto);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Zadanie zaktualizowane")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDto) {
        Task updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Zadanie usunięte (No Content)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/export/csv")
//    public void exportToCsv(HttpServletResponse response) throws IOException {
//        response.setContentType("text/csv");
//        response.addHeader("Content-Disposition", "attachment; filename=\"tasks_export.csv\"");
//        taskService.exportTasksToCsv(response.getWriter());
//    }
}