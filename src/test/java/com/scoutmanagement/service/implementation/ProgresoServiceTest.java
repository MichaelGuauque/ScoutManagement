package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProgresoServiceTest {

    @Mock
    private ProgresoRepository progresoRepository;

    @InjectMocks
    private ProgresoService progresoService;

    @Test
    void testFindById() {
        Progreso progreso = new Progreso(1L, true, new Persona(), new Reto());
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        Optional<Progreso> result = progresoService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(progresoRepository).findById(1L);
    }

    @Test
    void testSave() {
        Progreso progreso = new Progreso(null, true, new Persona(), new Reto());

        progresoService.save(progreso);

        verify(progresoRepository).save(progreso);
    }

    @Test
    void testUpdate() {
        Progreso progreso = new Progreso(1L, false, new Persona(), new Reto());

        progresoService.update(progreso);

        verify(progresoRepository).save(progreso);
    }

    @Test
    void testFindAll() {
        List<Progreso> lista = List.of(
                new Progreso(1L, true, new Persona(), new Reto()),
                new Progreso(2L, false, new Persona(), new Reto())
        );
        when(progresoRepository.findAll()).thenReturn(lista);

        List<Progreso> result = progresoService.findAll();

        assertEquals(2, result.size());
        verify(progresoRepository).findAll();
    }

    @Test
    void testFindAllByPersona() {
        Persona persona = new Persona();
        List<Progreso> lista = List.of(new Progreso(1L, true, persona, new Reto()));
        when(progresoRepository.findAllByPersona(persona)).thenReturn(lista);

        List<Progreso> result = progresoService.findAllByPersona(persona);

        assertEquals(1, result.size());
        assertEquals(persona, result.get(0).getPersona());
        verify(progresoRepository).findAllByPersona(persona);
    }

    @Test
    void testFindByIdThrowsException() {
        when(progresoRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            progresoService.findById(1L);
        });

        assertTrue(exception.getMessage().contains("Progreso no encontrado"));
    }

    @Test
    void testSaveThrowsException() {
        Progreso progreso = new Progreso(null, true, new Persona(), new Reto());
        doThrow(new RuntimeException("DB error")).when(progresoRepository).save(progreso);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            progresoService.save(progreso);
        });

        assertTrue(exception.getMessage().contains("No se pudo guardar el progreso"));
    }

    @Test
    void testUpdateThrowsException() {
        Progreso progreso = new Progreso(1L, false, new Persona(), new Reto());
        doThrow(new RuntimeException("DB error")).when(progresoRepository).save(progreso);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            progresoService.update(progreso);
        });

        assertTrue(exception.getMessage().contains("No se pudo actualizar el progreso"));
    }

    @Test
    void testFindAllThrowsException() {
        when(progresoRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            progresoService.findAll();
        });

        assertTrue(exception.getMessage().contains("No se encontraron los datos"));
    }

    @Test
    void testFindAllByPersonaThrowsException() {
        Persona persona = new Persona();
        when(progresoRepository.findAllByPersona(persona)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            progresoService.findAllByPersona(persona);
        });

        assertTrue(exception.getMessage().contains("No se encontraron los datos de la persona"));
    }
}
