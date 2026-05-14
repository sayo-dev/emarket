package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.e_market.entities.enums.DisputeResolutionType;

public record ResolveDisputeRequest(
    @NotBlank(message = "Resolution notes are required")
    String resolutionNotes,
    
    @NotNull(message = "Resolution type is required")
    DisputeResolutionType resolutionType
) {}
