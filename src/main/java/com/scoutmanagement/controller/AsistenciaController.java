package com.scoutmanagement.controller;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(actividadId);
            model.addAttribute("asistencias", asistencias);
            model.addAttribute("actividadSeleccionada", actividadId);
            model.addAttribute("tabSeleccionada", tab);

            return "actividades/modalAsistencia";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }


    @PostMapping("/guardar/{actividadId}")
    public String guardarAsistencias(@PathVariable Long actividadId,
                                     @RequestParam Map<String, String> params,
                                     @RequestParam("tab") String tabSeleccionada, HttpSession session) {

        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            List<Asistencia> asistenciasExistentes = asistenciaService.findByActividadOrdenado(actividadId);

            Map<Long, Boolean> asistenciasPorMiembro = asistenciasExistentes.stream()
                    .collect(Collectors.toMap(
                            asistencia -> asistencia.getMiembro().getId(),
                            asistencia -> params.containsKey("asistio_" + asistencia.getMiembro().getId())
                    ));

            asistenciaService.registrarAsistenciasMasivas(actividadId, asistenciasPorMiembro);
            return "redirect:/actividades?tab=" + tabSeleccionada;
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @GetMapping
    public String mostrarActividades(@RequestParam(value = "tab", defaultValue = "activas") String tab,
                                     @RequestParam(value = "asistenciaActividadId", required = false) Long asistenciaActividadId,
                                     Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            List<Actividad> actividades = actividadService.findAllActividad();
            model.addAttribute("actividades", actividades);
            model.addAttribute("tabSeleccionada", tab);

            if (asistenciaActividadId != null) {
                List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(asistenciaActividadId);

                asistencias.forEach(a -> System.out.println(" - " + a.getMiembro().getPrimerNombre()));

                model.addAttribute("asistencias", asistencias);
                model.addAttribute("actividadSeleccionada", asistenciaActividadId);
            }

            return "actividades/vistaActividadesAdmin";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }
}