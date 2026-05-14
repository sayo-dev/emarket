package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record RaiseDisputeRequest(
    @NotBlank(message = "Reason is required")
    String reason,
    
    @NotBlank(message = "Description is required")
    String description
) {}
