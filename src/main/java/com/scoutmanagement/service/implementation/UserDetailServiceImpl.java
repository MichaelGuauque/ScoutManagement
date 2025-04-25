package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.UserDTO;
import com.scoutmanagement.dto.UserRegistroDTO;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

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
    }

    @Override
    public UserEntity cambioUserDTO(UserRegistroDTO userDTO) {

        String passwordGenerada = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        logger.info("Contrasena sin Encriptar: {}", passwordGenerada);

        RoleEntity userRole = roleRepository.findByRole(userDTO.getRol());

        UserEntity user = UserEntity.builder()
                .username(userDTO.getUsername())
                //.password(bCryptPasswordEncoder.encode(userDTO.password()))
                .password(bCryptPasswordEncoder.encode(passwordGenerada))
                .roles(Set.of(userRole))
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();

        String asunto = "Tu cuenta ha sido creada";
        String cuerpo = String.format("Hola, %n%nTu cuenta ha sido creada correctamente.%nTu contraseña temporal es: %s%n%n por favor cámbiala después de iniciar sesión",
                passwordGenerada);

        emailService.enviarCorreo(user.getUsername(), asunto, cuerpo);

        return user;
    }

    @Override
    public Optional<UserEntity> findByEmail(UserDTO userDTO) {
        return userRepository.findUserEntityByUsername(userDTO.username());
    }

    @Override
    public Optional<UserEntity> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        Optional<UserEntity> userOptional = userRepository.findUserEntityByUsername(username);
        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            if (bCryptPasswordEncoder.matches(oldPassword, userEntity.getPassword())) {
                userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepository.save(userEntity);
            }

        }
    }
}
