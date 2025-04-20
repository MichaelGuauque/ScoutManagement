package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/miembros")
public class PersonaController {

    @Autowired
    IPersonaService personaService;

    private final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping()
    public String Miembros(Model model) {
        return "miembros/consultarMiembros";
    }

    @GetMapping("/jefes")
    public String Jefes(Model model) {
        return "miembros/consultarJefes";
    }


}
