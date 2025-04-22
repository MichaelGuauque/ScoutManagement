package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.RetoDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.service.interfaces.IRetoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reto")
public class RetoController {

    @Autowired
    private IRetoService retoService;
    private final Logger logger = LoggerFactory.getLogger(RetoController.class);

    @GetMapping("/registrar")
    public String showRegistrarReto(HttpSession session) {
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
    public String registrarReto(@ModelAttribute @Valid RetoDTO retoDTO, HttpSession session) {
        logger.info("Registrar reto {}", retoDTO);
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            retoService.save(retoDTO);
            return null; //poner vista
        }
        if (rol == null) {
            return null; //poner vista
        }
        return "error/pageNotFound";
    }


}
