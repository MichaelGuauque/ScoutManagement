package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.DTO.UserDTO;
import com.scoutmanagement.DTO.UserRegistroDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IUserEntity;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private IUserEntity userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IPersonaService personaService;

    @GetMapping()
    public String login() {
        return "user/login";
    }

    @PostMapping("/acceder")
    public String acceder(UserDTO userDTO, HttpSession session, Model model) {
        logger.info("Usuario accedido: {}", userDTO);
        Optional<UserEntity> user = userService.findByEmail(userDTO);
        if (user.isPresent()) {
            UserEntity usuarioBuscado = user.get();
            logger.info("Usuario de la BD: {}", usuarioBuscado);
            session.setAttribute("idUsuario", usuarioBuscado.getId());
            Optional<Rol> optionalRol = usuarioBuscado.getRoles().stream()
                    .map(RoleEntity::getRole)
                    .findFirst();
            Rol rolEnum = optionalRol.get();
            String rol = rolEnum.name();
            logger.info("Rol del usuario: {}", rol);

            if (passwordEncoder.matches(userDTO.password(), usuarioBuscado.getPassword())) {
                if (rol.equals("ADULTO")) {
                    return "redirect:/administrador/home";
                } else {
                    // ACÁ IRIA LA LOGICA PARA REDIRIGIR AL HOME DE JOVEN
                    return "redirect:login";
                }
            }
        }
        logger.info("Usuario no encontrado");
        return "redirect:login";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestBody UserRegistroDTO usuario  /*PersonaRegistroDTO personaRegistroDTO*/) {
        logger.info("Usuario registrado: {}", usuario);
        for (Rol rol : Rol.values()) {
            // Verificar si el rol existe en la base de datos
            if (roleRepository.findByRole(rol) == null) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setRole(rol);
                roleRepository.save(roleEntity);
                logger.info("✅ Rol creado: {}", rol);
            }
        }
      //logger.info("Persona registrado: {}", personaRegistroDTO);
        UserEntity user = userService.cambioUserDTO(usuario);
        logger.info("Usuario cambiado: {}", user);
        //personaRegistroDTO.setUserEntity(user);
        userService.save(user);
        //personaService.save(personaRegistroDTO);
        return "redirect:login";
    }

}
