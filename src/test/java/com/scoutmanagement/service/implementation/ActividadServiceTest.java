package com.scoutmanagement.service.implementation;

import com.scoutmanagement.tempDTO.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class ActividadServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ActividadServiceTest.class);

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private ActividadRepository actividadRepository;

    @BeforeEach
    void setUp() {
        actividadRepository.deleteAll();
    }

    @Test
    void testCrearActividad() {

        ActividadDTO actividadDTO = new ActividadDTO(
                "Campamento de Invierno",
                "Un campamento para todos los miembros",
                Rama.MANADA,
                LocalDate.now().plusDays(7),
                "Cabaña en la montaña"
        );

        actividadService.crearActividad(actividadDTO);

        List<Actividad> actividadesGuardadas = actividadService.findAllActividad();

        if (!actividadesGuardadas.isEmpty()) {
            String nombreActividad = actividadesGuardadas.get(0).getNombre();
            logger.info("Actividad guardada: {}", nombreActividad);
        } else {
            logger.info("No se encontró ninguna actividad guardada.");
        }

        assertEquals("Campamento de Invierno", actividadDTO.nombre(), "El nombre no coincide.");
        assertEquals("Un campamento para todos los miembros", actividadDTO.descripcion(), "La descripción no coincide.");
        assertEquals("Cabaña en la montaña", actividadDTO.ubicacion(), "La ubicación no coincide.");
        assertEquals(Rama.MANADA, actividadDTO.rama(), "La rama no coincide.");

    }

}
