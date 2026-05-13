package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        Long parentCategoryId
) {
}
