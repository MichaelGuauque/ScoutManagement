package com.scoutmanagement.controller;


import com.scoutmanagement.persistence.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import static com.scoutmanagement.util.constants.AppConstants.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/miembros")
public class PersonaController {



    @GetMapping({ "", "/jefes" })
    public String miembrosYJefes(Model model, HttpSession session,
                                 @RequestParam(required = false) Boolean cancelado,
                                 HttpServletRequest request) {
        Object rol = session.getAttribute("rol");
        String path = request.getRequestURI(); //

        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.ADULTO.name())) {
            if (path.endsWith("/jefes")) {
                session.setAttribute("miembro", "jefe");
                if (Boolean.TRUE.equals(cancelado)) {
                    model.addAttribute(EXCEPTION_MESSAGE, "Registro cancelado.");
                    model.addAttribute("type", EXCEPTION_INFO);
                }
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
