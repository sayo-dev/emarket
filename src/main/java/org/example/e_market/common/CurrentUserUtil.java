package org.example.e_market.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.AccountType;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class CurrentUserUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new CustomNotFoundException("No active authentication found");
        }
        String email = auth.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.debug("User not found in context: {}", email);
                    return new CustomNotFoundException("User not found in context");
                });
    }


}
