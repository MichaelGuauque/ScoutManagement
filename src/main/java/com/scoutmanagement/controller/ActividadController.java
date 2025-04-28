package com.scoutmanagement.controller;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.*;

import static com.scoutmanagement.util.constants.AppConstants.*;

import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/actividades")
public class ActividadController {

    @Autowired
    private IActividadService actividadService;

    @Autowired
    private IAsistenciaService asistenciaService;

    @Autowired
    private IPersonaService personaService;

    private final String ID_USUARIO = "idUsuario";

    @GetMapping
    public String actividades(Model model, HttpSession session,
                              @RequestParam(required = false, defaultValue = "proximas") String tab,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size,
                              @RequestParam(value = "asistenciaActividadId", required = false) Long asistenciaActividadId,
                              @RequestParam(required = false) Boolean cancelado,
                              @RequestParam(required = false, defaultValue = "") Rama ramaSeleccionada) {

        if (Boolean.TRUE.equals(cancelado)) {
            model.addAttribute(EXCEPTION_MESSAGE, "Acción cancelada.");
            model.addAttribute("type", EXCEPTION_INFO);
        }

        Object rol = session.getAttribute("rol");
        if (Rol.ADULTO.name().equals(rol)) {

            Persona jefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute("persona", jefe);

            List<Actividad> actividades = actividadService.findAllActividadesOrdenadas();
            LocalDate hoy = LocalDate.now();

            List<Actividad> actividadesFiltradas = actividadService.filtrarYOrdenarActividadesPorTab(actividades, ramaSeleccionada, tab, hoy);
            int totalActividades = actividadesFiltradas.size();
            int totalPaginas = Math.max((int) Math.ceil((double) totalActividades / size), 1);

            List<Actividad> paginaActividades = actividadService.paginarActividades(actividadesFiltradas, page, size);
            Map<Long, Boolean> actividadEsMasProxima = actividadService.encontrarActividadMasProxima(paginaActividades, page, tab);

            if (asistenciaActividadId != null) {
                List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(asistenciaActividadId);
                model.addAttribute("asistencias", asistencias);
                model.addAttribute("actividadSeleccionada", asistenciaActividadId);
            }

            model.addAttribute("actividades", paginaActividades);
            model.addAttribute("actividadEsMasProxima", actividadEsMasProxima);
            model.addAttribute("paginaActual", page);
            model.addAttribute("totalPaginas", totalPaginas);
            model.addAttribute("tabSeleccionada", tab);
            model.addAttribute("ramaSeleccionada", ramaSeleccionada);
            model.addAttribute("ramas", Rama.values());

            return "actividades/vistaActividadesAdmin";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }



    @GetMapping("/crear")
    public String crearActividadFormulario(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute("persona", sesionDelJefe);
            model.addAttribute("ramas", Rama.values());
            return "actividades/vistaCrearActividad";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @PostMapping("/crear")
    public String crearActividad(@Valid ActividadDTO actividadDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Object rol = session.getAttribute("rol");

        if (rol == null) {
            return VISTA_LOGIN;
        }
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            try {
                actividadService.crearActividad(actividadDTO);
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Actividad creada con éxito.");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
                return "redirect:/actividades";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return "redirect:/actividades";

            }
        }
        return VISTA_ERROR;
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, @RequestParam String tab, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            actividadService.eliminarActividad(id);
            return "redirect:/actividades?tab=" + tab;
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

}
