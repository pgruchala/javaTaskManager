package org.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE lower(t.title) LIKE lower(concat('%', :keyword, '%'))")
    Page<Task> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    Page<Task> findByStatusAndCategoryId(Status status, Long categoryId, Pageable pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Task> findByDueDateBefore(LocalDateTime date, Pageable pageable);
    Page<Task> findByDueDateAfter(LocalDateTime date, Pageable pageable);

    List<Task> findAllByCategoryId(Long categoryId);
}
