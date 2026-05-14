package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.dto.requests.CreateCategoryRequest;
import org.example.e_market.dto.responses.CategoryResponse;
import org.example.e_market.entities.Category;
import org.example.e_market.exceptions.CustomConflictException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.mapper.CategoryMapper;
import org.example.e_market.repositories.CategoryRepository;
import org.example.e_market.services.CategoryService;
import org.example.e_market.services.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditLogService auditLogService;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.name()))
            throw new CustomConflictException("Category already created");

        Category parent = null;
        if (request.parentCategoryId() != null) {
            parent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new CustomNotFoundException("Parent category not found"));
        }

        Category category = Category.builder()
                .name(request.name())
                .parentCategory(parent)
                .build();

        category = categoryRepository.save(category);
        auditLogService.log("CREATE_CATEGORY", "Category", category.getId(), null);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Category not found"));

        Category parent = null;
        if (request.parentCategoryId() != null) {
            parent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new CustomNotFoundException("Parent category not found"));
            
            // Check circular reference
            Category current = parent;
            while (current != null) {
                if (current.getId().equals(id)) {
                    throw new CustomConflictException("Circular reference detected: cannot set parent to a child category");
                }
                current = current.getParentCategory();
            }
        }

        category.setName(request.name());
        category.setParentCategory(parent);
        category = categoryRepository.save(category);
        auditLogService.log("UPDATE_CATEGORY", "Category", category.getId(), null);
        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Category not found"));
        categoryRepository.delete(category);
        auditLogService.log("DELETE_CATEGORY", "Category", id, null);
    }

    @Override
    public List<CategoryResponse> getCategories() {
        List<Category> allCategories = categoryRepository.findAll();

        Map<Long, List<Category>> byParentId = allCategories.stream()
                .filter(c -> c.getParentCategory() != null)
                .collect(Collectors.groupingBy(c -> c.getParentCategory().getId()));

        List<Category> roots = allCategories.stream()
                .filter(c -> c.getParentCategory() == null)
                .collect(Collectors.toList());

        return roots.stream()
                .map(root -> buildTree(root, byParentId))
                .collect(Collectors.toList());
    }

    private CategoryResponse buildTree(Category category, Map<Long, List<Category>> byParentId) {
        List<Category> children = byParentId.getOrDefault(category.getId(), List.of());
        List<CategoryResponse> childResponses = children.stream()
                .map(child -> buildTree(child, byParentId))
                .collect(Collectors.toList());

        return categoryMapper.toResponseWithChildren(category, childResponses);
    }
}
