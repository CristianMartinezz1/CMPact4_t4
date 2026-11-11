package com.example.cmpact4_t4.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmpact4_t4.entity.Alumno;

// JpaRepository ya incluye findAll(Pageable) para la paginación
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Page<Alumno> findByCarreraId(Long carreraId, Pageable pageable);

    boolean existsByMatricula(String matricula);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
