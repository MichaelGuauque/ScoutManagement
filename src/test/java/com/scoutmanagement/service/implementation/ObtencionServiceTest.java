package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ObtencionRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ObtencionServiceTest {

    @Mock
    private ObtencionRepository obtencionRepository;

    @InjectMocks
    private ObtencionService obtencionService;

    @Test
    void testFindById() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(1L, true, LocalDate.now(), persona, etapa, "imagen");

        when(obtencionRepository.findById(1L)).thenReturn(Optional.of(obtencion));

        Optional<Obtencion> result = obtencionService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(true, result.get().isEstado());
    }

    @Test
    void testSave() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(null, true, LocalDate.now(), persona, etapa, "imagen");

        obtencionService.save(obtencion);

        verify(obtencionRepository).save(obtencion);
    }

    @Test
    void testUpdate() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(2L, false, LocalDate.of(2023, 5, 20), persona, etapa,  "imagen");

        obtencionService.update(obtencion);

        verify(obtencionRepository).save(obtencion);
    }

    @Test
    void testFindAllByPersona() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        List<Obtencion> lista = Arrays.asList(
                new Obtencion(1L, true, LocalDate.now(), persona, etapa,  "imagen"),
                new Obtencion(2L, false, LocalDate.now().minusDays(1), persona, etapa, "imagen")
        );

        when(obtencionRepository.findAllByPersona(persona)).thenReturn(lista);

        List<Obtencion> result = obtencionService.findAllByPersona(persona);

        assertEquals(2, result.size());
    }

    @Test
    void testFindAll() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        List<Obtencion> lista = Arrays.asList(
                new Obtencion(1L, true, LocalDate.now(), persona, etapa,  "imagen"),
                new Obtencion(2L, false, LocalDate.now().minusDays(1), persona, etapa, "imagen")
        );

        when(obtencionRepository.findAll()).thenReturn(lista);

        List<Obtencion> result = obtencionService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByIdThrowsException() {
        when(obtencionRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.findById(1L);
        });

        assertTrue(exception.getMessage().contains("Obtención no encontrada"));
    }

    @Test
    void testSaveThrowsException() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(null, true, LocalDate.now(), persona, etapa,  "imagen");

        Mockito.doThrow(new RuntimeException("DB error")).when(obtencionRepository).save(obtencion);

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.save(obtencion);
        });

        assertTrue(exception.getMessage().contains("No se pudo guardar"));
    }

    @Test
    void testUpdateThrowsException() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(2L, false, LocalDate.now(), persona, etapa,  "imagen");

        Mockito.doThrow(new RuntimeException("DB error")).when(obtencionRepository).save(obtencion);

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.update(obtencion);
        });

        assertTrue(exception.getMessage().contains("No se pudo actualizar"));
    }

    @Test
    void testFindAllByPersonaThrowsException() {
        Persona persona = new Persona();

        when(obtencionRepository.findAllByPersona(persona)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.findAllByPersona(persona);
        });

        assertTrue(exception.getMessage().contains("No se encontraron los datos de la persona"));
    }

    @Test
    void testFindAllThrowsException() {
        when(obtencionRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.findAll();
        });

        assertTrue(exception.getMessage().contains("No se encontraron los datos"));
    }

    @Test
    void testFindIdEtapasObtenidasByPersona() {
        Persona persona = new Persona();

        Etapa etapa1 = new Etapa();
        etapa1.setId(1L);
        Etapa etapa2 = new Etapa();
        etapa2.setId(2L);

        Obtencion obt1 = new Obtencion();
        obt1.setEtapa(etapa1);
        Obtencion obt2 = new Obtencion();
        obt2.setEtapa(etapa2);

        List<Obtencion> obtenciones = Arrays.asList(obt1, obt2);

        when(obtencionRepository.findAllByPersona(persona)).thenReturn(obtenciones);

        Set<Long> resultado = obtencionService.findIdEtapasObtenidasByPersona(persona);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(1L));
        assertTrue(resultado.contains(2L));
    }

    @Test
    void testFindIdEtapasObtenidasByPersonaThrowsException() {
        Persona persona = new Persona();

        when(obtencionRepository.findAllByPersona(persona))
                .thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            obtencionService.findIdEtapasObtenidasByPersona(persona);
        });

        assertTrue(exception.getMessage().contains("Error al obtener las etapas obtenidas"));
    }

    @Test
    void testFindByPersona_ReturnsObtencion() {
        // Arrange
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setPrimerNombre("Juan");
        persona.setPrimerApellido("Pérez");
        persona.setNumeroDeDocumento(12345678901L);
        persona.setRama(Rama.COMUNIDAD);
        // puedes añadir más campos si tu entidad los necesita para no lanzar excepciones

        Etapa etapa = new Etapa();
        etapa.setId(1L);
        etapa.setNombre("Peregrino");

        Obtencion obtencion = new Obtencion();
        obtencion.setId(1L);
        obtencion.setEstado(true);
        obtencion.setFecha(LocalDate.now());
        obtencion.setPersona(persona);
        obtencion.setEtapa(etapa);

        when(obtencionRepository.findByPersona(persona)).thenReturn(Optional.of(obtencion));

        // Act
        Optional<Obtencion> result = obtencionService.findByPersona(persona);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(obtencion, result.get());
        verify(obtencionRepository).findByPersona(persona);
    }

    @Test
    void testFindByPersona_ReturnsEmpty() {
        // Arrange
        Persona persona = new Persona();
        persona.setId(2L);
        persona.setPrimerNombre("Ana");
        persona.setPrimerApellido("López");
        persona.setNumeroDeDocumento(98765432100L);

        when(obtencionRepository.findByPersona(persona)).thenReturn(Optional.empty());

        // Act
        Optional<Obtencion> result = obtencionService.findByPersona(persona);

        // Assert
        assertTrue(result.isEmpty());
        verify(obtencionRepository).findByPersona(persona);
    }

    @Test
    void testUltimasObtenciones_filtraPorRama() {

        Persona persona = new Persona();
        persona.setRama(Rama.CLAN);

        Etapa etapaConMismaRama = new Etapa();
        etapaConMismaRama.setRama(Rama.CLAN);

        Obtencion obtencion1 = new Obtencion();
        obtencion1.setEtapa(etapaConMismaRama);


        List<Obtencion> todasLasObtenciones = List.of(obtencion1);

        when(obtencionRepository.findAllByPersona(persona)).thenReturn(todasLasObtenciones);

        // Act
        List<Obtencion> resultado = obtencionService.ultimasObtenciones(persona);

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(obtencion1));

        verify(obtencionRepository).findAllByPersona(persona);
    }

}
