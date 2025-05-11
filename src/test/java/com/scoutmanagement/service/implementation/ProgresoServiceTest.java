package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProgresoServiceTest {

    @Mock
    private ProgresoRepository progresoRepository;

    @Mock
    private RetoService retoService;

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

    @Test
    void testCalcularProgresosPorEtapaThrowsException() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setId(1L);
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa))
                .thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            progresoService.calcularProgresosPorEtapa(etapas, persona);
        });

        Assertions.assertTrue(exception.getMessage().contains("Error al calcular los progresos"));
    }

    @Test
    void testPrepararRetosPorEtapaThrowsException() {
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa 1");
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa))
                .thenThrow(new RuntimeException("DB error"));

        Map<String, Map<Long, Boolean>> estadosMock = new HashMap<>();
        estadosMock.put("Etapa 1", new HashMap<>());

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            progresoService.prepararRetosPorEtapa(etapas, estadosMock);
        });

        Assertions.assertTrue(exception.getMessage().contains("Error al preparar los retos por etapa"));
    }


    @Test
    void testCalcularEstadoRetosThrowsException() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa 1");
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa))
                .thenThrow(new RuntimeException("DB error"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> {
            progresoService.calcularEstadoRetos(etapas, persona);
        });

        Assertions.assertTrue(exception.getMessage().contains("Error al calcular los estados de los retos"));
    }

    @Test
    void testCalcularProgresosConEtapaSinRetos() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setId(1L);
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(List.of());
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(List.of());

        Map<Long, Float> resultado = progresoService.calcularProgresosPorEtapa(etapas, persona);

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals(0f, resultado.get(1L));
    }

    @Test
    void testCalcularProgresosConTodosRetosCompletados() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setId(2L);
        List<Etapa> etapas = List.of(etapa);

        Reto reto1 = new Reto();
        Reto reto2 = new Reto();
        List<Reto> retos = List.of(reto1, reto2);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(retos);
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(retos);

        Map<Long, Float> resultado = progresoService.calcularProgresosPorEtapa(etapas, persona);

        Assertions.assertEquals(100f, resultado.get(2L));
    }

    @Test
    void testCalcularProgresosConProgresoParcial() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setId(3L);
        List<Etapa> etapas = List.of(etapa);

        Reto reto1 = new Reto();
        Reto reto2 = new Reto();
        Reto reto3 = new Reto();
        List<Reto> todos = List.of(reto1, reto2, reto3);
        List<Reto> completados = List.of(reto1);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(todos);
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(completados);

        Map<Long, Float> resultado = progresoService.calcularProgresosPorEtapa(etapas, persona);

        Assertions.assertEquals(3L, etapa.getId());
        Assertions.assertEquals(33.3333f, resultado.get(3L), 0.0001f);
    }

    @Test
    void testCalcularProgresosVariasEtapas() {
        Persona persona = new Persona();

        Etapa etapa1 = new Etapa(); etapa1.setId(10L);
        Etapa etapa2 = new Etapa(); etapa2.setId(20L);
        List<Etapa> etapas = List.of(etapa1, etapa2);

        Reto reto1 = new Reto(); Reto reto2 = new Reto();
        Reto reto3 = new Reto();

        Mockito.when(retoService.findAllRetosEtapa(etapa1)).thenReturn(List.of(reto1, reto2));
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa1)).thenReturn(List.of(reto1));

        Mockito.when(retoService.findAllRetosEtapa(etapa2)).thenReturn(List.of(reto3));
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa2)).thenReturn(List.of(reto3));

        Map<Long, Float> resultado = progresoService.calcularProgresosPorEtapa(etapas, persona);

        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals(50f, resultado.get(10L));
        Assertions.assertEquals(100f, resultado.get(20L));
    }

    @Test
    void testPrepararRetosPorEtapaSinRetos() {
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa Vacía");
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(List.of());

        Map<String, Map<Long, Boolean>> estadosMock = new HashMap<>();
        estadosMock.put("Etapa Vacía", new HashMap<>());

        Map<String, List<Reto>> resultado = progresoService.prepararRetosPorEtapa(etapas, estadosMock);

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertTrue(resultado.containsKey("Etapa Vacía"));
        Assertions.assertTrue(resultado.get("Etapa Vacía").isEmpty());
    }


    @Test
    void testPrepararRetosPorEtapaConRetos() {
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa 1");
        List<Etapa> etapas = List.of(etapa);

        Reto reto1 = new Reto();
        reto1.setId(1L);
        Reto reto2 = new Reto();
        reto2.setId(2L);
        List<Reto> retos = List.of(reto1, reto2);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(retos);

        Map<String, Map<Long, Boolean>> estadosMock = new HashMap<>();
        Map<Long, Boolean> estadoEtapa = new HashMap<>();
        estadoEtapa.put(1L, false);
        estadoEtapa.put(2L, true);
        estadosMock.put("Etapa 1", estadoEtapa);

        Map<String, List<Reto>> resultado = progresoService.prepararRetosPorEtapa(etapas, estadosMock);

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertTrue(resultado.containsKey("Etapa 1"));
        Assertions.assertEquals(2, resultado.get("Etapa 1").size());
    }


    @Test
    void testPrepararRetosPorEtapaMultiplesEtapas() {
        Etapa etapa1 = new Etapa();
        etapa1.setNombre("Etapa A");
        Etapa etapa2 = new Etapa();
        etapa2.setNombre("Etapa B");
        List<Etapa> etapas = List.of(etapa1, etapa2);

        Reto retoA1 = new Reto();
        retoA1.setId(10L);
        Reto retoB1 = new Reto();
        retoB1.setId(20L);
        Reto retoB2 = new Reto();
        retoB2.setId(21L);

        Mockito.when(retoService.findAllRetosEtapa(etapa1)).thenReturn(List.of(retoA1));
        Mockito.when(retoService.findAllRetosEtapa(etapa2)).thenReturn(List.of(retoB1, retoB2));

        Map<String, Map<Long, Boolean>> estadosMock = new HashMap<>();
        estadosMock.put("Etapa A", Map.of(10L, true));
        estadosMock.put("Etapa B", Map.of(20L, false, 21L, true));

        Map<String, List<Reto>> resultado = progresoService.prepararRetosPorEtapa(etapas, estadosMock);

        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals(1, resultado.get("Etapa A").size());
        Assertions.assertEquals(2, resultado.get("Etapa B").size());
    }


    @Test
    void testCalcularEstadoRetosEtapaSinRetos() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa Vacía");
        List<Etapa> etapas = List.of(etapa);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(List.of());
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(List.of());

        Map<String, Map<Long, Boolean>> resultado = progresoService.calcularEstadoRetos(etapas, persona);

        Assertions.assertTrue(resultado.containsKey("Etapa Vacía"));
        Assertions.assertTrue(resultado.get("Etapa Vacía").isEmpty());
    }

    @Test
    void testCalcularEstadoRetosConUnRetoCompletado() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa 1");
        List<Etapa> etapas = List.of(etapa);

        Reto reto = new Reto();
        reto.setId(101L);
        List<Reto> todos = List.of(reto);
        List<Reto> completados = List.of(reto);

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(todos);
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(completados);

        Map<String, Map<Long, Boolean>> resultado = progresoService.calcularEstadoRetos(etapas, persona);

        Assertions.assertTrue(resultado.containsKey("Etapa 1"));
        Assertions.assertEquals(Boolean.TRUE, resultado.get("Etapa 1").get(101L));
    }

    @Test
    void testCalcularEstadoRetosConUnRetoNoCompletado() {
        Persona persona = new Persona();
        Etapa etapa = new Etapa();
        etapa.setNombre("Etapa 2");
        List<Etapa> etapas = List.of(etapa);

        Reto reto = new Reto();
        reto.setId(102L);
        List<Reto> todos = List.of(reto);
        List<Reto> completados = List.of(); // vacío

        Mockito.when(retoService.findAllRetosEtapa(etapa)).thenReturn(todos);
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa)).thenReturn(completados);

        Map<String, Map<Long, Boolean>> resultado = progresoService.calcularEstadoRetos(etapas, persona);

        Assertions.assertTrue(resultado.containsKey("Etapa 2"));
        Assertions.assertEquals(Boolean.FALSE, resultado.get("Etapa 2").get(102L));
    }

    @Test
    void testCalcularEstadoRetosMultiplesEtapas() {
        Persona persona = new Persona();

        Etapa etapa1 = new Etapa();
        etapa1.setNombre("Etapa A");

        Etapa etapa2 = new Etapa();
        etapa2.setNombre("Etapa B");

        Reto retoA = new Reto();
        retoA.setId(201L);

        Reto retoB = new Reto();
        retoB.setId(202L);

        List<Etapa> etapas = List.of(etapa1, etapa2);

        Mockito.when(retoService.findAllRetosEtapa(etapa1)).thenReturn(List.of(retoA));
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa1)).thenReturn(List.of());

        Mockito.when(retoService.findAllRetosEtapa(etapa2)).thenReturn(List.of(retoB));
        Mockito.when(retoService.findCompletadosByPersonaAndEtapa(persona, etapa2)).thenReturn(List.of(retoB));

        Map<String, Map<Long, Boolean>> resultado = progresoService.calcularEstadoRetos(etapas, persona);

        Assertions.assertEquals(Boolean.FALSE, resultado.get("Etapa A").get(201L));
        Assertions.assertEquals(Boolean.TRUE, resultado.get("Etapa B").get(202L));
    }

}
