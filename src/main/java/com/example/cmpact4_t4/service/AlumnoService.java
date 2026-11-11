package com.example.cmpact4_t4.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmpact4_t4.dto.AlumnoDtos.AlumnoRequest;
import com.example.cmpact4_t4.dto.AlumnoDtos.AlumnoResponse;
import com.example.cmpact4_t4.dto.AlumnoDtos.CarreraResponse;
import com.example.cmpact4_t4.dto.AlumnoDtos.PaginaResponse;
import com.example.cmpact4_t4.entity.Alumno;
import com.example.cmpact4_t4.entity.Carrera;
import com.example.cmpact4_t4.exception.ConflictoException;
import com.example.cmpact4_t4.exception.RecursoNoEncontradoException;
import com.example.cmpact4_t4.repository.AlumnoRepository;
import com.example.cmpact4_t4.repository.CarreraRepository;

// Lógica de negocio del CRUD. Recibe y regresa DTOs; las entidades no salen de aquí.
@Service
@Transactional
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final CarreraRepository carreraRepository;

    public AlumnoService(AlumnoRepository alumnoRepository, CarreraRepository carreraRepository) {
        this.alumnoRepository = alumnoRepository;
        this.carreraRepository = carreraRepository;
    }

    @Transactional(readOnly = true)
    public PaginaResponse<AlumnoResponse> listar(Long carreraId, Pageable pageable) {
        var pagina = carreraId == null
                ? alumnoRepository.findAll(pageable)
                : alumnoRepository.findByCarreraId(carreraId, pageable);
        return PaginaResponse.de(pagina.map(this::aRespuesta));
    }

    @Transactional(readOnly = true)
    public AlumnoResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public AlumnoResponse crear(AlumnoRequest datos) {
        if (alumnoRepository.existsByMatricula(datos.matricula())) {
            throw new ConflictoException("Ya existe un alumno con la matrícula " + datos.matricula());
        }
        Alumno alumno = new Alumno();
        copiar(datos, alumno);
        return aRespuesta(alumnoRepository.save(alumno));
    }

    public AlumnoResponse actualizar(Long id, AlumnoRequest datos) {
        Alumno alumno = buscar(id);
        if (alumnoRepository.existsByMatriculaAndIdNot(datos.matricula(), id)) {
            throw new ConflictoException("Ya existe otro alumno con la matrícula " + datos.matricula());
        }
        copiar(datos, alumno);
        return aRespuesta(alumnoRepository.save(alumno));
    }

    public void eliminar(Long id) {
        alumnoRepository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<CarreraResponse> carreras() {
        return carreraRepository.findAll(Sort.by("nombre")).stream().map(this::aRespuesta).toList();
    }

    private Alumno buscar(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el alumno con id " + id));
    }

    private void copiar(AlumnoRequest datos, Alumno alumno) {
        Carrera carrera = carreraRepository.findById(datos.carreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la carrera con id " + datos.carreraId()));
        alumno.setMatricula(datos.matricula());
        alumno.setNombre(datos.nombre().trim());
        alumno.setEmail(datos.email() == null || datos.email().isBlank() ? null : datos.email().trim());
        alumno.setSemestre(datos.semestre());
        alumno.setCarrera(carrera);
    }

    private AlumnoResponse aRespuesta(Alumno a) {
        return new AlumnoResponse(a.getId(), a.getMatricula(), a.getNombre(), a.getEmail(), a.getSemestre(),
                aRespuesta(a.getCarrera()));
    }

    private CarreraResponse aRespuesta(Carrera c) {
        return new CarreraResponse(c.getId(), c.getClave(), c.getNombre());
    }
}
