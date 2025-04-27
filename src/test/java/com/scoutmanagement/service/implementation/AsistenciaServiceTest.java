package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.AsistenciaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Persona persona;
    private Actividad actividad;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        persona = Persona.builder()
                .id(1L)
                .primerNombre("Ana")
                .segundoNombre("Lucía")
                .primerApellido("Torres")
                .segundoApellido("Mejía")
                .numeroDeDocumento(1122334455L)
                .tipoDeDocumento(TipoDeDocumento.CC)
                .genero(Genero.FEMENINO)
                .rama(Rama.CLAN)
                .cargo(Cargo.ROVER)
                .userEntity(UserEntity.builder()
                        .id(1L)
                        .username("ana_lucia@gmail.com")
                        .password("salchipapa123")
                        .isEnabled(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialNoExpired(true)
                        .roles(Set.of())
                        .build())
                .build();

        actividad = Actividad.builder()
                .id(1L)
                .nombre("Campamento Clan")
                .build();
    }

    @Test
    void testRegistrarAsistencia_AsistioTrue_CreaAsistencia() {
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, true);

        when(asistenciaRepository.findByMiembroIdAndActividadId(persona.getId(), actividad.getId()))
                .thenReturn(Optional.empty());

        when(asistenciaRepository.save(any(Asistencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Asistencia asistencia = asistenciaService.registrarAsistencia(asistenciaDTO);

        assertNotNull(asistencia);
        assertTrue(asistencia.isAsistio());
        assertEquals(persona.getId(), asistencia.getMiembro().getId());
        assertEquals(actividad.getId(), asistencia.getActividad().getId());
        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void testRegistrarAsistencia_AsistioFalse_EliminaAsistencia() {
        Asistencia asistenciaExistente = Asistencia.builder()
                .id(1L)
                .miembro(persona)
                .actividad(actividad)
                .asistio(true)
                .build();

        AsistenciaDTO asistenciaDTO = new AsistenciaDTO(persona, actividad, false);

        when(asistenciaRepository.findByMiembroIdAndActividadId(persona.getId(), actividad.getId()))
                .thenReturn(Optional.of(asistenciaExistente));

        Asistencia resultado = asistenciaService.registrarAsistencia(asistenciaDTO);

        assertNull(resultado);
        verify(asistenciaRepository, times(1)).delete(asistenciaExistente);
        verify(asistenciaRepository, never()).save(any());
    }
}
