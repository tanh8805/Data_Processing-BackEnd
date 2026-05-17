package com.example.data_processing_be.config.jwt;

import com.example.data_processing_be.config.security.CustomerDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
  @Value("${jwt.secret}")
  private String JWT_SECRET;

  @Value("${jwt.expiration}")
  private long JWT_EXPIRATION;

  public String generateToken(Authentication authentication) {
    CustomerDetails userDetails = (CustomerDetails) authentication.getPrincipal();
    String role = authentication.getAuthorities().iterator().next().getAuthority();
    return Jwts.builder()
            .claim("role", role)
            .setSubject(userDetails.getUser().getEmail())
            .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()), SignatureAlgorithm.HS256)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .compact();
  }

  public String extractEmail(String token){
    return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }
  public Date extractExpiration(String token) {
    return extractAllClaims(token).getExpiration();
  }
  public boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (Exception e) {
      return true;
    }
  }
}
