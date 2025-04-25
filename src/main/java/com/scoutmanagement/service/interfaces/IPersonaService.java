package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.Persona;

public interface IPersonaService {
    void save(PersonaRegistroDTO personaRegistroDTO);

    Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO);

}
