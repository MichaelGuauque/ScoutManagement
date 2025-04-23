package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private UserRepository userRepository;

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

    @Override
    public Persona personaModelSession(String nombreSession, HttpSession session) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(Long.parseLong(session.getAttribute(nombreSession).toString()));
        UserEntity usuario = optionalUserEntity.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Persona> optionalPersona = findByUsuario_Id(usuario.getId());
        Persona persona = optionalPersona.orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return persona;
    }
}

