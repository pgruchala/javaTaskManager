package org.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.taskmanager.model.Category;
import org.taskmanager.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser(User user);

    boolean existsByNameAndUser(String name, User user);

    Optional<Category> findByIdAndUser(Long id, User user);
}
