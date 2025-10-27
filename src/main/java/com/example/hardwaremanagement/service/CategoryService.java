package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Category;
import com.example.hardwaremanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category create(Category category) { return categoryRepository.save(category); }
    public List<Category> list() { return categoryRepository.findAll(); }
    public Category get(String id) { return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found: " + id)); }
    public Category update(String id, Category update) {
        Category existing = get(id);
        existing.setName(update.getName());
        existing.setDescription(update.getDescription());
        existing.setActive(update.isActive());
        existing.setDisplayOrder(update.getDisplayOrder());
        return categoryRepository.save(existing);
    }
    public void delete(String id) { categoryRepository.deleteById(id); }
}
