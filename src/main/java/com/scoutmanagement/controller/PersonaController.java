package com.scoutmanagement.controller;


import com.scoutmanagement.DTO.PersonaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/miembros")
public class PersonaController {

    @Autowired
    IPersonaService personaService;

    private final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping()
    public String Personas(Model model) {
        model.addAttribute("ramas", Rama.values());
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("generos", Genero.values());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("tipodedocumentos", TipoDeDocumento.values());
        return "VistaNotFound";
    }

    @PostMapping("/crear")
    public String crearMiembro(@RequestBody @Valid PersonaDTO personaDTO) {

        logger.info("Creando miembro");
        logger.info("Esta es la Persona {}", personaDTO);
        personaService.save(personaDTO);
        return "VistaNotFound";

    }
}
