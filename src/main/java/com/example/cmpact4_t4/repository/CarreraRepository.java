package com.example.cmpact4_t4.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmpact4_t4.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    Optional<Carrera> findByClave(String clave);
}
