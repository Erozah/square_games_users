package fr.games.square.controller;

import fr.games.square.dao.UserDao;
import fr.games.square.dto.LoginRequestDto;
import fr.games.square.entity.UserEntity;
import fr.games.square.security.RsaKeyProvider;
import fr.games.square.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentification", description = "Endpoints d'authentification et clés publiques")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDao userDao;
    private final RsaKeyProvider rsaKeyProvider;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDao userDao,
            RsaKeyProvider rsaKeyProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDao = userDao;
        this.rsaKeyProvider = rsaKeyProvider;
    }

    @Operation(summary = "Connexion utilisateur et obtention du token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UserDetails user = (UserDetails) authentication.getPrincipal();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur introuvable"));
            }
            String roles = user.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            UserEntity userEntity = userDao.findByUsername(user.getUsername()).orElseThrow();
            String token = jwtService.generateToken(user.getUsername(), roles, userEntity.getId());
            return ResponseEntity.ok(Map.of("token", token, "Token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Identifiants invalides"));
        }
    }

    @Operation(summary = "Exposition de la clé publique RSA pour vérification décentralisée des tokens")
    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", rsaKeyProvider.getPublicKeyBase64());
    }
}
