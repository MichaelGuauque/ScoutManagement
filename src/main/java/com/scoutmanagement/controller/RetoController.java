package com.scoutmanagement.controller;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IEtapaService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IRetoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.scoutmanagement.util.constants.AppConstants.*;

@Controller
@RequestMapping("/retos")
public class RetoController {

    @Autowired
    private IRetoService retoService;
    @Autowired
    private IEtapaService etapaService;
    @Autowired
    private IPersonaService personaService;

    private static final String ID_USUARIO = "idUsuario";

    @GetMapping("/registrar")
    public String showRegistrarReto(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {

            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute("persona", sesionDelJefe);

            Rama rama = sesionDelJefe.getRama();

            List<Etapa> etapas = etapaService.findAllByRama(rama);
            model.addAttribute("etapas", etapas);

            return VISTA_PROGRESIONES;
        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;
    }

    @PostMapping("/registrar")
    public String registrarReto(@ModelAttribute @Valid RetoDTO retoDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Object rol = session.getAttribute("rol");

        if (rol == null) {
            return null;
        }
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            try {
                retoService.save(retoDTO);
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Reto creado con éxito.");
                redirectAttributes.addFlashAttribute("type", EXCEPTION_SUCCESS);
                return VISTA_PROGRESIONES;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, e.getMessage());
                redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
                return VISTA_PROGRESIONES;
            }
        }
        return VISTA_ERROR;
    }


}
