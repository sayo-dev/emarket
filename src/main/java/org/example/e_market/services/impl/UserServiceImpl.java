package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.responses.UserProfileResponse;
import org.example.e_market.entities.User;
import org.example.e_market.services.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserUtil currentUserUtil;

    @Override
    public UserProfileResponse getUserProfile() {
        User user = currentUserUtil.getCurrentUser();
        
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .accountType(user.getAccountType())
                .isVerified(user.isVerified());

        if (user.getVendor() != null) {
            builder.vendorProfile(UserProfileResponse.VendorProfile.builder()
                    .id(user.getVendor().getId())
                    .businessName(user.getVendor().getBusinessName())
                    .businessEmail(user.getVendor().getBusinessEmail())
                    .phone(user.getVendor().getPhone())
                    .build());
        }

        return builder.build();
    }
}
