package org.example.e_market.dto.response;

import lombok.Value;
import org.example.e_market.entity.enums.VendorStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class VendorResponse {

    UUID id;

    String businessName;

    VendorStatus status;

    BigDecimal totalEarnings;

}
