package org.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @GetMapping
    public void getCategories(){

    }
    @PostMapping
    public void createCategory(){

    }
    @PutMapping("/{id}")
    public void updateCategory(){

    }
    @DeleteMapping("/{id}")
    public void deleteCategory(){

    }


}
