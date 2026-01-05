package org.taskmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.taskmanager.dto.StatsDTO;
import org.taskmanager.model.Category;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DataLayerTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp(){
        testUser = new User("testuser", "password");
        entityManager.persist(testUser);

        testCategory = new Category("Work", "#FFFFFF",testUser);
        entityManager.persist(testCategory);

        entityManager.flush();
    }

    @Test
    @DisplayName("CRUD Testy - zapis zadania")
    void testShouldSaveTask(){
        Task task = new Task("Task 1", "Desc", LocalDate.now() ,testCategory, Status.TODO, testUser);
        Task saved = taskRepository.save(task);
        Optional<Task> found = taskRepository.findById(saved.getId());
        assertAll("Sprawdzenie zapisu zadania",
                () -> assertTrue(found.isPresent()),
                () -> assertEquals("Task 1", found.get().getTitle()),
                () -> assertEquals(testUser.getId(), found.get().getUser().getId())
        );
    }
    @Test
    @DisplayName("CRUD Testy - aktualizacja zadanmia")
    void testShouldUpdateTask(){
        Task task = new Task("Old Title", "Desc", LocalDate.now(), testCategory, Status.TODO, testUser);
        entityManager.persist(task);

        task.setTitle("New Title");
        taskRepository.save(task);

        Task updated = taskRepository.findById(task.getId()).orElseThrow();

        assertEquals("New Title", updated.getTitle());
    }
    @Test
    @DisplayName("CRUD Testy - usuwanie zadania")
    void testShouldDeleteTask() {
        Task task = new Task("To Delete", "Desc", LocalDate.now(), null, Status.TODO, testUser);
        Task saved = entityManager.persist(task);

        taskRepository.delete(saved);

        assertTrue(taskRepository.findById(saved.getId()).isEmpty());
    }
    @Test
    @DisplayName("Custom Query:po tytule i użytkowniku")
    void testShouldSearchByTitleAndUser() {
        entityManager.persist(new Task("Buy Milk", "Desc", LocalDate.now(), null, Status.TODO, testUser));
        entityManager.persist(new Task("Buy Bread", "Desc", LocalDate.now(), null, Status.TODO, testUser));
        entityManager.persist(new Task("Sleep", "Desc", LocalDate.now(), null, Status.TODO, testUser));

        Page<Task> results = taskRepository.searchByTitleAndUser("Buy", testUser, Pageable.unpaged());

        assertAll("Weryfikacja wyszukiwania",
                () -> assertEquals(2, results.getTotalElements()),
                () -> assertTrue(results.getContent().stream().anyMatch(t -> t.getTitle().equals("Buy Milk")))
        );
    }

    @Test
    @DisplayName("Znajdź zadania po Statusie i Użytkowniku")
    void testShouldFindByStatusAndUser() {
        entityManager.persist(new Task("T1", "D", LocalDate.now(), null, Status.IN_PROGRESS, testUser));
        entityManager.persist(new Task("T2", "D", LocalDate.now(), null, Status.DONE, testUser));

        Page<Task> inProgress = taskRepository.findByStatusAndUser(Status.IN_PROGRESS, testUser, Pageable.unpaged());

        assertEquals(1, inProgress.getTotalElements());
    }


    @Test
    @DisplayName("Znajdź wszystkie kategorie użytkownika")
    void testShouldFindAllCategoriesByUser() {
        User otherUser = new User("other", "pass");
        entityManager.persist(otherUser);
        entityManager.persist(new Category("Other Cat", "#000", otherUser));

        List<Category> myCategories = categoryRepository.findAllByUser(testUser);

        assertAll("Kategorie użytkownika",
                () -> assertEquals(1, myCategories.size()),
                () -> assertEquals("Work", myCategories.get(0).getName())
        );
    }

    @Test
    @DisplayName("Sprawdź czy kategoria istnieje (existsByNameAndUser)")
    void testShouldCheckIfCategoryExists() {
        boolean exists = categoryRepository.existsByNameAndUser("Work", testUser);
        boolean notExists = categoryRepository.existsByNameAndUser("Gym", testUser);

        assertAll("Sprawdzenie istnienia",
                () -> assertTrue(exists),
                () -> assertFalse(notExists)
        );
    }



    @Test
    @DisplayName("9. findByCategoryIdAndUser")
    void testShouldFindTasksByCategory() {
        entityManager.persist(new Task("Cat Task", "D", LocalDate.now(), testCategory, Status.TODO, testUser));

        Page<Task> tasks = taskRepository.findByCategoryIdAndUser(testCategory.getId(), testUser, Pageable.unpaged());

        assertEquals(1, tasks.getTotalElements());
    }

    @Test
    @DisplayName("findByIdAndUser - tylko kategorie użytkownika ")
    void testShouldFindCategoryByIdAndUser() {
        Optional<Category> cat = categoryRepository.findByIdAndUser(testCategory.getId(), testUser);
        assertTrue(cat.isPresent());
    }

    @Test
    @DisplayName("CRUD Testy - zapis kategorii")
    void testShouldSaveCategory(){
        Category cat = new Category("NewCat", "#ABC", testUser);
        Category saved = categoryRepository.save(cat);

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertAll("Sprawdzenie zapisu kategorii",
                () -> assertTrue(found.isPresent()),
                () -> assertEquals("NewCat", found.get().getName()),
                () -> assertEquals(testUser.getId(), found.get().getUser().getId())
        );
    }

    @Test
    @DisplayName("CRUD Testy - aktualizacja kategorii")
    void testShouldUpdateCategory(){
        Category cat = new Category("OldName", "#000", testUser);
        entityManager.persist(cat);

        cat.setName("UpdatedName");
        categoryRepository.save(cat);

        Category updated = categoryRepository.findById(cat.getId()).orElseThrow();
        assertEquals("UpdatedName", updated.getName());
    }

    @Test
    @DisplayName("CRUD Testy - usuwanie kategorii")
    void testShouldDeleteCategory(){
        Category cat = new Category("ToDelete", "#111", testUser);
        Category saved = entityManager.persist(cat);

        categoryRepository.delete(saved);

        assertTrue(categoryRepository.findById(saved.getId()).isEmpty());
    }

}
