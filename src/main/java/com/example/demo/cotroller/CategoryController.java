package com.example.demo.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.services.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class CategoryController {
    final CategoryService categoryService;

    @GetMapping("/categories")
    public String showAllCategories(Model model) {
        model.addAttribute("title", "List of Categories");
        model.addAttribute("categories", categoryService.showAllCategories());
        model.addAttribute("action", "/saveCategory");
        return "/categories/categories";
    }

    @GetMapping("/categoryDetails/{id}")
    public String showCategoryDetails(@PathVariable Long id, Model model) {
        var optionalCategory = categoryService.findByCategoryId(id);
        if (optionalCategory.isEmpty()) {
            model.addAttribute("error", "Current category witj id=" + id + " doesn't exist");
            return "/error-page";
        }
        model.addAttribute("category", categoryService.findByCategoryId(id).get());
        return "/categories/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.insertCategory(category);
        } catch (CategoryArleadyExistException e) {
            redirectAttributes.addFlashAttribute("error", "Category arleady exists");
        }
        return "redirect:/categories";
    }

    @GetMapping("removeCategory/{id}")
    public String removeCategory(@PathVariable Long id) {
        categoryService.removeCategoryById(id);
        return "redirect:/categories";
    }

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        var optionalCategory = categoryService.findByCategoryId(id);
        if (optionalCategory.isEmpty()) {
            model.addAttribute("error", "Edit of category with id=" + id + " is NOT posible");
            return "/error-page";
        }
        model.addAttribute("action", "/editedCategory/" + id);
        model.addAttribute("category", optionalCategory.get());
        return "/categories/edit-category";
    }

    @PostMapping("/editedCategory/{id}")
    public String saveEditedCategory(@PathVariable Long id, Category category) {
        categoryService.updateCurrentCategory(category, id);
        var url = "redirect:/categoryDetails/" + id;
        return url;
    }

}
