package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.service.interfaces.IActividadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/actividades")
public class ActividadController {

    private final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @Autowired
    private IActividadService actividadService;

    @GetMapping()
    public String actividades(Model model,@RequestParam(required = false, defaultValue = "proximas") String tab) {
        List<Actividad> listaActividades = actividadService.findAllActividad();
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

        model.addAttribute("tabSeleccionada", tab);
        model.addAttribute("actividades", actividadesFiltradas);
        return "actividades/vistaActividadesAdmin";
    }

    @GetMapping("/crear")
    public String crearActividadFormulario(Model model) {
        model.addAttribute("ramas", Rama.values());
        return "actividades/vistaCrearActividad";
    }

    @PostMapping("/crear")
    public String crearActividad(@Valid ActividadDTO actividadDTO) {
        logger.info("Creando actividad");
        logger.info("Esta es la actividad {}", actividadDTO);
        actividadService.crearActividad(actividadDTO);
        return "redirect:/actividades";
    }
}
