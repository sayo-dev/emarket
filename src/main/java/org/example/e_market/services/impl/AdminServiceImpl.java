package org.example.e_market.services.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.PlatformConfig;
import org.example.e_market.entities.enums.PayoutSchedule;
import org.example.e_market.entities.enums.VendorStatus;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.mapper.VendorMapper;
import org.example.e_market.repositories.PlatformConfigRepository;
import org.example.e_market.services.AdminService;
import org.example.e_market.services.ProvisioningService;
import org.example.e_market.utils.filters.VendorFilter;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.utils.specifications.VendorSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final VendorRepository vendorRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final VendorMapper vendorMapper;
    private final ProvisioningService provisioningService;


    @Override
    public void approveVendor(String vendorId) {

        Vendor vendor = findVendorById(vendorId);

        if (vendor.getStatus() != VendorStatus.PENDING)
            throw new CustomBadRequestException("Vendor is not pending");
        vendor.setStatus(VendorStatus.ACTIVE);
        vendorRepository.save(vendor);

        try {
            provisioningService.provisionVendor(vendor);


        } catch (final Exception e) {
            rollbackVendorStatus(vendor);
        }
    }

    @Override
    public void suspendVendor(String vendorId) {

        Vendor vendor = findVendorById(vendorId);

        if (vendor.getStatus() != VendorStatus.ACTIVE)
            throw new CustomBadRequestException("Vendor is not active");

        vendor.setStatus(VendorStatus.SUSPENDED);
        vendorRepository.save(vendor);
    }


    @Override
    public void updateGlobalConfig(BigDecimal rate, BigDecimal threshold, PayoutSchedule schedule) {

        PlatformConfig platformConfig = platformConfigRepository.findById(1).orElseGet(PlatformConfig::new);
        platformConfig.setCommissionRatePercent(rate);
        platformConfig.setPayoutSchedule(schedule);
        platformConfig.setMinPayoutThreshold(threshold);

        platformConfigRepository.save(platformConfig);
    }

    @Override
    public PageResponse<VendorResponse> getAllVendors(VendorFilter filter, int page, int size, String sortBy, String sortDir) {

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Specification<Vendor> specification = VendorSpecification.filter(filter);

        var vendorPage = vendorRepository.findAll(specification, pageable).map(vendorMapper::toResponse);
        return PageResponse.of(vendorPage);
    }


    private void rollbackVendorStatus(Vendor vendor) {
        vendor.setStatus(VendorStatus.PENDING);
        vendorRepository.save(vendor);
    }

    private Vendor findVendorById(final String vendorId) {

        return vendorRepository.findById(vendorId).orElseThrow(()
                -> new CustomNotFoundException("Vendor not found"));

    }

    @PostConstruct
    public void initPlatformConfig(

    ) {
        updateGlobalConfig(BigDecimal.valueOf(23), BigDecimal.valueOf(50000), PayoutSchedule.MONTHLY);
    }
}
