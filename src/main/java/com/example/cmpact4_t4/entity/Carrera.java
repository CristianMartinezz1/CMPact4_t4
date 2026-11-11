package com.example.cmpact4_t4.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Una Carrera tiene muchos Alumnos (lado inverso de la relación)
@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String clave;

    @Column(nullable = false, length = 100)
    private String nombre;

    @OneToMany(mappedBy = "carrera")
    private List<Alumno> alumnos = new ArrayList<>();

    public Carrera() {
    }

    public Carrera(String clave, String nombre) {
        this.clave = clave;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }
}
