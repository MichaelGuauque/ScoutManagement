package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.UserEntity;

import java.util.List;

public interface IPersonaService {
    void save(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);

    Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);
    boolean existsByNumeroDeDocumento(Long numeroDeDocumento);
    List<Persona> findJefes();


}
