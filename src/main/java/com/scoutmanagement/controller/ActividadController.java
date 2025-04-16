package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.ActividadDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IActividadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/actividades")
public class ActividadController {

    private final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @Autowired
    private IActividadService actividadService;

    @GetMapping()
    public String actividades(Model model) {
        model.addAttribute("ramas", Rama.values());
        return "actividades";
    }

    @PostMapping("/crear")
    public String crearActividad(@RequestBody @Valid ActividadDTO actividadDTO) {
        logger.info("Creando actividad");
        logger.info("Esta es la actividad {}", actividadDTO);
        actividadService.crearActividad(actividadDTO);
        return "actividades";
    }




}
