package org.taskmanager.dto;

import org.taskmanager.model.Status;
import org.taskmanager.model.Task;

import java.time.LocalDate;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long categoryid;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Status status;

    public TaskDTO(){}
    public TaskDTO(Long id, String title, String description, LocalDate dueDate, Long categoryid, Status status){
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.categoryid = categoryid;
        this.status = status;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setCategoryid(Long categoryid) {
        this.categoryid = categoryid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public Long getCategoryId() {
        return categoryid;
    }
}
