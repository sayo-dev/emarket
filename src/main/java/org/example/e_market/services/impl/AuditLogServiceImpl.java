package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.entities.AuditLog;
import org.example.e_market.repositories.AuditLogRepository;
import org.example.e_market.services.AuditLogService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserUtil currentUserUtil;

    @Override
    public void log(String action, String entityType, Long entityId, String metadata) {
        AuditLog auditLog = AuditLog.builder()
                .user(currentUserUtil.getCurrentUser())
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .metadata(metadata)
                .build();
        auditLogRepository.save(auditLog);
    }
}
