package org.example.e_market.services;

import org.example.e_market.dto.requests.CreateCategoryRequest;
import org.example.e_market.dto.responses.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, CreateCategoryRequest request);

    void deleteCategory(Long id);

    List<CategoryResponse> getCategories();
}
