package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.ActividadDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.persistence.repository.AsistenciaRepository;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AsistenciaServiceTest {
//
//    private static final Logger logger = LoggerFactory.getLogger(AsistenciaServiceTest.class);
//
//    @Autowired
//    private ActividadService actividadService;
//
//    @Autowired
//    private ActividadRepository actividadRepository;
//
//    @Autowired
//    private PersonaRepository personaRepository;
//
//    @Autowired
//    private AsistenciaRepository asistenciaRepository;
//
//    @Autowired
//    private IAsistenciaService asistenciaService;
//
//    @BeforeEach
//    void setUp() {
//        asistenciaRepository.deleteAll();
//        actividadRepository.deleteAll();
//        // No eliminamos personas para mantener datos de prueba
//    }
//
//    @Test
//    @Rollback(false)  // Esto deshabilita el rollback automático
//    void testCrearActividad() {
//        ActividadDTO actividadDTO = new ActividadDTO(
//                "Campamento de Invierno",
//                "Un campamento para todos los miembros",
//                Rama.MANADA,
//                LocalDate.now().plusDays(7),
//                "Cabaña en la montaña"
//        );
//
//        actividadService.crearActividad(actividadDTO);
//
//        List<Actividad> actividadesGuardadas = actividadService.findAllActividad();
//
//        if (!actividadesGuardadas.isEmpty()) {
//            String nombreActividad = actividadesGuardadas.get(0).getNombre();
//            logger.info("Actividad guardada: {}", nombreActividad);
//        } else {
//            logger.info("No se encontró ninguna actividad guardada.");
//        }
//
//        assertEquals("Campamento de Invierno", actividadDTO.nombre(), "El nombre no coincide.");
//        assertEquals("Un campamento para todos los miembros", actividadDTO.descripcion(), "La descripción no coincide.");
//        assertEquals("Cabaña en la montaña", actividadDTO.ubicacion(), "La ubicación no coincide.");
//        assertEquals(Rama.MANADA, actividadDTO.rama(), "La rama no coincide.");
//    }
//
//    @Test
//    @Transactional
//    @Rollback(false)
//    void testCrearActividadConAsistenciasAutomaticas() {
//        // Preparar datos de prueba: crear personas de MANADA
//        crearPersonasDePrueba();
//
//        // Verificar que se hayan creado personas para la prueba
//        List<Persona> personasManada = personaRepository.findByRama(Rama.MANADA);
//        assertFalse(personasManada.isEmpty(), "Debe haber personas en la MANADA para la prueba");
//        logger.info("Personas en MANADA encontradas: {}", personasManada.size());
//
//        // Crear actividad para MANADA
//        ActividadDTO actividadDTO = new ActividadDTO(
//                "Reunión MANADA",
//                "Reunión semanal de la MANADA",
//                Rama.MANADA,
//                LocalDate.now().plusDays(3),
//                "Sede del grupo"
//        );
//
//        // Ejecutar método a probar
//        actividadService.crearActividad(actividadDTO);
//
//        // Verificar que se haya creado la actividad
//        List<Actividad> actividades = actividadService.findAllByRama(Rama.MANADA);
//        assertFalse(actividades.isEmpty(), "Debe haberse creado al menos una actividad");
//
//        Actividad actividadCreada = actividades.stream()
//                .filter(a -> a.getNombre().equals("Reunión MANADA"))
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(actividadCreada, "La actividad debería haberse creado correctamente");
//
//        // Verificar que se hayan creado las asistencias para cada miembro de la MANADA
//        List<Asistencia> asistencias = asistenciaService.findByActividad(actividadCreada.getId());
//
//        assertEquals(personasManada.size(), asistencias.size(),
//                "Debe crearse una asistencia por cada persona de la MANADA");
//
//        // Verificar que todas las asistencias estén inicialmente como false
//        boolean todasAsistenciasFalse = asistencias.stream()
//                .allMatch(a -> !a.isAsistio());
//
//        assertTrue(todasAsistenciasFalse, "Todas las asistencias deben estar inicialmente como false");
//
//        // Simular actualización de asistencias (como se haría desde la vista)
//        Map<Long, Boolean> actualizaciones = asistencias.stream()
//                .limit(2) // Actualizamos solo 2 asistencias para la prueba
//                .collect(Collectors.toMap(
//                        a -> a.getMiembro().getId(),
//                        a -> true // Marcamos como asistieron
//                ));
//
//        asistenciaService.registrarAsistenciasMasivas(actividadCreada.getId(), actualizaciones);
//
//        // Verificar que se actualizaron correctamente
//        List<Asistencia> asistenciasActualizadas = asistenciaService.findByActividad(actividadCreada.getId());
//
//        long cantidadAsistio = asistenciasActualizadas.stream()
//                .filter(Asistencia::isAsistio)
//                .count();
//
//        assertEquals(2, cantidadAsistio, "Deberían haberse actualizado 2 asistencias a true");
//
//        logger.info("Test completado con éxito. Se verificó la creación automática de asistencias.");
//    }
//
//    private void crearPersonasDePrueba() {
//        // Solo crear personas si no existen ya en la base de datos
//        List<Persona> personasExistentes = (List<Persona>) personaRepository.findAll();
//        if (!personasExistentes.isEmpty()) {
//            // Verificar si hay personas de MANADA
//            List<Persona> personasManada = personaRepository.findByRama(Rama.MANADA);
//            if (!personasManada.isEmpty()) {
//                logger.info("Ya existen {} personas en MANADA, no se crearán nuevas", personasManada.size());
//                return;
//            }
//        }
//
//        // Crear personas de prueba para MANADA
//        Persona persona1 = Persona.builder()
//                .primerNombre("Juan")
//                .segundoNombre("Pablo")
//                .rama(Rama.MANADA)
//                .cargo(Cargo.LOBATO).rol(Cargo.LOBATO.getRolAsociado())
//                .genero(Genero.MASCULINO)
//                .build();
//
//        Persona persona2 = Persona.builder()
//                .primerNombre("María")
//                .segundoNombre("José")
//                .rama(Rama.MANADA)
//                .cargo(Cargo.LOBATO)
//                .rol(Cargo.LOBATO.getRolAsociado())
//                .genero(Genero.FEMENINO)
//                .build();
//
//        Persona persona3 = Persona.builder()
//                .primerNombre("Carlos")
//                .rama(Rama.MANADA)
//                .cargo(Cargo.LOBATO)
//                .rol(Cargo.LOBATO.getRolAsociado())
//                .genero(Genero.MASCULINO)
//                .build();
//
//        // Crear persona de otra rama para verificar que no se crea asistencia para ella
//        Persona persona4 = Persona.builder()
//                .primerNombre("Ana")
//                .rama(Rama.TROPA)
//                .cargo(Cargo.SCOUT)
//                .rol(Cargo.SCOUT.getRolAsociado())
//                .genero(Genero.FEMENINO)
//                .build();
//
//        personaRepository.save(persona1);
//        personaRepository.save(persona2);
//        personaRepository.save(persona3);
//        personaRepository.save(persona4);
//
//        logger.info("Se crearon 4 personas de prueba (3 de MANADA, 1 de TROPA)");
//    }
}
