package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.EtapaDTO;
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

import java.util.*;

@Controller
@RequestMapping("/progresiones")
public class EtapaController {

    @Autowired
    private IEtapaService etapaService;
    @Autowired
    private IRetoService retoService;
    @Autowired
    private IUserEntity userService;
    @Autowired
    private IPersonaService personaService;
    private final Logger logger = LoggerFactory.getLogger(EtapaController.class);

    private Persona adultoSession(String nombreSession, HttpSession session) {
        Optional<UserEntity> optionalUserEntity = userService.findById(Long.parseLong(session.getAttribute(nombreSession).toString()));
        UserEntity usuario = optionalUserEntity.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Persona> optionalPersona = personaService.findByUsuario_Id(usuario.getId());
        Persona persona = optionalPersona.orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return persona;
    }

    private final String ID_USUARIO = "idUsuario";

    @GetMapping()
    public String progresiones (@RequestParam(name = "etapaSeleccionada", required = false) String etapaSeleccionada, Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {

            Persona sesionDelJefe = adultoSession(ID_USUARIO,session);
            model.addAttribute("persona", sesionDelJefe);
            logger.info("Persona: " + sesionDelJefe.getPrimerNombre() + " " + sesionDelJefe.getPrimerApellido());

            Rama rama = sesionDelJefe.getRama();
            List<Etapa> etapas = etapaService.findAllByRama(rama);
            model.addAttribute("etapas", etapas);

            Etapa etapaSeleccionadaObj = etapas.stream()
                    .filter(etapa -> etapa.getNombre().equals(etapaSeleccionada))
                    .findFirst()
                    .orElse(etapas.get(0)); // Si no hay etapa seleccionada, tomar la primera.

            if (etapaSeleccionadaObj != null) {
                List<Reto> retos = retoService.findAllRetosEtapa(etapaSeleccionadaObj);
                model.addAttribute("retos", retos);
            } else {
                model.addAttribute("retos", new ArrayList<>()); // Si no hay retos, asignamos una lista vacía
            }
            model.addAttribute("etapaSeleccionada", etapaSeleccionada != null ? etapaSeleccionada : etapas.get(0).getNombre());

            return "retos/gestionarRetos";
        }
        if (rol == null) {
            return "redirect:/";
        }
        return "error/pageNotFound";
    }


    @GetMapping("/registrar")
    public String showRegistrarEtapa(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            return null; //poner vista
        }
        if (rol == null) {
            return null; //poner vista
        }
        return "error/pageNotFound";
    }

    @PostMapping("/registrar")
    public String registrarEtapa(HttpSession session, @ModelAttribute @Valid EtapaDTO etapaDTO) {
        logger.info("Etapa ",etapaDTO);
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            etapaService.save(etapaDTO);
            return null; //poner vista
        }
        if (rol == null) {
            return null; //poner vista
        }
        return "error/pageNotFound";
    }


}
