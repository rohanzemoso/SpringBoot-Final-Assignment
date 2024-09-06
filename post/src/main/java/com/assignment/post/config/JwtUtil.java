package com.assignment.post.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import static com.assignment.post.util.Constants.JWT_PARSER_NOT_INITIALIZED;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public boolean validateToken(String token) {
        try {
            if (jwtParser == null) {
                throw new IllegalStateException(JWT_PARSER_NOT_INITIALIZED);
            }
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        try {
            if (jwtParser == null) {
                throw new IllegalStateException("JWT parser not initialized");
            }
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error retrieving username from token: {}", e.getMessage());
        }
        return null;
    }

    public Integer getUserIdFromToken(String token) {
        try {
            if (jwtParser == null) {
                throw new IllegalStateException("JWT parser not initialized");
            }
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return (Integer) claims.get("userID");
        } catch (Exception e) {
            log.error("Error retrieving userId from token: {}", e.getMessage());
        }
        return null;
    }
}
