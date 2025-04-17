package com.scoutmanagement.controller;


import com.scoutmanagement.DTO.PersonaDTO;
import com.scoutmanagement.DTO.RegistroCompletoDTO;
import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.DTO.UsuarioRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.service.interfaces.IPersonaService;
import com.scoutmanagement.service.interfaces.IUsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/miembros")
public class PersonaController {

    @Autowired
    IPersonaService personaService;
    @Autowired
    IUsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping()
    public String Personas(Model model) {
        model.addAttribute("ramas", Rama.values());
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("generos", Genero.values());
        model.addAttribute("tipodedocumentos", TipoDeDocumento.values());
        return "VistaNotFound";
    }

    @PostMapping("/crear")
    public String crearMiembro(@RequestBody @Valid PersonaDTO personaDTO, UsuarioRegistroDTO usuarioRegistroDTO) {
        Usuario usuario= usuarioService.cambiarRegistroUsuarioDTO(usuarioRegistroDTO);
        logger.info("Creando miembro");
        logger.info("Esta es la Persona {}", personaDTO);
        personaDTO.usuario();
        usuarioService.save(usuarioRegistroDTO);
        personaService.save(personaDTO);
        return "VistaNotFound";

    }

    @PostMapping("/crearNuevo")
    public String crearMiembroNuevo(@RequestBody @Valid RegistroCompletoDTO dto) {
        logger.info("Creando miembro con DTO: {}", dto);

        // 1. Crear el usuario

        UsuarioRegistroDTO usuarioDTO = new UsuarioRegistroDTO(dto.username(), dto.rol());

        // 2. Crear el Usuario desde el DTO
        Usuario usuario = usuarioService.cambiarRegistroUsuarioDTO(usuarioDTO);


        // 3. Guardar el Usuario para que obtenga su ID
        usuarioService.save(usuarioDTO);


        // 2. Crear la persona
        PersonaDTO personaDTO = new PersonaDTO(
                dto.primerNombre(),
                dto.segundoNombre(),
                dto.primerApellido(),
                dto.segundoApellido(),
                dto.numeroDeDocumento(),
                dto.tipoDeDocumento(),
                dto.genero(),
                dto.rama(),
                dto.cargo(),
                usuario
        );
        personaService.save(personaDTO);

        return "VistaNotFound"; // o la vista que vos quieras retornar
    }













}
