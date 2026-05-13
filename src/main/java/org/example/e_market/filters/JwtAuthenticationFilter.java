package org.example.e_market.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.config.VendorContext;
import org.example.e_market.config.SchemaResolver;
import org.example.e_market.exceptions.UnauthorizedException;
import org.example.e_market.security.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CurrentUserUtil userUtil;
    private final SchemaResolver schemaResolver;

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getServletPath();
//        return path.startsWith("/api/v1/auth/");
//    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            final String jwt = extractJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                final String userEmail = jwtService.extractUsername(jwt);
                final String vendorId = jwtService.extractVendorId(jwt);
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(jwt, userDetails)) {

                        if (vendorId != null) {
                            VendorContext.setVendor(vendorId);
                            final String schema = schemaResolver.resolveSchema(vendorId);
                            VendorContext.setSchema(schema);
                        }
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("User authenticated for user Email: {}, vendor ID: {} and role: {}",
                                userDetails.getUsername(),
                                vendorId,
                                userDetails.getAuthorities());
                    }
                }
            }
        } catch (UnauthorizedException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return;
        } catch (final Exception e) {
            log.error("Error authenticating user", e);
        }

        filterChain.doFilter(request, response);

        VendorContext.clear();
    }

    private String extractJwtFromRequest(final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
