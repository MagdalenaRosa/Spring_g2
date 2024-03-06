package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    final CategoryRepository categoryRepository;

    public List<Category> showAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findByCategoryId(Long id) {
        return categoryRepository.findById(id);
    }

    public boolean existCategoryByCategoryName(Category category) {
        return categoryRepository.existsByName(category.getName());
    }

    public void insertCategory(Category category) throws CategoryArleadyExistException {
        var categoryExist = existCategoryByCategoryName(category);
        if (!categoryExist) {
            category.setId(null);
            categoryRepository.save(category);
        } else {
            throw new CategoryArleadyExistException();
        }
    }

    public void removeCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    public void updateCurrentCategory(Category category, Long id) {
        category.setId(id);
        categoryRepository.save(category);
    }

}
