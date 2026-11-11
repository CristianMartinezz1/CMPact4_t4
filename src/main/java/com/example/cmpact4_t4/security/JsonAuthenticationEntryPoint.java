package com.example.cmpact4_t4.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Respuesta 401 en JSON cuando falta el token o no es válido
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String mensaje;
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            mensaje = "Falta el token. Envía el header Authorization: Bearer <token>";
        } else if (authException instanceof InvalidBearerTokenException) {
            mensaje = "Token inválido o vencido. Inicia sesión otra vez en /api/auth/login";
        } else {
            mensaje = "No autenticado";
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("WWW-Authenticate", "Bearer");
        response.getWriter().write("{\"timestamp\":\"" + Instant.now() + "\",\"status\":401,"
                + "\"error\":\"Unauthorized\",\"mensaje\":\"" + mensaje + "\","
                + "\"ruta\":\"" + escapar(request.getRequestURI()) + "\"}");
    }

    private String escapar(String texto) {
        return texto == null ? "" : texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
