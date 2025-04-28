package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.repository.ObtencionRepository;
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
        Obtencion obtencion = new Obtencion(1L, true, LocalDate.now(), persona, etapa);

        Mockito.when(obtencionRepository.findById(1L)).thenReturn(Optional.of(obtencion));

        Optional<Obtencion> result = obtencionService.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(true, result.get().isEstado());
    }

    @Test
    void testSave() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(null, true, LocalDate.now(), persona, etapa);

        obtencionService.save(obtencion);

        Mockito.verify(obtencionRepository).save(obtencion);
    }

    @Test
    void testUpdate() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        Obtencion obtencion = new Obtencion(2L, false, LocalDate.of(2023, 5, 20), persona, etapa);

        obtencionService.update(obtencion);

        Mockito.verify(obtencionRepository).save(obtencion);
    }

    @Test
    void testFindAllByPersona() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        List<Obtencion> lista = Arrays.asList(
                new Obtencion(1L, true, LocalDate.now(), persona, etapa),
                new Obtencion(2L, false, LocalDate.now().minusDays(1), persona, etapa)
        );

        Mockito.when(obtencionRepository.findAllByPersona(persona)).thenReturn(lista);

        List<Obtencion> result = obtencionService.findAllByPersona(persona);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testFindAll() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        List<Obtencion> lista = Arrays.asList(
                new Obtencion(1L, true, LocalDate.now(), persona, etapa),
                new Obtencion(2L, false, LocalDate.now().minusDays(1), persona, etapa)
        );

        Mockito.when(obtencionRepository.findAll()).thenReturn(lista);

        List<Obtencion> result = obtencionService.findAll();

        Assertions.assertEquals(2, result.size());
    }
}
