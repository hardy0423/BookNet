package com.ydrah.book.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final UserDetailsService userDetailsService;

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    public JwtService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder();

        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }
        builder.claim(Claims.SUBJECT, userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .claim(Claims.ISSUED_AT, new Date(System.currentTimeMillis()))
                .claim(Claims.EXPIRATION, new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 2))
                .signWith(getSignInKey());

        return builder.compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username != null && username.equals(userDetails.getUsername()) && isTokenNotExpired(token);
    }

    private boolean isTokenNotExpired(String token) {
        return extractExpiration(token).after(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateRefresh(Map<String, Object> extraClaims, UserDetails userDetails) {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder();

        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }

        builder.claim(Claims.SUBJECT, userDetails.getUsername())
                .claim(Claims.ISSUED_AT, new Date(System.currentTimeMillis()))
                .claim(Claims.EXPIRATION, new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 2))
                .signWith(getSignInKey());

        return builder.compact();
    }

    public String getEmailFromToken(String token) {
        return extractUserName(token);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String userEmail = extractUserName(token);
        if (userEmail != null && !userEmail.isBlank() && isTokenNotExpired(token)) {
            userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            return isTokenValid(token, userDetails);
        }
        return false;
    }
}