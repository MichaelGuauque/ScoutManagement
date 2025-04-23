package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.Persona;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public interface IPersonaService {
    void save(PersonaRegistroDTO personaRegistroDTO);

    Optional<Persona> findById(long id);

    Optional<Persona> findByUsuario_Id(Long usuario_id);


    Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO);

    Persona personaModelSession(String nombreSession, HttpSession session);

}
