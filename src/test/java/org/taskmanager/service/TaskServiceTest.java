package org.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.exceptions.ResourceNotFoundException;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;
import org.taskmanager.repository.CategoryRepository;
import org.taskmanager.repository.TaskRepository;
import org.taskmanager.repository.UserRepository;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("mockUser", "pass");
        user.setId(1L);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    private void mockSecurity() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("mockUser");
        when(userRepository.findByUsername("mockUser")).thenReturn(Optional.of(user));
    }
    @Test
    @DisplayName("Happy Path: Utworzenie zadania")
    void testShouldCreateTaskSuccessfully() {
        mockSecurity();
        TaskDTO dto = new TaskDTO("Title", "Desc", Status.TODO, LocalDate.now().plusDays(1), null);
        Task savedTask = new Task("Title", "Desc", dto.getDueDate(), null, Status.TODO, user);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(dto);

        assertAll("Tworzenie zadania",
                () -> assertNotNull(result),
                () -> assertEquals("Title", result.getTitle()),
                () -> verify(taskRepository, times(1)).save(any(Task.class))
        );
    }

    @Test
    @DisplayName("Error Case: Pobranie zadania innego użytkownika")
    void testShouldThrowExceptionWhenAccessingOtherUserTask() {
        mockSecurity();
        Task otherTask = new Task();
        User otherUser = new User();
        otherUser.setId(99L);
        otherTask.setUser(otherUser);
        otherTask.setId(10L);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(otherTask));

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(10L));
    }

    @Test
    @DisplayName("Happy Path: Aktualizacja zadania")
    void testShouldUpdateTaskSuccessfully() {
        mockSecurity();
        Task existing = new Task("Old", "Desc", LocalDate.now(), null, Status.TODO, user);
        existing.setId(1L);

        TaskDTO updateDto = new TaskDTO("New", "New Desc", Status.DONE, LocalDate.now(), null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateTask(1L, updateDto);

        assertAll("Aktualizacja",
                () -> assertEquals("New", updated.getTitle()),
                () -> assertEquals(Status.DONE, updated.getStatus())
        );
    }

    @Test
    @DisplayName("Error Case: Usuwanie nieistniejącego zadania")
    void testShouldThrowExceptionWhenDeletingNonExistentTask() {
        mockSecurity();
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(999L));
    }
    @Test
    @DisplayName("Pobieranie zadań: Filtrowanie po tytule")
    void testShouldReturnTasksFilteredByTitle() {
        mockSecurity();
        Page<Task> page = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.searchByTitleAndUser(eq("Test"), eq(user), any(Pageable.class)))
                .thenReturn(page);

        Page<Task> result = taskService.getTasks("Test", null, null, Pageable.unpaged());

        assertAll("Search by title",
                () -> assertEquals(1, result.getTotalElements()),
                () -> verify(taskRepository).searchByTitleAndUser(eq("Test"), eq(user), any(Pageable.class))
        );
    }
    @Test
    @DisplayName("Pobieranie zadań: Filtrowanie po statusie i kategorii")
    void testShouldReturnTasksFilteredByStatusAndCategory() {
        mockSecurity();
        Page<Task> page = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByStatusAndCategoryIdAndUser(eq(Status.TODO), eq(5L), eq(user), any(Pageable.class)))
                .thenReturn(page);

        Page<Task> result = taskService.getTasks(null, Status.TODO, 5L, Pageable.unpaged());

        assertAll("Search by Status and Category",
                () -> assertEquals(1, result.getTotalElements()),
                () -> verify(taskRepository).findByStatusAndCategoryIdAndUser(any(), any(), any(), any())
        );
    }
    @Test
    @DisplayName("Pobieranie zadań: Filtrowanie tylko po statusie")
    void testShouldReturnTasksFilteredByStatusOnly() {
        mockSecurity();
        Page<Task> page = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByStatusAndUser(eq(Status.DONE), eq(user), any(Pageable.class)))
                .thenReturn(page);

        Page<Task> result = taskService.getTasks(null, Status.DONE, null, Pageable.unpaged());

        verify(taskRepository).findByStatusAndUser(eq(Status.DONE), eq(user), any());
    }
    @Test
    @DisplayName("Pobieranie zadań: Brak filtrów (zwraca wszystkie)")
    void testShouldReturnAllTasksWhenNoFilterProvided() {
        mockSecurity();
        Page<Task> page = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);

        taskService.getTasks(null, null, null, Pageable.unpaged());

        verify(taskRepository).findByUser(eq(user), any());
    }
    @Test
    @DisplayName("Załączniki: Dodanie pliku do zadania")
    void testShouldUploadAttachmentSuccessfully() {
        mockSecurity();
        Task task = new Task();
        task.setId(1L);
        task.setTitle("TaskFile");
        task.setUser(user);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(fileStorageService.storeFile(mockFile, "TaskFile")).thenReturn("stored_file.pdf");
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        Task updatedTask = taskService.uploadAttachment(1L, mockFile);

        assertAll("Upload attachment",
                () -> assertEquals("stored_file.pdf", updatedTask.getAttachmentFilename()),
                () -> verify(fileStorageService).storeFile(mockFile, "TaskFile")
        );
    }

    @Test
    @DisplayName("Załączniki: Pobranie ścieżki pliku")
    void testShouldGetAttachmentPath() {
        mockSecurity();
        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setAttachmentFilename("file.txt");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(fileStorageService.loadFile("file.txt")).thenReturn(Paths.get("uploads/file.txt"));

        Path path = taskService.getAttachment(1L);

        assertEquals(Paths.get("uploads/file.txt"), path);
    }

    @Test
    @DisplayName("Załączniki: Błąd przy pobieraniu braku pliku")
    void testShouldThrowExceptionWhenNoAttachmentFound() {
        mockSecurity();
        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setAttachmentFilename(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.getAttachment(1L));
    }

    @Test
    @DisplayName("Załączniki: Usuwanie załącznika")
    void testShouldDeleteAttachment() {
        mockSecurity();
        Task task = new Task();
        task.setId(1L);
        task.setUser(user);
        task.setAttachmentFilename("todelete.jpg");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteAttachment(1L);

        assertAll("Delete attachment",
                () -> assertNull(task.getAttachmentFilename()),
                () -> verify(fileStorageService).deleteFile("todelete.jpg"),
                () -> verify(taskRepository).save(task)
        );
    }
    @Test
    @DisplayName("Export: Generowanie CSV")
    void testShouldExportTasksToCsv() throws IOException {
        mockSecurity();
        Task t1 = new Task("T1", "D1", LocalDate.now(), null, Status.TODO, user);
        t1.setId(10L);
        when(taskRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(t1)));

        StringWriter writer = new StringWriter();
        taskService.exportTasksToCsv(writer);

        String content = writer.toString();
        assertAll("CSV Content",
                () -> assertTrue(content.contains("ID,Title,Description")),
                () -> assertTrue(content.contains("T1")),
                () -> assertTrue(content.contains("D1"))
        );
    }
}
