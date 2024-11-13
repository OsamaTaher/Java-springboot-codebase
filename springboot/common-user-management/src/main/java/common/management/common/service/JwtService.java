package common.management.common.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtService {
    String extractUserId(String token);

    String generateToken(Authentication authentication);

    boolean isTokenValid(String token);

    String generateToken(String userId, List<String> roles);

    Claims extractAllClaims(String token);
}
