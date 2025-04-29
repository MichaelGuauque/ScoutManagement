package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public void save(PersonaRegistroDTO personaRegistroDTO,UserEntity userEntity) {
        personaRepository.save(cambiarRegistroPersonaRegistroDTO(personaRegistroDTO, userEntity));
    }


    @Override
    public Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO,UserEntity userEntity) {

        return Persona.builder()
                .primerNombre(personaRegistroDTO.getPrimerNombre())
                .segundoNombre(personaRegistroDTO.getSegundoNombre())
                .primerApellido(personaRegistroDTO.getPrimerApellido())
                .segundoApellido(personaRegistroDTO.getSegundoApellido())
                .numeroDeDocumento(personaRegistroDTO.getNumeroDeDocumento())
                .tipoDeDocumento(personaRegistroDTO.getTipoDeDocumento())
                .rama(personaRegistroDTO.getRama())
                .cargo(personaRegistroDTO.getCargo())
                .userEntity(userEntity)
                .build();

    }

    @Override
    public boolean existsByNumeroDeDocumento(Long numeroDeDocumento) {
       return personaRepository.existsByNumeroDeDocumento(numeroDeDocumento);
    }

    public List<Persona> findJefes() {
        return personaRepository.findJefes();
    }


}

