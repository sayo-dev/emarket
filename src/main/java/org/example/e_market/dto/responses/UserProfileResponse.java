package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Data;
import org.example.e_market.entities.enums.AccountType;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private AccountType accountType;
    private boolean isVerified;
    private VendorProfile vendorProfile;

    @Data
    @Builder
    public static class VendorProfile {
        private String id;
        private String businessName;
        private String businessEmail;
        private String phone;
    }
}
