package com.example.cmpact4_t4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmpact4_t4.dto.AuthDtos.AuthResponse;
import com.example.cmpact4_t4.dto.AuthDtos.LoginRequest;
import com.example.cmpact4_t4.dto.AuthDtos.RegisterRequest;
import com.example.cmpact4_t4.dto.AuthDtos.UsuarioResponse;
import com.example.cmpact4_t4.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Público. 201 Created con los datos del usuario (sin password)
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(datos));
    }

    // Público. 200 OK con el token JWT; 401 si el correo o la contraseña no coinciden
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest datos) {
        return ResponseEntity.ok(authService.login(datos));
    }

    // Protegido. Regresa el usuario dueño del token
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(authService.perfil(jwt.getSubject()));
    }
}
