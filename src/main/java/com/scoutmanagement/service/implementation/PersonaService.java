package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private UserRepository userRepository;

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

    @Override
    public Optional<Persona> findByUsuarioId(Long usuarioId) {
        return personaRepository.findByUserEntity_Id(usuarioId);
    }

    @Override
    public Persona personaModelSession(String nombreSession, HttpSession session) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(Long.parseLong(session.getAttribute(nombreSession).toString()));
        UserEntity usuario = optionalUserEntity.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Optional<Persona> optionalPersona = findByUsuarioId(usuario.getId());
        Persona persona = optionalPersona.orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return persona;
    }

    public List<Persona> findJefes() {
        return personaRepository.findJefes();
    }


}

