package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.EtapaDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.EtapaRepository;
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
class EtapaServiceTest {

    @Mock
    private EtapaRepository etapaRepository;

    @InjectMocks
    private EtapaService etapaService;

    @Test
    void testFindById() {
        Etapa etapa = new Etapa(1L, "Etapa 1", 1, Rama.MANADA);
        Mockito.when(etapaRepository.findById(1L)).thenReturn(Optional.of(etapa));

        Optional<Etapa> result = etapaService.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Etapa 1", result.get().getNombre());
    }

    @Test
    void testSaveFromDTO() {
        EtapaDTO etapaDTO = new EtapaDTO("Etapa DTO", 2, Rama.TROPA);
        Etapa etapa = new Etapa(null, "Etapa DTO", 2, Rama.TROPA);

        Mockito.when(etapaRepository.save(Mockito.any(Etapa.class))).thenReturn(etapa);

        etapaService.save(etapaDTO);

        Mockito.verify(etapaRepository).save(Mockito.argThat(e ->
                e.getNombre().equals("Etapa DTO") &&
                        e.getOrden() == 2 &&
                        e.getRama() == Rama.TROPA
        ));
    }

    @Test
    void testUpdate() {
        Etapa etapa = new Etapa(2L, "Actualizada", 3, Rama.COMUNIDAD);

        etapaService.update(etapa);

        Mockito.verify(etapaRepository).save(etapa);
    }

    @Test
    void testFindAll() {
        List<Etapa> lista = Arrays.asList(
                new Etapa(1L, "Etapa 1", 1, Rama.MANADA),
                new Etapa(2L, "Etapa 2", 2, Rama.TROPA)
        );

        Mockito.when(etapaRepository.findAll()).thenReturn(lista);

        List<Etapa> result = etapaService.findAll();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testFindAllByRama() {
        List<Etapa> lista = Collections.singletonList(
                new Etapa(1L, "Etapa", 1, Rama.CLAN)
        );

        Mockito.when(etapaRepository.findAllByRamaOrderByOrdenAsc(Rama.CLAN)).thenReturn(lista);

        List<Etapa> result = etapaService.findAllByRama(Rama.CLAN);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Rama.CLAN, result.get(0).getRama());
    }

    @Test
    void testCambiarEtapaDTO() {
        EtapaDTO dto = new EtapaDTO("Nombre", 5, Rama.MANADA);

        Etapa result = etapaService.cambiarEtapaDTO(dto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals("Nombre", result.getNombre());
        Assertions.assertEquals(5, result.getOrden());
        Assertions.assertEquals(Rama.MANADA, result.getRama());
    }

    @Test
    void testFindByIdThrowsException() {
        Mockito.when(etapaRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            etapaService.findById(1L);
        });

        Assertions.assertTrue(exception.getMessage().contains("Etapa no encontrada"));
    }

    @Test
    void testSaveFromDTOThrowsException() {
        EtapaDTO etapaDTO = new EtapaDTO("Etapa Falla", 3, Rama.CLAN);
        Mockito.when(etapaRepository.save(Mockito.any(Etapa.class))).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            etapaService.save(etapaDTO);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se pudo guardar"));
    }

    @Test
    void testUpdateThrowsException() {
        Etapa etapa = new Etapa(3L, "Etapa Update", 4, Rama.COMUNIDAD);
        Mockito.when(etapaRepository.save(etapa)).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            etapaService.update(etapa);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se pudo actualizar"));
    }

    @Test
    void testFindAllThrowsException() {
        Mockito.when(etapaRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            etapaService.findAll();
        });

        Assertions.assertTrue(exception.getMessage().contains("No se encontraron los datos"));
    }

    @Test
    void testFindAllByRamaThrowsException() {
        Mockito.when(etapaRepository.findAllByRamaOrderByOrdenAsc(Rama.TROPA))
                .thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            etapaService.findAllByRama(Rama.TROPA);
        });

        Assertions.assertTrue(exception.getMessage().contains("No se encontraron los datos de la rama"));
    }
}
