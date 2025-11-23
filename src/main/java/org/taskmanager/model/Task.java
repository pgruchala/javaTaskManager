package org.taskmanager.model;

import java.time.LocalDate;
import java.util.UUID;
import java.util.random.RandomGenerator;

public class Task {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long categoryid;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Status status;

    public Task(){}
    public Task(String title, String description, LocalDate dueDate, Long categoryId, Status status){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.categoryid = categoryId;
        this.status = status;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Long categoryid) {
        this.categoryid = categoryid;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    @Override
    public String toString(){
        return "Task{"+
                "id="+id+
                ", title="+title +
                ", description="+description+
                ", status="+status+
                ", due date="+dueDate+
                ", created at"+createdAt+
                ", updated at"+ updatedAt+
                ", category Id"+categoryid+"}";
    }
}
