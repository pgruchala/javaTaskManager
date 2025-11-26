package org.taskmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
    private LocalDate dueDate;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    public Task(){}
    public Task(String title, String description, LocalDate dueDate, Category category, Status status){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.status = status;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDate.now();
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
                ", category"+category.toString()+"}";
    }
}
