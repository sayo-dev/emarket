package org.example.e_market.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}