package org.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Status status);
    Page<Task> findAll(Pageable pageable);
    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.status <> 'DONE'")
    List<Task> findOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t JOIN t.category c WHERE c.name = :categoryName")
    List<Task> findByCategoryName(@Param("categoryName") String categoryName);
}
