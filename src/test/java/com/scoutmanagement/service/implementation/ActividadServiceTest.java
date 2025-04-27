package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ActividadServiceTest {

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private ActividadRepository actividadRepository;

    @BeforeEach
    void setUp() {
        actividadRepository.deleteAll();
    }

    @Test
    void testCrearEliminarActividad() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Invierno",
                "Un campamento para todos los miembros",
                Rama.MANADA,
                LocalDate.now().plusDays(7),
                "Cabaña en la montaña"
        );

        actividadService.crearActividad(actividadDTO);

        List<Actividad> actividadesGuardadas = actividadService.findAllActividad();
        List<Actividad> ordenadas = actividadService.findAllActividadesOrdenadas();

        assertFalse(actividadesGuardadas.isEmpty(), "No se encontraron actividades guardadas.");

        Actividad actividadGuardada = ordenadas.get(0);

        assertEquals("Campamento de Invierno", actividadGuardada.getNombre(), "El nombre no coincide.");
        assertEquals("Un campamento para todos los miembros", actividadGuardada.getDescripcion(), "La descripción no coincide.");
        assertEquals("Cabaña en la montaña", actividadGuardada.getUbicacion(), "La ubicación no coincide.");
        assertEquals(Rama.MANADA, actividadGuardada.getRama(), "La rama no coincide.");

        actividadService.eliminarActividad(actividadGuardada.getId());
        Optional<Actividad> actividadEliminada = actividadService.findById(actividadGuardada.getId());
        assertFalse(actividadEliminada.isPresent(), "La actividad no fue eliminada correctamente.");

    }

    @Test
    void testCrearActividadFallo() {

        ActividadDTO actividadDTO = new ActividadDTO(null,null,null,null,null);

        assertThrows(ServiceException.class, () -> {
            actividadService.crearActividad(actividadDTO);
        });
    }

    @Test
    void testModificarActividadFallo() {
        Actividad actividad = new Actividad();
        actividad.setId(123L);

        assertThrows(IllegalArgumentException.class, () -> {
            actividadService.modificarActividad(actividad);
        });
    }

    @Test
    void buscarActividadPorNombre() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Verano",
                "Un campamento para tropa",
                Rama.TROPA,
                LocalDate.now().plusDays(7),
                "Cabaña en la laguna"
        );

        actividadService.crearActividad(actividadDTO);

        Optional<Actividad> actividadEncontrada = actividadService.findByNombre("Campamento de Verano");

        assertFalse(actividadEncontrada.isEmpty(), "No se encontraron actividades con ese nombre.");
        assertEquals("Campamento de Verano", actividadEncontrada.get().getNombre(), "El nombre de la actividad encontrada no es correcto.");
        assertEquals(Rama.TROPA, actividadEncontrada.get().getRama(), "La rama de la actividad encontrada no es correcta.");

        List<Actividad> actividadesPorRama = actividadService.findAllByRama(Rama.TROPA);

        assertFalse(actividadesPorRama.isEmpty(), "No se encontraron actividades por rama.");
        assertTrue(actividadesPorRama.stream().anyMatch(a -> a.getNombre().equals("Campamento de Verano")),
                "No se encontró la actividad en la lista por rama.");

    }

    @Test
    void testModificarActividad() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Otoño",
                "Un campamento para todos los miembros",
                Rama.CLAN,
                LocalDate.now().plusDays(7),
                "Cabaña en la montaña"
        );

        actividadService.crearActividad(actividadDTO);

        Optional<Actividad> actividadCreada = actividadService.findByNombre("Campamento de Otoño");
        assertTrue(actividadCreada.isPresent(), "La actividad no fue creada correctamente.");

        Actividad actividad = actividadCreada.get();
        actividad.setNombre("Campamento de Verano");
        actividad.setDescripcion("Un campamento para jóvenes");
        actividad.setUbicacion("Playa");
        actividad.setRama(Rama.COMUNIDAD);

        Actividad actividadModificada = actividadService.modificarActividad(actividad);

        assertNotNull(actividadModificada, "La actividad modificada no debe ser nula.");
        assertEquals("Campamento de Verano", actividadModificada.getNombre(), "El nombre no se modificó correctamente.");
        assertEquals("Un campamento para jóvenes", actividadModificada.getDescripcion(), "La descripción no se modificó correctamente.");
        assertEquals("Playa", actividadModificada.getUbicacion(), "La ubicación no se modificó correctamente.");
        assertEquals(Rama.COMUNIDAD, actividadModificada.getRama(), "La rama no se modificó correctamente.");
    }
}