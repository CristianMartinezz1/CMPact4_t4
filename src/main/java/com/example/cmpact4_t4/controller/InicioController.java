package com.example.cmpact4_t4.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Ruta pública con la descripción de la API (la URL base responde JSON, no una página)
@RestController
public class InicioController {

    @GetMapping({"/", "/api"})
    public ResponseEntity<Map<String, Object>> inicio() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("api", "CMPact4_t4 - API REST de Alumnos con Spring Security y JWT");
        info.put("publicos", Map.of(
                "POST /api/auth/register", "Crear cuenta",
                "POST /api/auth/login", "Obtener token JWT"));
        info.put("protegidos (Authorization: Bearer <token>)", Map.of(
                "GET /api/auth/me", "Usuario del token",
                "GET /api/alumnos?page=0&size=5", "Listar alumnos (paginado)",
                "GET /api/alumnos/{id}", "Ver un alumno",
                "POST /api/alumnos", "Crear alumno",
                "PUT /api/alumnos/{id}", "Actualizar alumno",
                "DELETE /api/alumnos/{id}", "Eliminar alumno",
                "GET /api/carreras", "Listar carreras"));
        return ResponseEntity.ok(info);
    }
}
