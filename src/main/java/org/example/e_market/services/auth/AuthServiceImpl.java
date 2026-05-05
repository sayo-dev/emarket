package org.example.e_market.services.auth;

import lombok.RequiredArgsConstructor;
import org.example.e_market.dto.LoginRequest;
import org.example.e_market.dto.RegisterCustomerRequest;
import org.example.e_market.entity.User;
import org.example.e_market.entity.enums.AccountType;
import org.example.e_market.exceptions.CustomConflictException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.UserRepository;
import org.example.e_market.security.JwtService;
import org.example.e_market.utils.TokenPair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    public void registerCustomer(RegisterCustomerRequest dto) {

        if (userRepository.existsByEmail(dto.email())) throw new CustomConflictException("Email already registered");

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .accountType(AccountType.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

    }

    @Override
    public TokenPair login(LoginRequest request) {

        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(()
                -> new CustomNotFoundException("User not found"));
        Long vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;
        return jwtService.generateTokenPair(auth, user.getAccountType().name(), vendorId);
    }
}
