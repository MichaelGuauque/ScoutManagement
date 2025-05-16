package com.scoutmanagement.controller;


import com.scoutmanagement.dto.PersonaActualizacionDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static com.scoutmanagement.util.constants.AppConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/miembros")
public class PersonaController {

    @Autowired
    private IPersonaService personaService;

    private static final String ID_USUARIO = "idUsuario";


    @GetMapping({"", "/jefes"})
    public String miembrosYJefes(Model model, HttpSession session,
                                 @RequestParam(required = false) Boolean cancelado,
                                 HttpServletRequest request,
                                 @RequestParam(defaultValue = "activos") String tab) {
        Object rol = session.getAttribute("rol");
        String path = request.getRequestURI();

        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.ADULTO.name())) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute("persona", sesionDelJefe);
            if (path.endsWith("/jefes")) {

                session.setAttribute("miembro", "jefe");
                if (Boolean.TRUE.equals(cancelado)) {
                    model.addAttribute(EXCEPTION_MESSAGE, ACCION_CANCELADA);
                    model.addAttribute("type", EXCEPTION_INFO);
                }
                List<Persona> jefes = personaService.findJefes();
                List<Persona> jefesFiltrados = personaService.filtrarYOrdenarPorEstado(jefes, tab);
                model.addAttribute("tab", tab);
                model.addAttribute("jefes", jefesFiltrados);

                return "miembros/consultarJefes";
            } else {
                session.setAttribute("miembro", "miembro");
                if (Boolean.TRUE.equals(cancelado)) {
                    model.addAttribute(EXCEPTION_MESSAGE, ACCION_CANCELADA);
                    model.addAttribute("type", EXCEPTION_INFO);

                }
                List<Persona> miembros = personaService.findMiembros();

                List<Persona> miembrosFiltrados = personaService.filtrarYOrdenarPorEstado(miembros, tab);

                model.addAttribute("tab", tab);
                model.addAttribute("miembros", miembrosFiltrados);
                return "miembros/consultarMiembros";
            }

        }

        return VISTA_ERROR;
    }

    @GetMapping("/modificarMiembro/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable Long id, Model model,
                                             HttpSession session, RedirectAttributes redirectAttributes,
                                             @RequestParam(required = false) String origen) {
        try {
            Persona persona = personaService.findByUsuarioId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

            String nombreRol = persona.getUserEntity()
                    .getRoles()
                    .stream()
                    .findFirst()
                    .map(roleEntity -> roleEntity.getRole().name())
                    .orElseThrow(() -> new IllegalStateException("El usuario no tiene roles asignados"));

            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            prepararModeloDeModificacion(model, sesionDelJefe, persona, nombreRol);
            if (origen != null) {
                session.setAttribute("miembro", origen);
            }
            return "user/modificarMiembro";

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("EXCEPTION_MESSAGE", e.getMessage());
            redirectAttributes.addFlashAttribute("type", "error");
            return VISTA_MIEMBROS;
        } catch (IllegalStateException e) {
            model.addAttribute(EXCEPTION_MESSAGE, e.getMessage());
            return VISTA_ERROR;
        }
    }

    @PostMapping("/actualizarMiembro/{id}")
    public String actualizarPersona(@PathVariable Long id,
                                    @ModelAttribute PersonaActualizacionDTO personaActualizacionDTO,
                                    RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            personaService.actualizarPersona(id, personaActualizacionDTO);
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Usuario actualizado con éxito");
            redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
            return redireccionSegunTipo(session);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Persona no encontrada");
            return redireccionSegunTipo(session);
        } catch (DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute("errorDocumento", true);
            redirectAttributes.addFlashAttribute("documentoIngresado", personaActualizacionDTO.getNumeroDeDocumento());
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
            return "redirect:/miembros/modificarMiembro/" + id;
        }
    }


    private void prepararModeloDeModificacion(Model model, Persona sesionDelJefe, Persona personaModificada, String nombreRol) {
        model.addAttribute("persona", sesionDelJefe);
        model.addAttribute("rolSeleccionado", nombreRol);
        model.addAttribute("ramas", Rama.values());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("tiposDeDocumento", TipoDeDocumento.values());
        model.addAttribute("personaModificada", personaModificada);
    }
    private String redireccionSegunTipo(HttpSession session) {
        String tipoMiembro = (String) session.getAttribute("miembro");
        return "jefe".equals(tipoMiembro) ? VISTA_JEFES : VISTA_MIEMBROS;
    }
}
