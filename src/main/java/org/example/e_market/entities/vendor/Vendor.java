package org.example.e_market.entities.vendor;

import jakarta.persistence.*;
import lombok.*;
import org.example.e_market.entities.enums.VendorStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vendors")
@EntityListeners(AuditingEntityListener.class)
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessEmail;

    private String phone;

    private String bankAccountNumber;

    private String bankName;

    @Enumerated(EnumType.STRING)
    private VendorStatus status = VendorStatus.PENDING;

    private BigDecimal totalEarnings = BigDecimal.ZERO;

    private BigDecimal availablePayoutBalance = BigDecimal.ZERO;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;
}
