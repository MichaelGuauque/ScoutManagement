package com.scoutmanagement.controller;

import com.scoutmanagement.DTO.DatosJWTtoken;
import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.persistence.model.Usuario;
import com.scoutmanagement.service.implementation.TokenService;
import com.scoutmanagement.service.interfaces.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

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

    @PostMapping
    public ResponseEntity registrarUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        usuarioService.save(usuarioDTO);
        return ResponseEntity.ok().build();
    }
}
