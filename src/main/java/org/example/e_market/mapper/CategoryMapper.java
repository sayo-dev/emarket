package org.example.e_market.mapper;

import org.example.e_market.dto.responses.CategoryResponse;
import org.example.e_market.entities.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .children(List.of())
                .build();
    }

    public CategoryResponse toResponseWithChildren(Category category, List<CategoryResponse> children) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .children(children)
                .build();
    }
}
