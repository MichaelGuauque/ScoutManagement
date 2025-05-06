package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

import static com.scoutmanagement.util.constants.AppConstants.*;

@Controller
@RequestMapping("/miembro")
public class MiembroController {

    @Autowired
    private IPersonaService personaService;
    @Autowired
    private IAsistenciaService asistenciaService;
    private final Logger log = LoggerFactory.getLogger(MiembroController.class);

    @GetMapping()
    public String showMiembros(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.JOVEN.name()) {

            try{
                Persona personaSesion = personaService.personaModelSession("idUsuario", session);
                List<Persona> miembrosRama = asistenciaService.findPersonasByRama(personaSesion.getRama());
                model.addAttribute("persona", personaSesion);
                model.addAttribute("miembros", miembrosRama);
                return "miembros/miembrosRama";
            }catch(Exception e){

            }
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }
}
