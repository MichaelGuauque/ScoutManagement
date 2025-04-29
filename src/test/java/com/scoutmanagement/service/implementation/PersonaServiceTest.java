package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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
    @Test
    void testFindJefes() {
        // Arrange: Datos de prueba
        Persona jefe1 = new Persona();
        jefe1.setCargo(Cargo.JEFE_MANADA);

        Persona jefe2 = new Persona();
        jefe2.setCargo(Cargo.JEFE_COMUNIDAD);

        List<Persona> jefesMock = Arrays.asList(jefe1, jefe2);

        when(personaRepository.findJefes()).thenReturn(jefesMock);

        List<Persona> resultado = personaService.findJefes();


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(Cargo.JEFE_MANADA, resultado.get(0).getCargo());
        assertEquals(Cargo.JEFE_COMUNIDAD, resultado.get(1).getCargo());

        verify(personaRepository, times(1)).findJefes();
    }
}
