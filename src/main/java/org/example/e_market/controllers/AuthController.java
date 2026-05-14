package org.example.e_market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.dto.requests.*;
import org.example.e_market.services.AuthService;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.common.TokenPair;
import org.example.e_market.dto.responses.LoginResponse;
import org.example.e_market.utils.views.OtpView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<String>> registerCustomer(@Valid @RequestBody RegisterAccountRequest request) {

        authService.registerAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", null));

    }

    @PostMapping("/register/vendor")
    public ResponseEntity<ApiResponse<String>> registerVendor(@Valid @RequestBody VendorRequest request) {

        authService.registerVendor(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Vendor created successfully", null));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<TokenPair>> verifyOtp(@JsonView(OtpView.Optional.class) @Valid @RequestBody OtpRequest request) {

        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("User verification successful", null));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<TokenPair>> resendOtp(@JsonView(OtpView.Base.class) @Valid @RequestBody OtpRequest request) {

        authService.resendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Otp resend successful", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenPair>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {

        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refresh successful", tokenPair));
    }


}
