package com.scoutmanagement.TestUser;

import com.scoutmanagement.dto.UserDTO;
import com.scoutmanagement.dto.UserRegistroDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.implementation.EmailService;
import com.scoutmanagement.service.implementation.UserDetailServiceImpl;
import com.scoutmanagement.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserDetailServiceImplTest {

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private UserRegistroDTO userDTO;
    private UserEntity userEntity;
    private RoleEntity roleEntity;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mockeamos el DTO y la entidad de usuario
        userDTO = new UserRegistroDTO("correito@gmail.com", Rol.ADULTO);

        roleEntity = new RoleEntity();
        roleEntity.setRole(userDTO.getRol());

        userEntity = UserEntity.builder()
                .username("correito@gmail.com")
                .password("passwordEncoded")
                .roles(Set.of(roleEntity))
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Preparar el mock de la base de datos
        when(userRepository.findUserEntityByUsername("correito@gmail.com")).thenReturn(Optional.of(userEntity));

        // Llamamos al método
        UserDetails userDetails = userDetailService.loadUserByUsername("correito@gmail.com");

        // Verificar los resultados
        assertNotNull(userDetails);
        assertEquals("correito@gmail.com", userDetails.getUsername());
        verify(userRepository, times(1)).findUserEntityByUsername("correito@gmail.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Preparar el mock para devolver un usuario no encontrado
        when(userRepository.findUserEntityByUsername("correito@gmail.com")).thenReturn(Optional.empty());

        // Llamamos al método y verificamos que lanza la excepción
        assertThrows(ServiceException.class, () -> userDetailService.loadUserByUsername("correito@gmail.com"));
        verify(userRepository, times(1)).findUserEntityByUsername("correito@gmail.com");
    }

    @Test
    void testCambioUserDTO_Success() {

        UserRegistroDTO userDTO = new UserRegistroDTO();
        userDTO.setUsername("correito@gmail.com");
        userDTO.setRol(Rol.JOVEN);

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(Rol.JOVEN);

        // Mockear dependencias
        when(roleRepository.findByRole(Rol.JOVEN)).thenReturn(roleEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        doNothing().when(emailService).enviarCorreo(anyString(), anyString(), anyString());

        // Ejecutar
        UserEntity result = userDetailService.cambioUserDTO(userDTO);

        // Verificar
        assertNotNull(result);
        assertEquals("correito@gmail.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(roleEntity));
        assertTrue(result.isAccountNoExpired());
        assertTrue(result.isAccountNoLocked());
        assertTrue(result.isCredentialNoExpired());
        assertTrue(result.isEnabled());

        // Verificar interacciones
        verify(roleRepository, times(1)).findByRole(Rol.JOVEN);
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(emailService, times(1)).enviarCorreo(anyString(), anyString(), anyString());
    }



    @Test
    void testUpdatePassword_UserFoundAndPasswordMatches() {
        // Mockeamos la base de datos para un usuario existente
        when(userRepository.findUserEntityByUsername("correito@gmail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", userEntity.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        // Llamamos al método
        userDetailService.updatePassword("correito@gmail.com", "password123", "newPassword");

        // Verificamos que el método save fue llamado
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        // Mockeamos la base de datos para que el usuario no sea encontrado
        when(userRepository.findUserEntityByUsername("correito@gmail.com")).thenReturn(Optional.empty());

        // Llamamos al método y verificamos que no hace nada
        userDetailService.updatePassword("correito@gmail.com", "password123", "newPassword");

        // Verificamos que el método save nunca fue llamado
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void testUpdatePassword_InvalidOldPassword() {
        // Mockeamos la base de datos para un usuario existente
        when(userRepository.findUserEntityByUsername("correito@gmail.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("wrongOldPassword", userEntity.getPassword())).thenReturn(false);

        // Llamamos al método
        userDetailService.updatePassword("correito@gmail.com", "wrongOldPassword", "newPassword");

        // Verificamos que el método save nunca fue llamado porque la contraseña no coincide
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }
    @Test
    void testExistsByUsername_WhenUserExists() {

        String email = "test@example.com";

        when(userRepository.existsByUsername(email)).thenReturn(true);

        boolean exists = userDetailService.existsByUsername(email);

        assertTrue(exists);

        verify(userRepository).existsByUsername(email);
    }

    @Test
    void testExistsByUsername_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";

        when(userRepository.existsByUsername(email)).thenReturn(false);

        boolean exists = userDetailService.existsByUsername(email);

        assertFalse(exists);

        verify(userRepository).existsByUsername(email);
    }
    @Test
    public void testCambioUserDTO_ExceptionRoleNotFound() {

        UserRegistroDTO userDTO = new UserRegistroDTO();
        userDTO.setUsername("testUser@gmail.com");
        userDTO.setRol(Rol.ADULTO); // Rol que no existe en el repositorio


        when(roleRepository.findByRole(Rol.ADULTO)).thenReturn(null);


        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailService.cambioUserDTO(userDTO);
        });

        assertTrue(exception.getMessage().contains("Error: no se pudo convertir el DTO de usuario"));
    }

    @Test
    public void testFindByEmail_ExceptionThrown() {
        // Preparar
        UserDTO userDTO = new UserDTO(
                "testUser@gmail.com",
                "password123",
                "newPassword123",
                Rol.JOVEN
        );

        when(userRepository.findUserEntityByUsername(userDTO.username()))
                .thenThrow(new RuntimeException("Base de datos caída"));

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailService.findByEmail(userDTO);
        });


        assertTrue(exception.getMessage().contains("Error: no se encontró el usuario"));
    }

    @Test
    public void testFindById_ExceptionThrown() {

        long id = 1L;


        when(userRepository.findById(id))
                .thenThrow(new RuntimeException("Base de datos caída"));


        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailService.findById(id);
        });

        assertTrue(exception.getMessage().contains("Error: no se encontró el usuario"));
    }

    @Test
    public void testSave_ExceptionThrown() {

        UserEntity userEntity = UserEntity.builder()
                .username("testUser@gmail.com")
                .password("password")
                .build();


        doThrow(new RuntimeException("Base de datos caída"))
                .when(userRepository).save(userEntity);


        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailService.save(userEntity);
        });

        assertTrue(exception.getMessage().contains("No se pudo guardar el usuario"));
    }
    @Test
    public void testUpdatePassword_ExceptionThrown() {
        // Preparar datos de entrada
        String username = "testUser@gmail.com";
        String oldPassword = "oldPass";
        String newPassword = "newPass";


        when(userRepository.findUserEntityByUsername(username))
                .thenThrow(new RuntimeException("Error en la base de datos"));


        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userDetailService.updatePassword(username, oldPassword, newPassword);
        });


        assertTrue(exception.getMessage().contains("Contraseñas no coinciden"));
    }
}
