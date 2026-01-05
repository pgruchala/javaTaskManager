package org.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.model.Category;
import org.taskmanager.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "user")
    @DisplayName("GET /api/categories")
    void testShouldReturnAllCategories() throws Exception{
        List<Category> categories = Arrays.asList(
                new Category("Work","#000",null),
                new Category("Home","FFF",null)
        );
        when(categoryService.getAllCategories()).thenReturn(categories);
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Work")));
    }
    @Test
    @WithMockUser
    @DisplayName("GET /api/categories/{id} - Pobranie po ID")
    void testShouldReturnCategoryById() throws Exception {
        Category category = new Category("Work", "#000", null);
        category.setId(1L);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Work")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/categories - Utworzenie (Success 201)")
    void testShouldCreateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO("New Cat", "#123456");
        Category created = new Category("New Cat", "#123456", null);
        created.setId(10L);

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("New Cat")));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/categories/{id} - Aktualizacja")
    void testShouldUpdateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO("Updated", "#654321");
        Category updated = new Category("Updated", "#654321", null);

        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/categories/{id} - Usunięcie (204)")
    void testShouldDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }
}
