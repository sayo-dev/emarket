package org.example.e_market.services;

import org.example.e_market.dto.requests.*;
import org.example.e_market.common.TokenPair;
import org.example.e_market.dto.responses.LoginResponse;

public interface AuthService {

    void registerAccount(RegisterAccountRequest request);

    void registerVendor(VendorRequest request);

    LoginResponse login(LoginRequest request);

    TokenPair refreshToken(TokenRefreshRequest request);

    void verifyOtp(OtpRequest request);

    void resendOtp(OtpRequest request);

}
