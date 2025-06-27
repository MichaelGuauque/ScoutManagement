package com.scoutmanagement.controller;

import com.scoutmanagement.persistence.model.*;
import static com.scoutmanagement.util.constants.AppConstants.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IUserEntity;
import com.scoutmanagement.util.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final Logger logger = LoggerFactory.getLogger(ConfiguracionController.class);

    @Autowired
    private IUserEntity userService;

    @Autowired
    private IPersonaService personaService;

    private static final String ID_USUARIO = "idUsuario";
    private static final String ATRIBUTO_PERSONA = "persona";

    @GetMapping
    public String mostrarConfiguracion(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (session.getAttribute("rol").equals(Rol.ADULTO.name())) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelJefe);
            return "admin/configuracionAdmin";
        } else if (session.getAttribute("rol").equals(Rol.JOVEN.name())) {
            Persona sesionDelMiembro = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelMiembro);
            return "user/configuracionUser";
        }

        return VISTA_ERROR;
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam("currentPassword") String currentPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Object rol = session.getAttribute("rol");
            Long idUsuario = (Long) session.getAttribute("idUsuario");

            if (rol == null || idUsuario == null) {
                return VISTA_LOGIN;
            }

            // Validar que las contraseñas nuevas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Las contraseñas nuevas no coinciden");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return "redirect:/configuracion";
            }

            // Validar longitud mínima de la contraseña
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "La nueva contraseña debe tener al menos 6 caracteres");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return "redirect:/configuracion";
            }

            // Obtener el usuario actual
            Optional<UserEntity> userOptional = userService.findById(idUsuario);
            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Usuario no encontrado");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return "redirect:/configuracion";
            }

            UserEntity user = userOptional.get();
            String username = user.getUsername();

            // Llamar al servicio para actualizar la contraseña
            userService.updatePassword(username, currentPassword, newPassword);

            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Contraseña actualizada exitosamente");
            redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);

        } catch (ServiceException e) {
            logger.error("Error al cambiar la contraseña: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar la contraseña: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Error interno del servidor");
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
        }

        return "redirect:/configuracion";
    }
}