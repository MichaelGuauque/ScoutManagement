package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.DTO.UsuarioRegistroDTO;
import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.Usuario;
import com.scoutmanagement.persistence.repository.UsuarioRepository;
import com.scoutmanagement.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(UsuarioRegistroDTO usuarioRegistroDTO) {
        usuarioRepository.save(cambiarRegistroUsuarioDTO(usuarioRegistroDTO));
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario cambiarRegistroUsuarioDTO(UsuarioRegistroDTO usuarioRegistroDTO) {
        //Generador del metodo pw
        String randomPassword = UUID.randomUUID().toString().substring(0, 10);;
        System.out.println("Contraseña generada (sin encriptar): " + randomPassword);
        String contraseniaEncriptada = bCryptPasswordEncoder.encode(randomPassword);
        return new Usuario(
                usuarioRegistroDTO.username(),
                contraseniaEncriptada,
                usuarioRegistroDTO.rol());
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