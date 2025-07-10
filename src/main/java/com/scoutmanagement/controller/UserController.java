package com.scoutmanagement.controller;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.dto.UserDTO;
import com.scoutmanagement.persistence.model.*;

import static com.scoutmanagement.util.constants.AppConstants.*;

import com.scoutmanagement.service.interfaces.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private IUserEntity userService;

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IActividadService actividadService;

    @Autowired
    private IObtencionService obtencionService;

    @Autowired
    private IEtapaService etapaService;

    @GetMapping()
    public String login() {
        return "user/login";
    }

    private static final String ID_USUARIO = "idUsuario";

    private static final String ATRIBUTO_PERSONA = "persona";

    private static final Map<String, Integer> GRUPOS_RAMAS = Map.of(
            "MANADA", 30,
            "TROPA", 31,
            "COMUNIDAD", 32,
            "CLAN", 33
    );

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

                    session.setAttribute(ID_USUARIO, usuarioBuscado.getId());
                    session.setAttribute("rol", rol);

                    return "redirect:/home";
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
            return "user/crearMiembro";
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

                UserEntity tempUser = new UserEntity();
                tempUser.setUsername(dto.getUsuario().getUsername());

                if (documentoExiste) {
                    prepararVistaConErrores(
                            redirectAttributes,
                            "El número de documento ya está registrado.",
                            EXCEPTION_ERROR,
                            false,
                            tempUser,
                            dto
                    );
                    return VISTA_REGISTRAR;
                }
                boolean correoExiste = userService.existsByUsername(dto.getUsuario().getUsername());
                if (correoExiste) {
                    prepararVistaConErrores(
                            redirectAttributes,
                            "El correo electrónico ya está registrado.",
                            EXCEPTION_ERROR,
                            true,
                            tempUser,
                            dto
                    );
                    return VISTA_REGISTRAR;

                }
                UserEntity user = userService.cambioUserDTO(dto.getUsuario());
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

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (session.getAttribute("rol").equals(Rol.ADULTO.name())) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelJefe);
            agregarEstadisticasDashboard(model);
            List<Actividad> proximas = actividadService.obtenerTresProximasActividades();
            model.addAttribute("actividades", proximas);
            return "admin/home";
        } else if (session.getAttribute("rol").equals(Rol.JOVEN.name())) {
            Persona sesionDelMiembro = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelMiembro);
            Rama rama = sesionDelMiembro.getRama();
            List<Etapa> etapas = etapaService.findAllByRama(rama);
            model.addAttribute(ETAPAS, etapas);
            Set<Long> etapasObtenidas = obtencionService.findIdEtapasObtenidasByPersona(sesionDelMiembro);
            model.addAttribute("etapasObtenidas", etapasObtenidas);
            model.addAttribute("gruposRamas", GRUPOS_RAMAS);

            return "user/home";
        }

        return VISTA_ERROR;
    }

    private void agregarEstadisticasDashboard(Model model) {
        try {
            Long totalMiembros = personaService.contarMiembrosActivos();
            Long totalJefes = personaService.contarJefesActivos();
            Long totalActividades = actividadService.contarActividadesPendientes();
            Long actividadesEstaSemana = actividadService.contarActividadesEstaSemana();

            model.addAttribute("totalMiembros", totalMiembros);
            model.addAttribute("totalJefes", totalJefes);
            model.addAttribute("totalActividades", totalActividades);
            model.addAttribute("actividadesEstaSemana", actividadesEstaSemana);

            logger.info("Dashboard stats - Miembros: {}, Jefes: {}, Actividades pendientes: {}, Esta semana: {}",
                    totalMiembros, totalJefes, totalActividades, actividadesEstaSemana);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas del dashboard: {}", e.getMessage());
            model.addAttribute("totalMiembros", 0L);
            model.addAttribute("totalJefes", 0L);
            model.addAttribute("totalActividades", 0L);
            model.addAttribute("actividadesEstaSemana", 0L);
        }
    }

    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperacion() {
        return "user/recuperar";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperacionPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.recuperarPassword(email);
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Se ha enviado un correo con tu nueva contraseña");
            redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
            return "redirect:/";
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
            return "redirect:/recuperar";
        }
    }

    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session) {
        session.removeAttribute(ID_USUARIO);
        session.removeAttribute("rol");
        return VISTA_LOGIN;
    }
    @PostMapping("/usuarios/desactivar")
    public String desactivarUsuario(@RequestParam Long idUsuario,
                                    @RequestParam(required = false) String origen,
                                    RedirectAttributes redirectAttributes) {

        userService.desactivarUsuarioPorId(idUsuario);
        redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Usuario deshabilitado.");
        redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);

        if ("jefes".equals(origen)) {
            return VISTA_JEFES;
        } else {

            return VISTA_MIEMBROS;
        }

    }

    @PostMapping("/usuarios/habilitar")
    public String habilitarUsuario(@RequestParam Long idUsuario,
                                   @RequestParam(required = false) String origen,
                                   RedirectAttributes redirectAttributes) {
        userService.activarUsuarioPorId(idUsuario);
        redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Usuario habilitado.");
        redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);

        if ("jefes".equals(origen)) {
            return VISTA_JEFES;
        } else {

            return VISTA_MIEMBROS;
        }

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

    @GetMapping("/terminos-condiciones")
    public String mostrarTerminosCondiciones(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (rol == null) {
            return VISTA_LOGIN;
        }

        String userType;
        if (session.getAttribute("rol").equals(Rol.ADULTO.name())) {
            userType = "admin";
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelJefe);
        } else if (session.getAttribute("rol").equals(Rol.JOVEN.name())) {
            userType = "user";
            Persona sesionDelMiembro = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(ATRIBUTO_PERSONA, sesionDelMiembro);
        } else {
            return VISTA_ERROR;
        }

        model.addAttribute("userType", userType);
        return "user/terminosCondiciones";
    }
}
