package com.example.registerloginplant.service;



import com.example.registerloginplant.entity.Category;
import com.example.registerloginplant.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findOrCreateCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            category = new Category(categoryName);
            categoryRepository.save(category);
        }
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        return optionalCategory.orElse(null);
    }


}
