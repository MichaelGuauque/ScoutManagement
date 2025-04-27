package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService personaService;

    @Captor
    private ArgumentCaptor<Persona> personaCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_shouldCallPersonaRepositorySaveOnce() {
        PersonaRegistroDTO dto = PersonaRegistroDTO.builder()
                .primerNombre("Ana")
                .segundoNombre("Lucía")
                .primerApellido("Torres")
                .segundoApellido("Mejía")
                .numeroDeDocumento(1122334455L)
                .tipoDeDocumento(TipoDeDocumento.CC)
                .rama(Rama.CLAN)
                .cargo(Cargo.ROVER)
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("ana_lucia@gmail.com")
                .password("salchipapa123")
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .roles(Set.of())
                .build();

        personaService.save(dto, user);

        verify(personaRepository).save(personaCaptor.capture());
        Persona personaGuardada = personaCaptor.getValue();

        assertEquals("Ana", personaGuardada.getPrimerNombre());
        assertEquals("Lucía", personaGuardada.getSegundoNombre());
        assertEquals("Torres", personaGuardada.getPrimerApellido());
        assertEquals("Mejía", personaGuardada.getSegundoApellido());
        assertEquals(1122334455L, personaGuardada.getNumeroDeDocumento());
        assertEquals(TipoDeDocumento.CC, personaGuardada.getTipoDeDocumento());
        assertEquals(Rama.CLAN, personaGuardada.getRama());
        assertEquals(Cargo.ROVER, personaGuardada.getCargo());
        assertEquals(user, personaGuardada.getUserEntity());
    }

    @Test
    void testExistsByNumeroDeDocumento_WhenDocumentExists() {

        Long numeroDeDocumento = 123456789L;  // Número de documento ficticio
        when(personaRepository.existsByNumeroDeDocumento(numeroDeDocumento)).thenReturn(true);


        boolean exists = personaService.existsByNumeroDeDocumento(numeroDeDocumento);

        assertTrue(exists);  // Verificamos que el resultado sea true
        verify(personaRepository).existsByNumeroDeDocumento(numeroDeDocumento);
    }

    @Test
    void testExistsByNumeroDeDocumento_WhenDocumentDoesNotExist() {

        Long numeroDeDocumento = 987654321L;
        when(personaRepository.existsByNumeroDeDocumento(numeroDeDocumento)).thenReturn(false);


        boolean exists = personaService.existsByNumeroDeDocumento(numeroDeDocumento);


        assertFalse(exists);
        verify(personaRepository).existsByNumeroDeDocumento(numeroDeDocumento);
    }
}
