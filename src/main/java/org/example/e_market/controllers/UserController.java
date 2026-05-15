package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.responses.UserProfileResponse;
import org.example.e_market.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        return ResponseEntity.ok(ApiResponse.success("User profile fetched successfully", userService.getUserProfile()));
    }
}
