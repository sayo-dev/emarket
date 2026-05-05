package org.example.e_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private String action;

    private String entityType;

    private Long entityId;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
