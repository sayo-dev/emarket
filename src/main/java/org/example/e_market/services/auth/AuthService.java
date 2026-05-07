package org.example.e_market.services.auth;

import org.example.e_market.dto.request.*;
import org.example.e_market.utils.TokenPair;

public interface AuthService {

    void registerAccount(RegisterAccountRequest request);

    void registerVendor(RegisterVendorRequest request);

    TokenPair login(LoginRequest request);

    TokenPair refreshToken(TokenRefreshRequest request);

    void verifyOtp(OtpRequest request);

    void resendOtp(OtpRequest request);

}
