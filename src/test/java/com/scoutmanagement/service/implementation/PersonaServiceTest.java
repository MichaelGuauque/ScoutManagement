package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession httpSession;

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
    void testFindByUsuarioId_WhenFound() {
        Long usuarioId = 10L;
        Persona persona = new Persona();
        when(personaRepository.findByUserEntity_Id(usuarioId)).thenReturn(Optional.of(persona));

        Optional<Persona> result = personaService.findByUsuarioId(usuarioId);

        assertTrue(result.isPresent());
        assertEquals(persona, result.get());
        verify(personaRepository).findByUserEntity_Id(usuarioId);
    }

    @Test
    void testFindByUsuarioId_WhenNotFound() {
        Long usuarioId = 20L;
        when(personaRepository.findByUserEntity_Id(usuarioId)).thenReturn(Optional.empty());

        Optional<Persona> result = personaService.findByUsuarioId(usuarioId);

        assertFalse(result.isPresent());
        verify(personaRepository).findByUserEntity_Id(usuarioId);
    }

    @Test
    void testPersonaModelSession_WhenUsuarioYPersonaExisten() {
        String sessionKey = "usuarioId";
        Long userId = 30L;

        UserEntity user = UserEntity.builder().id(userId).build();
        Persona persona = new Persona();
        persona.setUserEntity(user);

        when(httpSession.getAttribute(sessionKey)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(personaRepository.findByUserEntity_Id(userId)).thenReturn(Optional.of(persona));

        Persona result = personaService.personaModelSession(sessionKey, httpSession);

        assertEquals(persona, result);
        verify(userRepository).findById(userId);
        verify(personaRepository).findByUserEntity_Id(userId);
    }

    @Test
    void testPersonaModelSession_WhenUsuarioNoExiste() {
        String sessionKey = "usuarioId";
        Long userId = 40L;

        when(httpSession.getAttribute(sessionKey)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personaService.personaModelSession(sessionKey, httpSession)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void testPersonaModelSession_WhenPersonaNoExiste() {
        String sessionKey = "usuarioId";
        Long userId = 50L;

        UserEntity user = UserEntity.builder().id(userId).build();

        when(httpSession.getAttribute(sessionKey)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(personaRepository.findByUserEntity_Id(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                personaService.personaModelSession(sessionKey, httpSession)
        );

        assertEquals("Persona no encontrada", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(personaRepository).findByUserEntity_Id(userId);
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
    @Test
    void testFindMiembros() {
        // Arrange: Datos de prueba
        Persona member1 = new Persona();
        member1.setCargo(Cargo.LOBATO);

        Persona member2 = new Persona();
        member2.setCargo(Cargo.CAMINANTE);

        List<Persona> membersMock = Arrays.asList(member1, member2);

        when(personaRepository.findMiembros()).thenReturn(membersMock);

        List<Persona> resultado = personaService.findMiembros();


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(Cargo.LOBATO, resultado.get(0).getCargo());
        assertEquals(Cargo.CAMINANTE, resultado.get(1).getCargo());

        verify(personaRepository, times(1)).findMiembros();
    }

    @Test
    public void testGetNombreCompleto() {
        // Arrange
        Persona persona = new Persona();
        persona.setPrimerNombre("Juan");
        persona.setSegundoNombre("Carlos");
        persona.setPrimerApellido("Pérez");
        persona.setSegundoApellido("Gómez");

        // Act
        String nombreCompleto = persona.getNombreCompleto();

        // Assert
        assertEquals("Juan Carlos Pérez Gómez", nombreCompleto);
    }
}
