package org.example.e_market.entity.vendor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.e_market.entity.enums.VendorPayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "vendor_payouts")
public class VendorPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private VendorPayoutStatus status = VendorPayoutStatus.PENDING;

    @Column(unique = true)
    private String reference;

    private LocalDateTime processedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
