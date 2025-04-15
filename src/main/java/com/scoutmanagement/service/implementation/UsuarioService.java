package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.persistence.model.Usuario;
import com.scoutmanagement.persistence.repository.UsuarioRepository;
import com.scoutmanagement.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(UsuarioDTO usuarioDTO) {
        usuarioRepository.save(cambiarRegistroUsuarioDTO(usuarioDTO));
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario cambiarRegistroUsuarioDTO(UsuarioDTO usuarioDTO) {
        String contraseniaEncriptada = bCryptPasswordEncoder.encode(usuarioDTO.password());
        return new Usuario(null,
                usuarioDTO.username(),
                contraseniaEncriptada);
    }

    @Override
    public Usuario findById(Long id) {
        Optional<Usuario> opcionalUsuario = usuarioRepository.findById(id);
        if (opcionalUsuario.isPresent()) {
            return opcionalUsuario.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }
}