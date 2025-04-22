package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.EtapaDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.service.interfaces.IEtapaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/etapa")
public class EtapaController {

    @Autowired
    private IEtapaService etapaService;
    private final Logger logger = LoggerFactory.getLogger(EtapaController.class);


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
