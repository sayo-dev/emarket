package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.CreateCategoryRequest;
import org.example.e_market.dto.responses.CategoryResponse;
import org.example.e_market.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", categoryService.createCategory(request)));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(ApiResponse.success("Category tree fetched successfully", categoryService.getCategoryTree()));
    }
}
