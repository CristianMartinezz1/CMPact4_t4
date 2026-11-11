package com.example.cmpact4_t4.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.cmpact4_t4.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

// Convierte las excepciones en respuestas JSON con el código HTTP correcto
@RestControllerAdvice
public class ManejadorErrores {

    // 400: falló la validación de @Valid (campo por campo)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errores.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(Instant.now(), 400, "Bad Request",
                "Los datos enviados no son válidos", req.getRequestURI(), errores));
    }

    // 400: JSON mal escrito o con tipos incorrectos
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> jsonInvalido(Exception e, HttpServletRequest req) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo o los parámetros de la petición no tienen el formato correcto", req);
    }

    // 401: login con correo o contraseña incorrectos
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credenciales(AuthenticationException e, HttpServletRequest req) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos", req);
    }

    // 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e, HttpServletRequest req) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaNoExiste(NoResourceFoundException e, HttpServletRequest req) {
        return respuesta(HttpStatus.NOT_FOUND, "La ruta no existe", req);
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodo(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "Método " + e.getMethod() + " no permitido en esta ruta", req);
    }

    // 409: correo o matrícula repetidos
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException e, HttpServletRequest req) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage(), req);
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.de(status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI()));
    }
}
