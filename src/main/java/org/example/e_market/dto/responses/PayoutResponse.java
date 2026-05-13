package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;
import org.example.e_market.entities.enums.VendorPayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class PayoutResponse {
    Long id;
    BigDecimal amount;
    VendorPayoutStatus status;
    String reference;
    LocalDateTime processedAt;
}
