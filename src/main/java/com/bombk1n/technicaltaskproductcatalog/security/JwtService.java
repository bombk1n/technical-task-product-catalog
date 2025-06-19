package com.bombk1n.technicaltaskproductcatalog.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String JWT_SECRET_KEY = "80a030a97221a8ce7aab4a880e186da46241caa40b2b6deb5b2f9768f9467f59";

    private final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;

    public String generateToken(String username) {
        return generateToken(username, JWT_EXPIRATION);
    }

    public String generateToken(String username, long expirationMs) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64URL.decode(JWT_SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid secret key", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        try {
            Claims claims = extractAllClaims(token);
            return resolver.apply(claims);
        } catch (Exception e) {
            throw e;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}