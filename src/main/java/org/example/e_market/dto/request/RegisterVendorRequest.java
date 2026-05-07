package org.example.e_market.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterVendorRequest(
        @NotBlank String businessName,
        @Email String businessEmail,
        @NotBlank String phone,
        @NotBlank String adminName,
        @Email String adminEmail,
        @NotBlank @Size(min = 8) String password
) {
}