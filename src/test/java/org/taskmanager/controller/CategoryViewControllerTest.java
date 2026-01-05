package org.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.model.Category;
import org.taskmanager.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryViewController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void testShouldDisplayListOfCategories() throws Exception {
        List<Category> categories = new ArrayList<>();
        Category c1 = new Category("Work", "#ffffff", null);
        c1.setId(1L);
        Category c2 = new Category("Home", "#000000", null);
        c2.setId(2L);
        categories.add(c1);
        categories.add(c2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", hasSize(2)))
                .andExpect(model().attribute("categories", everyItem(instanceOf(Category.class))));

        verify(categoryService).getAllCategories();
    }

    @Test
    void testShouldDisplayCreateForm() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/form"))
                .andExpect(model().attributeExists("categoryDTO"));
    }

    @Test
    void testShouldSaveCategoryAndRedirect() throws Exception {
        Category created = new Category("New Cat", "#123456", null);
        created.setId(99L);
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(created);

        mockMvc.perform(post("/categories/save")
                        .param("name", "New Cat")
                        .param("color", "#123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).createCategory(any(CategoryDTO.class));
    }

    @Test
    void testShouldShowEditForm() throws Exception {
        Category cat = new Category("EditMe", "#abc", null);
        cat.setId(5L);
        when(categoryService.getCategoryById(5L)).thenReturn(cat);

        mockMvc.perform(get("/categories/edit/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/form"))
                .andExpect(model().attributeExists("categoryDTO"))
                .andExpect(model().attribute("categoryId", is(5L)));

        verify(categoryService).getCategoryById(5L);
    }

    @Test
    void testShouldUpdateCategoryAndRedirect() throws Exception {
        Category updated = new Category("Updated", "#fff", null);
        updated.setId(5L);
        when(categoryService.updateCategory(eq(5L), any(CategoryDTO.class))).thenReturn(updated);

        mockMvc.perform(post("/categories/update/{id}", 5L)
                        .param("name", "Updated")
                        .param("color", "#fff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).updateCategory(eq(5L), any(CategoryDTO.class));
    }

    @Test
    void testShouldReturnFormOnUpdateValidationError() throws Exception {
        mockMvc.perform(post("/categories/update/{id}", 6L)
                        .param("name", "")
                        .param("color", "#fff"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/form"))
                .andExpect(model().attribute("categoryId", is(6L)));

        verify(categoryService, never()).updateCategory(anyLong(), any());
    }

    @Test
    void testShouldDeleteCategoryAndRedirect() throws Exception {
        doNothing().when(categoryService).deleteCategory(3L);

        mockMvc.perform(get("/categories/delete/{id}", 3L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).deleteCategory(3L);
    }
}
