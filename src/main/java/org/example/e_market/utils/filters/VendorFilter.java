package org.example.e_market.utils.filters;

import lombok.*;
import org.example.e_market.entity.enums.VendorStatus;

@Value
@Builder
public class VendorFilter {

    String businessName;
    VendorStatus status;

}
