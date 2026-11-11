package com.example.cmpact4_t4.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmpact4_t4.dto.AlumnoDtos.AlumnoRequest;
import com.example.cmpact4_t4.dto.AlumnoDtos.AlumnoResponse;
import com.example.cmpact4_t4.dto.AlumnoDtos.CarreraResponse;
import com.example.cmpact4_t4.dto.AlumnoDtos.PaginaResponse;
import com.example.cmpact4_t4.service.AlumnoService;

import jakarta.validation.Valid;

// CRUD protegido: todas estas rutas exigen "Authorization: Bearer <token>"
@RestController
@RequestMapping("/api")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    // READ (lista paginada): GET /api/alumnos?page=0&size=5&sort=nombre&carreraId=1
    @GetMapping("/alumnos")
    public ResponseEntity<PaginaResponse<AlumnoResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(required = false) Long carreraId) {
        int tamano = Math.min(Math.max(size, 1), 50);
        String campo = List.of("id", "matricula", "nombre", "semestre").contains(sort) ? sort : "nombre";
        var pageable = PageRequest.of(Math.max(page, 0), tamano, Sort.by(campo));
        return ResponseEntity.ok(alumnoService.listar(carreraId, pageable));
    }

    // READ (uno): GET /api/alumnos/1  -> 200 o 404
    @GetMapping("/alumnos/{id}")
    public ResponseEntity<AlumnoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.obtener(id));
    }

    // CREATE: POST /api/alumnos  -> 201 Created + header Location
    @PostMapping("/alumnos")
    public ResponseEntity<AlumnoResponse> crear(@Valid @RequestBody AlumnoRequest datos) {
        AlumnoResponse creado = alumnoService.crear(datos);
        return ResponseEntity.created(URI.create("/api/alumnos/" + creado.id())).body(creado);
    }

    // UPDATE: PUT /api/alumnos/1  -> 200
    @PutMapping("/alumnos/{id}")
    public ResponseEntity<AlumnoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoRequest datos) {
        return ResponseEntity.ok(alumnoService.actualizar(id, datos));
    }

    // DELETE: DELETE /api/alumnos/1  -> 204 No Content
    @DeleteMapping("/alumnos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Catálogo de carreras (para saber qué carreraId usar)
    @GetMapping("/carreras")
    public ResponseEntity<List<CarreraResponse>> carreras() {
        return ResponseEntity.ok(alumnoService.carreras());
    }
}
