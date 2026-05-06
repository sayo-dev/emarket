package org.example.e_market.services.auth;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.e_market.dto.*;
import org.example.e_market.entity.User;
import org.example.e_market.entity.enums.AccountType;
import org.example.e_market.entity.enums.OtpPurpose;
import org.example.e_market.entity.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomConflictException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.UserRepository;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.security.JwtService;
import org.example.e_market.services.EmailService;
import org.example.e_market.utils.Helper;
import org.example.e_market.utils.TokenPair;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
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
    public void registerCustomer(RegisterCustomerRequest request) {

        if (userRepository.existsByEmail(request.email()))
            throw new CustomConflictException("Email already registered");

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .accountType(AccountType.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        sendVerificationMail(user.getEmail(), user.getName());
    }

    @Transactional
    @Override
    public void registerVendor(RegisterVendorRequest request) {

        if (userRepository.existsByEmail(request.adminEmail()))
            throw new CustomConflictException("Admin already exist");
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
//        User user = User.builder()
//                .name(request.adminName())
//                .email(request.adminEmail())
//                .password(passwordEncoder.encode(request.password()))
//                .accountType(AccountType.VENDOR_ADMIN)
//                .vendor(vendor)
//                .build();

        user = userRepository.save(user);

        sendVerificationMail(user.getEmail(), user.getName());

    }

    @Override
    public TokenPair login(LoginRequest request) {

        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(()
                -> new CustomNotFoundException("User not found"));

        if (!user.isVerified()) throw new CustomBadRequestException("User not verified");
        UUID vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;
        return jwtService.generateTokenPair(auth, user.getAccountType().name(), vendorId);
    }

    @Override
    public TokenPair refreshToken(TokenRefreshRequest request) {
        String username = jwtService.extractUsername(request.refreshToken());

        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new CustomNotFoundException("Invalid token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(request.refreshToken(), userDetails))
            throw new BadCredentialsException("Token expired or invalid");

        UUID vendorId = (user.getVendor() != null) ? user.getVendor().getId() : null;

        return jwtService.generateTokenPair(userDetails, user.getAccountType().name(), vendorId);
    }

    @Transactional
    @Override
    public void verifyOtp(OtpRequest request) {

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(()
                -> new CustomNotFoundException("User not found"));

        if (user.isVerified()) throw new CustomBadRequestException("User already verified");

        String hashedPassword = redisTemplate.opsForValue().get(getOtpKey(request.email())).toString();
        if (!passwordEncoder.matches(request.otp(), hashedPassword))
            throw new CustomBadRequestException("Invalid or expired otp");

        user.setVerified(true);
    }

    @Override
    public void resendOtp(OtpRequest request) {

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(()
                -> new CustomNotFoundException("User not found"));

        sendVerificationMail(request.email(), user.getName());


    }

    private void generateAndStoreOtp(OtpRequest request) {


        String hashedOtp = passwordEncoder.encode(request.otp());

        assert (hashedOtp != null);
        redisTemplate.opsForValue().set(getOtpKey(request.email()), hashedOtp, Duration.ofMinutes(5));
    }

    private void sendVerificationMail(String email, String name) {
        String otp = Helper.generateNumericOtp(6);
        generateAndStoreOtp(OtpRequest.builder()
                .otp(otp)
                .email(email)
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .build());

        try {

            emailService.sendMail(email,
                    OtpPurpose.EMAIL_VERIFICATION.name().replaceAll("_", " "),
                    "otp-verification", Map.of("userName", name, "otp", otp));

        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getOtpKey(String email) {
        return "otp::".concat(email);
    }
}
