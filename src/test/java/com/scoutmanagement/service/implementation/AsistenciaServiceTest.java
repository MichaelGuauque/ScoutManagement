package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.AsistenciaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.persistence.repository.AsistenciaRepository;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import java.util.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;
    @Mock
    private ActividadRepository actividadRepository;
    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Persona persona;
    private Actividad actividad;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        persona = Persona.builder().id(1L).build();
        actividad = Actividad.builder().id(1L).build();
    }

    @Test
    void findByActividadOrdenado_Success() {
        when(asistenciaRepository.findByActividadOrdenado(1L)).thenReturn(List.of());
        List<Asistencia> result = asistenciaService.findByActividadOrdenado(1L);
        assertNotNull(result);
        verify(asistenciaRepository, times(1)).findByActividadOrdenado(1L);
    }

    @Test
    void findByActividadOrdenado_ActividadIdNull() {
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.findByActividadOrdenado(null));
    }

    @Test
    void findByActividadOrdenado_DataAccessException() {
        when(asistenciaRepository.findByActividadOrdenado(1L)).thenThrow(mock(DataAccessException.class));
        assertThrows(ServiceException.class, () -> asistenciaService.findByActividadOrdenado(1L));
    }

    @Test
    void registrarAsistencia_AsistioTrue_CrearNuevaAsistencia() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, true);
        when(asistenciaRepository.findByMiembroIdAndActividadId(1L, 1L)).thenReturn(Optional.empty());
        when(asistenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Asistencia result = asistenciaService.registrarAsistencia(asistenciaDTO);
        assertNotNull(result);
        assertTrue(result.isAsistio());
        verify(asistenciaRepository, times(1)).save(any());
    }

    @Test
    void registrarAsistencia_AsistioTrue_ActualizarExistente() {
        Asistencia existente = Asistencia.builder().id(1L).miembro(persona).actividad(actividad).asistio(false).build();
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, true);
        when(asistenciaRepository.findByMiembroIdAndActividadId(1L, 1L)).thenReturn(Optional.of(existente));
        when(asistenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Asistencia result = asistenciaService.registrarAsistencia(asistenciaDTO);
        assertNotNull(result);
        assertTrue(result.isAsistio());
    }

    @Test
    void registrarAsistencia_AsistioFalse_EliminarAsistencia() {
        Asistencia existente = Asistencia.builder().id(1L).build();
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, false);
        when(asistenciaRepository.findByMiembroIdAndActividadId(1L, 1L)).thenReturn(Optional.of(existente));

        Asistencia result = asistenciaService.registrarAsistencia(asistenciaDTO);
        assertNull(result);
        verify(asistenciaRepository, times(1)).delete(existente);
    }

    @Test
    void registrarAsistencia_NullDTO() {
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.registrarAsistencia(null));
    }

    @Test
    void registrarAsistencia_NullMiembro() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(null, actividad, true);
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.registrarAsistencia(asistenciaDTO));
    }

    @Test
    void registrarAsistencia_NullActividad() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, null, true);
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.registrarAsistencia(asistenciaDTO));
    }

    @Test
    void registrarAsistencia_DataAccessException() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, true);
        when(asistenciaRepository.findByMiembroIdAndActividadId(1L, 1L)).thenThrow(mock(DataAccessException.class));
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistencia(asistenciaDTO));
    }

    @Test
    void registrarAsistenciasMasivas_Success() {
        Map<Long, Boolean> asistencias = Map.of(1L, true);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(asistenciaRepository.findByMiembroIdAndActividadId(1L, 1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> asistenciaService.registrarAsistenciasMasivas(1L, asistencias));
    }

    @Test
    void registrarAsistenciasMasivas_NullActividadId() {
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistenciasMasivas(null, Map.of(1L, true)));
    }


    @Test
    void registrarAsistenciasMasivas_AsistenciasVacias() {
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistenciasMasivas(1L, null));
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistenciasMasivas(1L, Collections.emptyMap()));
    }

    @Test
    void registrarAsistenciasMasivas_ActividadNoEncontrada() {
        when(actividadRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistenciasMasivas(1L, Map.of(1L, true)));
    }

    @Test
    void registrarAsistenciasMasivas_DataAccessException() {
        when(actividadRepository.findById(1L)).thenThrow(mock(DataAccessException.class));
        assertThrows(ServiceException.class, () -> asistenciaService.registrarAsistenciasMasivas(1L, Map.of(1L, true)));
    }

    @Test
    void prepararRegistroAsistencias_Success() {
        List<Persona> miembros = List.of(persona);
        when(asistenciaRepository.findByActividadId(1L)).thenReturn(List.of());

        List<Asistencia> result = asistenciaService.prepararRegistroAsistencias(actividad, miembros);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isAsistio());
    }

    @Test
    void prepararRegistroAsistencias_NullActividad() {
        assertThrows(ServiceException.class, () -> asistenciaService.prepararRegistroAsistencias(null, List.of()));
    }


    @Test
    void prepararRegistroAsistencias_NullMiembros() {
        assertThrows(ServiceException.class, () -> asistenciaService.prepararRegistroAsistencias(actividad, null));
    }

    @Test
    void prepararRegistroAsistencias_DataAccessException() {
        when(asistenciaRepository.findByActividadId(1L)).thenThrow(mock(DataAccessException.class));
        assertThrows(ServiceException.class, () -> asistenciaService.prepararRegistroAsistencias(actividad, List.of(persona)));
    }

    @Test
    void prepararRegistroAsistencias_Exception() {
        when(asistenciaRepository.findByActividadId(1L)).thenThrow(new RuntimeException("Error inesperado"));
        assertThrows(ServiceException.class, () -> asistenciaService.prepararRegistroAsistencias(actividad, List.of(persona)));
    }

    @Test
    void convertirAsistenciaDTO_Success() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, true);
        Asistencia asistencia = asistenciaService.convertirAsistenciaDTO(asistenciaDTO);

        assertEquals(persona, asistencia.getMiembro());
        assertEquals(actividad, asistencia.getActividad());
        assertTrue(asistencia.isAsistio());
    }

    @Test
    void convertirAsistenciaDTO_Null() {
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.convertirAsistenciaDTO(null));
    }

    @Test
    void findPersonasByRama_Success() {
        when(personaRepository.findByRama(Rama.CLAN)).thenReturn(List.of(persona));
        List<Persona> result = asistenciaService.findPersonasByRama(Rama.CLAN);

        assertEquals(1, result.size());
    }

    @Test
    void findPersonasByRama_Null() {
        assertThrows(IllegalArgumentException.class, () -> asistenciaService.findPersonasByRama(null));
    }

    @Test
    void findPersonasByRama_DataAccessException() {
        when(personaRepository.findByRama(Rama.CLAN)).thenThrow(mock(DataAccessException.class));
        assertThrows(ServiceException.class, () -> asistenciaService.findPersonasByRama(Rama.CLAN));
    }
}
