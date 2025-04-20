package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.UserDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.RoleEntity;
import com.scoutmanagement.persistence.model.UserEntity;
import com.scoutmanagement.service.interfaces.IUserEntity;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/guardar")
    public String guardar(UserDTO usuario /*Persona persona*/) {
//        logger.info("Usuario registrado: {}", usuario);
//        logger.info("Cliente registrado: {}", cliente);
        UserEntity user = userService.cambioUserDTO(usuario);
        //cliente.setUsuario(user);
        userService.save(user);
        //clienteService.save(cliente);
        return "redirect:login";
    }


    @GetMapping("/home-admin")
    public String showAdminHomePage(HttpSession session) {
        if(session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "admin/home";
        }
        return "error/pageNotFound";
    }
}
