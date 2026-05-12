package org.example.e_market.mapper;

import org.example.e_market.dto.requests.VendorRequest;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.vendor.Vendor;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {

    public VendorResponse toResponse(final Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .businessEmail(vendor.getBusinessEmail())
                .phone(vendor.getPhone())
                .bankAccountNumber(vendor.getBankAccountNumber())
                .bankName(vendor.getBankName())
                .totalEarnings(vendor.getTotalEarnings())
                .status(vendor.getStatus())
                .build();
    }

    public Vendor toEntity(final VendorRequest request) {

        return Vendor.builder()
                .businessName(request.businessName())
                .businessEmail(request.businessEmail())
                .phone(request.phone())
                .build();

    }


}
