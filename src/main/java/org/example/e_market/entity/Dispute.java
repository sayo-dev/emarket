package org.example.e_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.e_market.entity.enums.DisputeStatus;
import org.example.e_market.entity.order.Order;
import org.example.e_market.entity.vendor.Vendor;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "disputes")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
