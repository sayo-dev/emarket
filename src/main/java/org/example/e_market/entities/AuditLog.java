package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "audit_logs")
public class AuditLog extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private String action;

    private String entityType;

    private Long entityId;

    @Column(columnDefinition = "jsonb")
    private String metadata;

}
