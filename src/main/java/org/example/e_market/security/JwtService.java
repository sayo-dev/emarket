package org.example.e_market.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.e_market.utils.TokenPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class JwtService {

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.jwt-expiration}")
    private long jwtExpiration;

    @Value("${app.security.jwt.refresh-expiration}")
    private long refreshExpiration;


    public TokenPair generateTokenPair(Authentication authentication, String accountType, UUID vendorId) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildTokenPair(userDetails.getUsername(), accountType, vendorId);
    }

    public TokenPair generateTokenPair(UserDetails userDetails, String accountType, UUID vendorId) {

        return buildTokenPair(userDetails.getUsername(), accountType, vendorId);
    }

    private TokenPair buildTokenPair(String username, String accountType, UUID vendorId) {

        String accessToken = generateAccessToken(username, accountType, vendorId);
        String refreshToken = generateRefreshToken(username);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateRefreshToken(String username) {

        return generateToken(username, refreshExpiration, Map.of());
    }

    public String generateAccessToken(String username, String accountType, UUID vendorId) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("accountType", accountType);
        claims.put("vendorId", vendorId);

        return generateToken(username, jwtExpiration, claims);
    }

    public String generateToken(String username, long expiration, Map<String, Object> claims) {


//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expires = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expires)
                .signWith(signKey())
                .compact();
    }


    public String extractUsername(String token) {

        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private SecretKey signKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
