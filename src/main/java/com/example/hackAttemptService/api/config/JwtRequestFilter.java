package com.example.hackAttemptService.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    private String secret = "NzMxNmY3YTcwMzdiMzQ0YmI2MTdkMmVkZDQ1Yzg0NDc=";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (secret == null || secret.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("Secret key is missing in configuration!");
            return;
        }

        if (request.getRequestURI().startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("token: {}", request.getHeader("Authorization"));

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = authHeader.substring(7);
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();


            log.debug("Token is valid, claims: {}", claims);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, new ArrayList<>()
            ));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("Token validation failed: " + e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }
}
