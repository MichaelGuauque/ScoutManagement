package com.scoutmanagement.controller;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rol;
import static com.scoutmanagement.util.constants.AppConstants.*;
import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.util.exception.ServiceException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired
    private IAsistenciaService asistenciaService;

    @Autowired
    private IActividadService actividadService;

    @GetMapping("/modal/{actividadId}")
    public String mostrarModalAsistencia(@PathVariable Long actividadId,
                                         @RequestParam("tab") String tab,
                                         Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            try {
                Optional<Actividad> actividadOptional = actividadService.findById(actividadId);
                if (!actividadOptional.isPresent()) {
                    model.addAttribute(EXCEPTION_MESSAGE, "La actividad solicitada no existe");
                    model.addAttribute("type", EXCEPTION_ERROR);
                    return VISTA_ERROR;
                }

                Actividad actividad = actividadOptional.get();

                List<Persona> miembrosRama = asistenciaService.findPersonasByRama(actividad.getRama());

                List<Asistencia> asistenciasDisplay = asistenciaService.prepararRegistroAsistencias(actividad, miembrosRama);

                model.addAttribute("asistencias", asistenciasDisplay);
                model.addAttribute("actividadSeleccionada", actividadId);
                model.addAttribute("tabSeleccionada", tab);

                return "actividades/modalAsistencia";
            } catch (ServiceException e) {
                model.addAttribute(EXCEPTION_MESSAGE, e.getMessage());
                model.addAttribute("type", EXCEPTION_ERROR);
                return VISTA_ERROR;
            }
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        model.addAttribute(EXCEPTION_MESSAGE, "No tiene permisos para acceder a esta función");
        model.addAttribute("type", EXCEPTION_ERROR);
        return VISTA_ERROR;
    }

    @PostMapping("/guardar/{actividadId}")
    public String guardarAsistencias(@PathVariable Long actividadId,
                                     @RequestParam Map<String, String> params,
                                     @RequestParam("tab") String tabSeleccionada,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) {

        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            try {
                Optional<Actividad> actividadOptional = actividadService.findById(actividadId);
                if (!actividadOptional.isPresent()) {
                    redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "La actividad solicitada no existe");
                    redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                    return VISTA_ACTIVIDADES_TAB + tabSeleccionada;
                }

                Actividad actividad = actividadOptional.get();

                List<Persona> miembrosRama = asistenciaService.findPersonasByRama(actividad.getRama());

                Map<Long, Boolean> asistenciasPorMiembro = new HashMap<>();

                for (Persona miembro : miembrosRama) {
                    boolean asistio = params.containsKey("asistio_" + miembro.getId());
                    asistenciasPorMiembro.put(miembro.getId(), asistio);
                }

                asistenciaService.registrarAsistenciasMasivas(actividadId, asistenciasPorMiembro);

                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Registro de asistencias guardado correctamente");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
                return VISTA_ACTIVIDADES_TAB + tabSeleccionada;
            } catch (ServiceException e) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return VISTA_ACTIVIDADES_TAB + tabSeleccionada;
            }
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "No tiene permisos para acceder a esta función");
        redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
        return VISTA_ACTIVIDADES_TAB + tabSeleccionada;
    }

    @GetMapping
    public String mostrarActividades(@RequestParam(value = "tab", defaultValue = "activas") String tab,
                                     @RequestParam(value = "asistenciaActividadId", required = false) Long asistenciaActividadId,
                                     Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            try {
                List<Actividad> actividades = actividadService.findAllActividad();
                model.addAttribute("actividades", actividades);
                model.addAttribute("tabSeleccionada", tab);

                if (asistenciaActividadId != null) {

                    Optional<Actividad> actividadOptional = actividadService.findById(asistenciaActividadId);
                    if (actividadOptional.isPresent()) {
                        Actividad actividad = actividadOptional.get();

                        List<Persona> miembrosRama = asistenciaService.findPersonasByRama(actividad.getRama());

                        List<Asistencia> asistenciasDisplay = asistenciaService.prepararRegistroAsistencias(actividad, miembrosRama);

                        model.addAttribute("asistencias", asistenciasDisplay);
                        model.addAttribute("actividadSeleccionada", asistenciaActividadId);
                    }
                }

                return "actividades/vistaActividadesAdmin";
            } catch (ServiceException e) {
                model.addAttribute(EXCEPTION_MESSAGE, e.getMessage());
                model.addAttribute("type", EXCEPTION_ERROR);
                return "actividades/vistaActividadesAdmin";
            }
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        model.addAttribute(EXCEPTION_MESSAGE, "No tiene permisos para acceder a esta función");
        model.addAttribute("type", EXCEPTION_ERROR);
        return VISTA_ERROR;
    }
}