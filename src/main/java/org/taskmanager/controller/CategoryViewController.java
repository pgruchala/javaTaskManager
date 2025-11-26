package org.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.taskmanager.dto.CategoryDTO;
import org.taskmanager.model.Category;
import org.taskmanager.service.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {
    private final CategoryService categoryService;

    public CategoryViewController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "categories/form";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);

        CategoryDTO dto = new CategoryDTO(category.getName(), category.getColor());

        model.addAttribute("categoryDTO", dto);
        model.addAttribute("categoryId", id);
        return "categories/form";
    }
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        categoryService.createCategory(categoryDTO);
        return "redirect:/categories";
    }
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
        categoryService.updateCategory(id, categoryDTO);
        return "redirect:/categories";
    }
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
