package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.RetoDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IEtapaService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IRetoService;
import com.scoutmanagement.service.interfaces.IUserEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/retos")
public class RetoController {

    @Autowired
    private IRetoService retoService;
    @Autowired
    private IEtapaService etapaService;
    @Autowired
    private IUserEntity userService;
    @Autowired
    private IPersonaService personaService;

    private final Logger logger = LoggerFactory.getLogger(RetoController.class);

    private final String ID_USUARIO = "idUsuario";

    @GetMapping("/registrar")
    public String showRegistrarReto(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {

            //el metodo para buscar persona con el idUser de la sesion
            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute("persona", sesionDelJefe);
            logger.info("Persona: " + sesionDelJefe.getPrimerNombre() + " " + sesionDelJefe.getPrimerApellido());

            Rama rama = sesionDelJefe.getRama();

            List<Etapa> etapas = etapaService.findAllByRama(rama);
            model.addAttribute("etapas", etapas);

            return "redirect:/progresiones";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }

    @PostMapping("/registrar")
    public String registrarReto(@ModelAttribute @Valid RetoDTO retoDTO, HttpSession session) {
        logger.info("Registrar reto {}", retoDTO);
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            retoService.save(retoDTO);
            return "redirect:/progresiones"; //poner vista
        }
        if (rol == null) {
            return null; //poner vista
        }
        return "error/pageNotFound";
    }


}
