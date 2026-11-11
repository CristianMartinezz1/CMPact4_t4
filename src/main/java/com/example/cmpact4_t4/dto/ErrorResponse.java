package com.example.cmpact4_t4.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

// Formato único para todos los errores de la API
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(Instant timestamp, int status, String error, String mensaje, String ruta,
                            Map<String, String> errores) {

    public static ErrorResponse de(int status, String error, String mensaje, String ruta) {
        return new ErrorResponse(Instant.now(), status, error, mensaje, ruta, null);
    }
}
