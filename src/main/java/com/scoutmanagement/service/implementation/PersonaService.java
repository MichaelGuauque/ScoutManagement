package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.PersonaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public void save(PersonaDTO personaDTO) {
        personaRepository.save(cambiarRegistroPersonaDTO(personaDTO));
    }

    @Override
    public Persona cambiarRegistroPersonaDTO(PersonaDTO personaDTO){
        Persona persona= Persona.builder()
                .primerNombre(personaDTO.primerNombre())
                .segundoNombre(personaDTO.segundoNombre())
                .primerApellido(personaDTO.primerApellido())
                .segundoApellido(personaDTO.segundoApellido())
                .numeroDeDocumento(personaDTO.numeroDeDocumento())
                .tipoDeDocumento(personaDTO.tipoDeDocumento())
                .genero(personaDTO.genero())
                .rama(personaDTO.rama())
                .cargo(personaDTO.cargo())
                .rol(personaDTO.rol())
                .build();

        return persona;

        }
}

