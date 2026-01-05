package org.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.taskmanager.dto.TaskDTO;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.model.User;
import org.taskmanager.service.FileStorageService;
import org.taskmanager.service.TaskService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/tasks")
    void testShouldReturnListOfTasks() throws Exception {
        Task task = new Task("Test Task", "Desc", LocalDate.now(), null, Status.TODO, new User());
        task.setId(1L);

        when(taskService.getTasks(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(task)));

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", is("Test Task")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/tasks/{id} - Pobranie konkretnego zadania")
    void testShouldReturnTaskById() throws Exception {
        Task task = new Task("Test Task", "Desc", LocalDate.now(), null, Status.TODO, new User());
        task.setId(10L);

        when(taskService.getTaskById(10L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("POST /api/tasks - Utworzenie zadania (Valid Data)")
    void testShouldCreateTask() throws Exception {
        TaskDTO dto = new TaskDTO("New Task", "Desc", Status.TODO, LocalDate.now().plusDays(1), null);
        Task createdTask = new Task("New Task", "Desc", dto.getDueDate(), null, Status.TODO, new User());
        createdTask.setId(1L);

        when(taskService.createTask(any(TaskDTO.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Task")));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("POST /api/tasks - Walidacja błędu (Title too short)")
    void testShouldReturnBadRequestWhenTitleIsInvalid() throws Exception {
        TaskDTO dto = new TaskDTO("No", "Desc", Status.TODO, LocalDate.now().plusDays(1), null);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("DELETE /api/tasks/{id} - Usunięcie zadania")
    void testShouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }
    @Test
    @WithMockUser(username = "user")
    @DisplayName("POST -/api/tasks/{id}/attachment  - Upload")
    void testShouldUploadFile() throws Exception{
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE,"Hello".getBytes());

        Task taskWithFile = new Task();
        taskWithFile.setId(1L);
        taskWithFile.setAttachmentFilename("test.txt");
        when(taskService.uploadAttachment(eq(1L),any(MultipartFile.class))).thenReturn(taskWithFile);

        mockMvc.perform(multipart("/api/tasks/1/attachment").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attachmentFilename", is("test.txt")));
    }
    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/tasks/export/csv - pobieranie CSV")
    void testShouldDownloadCSVFile() throws Exception{
        doNothing().when(taskService).exportTasksToCsv(any());
        mockMvc.perform(get("/api/tasks/export/csv")).andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"tasks_export.csv\""));

        verify(taskService).exportTasksToCsv(any());
    }
    @Test
    @WithMockUser(username = "user")
    @DisplayName("PUT /api/tasks/{id} - Aktualizacja zadania")
    void testShouldUpdateTask() throws Exception {
        TaskDTO dto = new TaskDTO("Updated", "Desc", Status.DONE, LocalDate.now(), null);
        Task updatedTask = new Task("Updated", "Desc", LocalDate.now(), null, Status.DONE, new User());
        updatedTask.setId(1L);

        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")))
                .andExpect(jsonPath("$.status", is("DONE")));
    }
    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/task/{id}/attachment - pobieranie załącznika")
    void testShouldDownloadUploadedFile() throws Exception {
        Long taskId = 1L;

        Path tempFile = Files.createTempFile("attachment-", ".txt");
        Files.writeString(tempFile, "Hello attachment");

        when(taskService.getAttachment(taskId)).thenReturn(tempFile);
        mockMvc.perform(get("/api/tasks/{id}/attachment", taskId))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"" + tempFile.getFileName().toString() + "\""
                ))
                .andExpect(content().string("Hello attachment"));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/task/{id}/attachment - błąd w pobieraniu załącznika (brak załącznika)")
    void testShouldReturn404WhenAttachmentDoesNotExist() throws Exception {
        when(taskService.getAttachment(1L))
                .thenReturn(Path.of("not/existing/file.txt"));

        mockMvc.perform(get("/api/tasks/1/attachment"))
                .andExpect(status().isNotFound());
    }
}