package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public void save(PersonaRegistroDTO personaRegistroDTO) {
        personaRepository.save(cambiarRegistroPersonaRegistroDTO(personaRegistroDTO));
    }

    @Override
    public Optional<Persona> findById(long id) {
        return personaRepository.findById(id);
    }

    @Override
    public Optional<Persona> findByUsuario_Id(Long usuario_id) {
        return personaRepository.findByUserEntity_Id(usuario_id);
    }

    @Override
    public Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO) {
        Persona persona = Persona.builder()
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

        return persona;

    }
}

