package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.DTO.UsuarioRegistroDTO;
import com.scoutmanagement.persistence.model.Usuario;

import java.util.Optional;

public interface IUsuarioService {
    void save(UsuarioRegistroDTO usuarioRegistroDTO);
    void update(Usuario usuario);
    Usuario cambiarRegistroUsuarioDTO(UsuarioRegistroDTO usuarioRegistroDTO);
    Usuario findById(Long id);

}
