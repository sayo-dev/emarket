package org.example.e_market.utils;

import lombok.RequiredArgsConstructor;
import org.example.e_market.entity.User;
import org.example.e_market.entity.enums.AccountType;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CurrentUserUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomNotFoundException("User not found in context"));
    }

    public UUID getCurrentVendorId() {
        return getCurrentUser().getVendor().getId();
    }

    public boolean isPlatformAdmin() {
        return getCurrentUser().getAccountType().equals(AccountType.PLATFORM_ADMIN);
    }
}
