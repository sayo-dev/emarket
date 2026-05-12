package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank String refreshToken
) {
}
