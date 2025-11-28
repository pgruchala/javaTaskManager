package org.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.taskmanager.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.user = :user AND lower(t.title) LIKE lower(concat('%', :keyword, '%'))")
    Page<Task> searchByTitleAndUser(@Param("keyword") String keyword, @Param("user") User user, Pageable pageable);

    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByStatusAndCategoryIdAndUser(Status status, Long categoryId,User user, Pageable pageable);
    Page<Task> findByStatusAndUser(Status status,User user, Pageable pageable);
    Page<Task> findByCategoryIdAndUser(Long categoryId,User user, Pageable pageable);

    Page<Task> findByDueDateBeforeAndUser(LocalDateTime date, User user, Pageable pageable);
    Page<Task> findByDueDateAfterAndUser(LocalDateTime date, User user, Pageable pageable);

}
