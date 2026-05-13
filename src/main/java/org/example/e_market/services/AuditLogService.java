package org.example.e_market.services;

public interface AuditLogService {
    void log(String action, String entityType, Long entityId, String metadata);
}
