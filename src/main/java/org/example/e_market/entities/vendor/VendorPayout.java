package org.example.e_market.entities.vendor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;
import org.example.e_market.entities.enums.VendorPayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vendor_payouts")
public class VendorPayout extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private VendorPayoutStatus status = VendorPayoutStatus.PENDING;

    @Column(unique = true)
    private String reference;

    private LocalDateTime processedAt;

}
