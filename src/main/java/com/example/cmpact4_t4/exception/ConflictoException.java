package com.example.cmpact4_t4.exception;

// Se traduce a 409 Conflict (ej. correo o matrícula repetidos)
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
