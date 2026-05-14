package org.example.e_market.services.impl;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.dto.requests.*;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.AccountType;
import org.example.e_market.entities.enums.OtpPurpose;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomConflictException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.exceptions.UnauthorizedException;
import org.example.e_market.repositories.UserRepository;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.security.JwtService;
import org.example.e_market.services.EmailService;
import org.example.e_market.services.AuthService;
import org.example.e_market.utils.Helper;
import org.example.e_market.common.TokenPair;
import org.example.e_market.dto.responses.LoginResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

    @Override
    public void registerAccount(RegisterAccountRequest request) {

        if (userRepository.existsByEmailAndIsVerifiedTrue(request.email()))
            throw new CustomConflictException("Email already registered");


        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseGet(User::new);

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAccountType(AccountType.CUSTOMER);

        user = userRepository.save(user);
        sendVerificationMail(user.getEmail(), user.getName());
    }

    @Transactional
    @Override
    public void registerVendor(VendorRequest request) {

        if (userRepository.existsByEmail(request.adminEmail())) {

            throw new CustomConflictException("Admin already exist");
        }

        if (vendorRepository.existsByNameOrEmail(request.businessName(), request.businessEmail()))
            throw new CustomConflictException("Business already exists");
        Vendor vendor = new Vendor();
        vendor.setBusinessName(request.businessName());
        vendor.setBusinessEmail(request.businessEmail());
        vendor.setPhone(request.phone());

//        Vendor vendor = Vendor.builder()
//                .businessName(request.businessName())
//                .businessEmail(request.businessEmail())
//                .phone(request.phone())
//                .build();

        vendor = vendorRepository.save(vendor);

        User user = new User();
        user.setVendor(vendor);
        user.setName(request.adminName());
        user.setEmail(request.adminEmail());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAccountType(AccountType.VENDOR_ADMIN);


        user = userRepository.save(user);

        sendVerificationMail(user.getEmail(), user.getName());

    }

    @Override
    public LoginResponse login(LoginRequest request) {

        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new CustomNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new CustomBadRequestException("User not verified");
        }
        String vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;
        TokenPair tokenPair = jwtService.generateTokenPair(auth, user.getAccountType().name(), vendorId);
        
        return LoginResponse.builder()
                .tokenPair(tokenPair)
                .accountType(user.getAccountType().name())
                .build();
    }

    @Override
    public TokenPair refreshToken(TokenRefreshRequest request) {
        String username = jwtService.extractUsername(request.refreshToken());

        User user = userRepository.findByEmailIgnoreCase(username).orElseThrow(() -> new CustomNotFoundException("Invalid token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(request.refreshToken(), userDetails))
            throw new UnauthorizedException("Token expired or invalid");


        String vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;

        return jwtService.generateTokenPair(userDetails, user.getAccountType().name(), vendorId);
    }

    @Transactional
    @Override
    public void verifyOtp(OtpRequest request) {

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new CustomNotFoundException("User not found"));

        if (user.isVerified()) {
            throw new CustomBadRequestException("User already verified");
        }
        String hashedOtp = redisTemplate.opsForValue().get(getOtpKey(request.email())).toString();
        if (!passwordEncoder.matches(request.otp(), hashedOtp)) {
            throw new CustomBadRequestException("Invalid or expired otp");
        }
        user.setVerified(true);
    }

    @Override
    public void resendOtp(OtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new CustomNotFoundException("User not found"));

        sendVerificationMail(request.email(), user.getName());

    }


    private void sendVerificationMail(String email, String name) {
        String otp = Helper.generateNumericOtp(6);
        log.info("Verification code {}", otp);

        OtpRequest request =
                OtpRequest.builder().otp(otp).email(email).purpose(OtpPurpose.EMAIL_VERIFICATION).build();
        String hashedOtp = passwordEncoder.encode(request.otp());

        assert (hashedOtp != null);
        redisTemplate.opsForValue().set(getOtpKey(request.email()), hashedOtp, Duration.ofMinutes(5));

        try {

            emailService.sendMail(email, OtpPurpose.EMAIL_VERIFICATION.name().replaceAll("_", " "), "otp-verification", Map.of("userName", name, "otp", otp));

        } catch (MessagingException ex) {
            log.debug(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private String getOtpKey(String email) {
        return "otp::".concat(email);
    }

}
