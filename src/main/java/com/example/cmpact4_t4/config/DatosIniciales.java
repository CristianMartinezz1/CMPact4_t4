package com.example.cmpact4_t4.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.cmpact4_t4.dto.AlumnoDtos.AlumnoRequest;
import com.example.cmpact4_t4.entity.Carrera;
import com.example.cmpact4_t4.repository.AlumnoRepository;
import com.example.cmpact4_t4.repository.CarreraRepository;
import com.example.cmpact4_t4.service.AlumnoService;

// Si la base de datos está vacía, carga carreras y algunos alumnos de ejemplo
@Component
public class DatosIniciales implements CommandLineRunner {

    private final CarreraRepository carreraRepository;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;

    public DatosIniciales(CarreraRepository carreraRepository, AlumnoRepository alumnoRepository,
                          AlumnoService alumnoService) {
        this.carreraRepository = carreraRepository;
        this.alumnoRepository = alumnoRepository;
        this.alumnoService = alumnoService;
    }

    @Override
    public void run(String... args) {
        if (carreraRepository.count() == 0) {
            carreraRepository.save(new Carrera("ISC", "Ingeniería en Sistemas Computacionales"));
            carreraRepository.save(new Carrera("II", "Ingeniería Industrial"));
            carreraRepository.save(new Carrera("IGE", "Ingeniería en Gestión Empresarial"));
            carreraRepository.save(new Carrera("IE", "Ingeniería Electrónica"));
        }
        if (alumnoRepository.count() == 0) {
            Long isc = id("ISC");
            alumnoService.crear(new AlumnoRequest("23161016", "Cristian Martínez Pacheco", "cristian@ejemplo.com", 7, isc));
            alumnoService.crear(new AlumnoRequest("23161020", "Ana López Ruiz", "ana@ejemplo.com", 7, isc));
            alumnoService.crear(new AlumnoRequest("23162031", "Luis Hernández Soto", "luis@ejemplo.com", 5, id("II")));
            alumnoService.crear(new AlumnoRequest("23163045", "María García Torres", "maria@ejemplo.com", 3, id("IGE")));
            alumnoService.crear(new AlumnoRequest("23164050", "Jorge Ramírez Díaz", "jorge@ejemplo.com", 2, id("IE")));
            alumnoService.crear(new AlumnoRequest("23161033", "Sofía Torres Méndez", "sofia@ejemplo.com", 1, isc));
        }
    }

    private Long id(String clave) {
        return carreraRepository.findByClave(clave).orElseThrow().getId();
    }
}
