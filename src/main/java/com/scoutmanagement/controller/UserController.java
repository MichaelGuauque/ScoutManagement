package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.PersonaConUsuarioDTO;
import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.DTO.UserDTO;
import com.scoutmanagement.DTO.UserRegistroDTO;
import com.scoutmanagement.persistence.model.*;
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

@Controller
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


    @GetMapping("/registrar")
    public String registrarUsuario(Model model) {

        model.addAttribute("ramas", Rama.values());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("tiposDeDocumento", TipoDeDocumento.values());
        return "CrearMiembro";
    }
    @PostMapping("/guardar")
    public String guardar( PersonaConUsuarioDTO dto ) {

        UserEntity user = userService.cambioUserDTO(dto.getUsuario());
        userService.save(user);

        PersonaRegistroDTO persona = new PersonaRegistroDTO();
        persona.setPrimerNombre(dto.getPrimerNombre());
        persona.setSegundoNombre(dto.getSegundoNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setNumeroDeDocumento(dto.getNumeroDeDocumento());
        persona.setTipoDeDocumento(dto.getTipoDeDocumento());
        persona.setGenero(dto.getGenero());
        persona.setRama(dto.getRama());
        persona.setCargo(dto.getCargo());
        persona.setUserEntity(user);
        personaService.save(persona);
        return "redirect:registrar";
    }


}
