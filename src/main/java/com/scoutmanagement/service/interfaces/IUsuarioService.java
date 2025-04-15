package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.UsuarioDTO;
import com.scoutmanagement.persistence.model.Usuario;

public interface IUsuarioService {
    void save(UsuarioDTO usuarioDTO);
    void update(Usuario usuario);
    Usuario cambiarRegistroUsuarioDTO(UsuarioDTO usuarioDTO);
    Usuario findById(Long id);
}
