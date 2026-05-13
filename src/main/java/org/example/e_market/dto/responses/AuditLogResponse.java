package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AuditLogResponse {
    Long id;
    String userEmail;
    String action;
    String entityType;
    Long entityId;
    String metadata;
    LocalDateTime createdAt;
}
