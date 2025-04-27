package com.scoutmanagement.TestUser;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testCambioUserDTO() {
        // Mockeamos el repositorio de roles
        when(roleRepository.findByRole(Rol.ADULTO)).thenReturn(roleEntity);

        // Llamamos al método
        UserEntity result = userDetailService.cambioUserDTO(userDTO);

        // Verificar que el resultado es el esperado
        assertNotNull(result);
        assertEquals("correito@gmail.com", result.getUsername());
        assertTrue(result.getRoles().contains(roleEntity));
        verify(roleRepository, times(1)).findByRole(Rol.ADULTO);
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
}
