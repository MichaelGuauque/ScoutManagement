package com.scoutmanagement.service.implementation;

import com.scoutmanagement.controller.PersonaController;
import com.scoutmanagement.dto.PersonaActualizacionDTO;
import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;


    private PersonaController personaController;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private PersonaService personaService;

    @Captor
    private ArgumentCaptor<Persona> personaCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        personaController = new PersonaController();
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
    void testFindMiembrosByRama_ReturnsListOfMiembros() {
        // Arrange
        Rama rama = Rama.TROPA;

        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setUsername("miembro1");
        user1.setActivo(true);

        Persona persona1 = new Persona();
        persona1.setId(1L);
        persona1.setPrimerNombre("Carlos");
        persona1.setCargo(Cargo.SCOUT); // No empieza por JEFE_
        persona1.setRama(rama);
        persona1.setUserEntity(user1);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setUsername("miembro2");
        user2.setActivo(true);

        Persona persona2 = new Persona();
        persona2.setId(2L);
        persona2.setPrimerNombre("Laura");
        persona2.setCargo(Cargo.SCOUT); // No empieza por JEFE_
        persona2.setRama(rama);
        persona2.setUserEntity(user2);

        List<Persona> mockResult = List.of(persona1, persona2);

        Mockito.when(personaRepository.findMiembrosByRama(rama)).thenReturn(mockResult);

        // Act
        List<Persona> result = personaService.findMiembrosByRama(rama);

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Carlos", result.get(0).getPrimerNombre());
        Assertions.assertEquals("Laura", result.get(1).getPrimerNombre());

        Mockito.verify(personaRepository).findMiembrosByRama(rama);
    }

    @Test
    void testFindMiembrosByRama_ReturnsEmptyList() {
        // Arrange
        Rama rama = Rama.MANADA;

        Mockito.when(personaRepository.findMiembrosByRama(rama)).thenReturn(Collections.emptyList());

        // Act
        List<Persona> result = personaService.findMiembrosByRama(rama);

        // Assert
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(personaRepository).findMiembrosByRama(rama);
    }

    @Test
    void testActualizarPersona() {

        Long userId = 1L;


        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setRoles(new HashSet<>());

        Persona persona = new Persona();
        persona.setId(10L);
        persona.setUserEntity(user);
        persona.setPrimerNombre("Pedro");
        persona.setSegundoNombre("Luis");
        persona.setPrimerApellido("González");
        persona.setSegundoApellido("Martínez");
        persona.setNumeroDeDocumento(12345678L);


        PersonaActualizacionDTO dto = new PersonaActualizacionDTO();
        dto.setPrimerNombre("Juan");
        dto.setSegundoNombre("Carlos");
        dto.setPrimerApellido("Pérez");
        dto.setSegundoApellido("Gómez");
        dto.setNumeroDeDocumento(12345678L);


        dto.setTipoDeDocumento(TipoDeDocumento.CC);
        dto.setRama(Rama.COMUNIDAD);
        dto.setCargo(Cargo.JEFE_COMUNIDAD);
        dto.setRol(Rol.ADULTO);


        when(personaRepository.findByUserEntity_Id(userId)).thenReturn(Optional.of(persona));
        when(personaRepository.findByNumeroDeDocumento(12345678L)).thenReturn(Optional.empty());


        RoleEntity rolEntity = new RoleEntity();
        rolEntity.setRole(Rol.ADULTO);
        when(roleRepository.findByRole(Rol.ADULTO)).thenReturn(rolEntity);


        personaService.actualizarPersona(userId, dto);


        assertEquals("Juan", persona.getPrimerNombre());
        assertEquals("Carlos", persona.getSegundoNombre());
        assertEquals("Pérez", persona.getPrimerApellido());
        assertEquals("Gómez", persona.getSegundoApellido());
        assertEquals(12345678L, persona.getNumeroDeDocumento());
        assertEquals(TipoDeDocumento.CC, persona.getTipoDeDocumento());
        assertEquals(Rama.COMUNIDAD, persona.getRama());
        assertEquals(Cargo.JEFE_COMUNIDAD, persona.getCargo());


        assertTrue(user.getRoles().contains(rolEntity));
    }
    @Test
    void testActualizarPersona_numeroDeDocumentoDuplicado_lanzaExcepcion() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setRoles(new HashSet<>());

        Persona persona = new Persona();
        persona.setId(10L);
        persona.setUserEntity(user);

        PersonaActualizacionDTO dto = new PersonaActualizacionDTO();
        dto.setPrimerNombre("Juan");
        dto.setSegundoNombre("Carlos");
        dto.setPrimerApellido("Pérez");
        dto.setSegundoApellido("Gómez");
        dto.setNumeroDeDocumento(12345678L);


        Persona personaExistente = new Persona();
        personaExistente.setId(11L);
        personaExistente.setNumeroDeDocumento(12345678L);


        when(personaRepository.findByUserEntity_Id(userId)).thenReturn(Optional.of(persona));
        when(personaRepository.findByNumeroDeDocumento(12345678L)).thenReturn(Optional.of(personaExistente));  // Devuelve personaExistente


        assertThrows(DataIntegrityViolationException.class, () -> {
            personaService.actualizarPersona(userId, dto);
        });
    }
    @Test
    void testFiltrarYOrdenarPorEstado() {
        // Arrange
        Persona persona1 = new Persona();
        persona1.setRama(Rama.TROPA);
        UserEntity user1 = new UserEntity();
        user1.setActivo(true);
        persona1.setUserEntity(user1);

        Persona persona2 = new Persona();
        persona2.setRama(Rama.COMUNIDAD);
        UserEntity user2 = new UserEntity();
        user2.setActivo(false);
        persona2.setUserEntity(user2);

        Persona persona3 = new Persona();
        persona3.setRama(Rama.MANADA);
        UserEntity user3 = new UserEntity();
        user3.setActivo(true);
        persona3.setUserEntity(user3);

        Persona persona4 = new Persona();
        persona4.setRama(Rama.CLAN);
        UserEntity user4 = new UserEntity();
        user4.setActivo(true);
        persona4.setUserEntity(user4);

        List<Persona> personas = Arrays.asList(persona1, persona2, persona3, persona4);


        List<Persona> resultadoActivos = personaService.filtrarYOrdenarPorEstado(personas, "activos");

        assertEquals(3, resultadoActivos.size());
        assertTrue(resultadoActivos.contains(persona1));
        assertTrue(resultadoActivos.contains(persona3));
        assertTrue(resultadoActivos.contains(persona4));

        assertTrue(resultadoActivos.get(0).getRama().compareTo(resultadoActivos.get(1).getRama()) < 0);
        assertTrue(resultadoActivos.get(1).getRama().compareTo(resultadoActivos.get(2).getRama()) < 0);


        List<Persona> resultadoInactivos = personaService.filtrarYOrdenarPorEstado(personas, "inactivos");


        assertEquals(1, resultadoInactivos.size());
        assertTrue(resultadoInactivos.contains(persona2));
    }
    @Test
    void testFiltrarYOrdenarPorEstado_listaVacia() {

        List<Persona> personas = new ArrayList<>();


        List<Persona> resultado = personaService.filtrarYOrdenarPorEstado(personas, "activos");


        assertTrue(resultado.isEmpty());
    }
    @Test
    void testFindByNumeroDeDocumento() {
        // Arrange
        Long numeroDeDocumentoExistente = 12345678L;
        Long numeroDeDocumentoNoExistente = 87654321L;

        Persona personaExistente = new Persona();
        personaExistente.setNumeroDeDocumento(numeroDeDocumentoExistente);


        when(personaRepository.findByNumeroDeDocumento(numeroDeDocumentoExistente)).thenReturn(Optional.of(personaExistente));
        when(personaRepository.findByNumeroDeDocumento(numeroDeDocumentoNoExistente)).thenReturn(Optional.empty());


        Optional<Persona> personaOptional = personaService.findByNumeroDeDocumento(numeroDeDocumentoExistente);
        assertTrue(personaOptional.isPresent());
        assertEquals(numeroDeDocumentoExistente, personaOptional.get().getNumeroDeDocumento());


        Optional<Persona> personaOptionalNoExistente = personaService.findByNumeroDeDocumento(numeroDeDocumentoNoExistente);
        assertFalse(personaOptionalNoExistente.isPresent());
    }
    @Test
    void testRedireccionSegunTipo_devuelveVistaJefes() throws Exception {
        when(httpSession.getAttribute("miembro")).thenReturn("jefe");

        Method metodo = PersonaController.class.getDeclaredMethod("redireccionSegunTipo", HttpSession.class);
        metodo.setAccessible(true);

        String resultado = (String) metodo.invoke(personaController, httpSession);

        assertEquals("redirect:/miembros/jefes", resultado);
    }

    @Test
    void testRedireccionSegunTipo_devuelveVistaMiembros_siNoEsJefe() throws Exception {
        when(httpSession.getAttribute("miembro")).thenReturn("miembro");

        Method metodo = PersonaController.class.getDeclaredMethod("redireccionSegunTipo", HttpSession.class);
        metodo.setAccessible(true);

        String resultado = (String) metodo.invoke(personaController, httpSession);

        assertEquals("redirect:/miembros", resultado);
    }

    @Test
    void testRedireccionSegunTipo_devuelveVistaMiembros_siEsNull() throws Exception {
        when(httpSession.getAttribute("miembro")).thenReturn(null);

        Method metodo = PersonaController.class.getDeclaredMethod("redireccionSegunTipo", HttpSession.class);
        metodo.setAccessible(true);

        String resultado = (String) metodo.invoke(personaController, httpSession);

        assertEquals("redirect:/miembros", resultado);
    }
}
