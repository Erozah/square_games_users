package fr.games.square.controller;

import fr.games.square.security.RsaKeyProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "JWKS", description = "JSON Web Key Set (RFC 7517)")
@RestController
public class JwksController {

    private final RsaKeyProvider rsaKeyProvider;

    public JwksController(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    @Operation(summary = "JWKS RFC 7517 pour fédération OpenID / OAuth2 / Microservices")
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return rsaKeyProvider.getJwks();
    }
}
