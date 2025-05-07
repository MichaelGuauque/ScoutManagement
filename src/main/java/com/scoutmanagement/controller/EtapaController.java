package com.scoutmanagement.controller;

import com.scoutmanagement.dto.EtapaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.scoutmanagement.util.constants.AppConstants.*;

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
    @Autowired
    private IProgresoService progresoService;
    private final Logger logger = LoggerFactory.getLogger(EtapaController.class);

    private Persona adultoSession(String nombreSession, HttpSession session) {
        Optional<UserEntity> optionalUserEntity = userService.findById(Long.parseLong(session.getAttribute(nombreSession).toString()));
        UserEntity usuario = optionalUserEntity.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Persona> optionalPersona = personaService.findByUsuarioId(usuario.getId());
        Persona persona = optionalPersona.orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return persona;
    }

    private final String ID_USUARIO = "idUsuario";

    @GetMapping()
    public String progresiones(@RequestParam(name = "etapaSeleccionada", required = false) String etapaSeleccionada,
                               Model model, HttpSession session, @RequestParam(required = false) boolean cancelado) {

        if (Boolean.TRUE.equals(cancelado)) {
            model.addAttribute(EXCEPTION_MESSAGE, "Registro cancelado.");
            model.addAttribute("type", EXCEPTION_INFO);
        }
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {

            Persona sesionDelJefe = adultoSession(ID_USUARIO, session);
            model.addAttribute("persona", sesionDelJefe);

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
        return VISTA_ERROR;
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
        return VISTA_ERROR;
    }

    @PostMapping("/registrar")
    public String registrarEtapa(HttpSession session, @ModelAttribute @Valid EtapaDTO etapaDTO) {
        logger.info("Etapa ", etapaDTO);
        Object rol = session.getAttribute("rol");
        if (session.getAttribute("rol") == Rol.ADULTO.name()) {
            etapaService.save(etapaDTO);
            return null; //poner vista
        }
        if (rol == null) {
            return null; //poner vista
        }
        return VISTA_ERROR;
    }

    @GetMapping("/miProgreso")
    public String verMiProgreso(Model model, HttpSession session) {

        Object rol = session.getAttribute("rol");
        if (rol == null) {
            return "redirect:/";
        }

        if (rol.equals(Rol.ADULTO.name())) {
            return "redirect:/progresiones";
        }

        Persona persona = adultoSession(ID_USUARIO, session);

        Rama rama = persona.getRama();
        List<Etapa> etapas = etapaService.findAllByRama(rama);

        Map<Long, Float> progresoPorEtapa = progresoService.calcularProgresosPorEtapa(etapas, persona);
        Map<String, List<Reto>> retosPorEtapa = progresoService.prepararRetosPorEtapa(etapas);
        Map<String, Map<Long, Boolean>> estadoRetosPorEtapa = progresoService.calcularEstadoRetos(etapas, persona);

        Etapa etapaDestacada = progresoService.encontrarEtapaDestacada(etapas, progresoPorEtapa);
        float progresoMaximo = etapaDestacada != null ?
                progresoPorEtapa.getOrDefault(etapaDestacada.getId(), 0f) : 0f;

        model.addAttribute("persona", persona);
        model.addAttribute("etapas", etapas);
        model.addAttribute("progresoPorEtapa", progresoPorEtapa);
        model.addAttribute("retosPorEtapa", retosPorEtapa);
        model.addAttribute("estadoRetosPorEtapa", estadoRetosPorEtapa);
        model.addAttribute("etapaDestacada", etapaDestacada != null ? etapaDestacada.getNombre() : ""); // Para mostrar en título
        model.addAttribute("progresoDestacado", progresoMaximo);

        return "/progresiones/verProgresiones";
        }


}
