package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.dto.LoginRequest;
import org.example.e_market.dto.RegisterCustomerRequest;
import org.example.e_market.services.auth.AuthService;
import org.example.e_market.utils.ApiResponse;
import org.example.e_market.utils.TokenPair;
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

    @PostMapping("/create-customer")
    public ResponseEntity<ApiResponse<String>> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {

        authService.registerCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", null));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody LoginRequest request) {

        TokenPair tokenPair = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", tokenPair));
    }
}
