
package com.example.demo.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private static final String SECRET = "myfinmyfinmyfinmyfinmyfinmyfin12";

    private Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(
            Long accountNo,
            String role) {

        return Jwts.builder()

                // Subject = accountNo
                .setSubject(String.valueOf(accountNo))

                .claim("role", role)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(System.currentTimeMillis() + 86400000))

                .signWith(key,
                        SignatureAlgorithm.HS256)

                .compact();
    }

    public String extractSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractAccountNo(String token) {
        try {
            return Long.parseLong(extractSubject(token));
        } catch (NumberFormatException e) {
            return null; // Return null if subject is not a numeric account number
        }
    }

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    public boolean validateToken(String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private Claims getClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(key)

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

}