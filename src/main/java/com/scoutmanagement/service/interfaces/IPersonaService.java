package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.PersonaActualizacionDTO;
import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.UserEntity;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

import java.util.List;

public interface IPersonaService {
    void save(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);

    Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity);
    boolean existsByNumeroDeDocumento(Long numeroDeDocumento);
    Optional<Persona> findByUsuarioId(Long usuario_id);
    Persona personaModelSession(String nombreSession, HttpSession session);
    List<Persona> findJefes();
    List<Persona> findMiembros();
    void actualizarPersona(Long id, PersonaActualizacionDTO dto);
    Optional<Persona> findByNumeroDeDocumento(Long numeroDeDocumento);
    public List<Persona> filtrarYOrdenarPorEstado(List<Persona> personas, String tab);

}
