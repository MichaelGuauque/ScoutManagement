package com.scoutmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scoutmanagement.dto.EtapaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private IObtencionService obtencionService;
    private final Logger logger = LoggerFactory.getLogger(EtapaController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, Integer> GRUPOS_RAMAS = Map.of(
            "MANADA", 30,
            "TROPA", 31,
            "COMUNIDAD", 32,
            "CLAN", 33
    );

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
            model.addAttribute(PERSONA, sesionDelJefe);

            Rama rama = sesionDelJefe.getRama();
            List<Etapa> etapas = etapaService.findAllByRama(rama);
            model.addAttribute(ETAPAS, etapas);

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
    public String verProgreso(Model model, HttpSession session) {

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

        String progresoJson = convertirProgresoAJson(progresoPorEtapa);
        model.addAttribute("progresoJson", progresoJson);

        Map<String, Map<Long, Boolean>> estadoRetosPorEtapa = progresoService.calcularEstadoRetos(etapas, persona);
        Map<String, List<Reto>> retosPorEtapa = progresoService.prepararRetosPorEtapa(etapas, estadoRetosPorEtapa);
        Set<Long> etapasObtenidas = obtencionService.findIdEtapasObtenidasByPersona(persona);

        Etapa etapaDestacada = etapas.isEmpty() ? null : etapas.get(0);
        float progresoMaximo = etapaDestacada != null ?
                progresoPorEtapa.getOrDefault(etapaDestacada.getId(), 0f) : 0f;

        model.addAttribute(PERSONA, persona);
        model.addAttribute(ETAPAS, etapas);
        model.addAttribute("progresoPorEtapa", progresoPorEtapa);
        model.addAttribute("retosPorEtapa", retosPorEtapa);
        model.addAttribute("estadoRetosPorEtapa", estadoRetosPorEtapa);
        model.addAttribute("etapaDestacada", etapaDestacada != null ? etapaDestacada.getNombre() : "");
        model.addAttribute("progresoDestacado", progresoMaximo);
        model.addAttribute("etapasObtenidas", etapasObtenidas);
        model.addAttribute("gruposRamas", GRUPOS_RAMAS);

        return "progresiones/verProgresiones";
    }

    @GetMapping("/miembros")
    public String progresionMiembros(Model model, HttpSession session) {
        Object rol = session.getAttribute("rol");

        if (session.getAttribute("rol") == Rol.ADULTO.name()) {

            Persona sesionDelJefe = personaService.personaModelSession(ID_USUARIO, session);
            model.addAttribute(PERSONA, sesionDelJefe);
            Rama rama = sesionDelJefe.getRama();
            Map<Long, Etapa> progresionesPorPersona = new HashMap<>();
            List<Persona> miembros = personaService.findMiembrosByRama(rama);

            for (Persona persona : miembros) {
                Optional<Obtencion> obtencion = obtencionService.findByPersona(persona);

                if (obtencion.isEmpty()) {
                    progresionesPorPersona.put(persona.getId(), null);
                } else {
                    progresionesPorPersona.put(persona.getId(), obtencion.get().getEtapa());
                }
            }

            model.addAttribute("miembros", miembros);
            model.addAttribute("progresionesPorPersona", progresionesPorPersona);
            return "progresiones/progresoMiembros";

        }
        if (rol == null) {
            return VISTA_LOGIN;
        }
        return VISTA_ERROR;

    }

    @GetMapping("/progreso/{id}")
    public String progresoMiembro(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        Object rol = session.getAttribute("rol");
        if (rol == null) {
            return VISTA_LOGIN;
        }

        if (rol.equals(Rol.JOVEN.name())) {
            return VISTA_ERROR;
        }
        Persona persona = adultoSession(ID_USUARIO, session);

        try {
            Persona miembro = personaService.findByUsuarioId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Miembro no encontrado"));

            Rama rama = persona.getRama();
            List<Etapa> etapas = etapaService.findAllByRama(rama);
            Map<Long, Float> progresoPorEtapa = progresoService.calcularProgresosPorEtapa(etapas, miembro);

            String progresoJson = convertirProgresoAJson(progresoPorEtapa);
            model.addAttribute("progresoJson", progresoJson);

            Map<String, Map<Long, Boolean>> estadoRetosPorEtapa = progresoService.calcularEstadoRetos(etapas, miembro);
            Map<String, List<Reto>> retosPorEtapa = progresoService.prepararRetosPorEtapa(etapas, estadoRetosPorEtapa);
            Set<Long> etapasObtenidas = obtencionService.findIdEtapasObtenidasByPersona(miembro);
            Etapa etapaDestacada = etapas.isEmpty() ? null : etapas.getFirst();

            model.addAttribute(PERSONA, persona);
            model.addAttribute("miembro", miembro);
            model.addAttribute(ETAPAS, etapas);
            model.addAttribute("progresoPorEtapa", progresoPorEtapa);
            model.addAttribute("retosPorEtapa", retosPorEtapa);
            model.addAttribute("estadoRetosPorEtapa", estadoRetosPorEtapa);
            model.addAttribute("etapasObtenidas", etapasObtenidas);
            model.addAttribute("etapaDestacada", etapaDestacada != null ? etapaDestacada.getNombre() : "");
            model.addAttribute("gruposRamas", GRUPOS_RAMAS);

            return "progresiones/progreso";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(EXCEPTION_MESSAGE, "Miembro no encontrado");
            redirectAttributes.addFlashAttribute("type", EXCEPTION_ERROR);
            return "redirect:/progresiones/miembros";
        }

    }

    private String convertirProgresoAJson(Map<Long, Float> progresoPorEtapa) {
        try {
            return objectMapper.writeValueAsString(progresoPorEtapa);
        } catch (Exception e) {
            return "{}";
        }
    }

}
