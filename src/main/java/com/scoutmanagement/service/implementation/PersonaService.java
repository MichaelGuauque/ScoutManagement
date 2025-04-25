package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public void save(PersonaRegistroDTO personaRegistroDTO) {
        personaRepository.save(cambiarRegistroPersonaRegistroDTO(personaRegistroDTO));
    }


    @Override
    public Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO) {

        return Persona.builder()
                .primerNombre(personaRegistroDTO.getPrimerNombre())
                .segundoNombre(personaRegistroDTO.getSegundoNombre())
                .primerApellido(personaRegistroDTO.getPrimerApellido())
                .segundoApellido(personaRegistroDTO.getSegundoApellido())
                .numeroDeDocumento(personaRegistroDTO.getNumeroDeDocumento())
                .tipoDeDocumento(personaRegistroDTO.getTipoDeDocumento())
                .genero(personaRegistroDTO.getGenero())
                .rama(personaRegistroDTO.getRama())
                .cargo(personaRegistroDTO.getCargo())
                .userEntity(personaRegistroDTO.getUserEntity())
                .build();

    }
}

