package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.persistence.repository.RetoRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class RetoServiceTest {
    @Mock
    private RetoRepository retoRepository;

    @Mock
    private EtapaRepository etapaRepository;

    @InjectMocks
    private RetoService retoService;

    @Test
    void testFindById() {
        Etapa etapa = new Etapa();
        Reto reto = new Reto(1L, 1, "Descripción del reto", etapa);

        Mockito.when(retoRepository.findById(1L)).thenReturn(Optional.of(reto));

        Optional<Reto> result = retoService.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Descripción del reto", result.get().getDescripcion());
    }

    @Test
    void testSave() {
        Etapa etapa = new Etapa();
        RetoDTO dto = new RetoDTO(1, "Etapa 1", "Descripción guardada");

        Mockito.when(etapaRepository.findByNombre("Etapa 1")).thenReturn(etapa);

        retoService.save(dto);

        Mockito.verify(retoRepository).save(Mockito.argThat(reto ->
                reto.getNumero() == 1 &&
                        reto.getDescripcion().equals("Descripción guardada") &&
                        reto.getEtapa() == etapa
        ));
    }

    @Test
    void testUpdate() {
        Etapa etapa = new Etapa();
        Reto reto = new Reto(2L, 2, "Actualizado", etapa);

        retoService.update(reto);

        Mockito.verify(retoRepository).save(reto);
    }

    @Test
    void testFindAll() {
        List<Reto> lista = Arrays.asList(
                new Reto(1L, 1, "Reto 1", new Etapa()),
                new Reto(2L, 2, "Reto 2", new Etapa())
        );

        Mockito.when(retoRepository.findAll()).thenReturn(lista);

        List<Reto> result = retoService.findAll();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testFindAllRetosEtapa() {
        Etapa etapa = new Etapa();
        List<Reto> lista = Collections.singletonList(
                new Reto(1L, 1, "Reto Etapa", etapa)
        );

        Mockito.when(retoRepository.findAllRetosByEtapaOrderByNumeroAsc(etapa)).thenReturn(lista);

        List<Reto> result = retoService.findAllRetosEtapa(etapa);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Reto Etapa", result.get(0).getDescripcion());
    }

    @Test
    void testCambiarRetoDTO() {
        Etapa etapa = new Etapa();
        RetoDTO dto = new RetoDTO(3, "EtapaDTO","Descripción DTO");

        Mockito.when(etapaRepository.findByNombre("EtapaDTO")).thenReturn(etapa);

        Reto reto = retoService.cambiarRetoDTO(dto);

        Assertions.assertNull(reto.getId());
        Assertions.assertEquals(3, reto.getNumero());
        Assertions.assertEquals("Descripción DTO", reto.getDescripcion());
        Assertions.assertEquals(etapa, reto.getEtapa());
    }

    @Test
    void testFindByIdThrowsException() {
        Mockito.when(retoRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.findById(1L);
        });

        Assertions.assertTrue(exception.getMessage().contains("Reto no encontrado"));
    }

    @Test
    void testSaveThrowsException() {
        RetoDTO dto = new RetoDTO(1, "Etapa 1", "Descripción con error");

        // Hacer que etapaRepository falle dentro de cambiarRetoDTO
        Mockito.when(etapaRepository.findByNombre("Etapa 1")).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.save(dto);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se pudo guardar el reto"));
    }

    @Test
    void testUpdateThrowsException() {
        Reto reto = new Reto(2L, 2, "Error Update", new Etapa());

        Mockito.doThrow(new RuntimeException("DB error")).when(retoRepository).save(reto);

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.update(reto);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se pudo actualizar el reto"));
    }

    @Test
    void testFindAllThrowsException() {
        Mockito.when(retoRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.findAll();
        });

        Assertions.assertTrue(exception.getMessage().contains("No se encontraron los datos"));
    }

    @Test
    void testFindAllRetosEtapaThrowsException() {
        Etapa etapa = new Etapa();

        Mockito.when(retoRepository.findAllRetosByEtapaOrderByNumeroAsc(etapa)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.findAllRetosEtapa(etapa);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se encontraron los retos por etapa"));
    }

    @Test
    void testCambiarRetoDTOThrowsException() {
        RetoDTO dto = new RetoDTO(5, "EtapaError", "Descripción");

        Mockito.when(etapaRepository.findByNombre("EtapaError")).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.cambiarRetoDTO(dto);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se puedo convertir el dto"));
    }
}
