package com.scoutmanagement.controller;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
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

    // ✅ Modal individual (puedes usarlo con AJAX o testing directo)
    @GetMapping("/modal/{actividadId}")
    public String mostrarModalAsistencia(@PathVariable Long actividadId,
                                         @RequestParam("tab") String tab,
                                         Model model) {
        System.out.println("🟢 Entrando a mostrarModalAsistencia: actividadId=" + actividadId + ", tab=" + tab);

        List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(actividadId);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("actividadSeleccionada", actividadId);
        model.addAttribute("tabSeleccionada", tab);

        // Cambia esta línea para que coincida con la ubicación real de la plantilla
        return "actividades/modalAsistencia"; // Antes era "asistencias/modalAsistencia"
    }


    // ✅ Guardar asistencia y redirigir
    @PostMapping("/guardar/{actividadId}")
    public String guardarAsistencias(@PathVariable Long actividadId,
                                     @RequestParam Map<String, String> params,
                                     @RequestParam("tab") String tabSeleccionada) {

        List<Asistencia> asistenciasExistentes = asistenciaService.findByActividadOrdenado(actividadId);

        Map<Long, Boolean> asistenciasPorMiembro = asistenciasExistentes.stream()
                .collect(Collectors.toMap(
                        asistencia -> asistencia.getMiembro().getId(),
                        asistencia -> params.containsKey("asistio_" + asistencia.getMiembro().getId())
                ));

        asistenciaService.registrarAsistenciasMasivas(actividadId, asistenciasPorMiembro);
        return "redirect:/actividades?tab=" + tabSeleccionada;
    }

    // ✅ Vista principal (actividades) que también carga el modal si corresponde
    @GetMapping
    public String mostrarActividades(@RequestParam(value = "tab", defaultValue = "activas") String tab,
                                     @RequestParam(value = "asistenciaActividadId", required = false) Long asistenciaActividadId,
                                     Model model) {
        List<Actividad> actividades = actividadService.findAllActividad();
        model.addAttribute("actividades", actividades);
        model.addAttribute("tabSeleccionada", tab);

        // ✅ Cargar asistencias si se solicitó mostrar el modal
        if (asistenciaActividadId != null) {
            List<Asistencia> asistencias = asistenciaService.findByActividadOrdenado(asistenciaActividadId);

            // 🔍 Debug temporal (puedes quitar después)
            System.out.println("Cargando asistencias para actividad " + asistenciaActividadId);
            asistencias.forEach(a -> System.out.println(" - " + a.getMiembro().getPrimerNombre()));

            model.addAttribute("asistencias", asistencias);
            model.addAttribute("actividadSeleccionada", asistenciaActividadId);
        }

        return "actividades/vistaActividadesAdmin"; // 👈 asegúrate que este sea el nombre real de la vista
    }
}