package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;
import org.example.e_market.entities.enums.VendorStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class VendorResponse {

    String id;

    String businessName;

    String businessEmail;

    String phone;

    String bankAccountNumber;

    String bankName;

    VendorStatus status;

    BigDecimal totalEarnings;

}
