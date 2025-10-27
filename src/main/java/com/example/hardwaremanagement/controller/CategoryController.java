package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Category;
import com.example.hardwaremanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.create(category), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Category> list() { return categoryService.list(); }

    @GetMapping("/{id}")
    public Category get(@PathVariable String id) { return categoryService.get(id); }

    @PutMapping("/{id}")
    public Category update(@PathVariable String id, @RequestBody Category category) { return categoryService.update(id, category); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { categoryService.delete(id); return ResponseEntity.noContent().build(); }
}
