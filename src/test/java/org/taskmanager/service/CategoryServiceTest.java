package org.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.exceptions.InsufficientDataProvidedException;
import org.taskmanager.exceptions.ResourceNotFoundException;
import org.taskmanager.model.Category;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "pass");
        user.setId(1L);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    private void mockSecurity() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
    }
    @Test
    @DisplayName("Pobierz wszystkie kategorie zalogowanego użytkownika")
    void testShouldGetAllCategoriesForCurrentUser() {
        mockSecurity();
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findAllByUser(user)).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAllByUser(user);
    }

    @Test
    @DisplayName("Utwórz kategorię - sukces")
    void testShouldCreateCategorySuccessfully() {
        mockSecurity();
        CategoryDTO dto = new CategoryDTO("Praca", "#FF0000");
        when(categoryRepository.existsByNameAndUser("Praca", user)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);

        Category result = categoryService.createCategory(dto);

        assertAll("Tworzenie kategorii",
                () -> assertEquals("Praca", result.getName()),
                () -> assertEquals("#FF0000", result.getColor()),
                () -> assertEquals(user, result.getUser())
        );
    }

    @Test
    @DisplayName("Utwórz kategorię - błąd duplikatu")
    void testShouldThrowExceptionWhenCreatingDuplicateCategory() {
        mockSecurity();
        CategoryDTO dto = new CategoryDTO("Praca", "#FF0000");
        when(categoryRepository.existsByNameAndUser("Praca", user)).thenReturn(true);

        assertThrows(InsufficientDataProvidedException.class, () -> categoryService.createCategory(dto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Usuń kategorię i odepnij zadania")
    void testShouldDeleteCategoryAndUnlinkTasks() {
        mockSecurity();
        Category category = new Category("Old", "#000", user);
        category.setId(10L);

        Task t1 = new Task();
        t1.setCategory(category);
        category.setTasks(List.of(t1));

        when(categoryRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(10L);

        assertAll("Usuwanie kaskadowe",
                () -> assertNull(t1.getCategory()), // Sprawdź czy zadanie zostało odpięte
                () -> verify(taskRepository).save(t1),
                () -> verify(categoryRepository).deleteById(10L)
        );
    }

    @Test
    @DisplayName("Pobierz kategorię po ID - nie znaleziono")
    void testShouldThrowExceptionWhenCategoryNotFound() {
        mockSecurity();
        when(categoryRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }
}
