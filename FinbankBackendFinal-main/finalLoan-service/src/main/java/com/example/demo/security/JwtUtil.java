package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    // 🔥 MUST BE SAME SECRET AS CUSTOMER-SERVICE
    private final String SECRET = "myfinmyfinmyfinmyfinmyfinmyfin12";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String extractUsername(String token) {
        // In this system, 'sub' is the accountNo (Long as String)
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractAccountNo(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("DEBUG: JWT Claims: " + claims);
        String sub = claims.getSubject();
        if (sub != null) {
            try {
                return Long.parseLong(sub);
            } catch (NumberFormatException e) {
                System.out.println("DEBUG: Failed to parse sub as Long: " + sub);
            }
        }

        // Fallbacks just in case
        Object accountNo = claims.get("accountNo");
        if (accountNo == null) {
            accountNo = claims.get("accountNumber");
        }

        if (accountNo instanceof Number) {
            return ((Number) accountNo).longValue();
        }
        if (accountNo instanceof String) {
            try {
                return Long.parseLong((String) accountNo);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

// package com.example.demo.security;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import org.springframework.stereotype.Component;
//
// import java.util.Date;
// import java.util.List;
//
// @Component
// public class JwtUtil {
//
// // 256-bit key (your secret)
// private final String SECRET = "myfinmyfinmyfinmyfinmyfinmyfin12";
//
// // Extract username (subject)
// public String extractUsername(String token) {
// return getClaims(token).getSubject();
// }
//
// // Validate token (expiration + signature)
// public boolean validateToken(String token) {
// try {
// getClaims(token);
// return true;
// } catch (Exception e) {
// return false;
// }
// }
//
// // Extract roles claim
// public List<String> extractRoles(String token) {
// return getClaims(token).get("roles", List.class);
// }
//
// // Parse JWT
// private Claims getClaims(String token) {
// return Jwts.parser()
// .setSigningKey(SECRET.getBytes())
// .parseClaimsJws(token)
// .getBody();
// }
//
// // Generate token
// public String generateToken(String username, List<String> roles) {
// return Jwts.builder()
// .setSubject(username)
// .claim("roles", roles)
// .setIssuedAt(new Date())
// .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //
// 10h
// .signWith(SignatureAlgorithm.HS256, SECRET.getBytes())
// .compact();
// }
// }