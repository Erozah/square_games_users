package fr.games.square.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RsaKeyProvider {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyProvider.class);

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public RsaKeyProvider(
            @Value("${jwt.rsa.private-key:}") String privateKeyStr,
            @Value("${jwt.rsa.public-key:}") String publicKeyStr
    ) {
        RSAPrivateKey priv = null;
        RSAPublicKey pub = null;

        if (privateKeyStr != null && !privateKeyStr.isBlank() && publicKeyStr != null && !publicKeyStr.isBlank()) {
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                byte[] privBytes = Base64.getDecoder().decode(cleanKey(privateKeyStr));
                priv = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));

                byte[] pubBytes = Base64.getDecoder().decode(cleanKey(publicKeyStr));
                pub = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(pubBytes));
                log.info("Initialized RSA keypair from configuration");
            } catch (Exception e) {
                log.warn("Failed to parse configured RSA keys, generating ephemeral keypair: {}", e.getMessage());
            }
        }

        if (priv == null || pub == null) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair kp = kpg.generateKeyPair();
                priv = (RSAPrivateKey) kp.getPrivate();
                pub = (RSAPublicKey) kp.getPublic();
                log.info("Generated new ephemeral 2048-bit RSA keypair");
            } catch (Exception e) {
                throw new IllegalStateException("Failed to generate RSA keypair", e);
            }
        }

        this.privateKey = priv;
        this.publicKey = pub;
    }

    private static String cleanKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public Map<String, Object> getJwks() {
        BigInteger modulus = publicKey.getModulus();
        BigInteger exponent = publicKey.getPublicExponent();

        Map<String, Object> key = new HashMap<>();
        key.put("kty", "RSA");
        key.put("use", "sig");
        key.put("alg", "RS256");
        key.put("kid", "square-key-1");
        key.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(modulus.toByteArray()));
        key.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(exponent.toByteArray()));

        return Collections.singletonMap("keys", List.of(key));
    }
}
