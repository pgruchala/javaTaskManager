package org.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.taskmanager.model.Category;
import org.taskmanager.model.Status;
import org.taskmanager.model.Task;
import org.taskmanager.service.CategoryService;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import org.taskmanager.service.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskViewController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void testShouldDisplayListOfTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("Task 1");
        t1.setStatus(Status.TODO);
        t1.setDueDate(LocalDate.now().plusDays(1));

        Task t2 = new Task();
        t2.setId(2L);
        t2.setTitle("Task 2");
        t2.setStatus(Status.IN_PROGRESS);
        t2.setDueDate(LocalDate.now().plusDays(2));

        tasks.add(t1);
        tasks.add(t2);

        when(taskService.getTasks(isNull(), isNull(), isNull(), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new PageImpl<>(tasks));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("tasks", hasSize(2)))
                .andExpect(model().attribute("statuses", arrayContainingInAnyOrder(Status.values())));

        verify(taskService).getTasks(isNull(), isNull(), isNull(), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void testShouldDisplayCreateForm() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attributeExists("taskDTO"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void testShouldSaveTaskAndRedirect() throws Exception {
        Task created = new Task();
        created.setId(10L);
        when(taskService.createTask(any())).thenReturn(created);

        mockMvc.perform(post("/tasks/save")
                        .param("title", "New Task")
                        .param("description", "Desc")
                        .param("status", "TODO")
                        .param("dueDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).createTask(any());
    }

    @Test
    void testShouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(post("/tasks/save")
                        .param("title", "")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"));

        verify(taskService, never()).createTask(any());
    }

    @Test
    void testShouldShowEditForm() throws Exception {
        Task t = new Task();
        t.setId(7L);
        t.setTitle("Old Task");
        t.setDescription("old");
        t.setStatus(Status.TODO);
        t.setDueDate(LocalDate.now().plusDays(3));
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Cat");
        t.setCategory(cat);

        when(taskService.getTaskById(7L)).thenReturn(t);
        when(categoryService.getAllCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/tasks/edit/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attributeExists("taskDTO"))
                .andExpect(model().attribute("taskId", is(7L)))
                .andExpect(model().attributeExists("categories"));

        verify(taskService).getTaskById(7L);
    }

    @Test
    void testShouldUpdateTaskAndRedirect() throws Exception {
        Task updated = new Task();
        updated.setId(7L);
        when(taskService.updateTask(eq(7L), any())).thenReturn(updated);

        mockMvc.perform(post("/tasks/update/{id}", 7L)
                        .param("title", "Updated Task")
                        .param("description", "desc")
                        .param("status", "DONE")
                        .param("dueDate", LocalDate.now().plusDays(5).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).updateTask(eq(7L), any());
    }

    @Test
    void testShouldReturnFormOnUpdateValidationError() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/tasks/update/{id}", 8L)
                        .param("title", "")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attribute("taskId", is(8L)));

        verify(taskService, never()).updateTask(anyLong(), any());
    }

    @Test
    void testShouldDeleteTaskAndRedirect() throws Exception {
        doNothing().when(taskService).deleteTask(3L);

        mockMvc.perform(get("/tasks/delete/{id}", 3L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).deleteTask(3L);
    }


    @Test
    void testShouldReturnFormWhenAttachmentInvalidOnSave() throws Exception {
        MockMultipartFile file = new MockMultipartFile("attachment", "file.txt", "text/plain", "data".getBytes());
        doThrow(new IllegalArgumentException("invalid")).when(taskService).validateAttachment(any());
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(multipart("/tasks/save").file(file)
                        .param("title", "New Task")
                        .param("description", "Desc")
                        .param("status", "TODO")
                        .param("dueDate", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attributeExists("attachmentError"));

        verify(taskService).validateAttachment(any());
    }

    @Test
    void testShouldReturnFormWhenAttachmentInvalidOnUpdate() throws Exception {
        MockMultipartFile file = new MockMultipartFile("attachment", "file.txt", "text/plain", "data".getBytes());
        doThrow(new IllegalArgumentException("invalid")).when(taskService).validateAttachment(any());
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        mockMvc.perform(multipart("/tasks/update/{id}", 9L).file(file)
                        .param("title", "Updated")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"))
                .andExpect(model().attribute("taskId", is(9L)))
                .andExpect(model().attributeExists("attachmentError"));

        verify(taskService).validateAttachment(any());
    }
}
