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

        Mockito.when(retoRepository.findAllRetosByEtapa(etapa)).thenReturn(lista);

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
    void testSave_whenRetoExists_throwsServiceException() {
        Etapa etapa = new Etapa();
        RetoDTO dto = new RetoDTO(1, "Etapa 1", "Descripción duplicada");
        Reto existingReto = new Reto(1L, 1, "Existente", etapa);

        Mockito.when(retoRepository.findRetoByNumero(1)).thenReturn(Optional.of(existingReto));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            retoService.save(dto);
        });

        Assertions.assertTrue(exception.getMessage().contains("El retro con numero 1 ya existe"));
        Mockito.verify(retoRepository, Mockito.never()).save(Mockito.any());
    }
}
