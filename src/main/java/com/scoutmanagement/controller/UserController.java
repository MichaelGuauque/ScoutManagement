package com.scoutmanagement.controller;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.dto.UserDTO;
import com.scoutmanagement.persistence.model.*;

import static com.scoutmanagement.util.constants.AppConstants.*;

import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IUserEntity;
import com.scoutmanagement.util.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private IUserEntity userService;

    @Autowired
    private IPersonaService personaService;

    @GetMapping()
    public String login() {
        return "user/login";
    }

    private static final String ID_USUARIO = "idUsuario";

    private static final String ATRIBUTO_PERSONA = "persona";

    @PostMapping("/acceder")
    public String acceder(@ModelAttribute UserDTO userDTO, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<UserEntity> user = userService.findByEmail(userDTO);
            if (user.isPresent()) {
                UserEntity usuarioBuscado = user.get();
                Optional<Rol> optionalRol = usuarioBuscado.getRoles().stream()
                        .map(RoleEntity::getRole)
                        .findFirst();
                Rol rolEnum = optionalRol.get();
                String rol = rolEnum.name();

                if (passwordEncoder.matches(userDTO.password(), usuarioBuscado.getPassword())) {
                    if (rol.equals("ADULTO")) {
                        session.setAttribute("idUsuario", usuarioBuscado.getId());
                        session.setAttribute("rol", rol);
                        return "redirect:/home-admin";

                    } else if (rol.equals("JOVEN")) {
                        session.setAttribute("idUsuario", usuarioBuscado.getId());
                        session.setAttribute("rol", rol);
                        return "redirect:/home-user";
                    }
                } else {
                    throw new ServiceException("Contraseña incorrecta");
                }
            } else {
                throw new ServiceException("El usuario no existe");
            }
        } catch (ServiceException e) {
            logger.error("Error al acceder al sistema: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
        }
        return VISTA_LOGIN;
    }


    @GetMapping("/registrar")
    public String registrarUsuario(Model model, HttpSession session) {

        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            prepararModeloDeRegistro(model, sesionDelJefe);
            return "/user/crearMiembro";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid PersonaRegistroDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Object rol = session.getAttribute("rol");
            if (rol == null) {
                return VISTA_LOGIN;
            }
            if (session.getAttribute("rol") == Rol.ADULTO.name()) {
                boolean documentoExiste = personaService.existsByNumeroDeDocumento(dto.getNumeroDeDocumento());
                UserEntity user = userService.cambioUserDTO(dto.getUsuario());
                if (documentoExiste) {
                    prepararVistaConErrores(
                            redirectAttributes,
                            "El número de documento ya está registrado.",
                            EXCEPTION_ERROR,
                            false,
                            user,
                            dto
                    );
                    return VISTA_REGISTRAR;
                }
                boolean correoExiste = userService.existsByUsername(user.getUsername());
                if (correoExiste) {
                    prepararVistaConErrores(
                            redirectAttributes,
                            "El correo electrónico ya está registrado.",
                            EXCEPTION_ERROR,
                            true,
                            user,
                            dto
                    );
                    return VISTA_REGISTRAR;

                }
                userService.save(user);
                personaService.save(dto, user);

                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Miembro guardado");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
                return VISTA_REGISTRAR;
            }
            return VISTA_ERROR;


        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
            return VISTA_REGISTRAR;

        }
    }

    @GetMapping("/home-admin")
    public String showAdminHomePage(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelJefe);
            return "admin/home";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @GetMapping("/home-user")
    public String showUserHomePage(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (session.getAttribute("rol") == Rol.JOVEN.name()) {
            Persona sesionDelMiembro = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelMiembro);
            return "user/home";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session) {
        session.removeAttribute("idUsuario");
        session.removeAttribute("rol");
        return VISTA_LOGIN;
    }

    private void prepararVistaConErrores(RedirectAttributes redirectAttributes, String mensaje, String tipoError, boolean errorCampo, UserEntity user, PersonaRegistroDTO dto) {
        redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, mensaje);
        redirectAttributes.addFlashAttribute("type", tipoError);
        redirectAttributes.addFlashAttribute(errorCampo ? "errorCorreo" : "errorPersona", true);
        redirectAttributes.addFlashAttribute("usuario", user);
        redirectAttributes.addFlashAttribute("personaAgregada", dto);
    }


    private void prepararModeloDeRegistro(Model model, Persona sesionDelJefe) {
        model.addAttribute(ATRIBUTO_PERSONA, sesionDelJefe);
        model.addAttribute("ramas", Rama.values());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("tiposDeDocumento", TipoDeDocumento.values());
    }
}
