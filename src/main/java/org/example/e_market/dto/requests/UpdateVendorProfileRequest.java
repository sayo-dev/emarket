package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateVendorProfileRequest(
        @NotBlank String businessName,
        @NotBlank String phone,
        @NotBlank String bankAccountNumber,
        @NotBlank String bankName
) {
}
