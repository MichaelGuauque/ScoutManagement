package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    UserDetails findByUsername(String username);

}
