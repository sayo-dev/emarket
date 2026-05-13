package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.requests.UpdateVendorProfileRequest;
import org.example.e_market.dto.responses.PayoutResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.AccountType;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.mapper.VendorMapper;
import org.example.e_market.repositories.UserRepository;
import org.example.e_market.repositories.VendorPayoutRepository;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.services.VendorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorPayoutRepository vendorPayoutRepository;
    private final UserRepository userRepository;
    private final CurrentUserUtil currentUserUtil;
    private final VendorMapper vendorMapper;

    @Override
    public void updateStoreProfile(UpdateVendorProfileRequest request) {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (vendor == null) {
            throw new CustomNotFoundException("Vendor profile not found for current user");
        }
        vendor.setBusinessName(request.businessName());
        vendor.setPhone(request.phone());
        vendor.setBankAccountNumber(request.bankAccountNumber());
        vendor.setBankName(request.bankName());
        vendorRepository.save(vendor);
        log.info("Updated store profile for vendor: {}", vendor.getId());
    }

    @Override
    public VendorResponse getEarningsSummary() {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (vendor == null) {
            throw new CustomNotFoundException("Vendor profile not found for current user");
        }
        return vendorMapper.toResponse(vendor);
    }

    @Override
    public List<PayoutResponse> getPayoutHistory() {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (vendor == null) {
            throw new CustomNotFoundException("Vendor profile not found for current user");
        }
        return vendorPayoutRepository.findByVendor(vendor).stream()
                .map(payout -> PayoutResponse.builder()
                        .id(payout.getId())
                        .amount(payout.getAmount())
                        .status(payout.getStatus())
                        .reference(payout.getReference())
                        .processedAt(payout.getProcessedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void inviteStaff(String email) {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (vendor == null) {
            throw new CustomNotFoundException("Vendor profile not found for current user");
        }

        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(()
                -> new CustomNotFoundException("User not found"));


        if (user.getAccountType() != AccountType.CUSTOMER)
            throw new CustomBadRequestException("Cannot invite user");

        user.setVendor(vendor);
        user.setAccountType(AccountType.VENDOR_STAFF);
        userRepository.save(user);
        log.info("Invited staff {} to vendor {}", email, vendor.getId());

        // TODO: Send email with setup link
    }
}
