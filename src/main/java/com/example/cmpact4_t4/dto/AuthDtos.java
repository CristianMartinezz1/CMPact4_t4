package com.example.cmpact4_t4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTOs de autenticación (entrada y salida)
public final class AuthDtos {

    private AuthDtos() {
    }

    // POST /api/auth/register
    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
            String nombre,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato válido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
            String password) {
    }

    // POST /api/auth/login
    public record LoginRequest(
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato válido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            String password) {
    }

    // Respuesta del login: el token JWT y los datos públicos del usuario (sin password)
    public record AuthResponse(String token, String tipo, long expiraEnSegundos, UsuarioResponse usuario) {
    }

    // Datos públicos del usuario. No incluye el password.
    public record UsuarioResponse(Long id, String nombre, String email, String rol) {
    }
}
