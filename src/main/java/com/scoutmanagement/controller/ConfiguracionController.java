package com.scoutmanagement.controller;

import com.scoutmanagement.dto.InformacionPersonaDTO;
import com.scoutmanagement.dto.ResponsablePersonaDTO;
import com.scoutmanagement.persistence.model.*;
import static com.scoutmanagement.util.constants.AppConstants.*;

import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.implementation.UploadFileService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IUserEntity;
import com.scoutmanagement.util.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final Logger logger = LoggerFactory.getLogger(ConfiguracionController.class);

    @Autowired
    private IUserEntity userService;

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private PersonaRepository personaRepository;

    private static final String ID_USUARIO = "idUsuario";
    private static final String ATRIBUTO_PERSONA = "persona";
    private static final String REDIRECT_CONFIGURACION = "redirect:/configuracion";
    private static final String IMAGEN_DEFAULT = "miembro.svg";

    @GetMapping
    public String mostrarConfiguracion(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.ADULTO.name())) {
            return configurarVista(model, session, "admin/configuracionAdmin");
        } else if (rol.equals(Rol.JOVEN.name())) {
            return configurarVista(model, session, "user/configuracionUser");
        }

        return VISTA_ERROR;
    }

    private String configurarVista(Model model, HttpSession session, String vista) {
        Persona persona = personaService.personaModelSession(ID_USUARIO, session);
        model.addAttribute("rol", persona.getUserEntity().getRoles().stream().findFirst().map(RoleEntity::getRole).orElse(null));
        model.addAttribute(ATRIBUTO_PERSONA, persona);
        model.addAttribute("tiposDeSangre", TipoDeSangre.values());

        return vista;
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam("currentPassword") String currentPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Object rol = session.getAttribute("rol");
            Long idUsuario = (Long) session.getAttribute(ID_USUARIO);

            if (rol == null || idUsuario == null) {
                return VISTA_LOGIN;
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Las contraseñas nuevas no coinciden");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return REDIRECT_CONFIGURACION;
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "La nueva contraseña debe tener al menos 6 caracteres");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return REDIRECT_CONFIGURACION;
            }

            Optional<UserEntity> userOptional = userService.findById(idUsuario);
            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Usuario no encontrado");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return REDIRECT_CONFIGURACION;
            }

            UserEntity user = userOptional.get();
            String username = user.getUsername();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "La contraseña actual es incorrecta");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return REDIRECT_CONFIGURACION;
            }

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "La nueva contraseña debe ser diferente a la contraseña actual");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return REDIRECT_CONFIGURACION;
            }

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

        return REDIRECT_CONFIGURACION;
    }

    @GetMapping("/modificar")
    public String modificarPerfil(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.ADULTO.name())) {
            return configurarVista(model, session, "admin/modificarAdmin");
        } else if (rol.equals(Rol.JOVEN.name())) {
            return configurarVista(model, session, "user/modificarUser");
        }

        return VISTA_ERROR;
    }

    @PostMapping("/actualizarInformacion")
    public String actualizarInformacion(HttpSession session,
                                        @ModelAttribute InformacionPersonaDTO informacionPersonaDTO,
                                        @ModelAttribute ResponsablePersonaDTO responsablePersonaDTO,
                                        RedirectAttributes redirectAttributes){
        try {
            Object rol = session.getAttribute("rol");
            if (rol.equals(Rol.ADULTO.name())){
                personaService.actualizarInformacionPersonal(informacionPersonaDTO, session, null);

            }else if (rol.equals(Rol.JOVEN.name())) {
                personaService.actualizarInformacionPersonal(informacionPersonaDTO, session, responsablePersonaDTO);
            }
            redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Información modificado con éxito.");
            return REDIRECT_CONFIGURACION;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
        }
        return REDIRECT_CONFIGURACION;
    }

    @PostMapping("/modificar")
    public String modificarImagen(@RequestParam("fileInput") MultipartFile file, HttpSession session) throws IOException {
        Persona persona = personaService.personaModelSession(ID_USUARIO, session);
        if (persona.getImagen() == null || persona.getImagen().equals(IMAGEN_DEFAULT)){
            String fileName = uploadFileService.saveImage(file);
            persona.setImagen(fileName);
            System.out.println("Nombre de la imagen: " + persona.getImagen());
        } else if (persona.getImagen() != null && !persona.getImagen().equals(IMAGEN_DEFAULT)) {
            uploadFileService.deleteImage(persona.getImagen());
            String fileName = uploadFileService.saveImage(file);
            persona.setImagen(fileName);
        } else if (file.isEmpty()) {
            persona.setImagen(IMAGEN_DEFAULT);
        }

        personaRepository.save(persona);
        return REDIRECT_CONFIGURACION;
    }

}