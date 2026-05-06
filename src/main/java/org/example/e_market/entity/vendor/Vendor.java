package org.example.e_market.entity.vendor;

import jakarta.persistence.*;
import lombok.*;
import org.example.e_market.entity.enums.VendorStatus;

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
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String businessName;

    private String businessEmail;

    private String phone;

    private String bankAccountNumber;

    private String bankName;

    @Enumerated(EnumType.STRING)
    private VendorStatus status = VendorStatus.PENDING;

    private BigDecimal totalEarnings = BigDecimal.ZERO;

    private BigDecimal availablePayoutBalance = BigDecimal.ZERO;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime deletedAt;
}
