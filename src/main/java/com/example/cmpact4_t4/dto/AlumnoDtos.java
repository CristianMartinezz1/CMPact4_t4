package com.example.cmpact4_t4.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTOs del CRUD de alumnos
public final class AlumnoDtos {

    private AlumnoDtos() {
    }

    // Lo que entra en POST y PUT. La carrera se indica con su id.
    public record AlumnoRequest(
            @NotBlank(message = "La matrícula es obligatoria")
            @Pattern(regexp = "\\d{8}", message = "La matrícula debe tener 8 dígitos")
            String matricula,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
            String nombre,

            @Email(message = "El correo no tiene un formato válido")
            String email,

            @NotNull(message = "El semestre es obligatorio")
            @Min(value = 1, message = "El semestre mínimo es 1")
            @Max(value = 12, message = "El semestre máximo es 12")
            Integer semestre,

            @NotNull(message = "La carrera es obligatoria (carreraId)")
            Long carreraId) {
    }

    // Lo que sale: el alumno con los datos de su carrera
    public record AlumnoResponse(Long id, String matricula, String nombre, String email, Integer semestre,
                                 CarreraResponse carrera) {
    }

    public record CarreraResponse(Long id, String clave, String nombre) {
    }

    // Respuesta paginada (en vez de exponer el objeto Page de Spring completo)
    public record PaginaResponse<T>(List<T> contenido, int pagina, int tamano, long totalElementos,
                                    int totalPaginas, boolean primera, boolean ultima) {

        public static <T> PaginaResponse<T> de(Page<T> page) {
            return new PaginaResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
        }
    }
}
