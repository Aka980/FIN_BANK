/*
 * package com.example.demo.Security;
 * 
 * import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import
 * org.springframework.stereotype.Component;
 * 
 * import java.security.Key;
 * 
 * @Component public class JwtUtil {
 * 
 * private static final String SECRET = "myfinmyfinmyfinmyfinmyfinmyfin12";
 * 
 * private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
 * 
 * public boolean validateToken(String token) { try { Jwts.parserBuilder()
 * .setSigningKey(key) .build() .parseClaimsJws(token); return true; } catch
 * (Exception e) { return false; }
 * 
 * public String extractUsername(String token) { return
 * extractClaims(token).getSubject(); }
 * 
 * public String extractRole(String token) { return
 * extractClaims(token).get("role", String.class); } } }
 */

package com.example.demo.Security;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private static final String SECRET =
            "myfinmyfinmyfinmyfinmyfinmyfin12";

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Validate Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Extract All Claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Extract Username (subject)
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ Extract Role
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}