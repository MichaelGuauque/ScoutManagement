package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.UserDTO;
import com.scoutmanagement.dto.UserRegistroDTO;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IUserEntity;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailServiceImpl implements IUserEntity, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

            //permisos
            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

            //Obtiene los roles del usuario
            userEntity.getRoles()
                    .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_" + role)));

            //Obtiene los permisos de los roles
            userEntity.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

            return new User(userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.isEnabled(),
                    userEntity.isAccountNoExpired(),
                    userEntity.isCredentialNoExpired(),
                    userEntity.isAccountNoLocked(),
                    authorityList);
        } catch (Exception e) {
            throw new ServiceException("Usuario no encontrado: " + e.getMessage());
        }

    }

    @Override
    public UserEntity cambioUserDTO(UserRegistroDTO userDTO) {

        try {
            String passwordGenerada = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            RoleEntity userRole = roleRepository.findByRole(userDTO.getRol());

            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername().toLowerCase())
                    .password(bCryptPasswordEncoder.encode(passwordGenerada))
                    .roles(Set.of(userRole))
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .isEnabled(true)
                    .activo(true)
                    .build();

            String asunto = "Tu cuenta ha sido creada";
            String cuerpo = String.format("Hola, %n%nTu cuenta ha sido creada correctamente.%nTu contraseña temporal es: %s%n%n por favor cámbiala después de iniciar sesión",
                    passwordGenerada);

            emailService.enviarCorreo(user.getUsername(), asunto, cuerpo);

            return user;
        } catch (Exception e) {
            throw new ServiceException("Error: no se pudo convertir el DTO de usuario. Excepcion: " + e.getMessage());
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(UserDTO userDTO) {
        try {
            return userRepository.findUserEntityByUsername(userDTO.username());

        } catch (Exception e) {
            throw new ServiceException("Error: no se encontró el usuario " + e.getMessage());
        }
    }

    @Override
    public Optional<UserEntity> findById(long id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error: no se encontró el usuario " + e.getMessage());
        }

    }

    @Override
    public boolean existsByUsername(String email) {
        return userRepository.existsByUsername(email);
    }

    @Override
    public void save(UserEntity userEntity) {
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        try {
            Optional<UserEntity> userOptional = userRepository.findUserEntityByUsername(username);
            if (userOptional.isPresent()) {
                UserEntity userEntity = userOptional.get();
                if (bCryptPasswordEncoder.matches(oldPassword, userEntity.getPassword())) {
                    userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
                    userRepository.save(userEntity);
                }

            }
        } catch (Exception e) {
            throw new ServiceException("Contraseñas no coinciden " + e.getMessage());
        }
    }
    public void desactivarUsuarioPorId(Long idUsuario) {
        UserEntity usuario = findById(idUsuario)
                .orElseThrow(() -> new ServiceException("Usuario no encontrado con ID: " + idUsuario));

        usuario.setActivo(false);
        save(usuario);
    }

}
