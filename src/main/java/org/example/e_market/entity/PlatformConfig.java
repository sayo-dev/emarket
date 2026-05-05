package org.example.e_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.e_market.entity.enums.PayoutSchedule;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class PlatformConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal commissionRatePercent;

    private BigDecimal minPayoutThreshold;

    @Enumerated(EnumType.STRING)
    private PayoutSchedule payoutSchedule;

    private LocalDateTime updatedAt = LocalDateTime.now();


}
