package org.taskmanager.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @GetMapping("/{page}")
    public void getTasks(){}
    @GetMapping("/{id}")
    public void getTaskById(){}
    @PostMapping
    public void createTask(){}
    @PutMapping("/{id}")
    public void updateTask(){}
    @DeleteMapping("/{id}")
    public void deleteTask(){}

}
