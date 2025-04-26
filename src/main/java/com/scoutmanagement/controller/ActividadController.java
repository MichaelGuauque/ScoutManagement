package com.scoutmanagement.controller;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.*;

import static com.scoutmanagement.util.constants.AppConstants.*;

import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/actividades")
public class ActividadController {

    @Autowired
    private IActividadService actividadService;

    @Autowired
    private IAsistenciaService asistenciaService;


    @GetMapping()
    public String actividades(Model model, HttpSession session,
                              @RequestParam(required = false, defaultValue = "proximas") String tab,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size,
                              @RequestParam(value = "asistenciaActividadId", required = false) Long asistenciaActividadId,
                              @RequestParam(required = false) Boolean cancelado) {

        if (Boolean.TRUE.equals(cancelado)) {
            model.addAttribute("message", "Acción cancelada.");
            model.addAttribute("type", "info");
        }

        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            List<Actividad> listaActividades = actividadService.findAllActividadesOrdenadas();
            LocalDate hoy = LocalDate.now();
            List<Actividad> actividadesFiltradas;

            if (tab.equals("pasadas")) {
                actividadesFiltradas = listaActividades.stream()
                        .filter(actividad -> actividad.getFecha().isBefore(hoy))
                        .collect(Collectors.toList());
            } else {
                actividadesFiltradas = listaActividades.stream()
                        .filter(actividad -> !actividad.getFecha().isBefore(hoy)) // hoy o después
                        .collect(Collectors.toList());
            }

            int totalActividades = actividadesFiltradas.size();
            int totalPaginas = (int) Math.ceil((double) totalActividades / size);
            if (totalPaginas == 0) {
                totalPaginas = 1;
            }

            int paginaActual = page;
            List<Actividad> paginaActividades = actividadesFiltradas.stream()
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());

            Map<Long, Boolean> actividadEsMasProxima = new HashMap<>();
            if (tab.equals("proximas")) {
                paginaActividades.stream()
                        .min(Comparator.comparing(actividad -> actividad.getFecha()))
                        .ifPresent(actividad -> actividadEsMasProxima.put(actividad.getId(), true));
            }

            if (asistenciaActividadId != null) {
                List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(asistenciaActividadId);
                model.addAttribute("asistencias", asistencias);
                model.addAttribute("actividadSeleccionada", asistenciaActividadId);
            }

            model.addAttribute("actividades", paginaActividades);
            model.addAttribute("actividadEsMasProxima", actividadEsMasProxima);
            model.addAttribute("paginaActual", paginaActual);
            model.addAttribute("totalPaginas", totalPaginas);
            model.addAttribute("tabSeleccionada", tab);

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
                redirectAttributes.addFlashAttribute("message", "Actividad creada con éxito.");
                redirectAttributes.addFlashAttribute("type", "success");
                return "redirect:/actividades";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                redirectAttributes.addFlashAttribute("type", "error");
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
