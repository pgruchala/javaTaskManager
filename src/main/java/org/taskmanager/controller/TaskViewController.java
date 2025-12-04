package org.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.service.CategoryService;
import org.taskmanager.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {
    private final TaskService taskService;
    private final CategoryService categoryService;

    public TaskViewController(TaskService taskService, CategoryService categoryService){
        this.taskService = taskService;
        this.categoryService = categoryService;
    }
    @GetMapping
    public String listTasks(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) Status status) {
        Page<Task> taskPage = taskService.getTasks(title, status, null, PageRequest.of(page,10, Sort.by("dueDate").ascending()));
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("statuses", Status.values());
        return "tasks/list";
    }
    @GetMapping("/new")
    public String ShowCreateForm(Model model){
        model.addAttribute("taskDTO", new TaskDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "tasks/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);

        TaskDTO dto = new TaskDTO(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getCategory() != null ? task.getCategory().getId() : null
        );

        model.addAttribute("taskDTO", dto);
        model.addAttribute("taskId", id);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "tasks/form";
    }
    @PostMapping("/save")
    public String saveTask(@Valid @ModelAttribute("taskDTO") TaskDTO taskDTO,
                           BindingResult result,
                           @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "tasks/form";
        }
        Task createdTask = taskService.createTask(taskDTO);

        if (attachment != null && !attachment.isEmpty()) {
            taskService.uploadAttachment(createdTask.getId(), attachment);
        }

        return "redirect:/tasks";
    }
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("taskDTO") TaskDTO taskDTO,
                             BindingResult result,
                             @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("taskId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "tasks/form";
        }
        taskService.updateTask(id, taskDTO);

        if (attachment != null && !attachment.isEmpty()) {
            taskService.uploadAttachment(id, attachment);
        }
        return "redirect:/tasks";
    }
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
