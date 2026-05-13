package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CategoryResponse {
    Long id;
    String name;
    Long parentCategoryId;
    List<CategoryResponse> children;
}
