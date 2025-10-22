package com.example.IMS_Backend.controller;

import com.example.IMS_Backend.model.Category;
import com.example.IMS_Backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            System.out.println("=== GETTING ALL CATEGORIES ===");
            List<Category> categories = categoryService.findAll();
            System.out.println("Found " + categories.size() + " categories");
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            System.out.println("ERROR getting categories: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            System.out.println("=== CREATING CATEGORY ===");
            System.out.println("Category name: " + category.getName());
            System.out.println("Category description: " + category.getDescription());

            // Validate input
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Category name is required!");
            }

            // Check if category already exists
            if (categoryService.existsByName(category.getName())) {
                return ResponseEntity.badRequest().body("Error: Category name already exists!");
            }

            // Create new category (ignore ID if provided)
            Category newCategory = new Category();
            newCategory.setName(category.getName().trim());
            newCategory.setDescription(category.getDescription());

            Category savedCategory = categoryService.save(newCategory);
            System.out.println("Category created successfully with ID: " + savedCategory.getId());

            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            System.out.println("ERROR creating category: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            System.out.println("Getting category by ID: " + id);
            return categoryService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("ERROR getting category: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            System.out.println("Updating category ID: " + id);

            // Check if category exists
            Category existingCategory = categoryService.findById(id).orElse(null);
            if (existingCategory == null) {
                return ResponseEntity.notFound().build();
            }

            // Validate name
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Category name is required!");
            }

            // Check if name is being changed and if new name already exists
            if (!existingCategory.getName().equals(category.getName()) &&
                    categoryService.existsByName(category.getName())) {
                return ResponseEntity.badRequest().body("Error: Category name already exists!");
            }

            existingCategory.setName(category.getName().trim());
            existingCategory.setDescription(category.getDescription());

            Category updatedCategory = categoryService.save(existingCategory);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            System.out.println("ERROR updating category: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            System.out.println("Deleting category ID: " + id);

            if (!categoryService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }

            categoryService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("ERROR deleting category: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}