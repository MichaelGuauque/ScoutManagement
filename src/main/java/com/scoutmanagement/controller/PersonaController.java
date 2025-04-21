package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
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
    public String Miembros(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "miembros/consultarMiembros";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @GetMapping("/jefes")
    public String Jefes(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "miembros/consultarJefes";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }


}
