package fr.games.square.service;

import fr.games.square.security.RsaKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final RsaKeyProvider rsaKeyProvider;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public JwtService(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    public String generateToken(String username, String roles, UUID userId) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("userId", userId != null ? userId.toString() : null)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiration))
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(rsaKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
