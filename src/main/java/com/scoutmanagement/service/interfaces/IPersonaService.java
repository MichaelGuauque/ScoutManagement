package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.PersonaDTO;
import com.scoutmanagement.persistence.model.Persona;

public interface IPersonaService {
    void save(PersonaDTO personaDTO);

    Persona cambiarRegistroPersonaDTO(PersonaDTO personaDTO);
}
