package com.scoutmanagement.controller;

import com.scoutmanagement.dto.PersonaConUsuarioDTO;
import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.dto.UserDTO;
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
    public String acceder(@ModelAttribute UserDTO userDTO, HttpSession session, Model model) {
        logger.info("Usuario accedido: {}", userDTO);
        Optional<UserEntity> user = userService.findByEmail(userDTO);
        if (user.isPresent()) {
            UserEntity usuarioBuscado = user.get();
            logger.info("Usuario de la BD: {}", usuarioBuscado);
            Optional<Rol> optionalRol = usuarioBuscado.getRoles().stream()
                    .map(RoleEntity::getRole)
                    .findFirst();
            Rol rolEnum = optionalRol.get();
            String rol = rolEnum.name();

            if (passwordEncoder.matches(userDTO.password(), usuarioBuscado.getPassword())) {
                if (rol.equals("ADULTO")) {
                    session.setAttribute("idUsuario", usuarioBuscado.getId());
                    session.setAttribute("rol", rol);
                    logger.info("Rol del usuario: {}", rol);
                    return "redirect:/home-admin";
                } else {
                    // ACÁ IRIA LA LOGICA PARA REDIRIGIR AL HOME DE JOVEN
                    return "user/login";
                }
            }
        }
        logger.info("Usuario no encontrado");
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        return "user/login";
    }


    @GetMapping("/registrar")
    public String registrarUsuario(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            model.addAttribute("ramas", Rama.values());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("cargos", Cargo.values());
            model.addAttribute("tiposDeDocumento", TipoDeDocumento.values());
            return "/user/crearMiembro";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @PostMapping("/guardar")
    public String guardar(PersonaConUsuarioDTO dto, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            UserEntity user = userService.cambioUserDTO(dto.getUsuario());
            userService.save(user);
            PersonaRegistroDTO personaDTO = new PersonaRegistroDTO(dto);
            personaDTO.setUserEntity(user);
            personaService.save(personaDTO);
            return "redirect:registrar";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @GetMapping("/home-admin")
    public String showAdminHomePage(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "admin/home";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session) {
        session.removeAttribute("idUsuario");
        session.removeAttribute("rol");
        return "redirect:/";
    }
}
