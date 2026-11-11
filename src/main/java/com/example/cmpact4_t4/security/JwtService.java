package com.example.cmpact4_t4.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.cmpact4_t4.entity.Usuario;

// Genera los tokens JWT firmados con HS256 (HMAC + clave secreta)
@Service
public class JwtService {

    public static final String EMISOR = "cmpact4_t4";

    private final JwtEncoder jwtEncoder;
    private final long expiracionSegundos;

    public JwtService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiracion-segundos}") long expiracionSegundos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracionSegundos = expiracionSegundos;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMISOR)                                   // iss: quién emite el token
                .subject(usuario.getEmail())                      // sub: a quién pertenece
                .issuedAt(ahora)                                  // iat: cuándo se creó
                .expiresAt(ahora.plusSeconds(expiracionSegundos)) // exp: cuándo vence
                .claim("uid", usuario.getId())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpiracionSegundos() {
        return expiracionSegundos;
    }
}
