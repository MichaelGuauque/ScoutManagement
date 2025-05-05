package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.PersonaActualizacionDTO;
import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void save(PersonaRegistroDTO personaRegistroDTO,UserEntity userEntity) {
        personaRepository.save(cambiarRegistroPersonaRegistroDTO(personaRegistroDTO, userEntity));
    }


    @Override
    public Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO,UserEntity userEntity) {
        Responsable responsable = new Responsable();
        return Persona.builder()
                .primerNombre(personaRegistroDTO.getPrimerNombre())
                .segundoNombre(personaRegistroDTO.getSegundoNombre())
                .primerApellido(personaRegistroDTO.getPrimerApellido())
                .segundoApellido(personaRegistroDTO.getSegundoApellido())
                .numeroDeDocumento(personaRegistroDTO.getNumeroDeDocumento())
                .tipoDeDocumento(personaRegistroDTO.getTipoDeDocumento())
                .rama(personaRegistroDTO.getRama())
                .cargo(personaRegistroDTO.getCargo())
                .responsable(responsable)
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

    public List<Persona> findMiembros() {
        return personaRepository.findMiembros();
    }


    @Override
    @Transactional
    public void actualizarPersona(Long id, PersonaActualizacionDTO dto) {
        Persona persona = personaRepository.findByUserEntity_Id(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada : " ));

        if (persona == null) {
            throw new EntityNotFoundException("Persona no encontrada");
        }

        persona.setPrimerNombre(dto.getPrimerNombre());
        persona.setSegundoNombre(dto.getSegundoNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setNumeroDeDocumento(dto.getNumeroDeDocumento());
        persona.setTipoDeDocumento(dto.getTipoDeDocumento());
        persona.setRama(dto.getRama());
        persona.setCargo(dto.getCargo());
        System.out.println("Aqui termino"+dto.getRol());
        // ACTUALIZACIÓN DE ROL
        if (dto.getRol() != null) {
            System.out.println("Se ha recibido un nuevo rol para actualizar: " + dto.getRol());

            UserEntity user = persona.getUserEntity();
            System.out.println("Usuario encontrado: " + user.getId() + " con roles actuales: " + user.getRoles());

            RoleEntity nuevoRol = roleRepository.findByRole(dto.getRol());
            if (nuevoRol == null) {
                System.out.println("ERROR: Rol no encontrado para el valor: " + dto.getRol());
                throw new EntityNotFoundException("Rol no encontrado");
            }

            System.out.println("Nuevo rol encontrado: " + nuevoRol.getRole());

            // Limpia los roles anteriores y asigna el nuevo
            user.getRoles().clear();
            System.out.println("Roles anteriores limpiados.");

            user.getRoles().add(nuevoRol);
            System.out.println("Nuevo rol asignado: " + nuevoRol.getRole());
        }
    }



}

