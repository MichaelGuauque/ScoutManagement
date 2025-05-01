package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import static com.scoutmanagement.util.constants.AppConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/miembros")
public class PersonaController {

@Autowired
private IPersonaService personaService;

private static final String ID_USUARIO = "idUsuario";



    @GetMapping({ "", "/jefes" })
    public String miembrosYJefes(Model model, HttpSession session,
                                 @RequestParam(required = false) Boolean cancelado,
                                 HttpServletRequest request,
                                 @RequestParam(defaultValue = "activos") String tab) {
        Object rol = session.getAttribute("rol");
        String path = request.getRequestURI(); //

        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.ADULTO.name())) {
            if (path.endsWith("/jefes")) {
                Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
                model.addAttribute("persona", sesionDelJefe);
                session.setAttribute("miembro", "jefe");
                if (Boolean.TRUE.equals(cancelado)) {
                    model.addAttribute(EXCEPTION_MESSAGE, "Registro cancelado.");
                    model.addAttribute("type", EXCEPTION_INFO);
                }
                List<Persona> jefes = personaService.findJefes();

                List<Persona> jefesFiltrados = jefes.stream()
                        .filter(p -> {
                            boolean estado = p.getUserEntity().isActivo(); // Supongo que 'estado' es booleano
                            return "inactivos".equals(tab) ? !estado : estado; // Filtra por estado (activo o inactivo)
                        })
                        .collect(Collectors.toList());

                model.addAttribute("tab", tab);
                model.addAttribute("jefes", jefesFiltrados);

                return "miembros/consultarJefes";
            } else {
                session.setAttribute("miembro", "miembro");
                if (Boolean.TRUE.equals(cancelado)) {
                    model.addAttribute(EXCEPTION_MESSAGE, "Registro cancelado.");
                    model.addAttribute("type", EXCEPTION_INFO);
                }
                return "miembros/consultarMiembros";
            }
        }

        return VISTA_ERROR;
    }


}
