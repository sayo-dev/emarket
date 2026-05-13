package org.example.e_market.services;

import org.example.e_market.dto.requests.UpdateVendorProfileRequest;
import org.example.e_market.dto.responses.PayoutResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.enums.AccountType;

import java.util.List;

public interface VendorService {

    void updateStoreProfile(UpdateVendorProfileRequest request);

    VendorResponse getEarningsSummary();

    List<PayoutResponse> getPayoutHistory();

    void inviteStaff(String email);
}
