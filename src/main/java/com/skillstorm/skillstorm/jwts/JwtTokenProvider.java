package com.skillstorm.skillstorm.jwts;

import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.utils.RoleHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpiration;

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",user.getUserId());
        claims.put("email",user.getEmail());
        claims.put("roles", RoleHelper.convertFromStringToSet(user.getRoles()));

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractEmail(String token){
        final Claims claims = extractAllClaims(token);
        return claims.get("email",String.class);
    }
    public int extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId",Integer.class);
    }
    public Role extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("roles", Role.class);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) throws ExpiredJwtException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

}
