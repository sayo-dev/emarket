package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.enums.DisputeStatus;
import org.example.e_market.entities.order.Order;
import org.example.e_market.entities.vendor.Vendor;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "disputes")
public class Dispute extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status = DisputeStatus.OPEN;

    @OneToOne(fetch = FetchType.LAZY)
    private User resolvedBy;

    private LocalDateTime resolvedAt;
}
