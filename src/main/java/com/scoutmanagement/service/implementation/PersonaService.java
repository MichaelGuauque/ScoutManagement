package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.InformacionPersonaDTO;
import com.scoutmanagement.dto.PersonaActualizacionDTO;
import com.scoutmanagement.dto.PersonaRegistroDTO;
import com.scoutmanagement.dto.ResponsablePersonaDTO;
import com.scoutmanagement.persistence.model.*;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.persistence.repository.ResponsableRepository;
import com.scoutmanagement.persistence.repository.RoleRepository;
import com.scoutmanagement.persistence.repository.UserRepository;
import com.scoutmanagement.service.interfaces.IPersonaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaService implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResponsableRepository  responsableRepository;

    private static final String ID_USUARIO = "idUsuario";

    @Override
    public void save(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity) {
        personaRepository.save(cambiarRegistroPersonaRegistroDTO(personaRegistroDTO, userEntity));
    }


    @Override
    public Persona cambiarRegistroPersonaRegistroDTO(PersonaRegistroDTO personaRegistroDTO, UserEntity userEntity) {
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
        Long userId = Long.parseLong(session.getAttribute(nombreSession).toString());

        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    }

    public List<Persona> findJefes() {
        return personaRepository.findJefes();
    }

    public List<Persona> findMiembros() {
        return personaRepository.findMiembros();
    }

    @Override
    public List<Persona> findMiembrosByRama(Rama rama) {
        return personaRepository.findMiembrosByRama(rama);
    }


    @Override
    @Transactional
    public void actualizarPersona(Long id, PersonaActualizacionDTO dto) {
        Persona persona = personaRepository.findByUserEntity_Id(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada : "));

        Optional<Persona> personaConMismoDocumento = personaRepository.findByNumeroDeDocumento(dto.getNumeroDeDocumento());

        if (personaConMismoDocumento.isPresent() && !personaConMismoDocumento.get().getId().equals(id)) {
            throw new DataIntegrityViolationException("El número de documento ya está registrado");
        }

        persona.setPrimerNombre(dto.getPrimerNombre());
        persona.setSegundoNombre(dto.getSegundoNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setNumeroDeDocumento(dto.getNumeroDeDocumento());
        persona.setTipoDeDocumento(dto.getTipoDeDocumento());
        persona.setRama(dto.getRama());
        persona.setCargo(dto.getCargo());


        if (dto.getRol() != null) {


            UserEntity user = persona.getUserEntity();


            RoleEntity nuevoRol = roleRepository.findByRole(dto.getRol());
            if (nuevoRol == null) {

                throw new EntityNotFoundException("Rol no encontrado");
            }

            user.getRoles().clear();
            user.getRoles().add(nuevoRol);

        }
    }

    @Override
    public Optional<Persona> findByNumeroDeDocumento(Long numeroDeDocumento) {
        return personaRepository.findByNumeroDeDocumento(numeroDeDocumento);
    }

    @Override
    public List<Persona> filtrarYOrdenarPorEstado(List<Persona> personas, String tab) {
        return personas.stream()
                .filter(p -> {
                    boolean estado = p.getUserEntity().isActivo();
                    return "inactivos".equals(tab) ? !estado : estado;
                })
                .sorted(Comparator.comparing(Persona::getRama))
                .collect(Collectors.toList());
    }

    @Override
    public Long contarMiembrosActivos() {
        return personaRepository.countMiembrosActivos();
    }

    @Override
    public Long contarJefesActivos() {
        return personaRepository.countJefesActivos();
    }

    @Override
    public void actualizarInformacionPerosnal(InformacionPersonaDTO dto, HttpSession session, ResponsablePersonaDTO responsableDTO) {
        Persona persona = personaModelSession(ID_USUARIO, session);
        persona.setPrimerNombre(dto.primerNombre());
        persona.setSegundoNombre(dto.segundoNombre());
        persona.setPrimerApellido(dto.primerApellido());
        persona.setSegundoApellido(dto.segundoApellido());
        persona.setNumeroDeDocumento(dto.numeroDeDocumento());
        persona.setEspecificacionAlergiasYRestricciones(dto.alergias());
        persona.setTipoDeSangre(TipoDeSangre.fromAlias(dto.tipoDeSangre()));
        persona.setEps(dto.eps());
        persona.setTomaMedicamentos(dto.medicamentos());
        persona.setEspecificacionMedicamentos(dto.especificacionMedicamentos());
        persona.setNombrePrimerContacto(dto.primerContacto());
        persona.setNumeroPrimerContacto(dto.numeroPrimerContacto());
        persona.setNombreSegundoContacto(dto.segundoContacto());
        persona.setNumeroSegundoContacto(dto.numeroSegundoContacto());
        if (persona.getResponsable() != null) {
            Responsable responsable = persona.getResponsable();
            responsable.setNombres(responsableDTO.nombresAcudiente());
            responsable.setApellidos(responsableDTO.apellidosAcudiente());
            responsable.setNumeroDocumento(responsableDTO.numeroDocumentoResponsable());
            responsable.setTelefono(responsableDTO.telefonoResponsable());
            persona.setDireccion(responsableDTO.direccion());
        }else {
            if (responsableDTO != null && responsableDTO.nombresAcudiente() != null) {
                Responsable responsable = new Responsable();
                responsable.setNombres(responsableDTO.nombresAcudiente());
                responsable.setApellidos(responsableDTO.apellidosAcudiente());
                responsable.setNumeroDocumento(responsableDTO.numeroDocumentoResponsable());
                responsable.setTelefono(responsableDTO.telefonoResponsable());
                persona.setDireccion(responsableDTO.direccion());
                persona.setResponsable(responsable);
            }
        }
        personaRepository.save(persona);
    }


}

