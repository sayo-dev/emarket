package org.example.e_market.utils.filters;

import lombok.*;
import org.example.e_market.entities.enums.VendorStatus;

@Value
@Builder
public class VendorFilter {

    String businessName;
    VendorStatus status;

}
