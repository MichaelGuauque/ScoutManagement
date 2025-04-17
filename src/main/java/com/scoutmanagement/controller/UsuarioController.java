package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.DatosJWTtoken;
import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.DTO.UsuarioRegistroDTO;
import com.scoutmanagement.persistence.model.Usuario;
import com.scoutmanagement.service.implementation.TokenService;
import com.scoutmanagement.service.interfaces.IUsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity autenticarUsuario(@RequestBody @Valid UsuarioDTO autenticarUsuarioDTO){
        Authentication authToken = new UsernamePasswordAuthenticationToken(autenticarUsuarioDTO.username(),
                autenticarUsuarioDTO.password());
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(new DatosJWTtoken(JWTtoken));
    }
/*
    @PostMapping
    public ResponseEntity registrarUsuario(@RequestBody @Valid UsuarioRegistroDTO usuarioRegistroDTO) {
        logger.info(usuarioRegistroDTO.toString());
        usuarioService.save(usuarioRegistroDTO);
        return ResponseEntity.ok().build();
    }
*/
    @PostMapping()
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody UsuarioRegistroDTO dto) {

        Usuario usuario = usuarioService.cambiarRegistroUsuarioDTO(dto);
        logger.info(usuario.toString());
        return ResponseEntity.ok(usuario);
    }




}
