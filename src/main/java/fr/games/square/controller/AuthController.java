package fr.games.square.controller;

import fr.games.square.dao.JpaUserDao;
import fr.games.square.dao.UserDao;
import fr.games.square.dto.LoginRequestDto;
import fr.games.square.entity.UserEntity;
import fr.games.square.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JpaUserDao jpaUserDao;
    private final UserDao userDao;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, JpaUserDao jpaUserDao, UserDao userDao) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jpaUserDao = jpaUserDao;
        this.userDao = userDao;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UserDetails user = (UserDetails) authentication.getPrincipal();
            assert user != null;
            String roles = user.getAuthorities().iterator().next().getAuthority();
            UserEntity userEntity = userDao.findByUsername(user.getUsername()).orElseThrow();
            String token = jwtService.generateToken(user.getUsername(), roles, userEntity.getId());
            return ResponseEntity.ok(Map.of("Token", token));
        } catch (BadCredentialsException e) {
           return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides");
        }
    }
}
