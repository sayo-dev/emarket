package org.example.e_market.mapper;

import org.example.e_market.dto.responses.AuditLogResponse;
import org.example.e_market.entities.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userEmail(auditLog.getUser() != null ? auditLog.getUser().getEmail() : null)
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .metadata(auditLog.getMetadata())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
