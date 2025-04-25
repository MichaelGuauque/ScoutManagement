package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.UserEntity;

public interface IPersonaService {
    void save(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);

    Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);

}
