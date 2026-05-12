package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.e_market.entities.enums.PayoutSchedule;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PlatformConfig {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = 1;

    private BigDecimal commissionRatePercent = BigDecimal.TEN;

    private BigDecimal minPayoutThreshold = BigDecimal.valueOf(2000.0);

    @Enumerated(EnumType.STRING)
    private PayoutSchedule payoutSchedule = PayoutSchedule.MONTHLY;

    @LastModifiedDate
    @Column(nullable = false, insertable = false)
    private LocalDateTime updatedAt;


}
