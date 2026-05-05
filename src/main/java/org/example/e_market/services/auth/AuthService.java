package org.example.e_market.services.auth;

import org.example.e_market.dto.LoginRequest;
import org.example.e_market.dto.RegisterCustomerRequest;
import org.example.e_market.utils.TokenPair;

public interface AuthService {

    void registerCustomer(RegisterCustomerRequest request);

    TokenPair login(LoginRequest request);

}
