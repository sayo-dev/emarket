package org.example.e_market.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenPair {

    private String accessToken;
    private String refreshToken;
}
