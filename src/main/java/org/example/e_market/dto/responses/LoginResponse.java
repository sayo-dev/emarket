package org.example.e_market.dto.responses;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.e_market.common.TokenPair;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    @JsonUnwrapped
    private TokenPair tokenPair;
    private String accountType;
}
