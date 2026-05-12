package org.example.e_market.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email String email,
        @NotBlank String password
) {}