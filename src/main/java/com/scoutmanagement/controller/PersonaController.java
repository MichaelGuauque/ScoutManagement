package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
import static com.scoutmanagement.util.constants.AppConstants.*;
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

    @GetMapping()
    public String miembros(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "miembros/consultarMiembros";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @GetMapping("/jefes")
    public String jefes(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return "miembros/consultarJefes";
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }


}
