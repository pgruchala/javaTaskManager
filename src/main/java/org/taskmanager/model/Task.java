package org.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "attachment_filename")
    private String attachmentFilename;

    public Task(){}
    public Task(String title, String description, LocalDate dueDate, Category category, Status status, User user){
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.status = status;
        this.user = user;
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
