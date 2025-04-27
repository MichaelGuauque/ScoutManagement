package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.AsistenciaDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.persistence.repository.AsistenciaRepository;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AsistenciaService implements IAsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private PersonaRepository personaRepository;



    @Override
    public List<Asistencia> findByActividadOrdenado(Long actividadId) {
        try {
            if (actividadId == null) {
                throw new IllegalArgumentException("El ID de la actividad no puede ser nulo");
            }
            return asistenciaRepository.findByActividadOrdenado(actividadId);
        } catch (DataAccessException e) {
            throw new ServiceException("Error al buscar asistencias ordenadas por actividad: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Asistencia registrarAsistencia(AsistenciaDTO asistenciaDTO) {
        try {
            if (asistenciaDTO == null) {
                throw new IllegalArgumentException("La asistencia no puede ser nula");
            }
            if (asistenciaDTO.miembro() == null || asistenciaDTO.miembro().getId() == null) {
                throw new IllegalArgumentException("El miembro de la asistencia no puede ser nulo");
            }
            if (asistenciaDTO.actividad() == null || asistenciaDTO.actividad().getId() == null) {
                throw new IllegalArgumentException("La actividad de la asistencia no puede ser nula");
            }

            if (asistenciaDTO.asistio()) {
                Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByMiembroIdAndActividadId(
                        asistenciaDTO.miembro().getId(),
                        asistenciaDTO.actividad().getId());

                Asistencia asistencia;
                if (asistenciaExistente.isPresent()) {
                    asistencia = asistenciaExistente.get();
                    asistencia.setAsistio(true);
                } else {
                    asistencia = convertirAsistenciaDTO(asistenciaDTO);
                }

                return asistenciaRepository.save(asistencia);
            } else {
                Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByMiembroIdAndActividadId(
                        asistenciaDTO.miembro().getId(),
                        asistenciaDTO.actividad().getId());

                asistenciaExistente.ifPresent(a -> asistenciaRepository.delete(a));
                return null;
            }
        } catch (DataAccessException e) {
            throw new ServiceException("Error al registrar asistencia: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void registrarAsistenciasMasivas(Long actividadId, Map<Long, Boolean> asistenciasPorMiembro) {
        try {
            if (actividadId == null) {
                throw new IllegalArgumentException("El ID de la actividad no puede ser nulo");
            }
            if (asistenciasPorMiembro == null || asistenciasPorMiembro.isEmpty()) {
                throw new IllegalArgumentException("la lista de asistencias no puede ser nula o vacía");
            }

            Actividad actividad = actividadRepository.findById(actividadId)
                    .orElseThrow(() -> new ServiceException("Actividad no encontrada con ID: " + actividadId));

            asistenciasPorMiembro.forEach((miembroId, asistio) -> {
                try {
                    if (miembroId == null) {
                        throw new IllegalArgumentException("El ID del miembro no puede ser nulo");
                    }

                    Persona miembro = personaRepository.findById(miembroId)
                            .orElseThrow(() -> new ServiceException("Miembro no encontrado con ID: " + miembroId));

                    Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByMiembroIdAndActividadId(miembroId, actividadId);

                    if (asistio) {
                        Asistencia asistencia;
                        if (asistenciaExistente.isPresent()) {
                            asistencia = asistenciaExistente.get();
                            asistencia.setAsistio(true);
                        } else {
                            asistencia = Asistencia.builder()
                                    .miembro(miembro)
                                    .actividad(actividad)
                                    .asistio(true)
                                    .build();
                        }
                        asistenciaRepository.save(asistencia);
                    } else {
                        asistenciaExistente.ifPresent(a -> asistenciaRepository.delete(a));
                    }
                } catch (Exception e) {
                    throw new ServiceException("Error al procesar asistencia para miembro ID " + miembroId + ": " + e.getMessage(), e);
                }
            });
        } catch (ServiceException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new ServiceException("Error de acceso a datos al registrar asistencias: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceException("Error al registrar asistencias: " + e.getMessage(), e);
        }
    }





    @Override
    public List<Asistencia> prepararRegistroAsistencias(Actividad actividad, List<Persona> miembros) {
        try {
            if (actividad == null) {
                throw new IllegalArgumentException("La actividad no puede ser nula");
            }
            if (miembros == null) {
                throw new IllegalArgumentException("La lista de miembros no puede ser nula");
            }

            List<Asistencia> asistencias = new ArrayList<>();

            List<Asistencia> asistenciasExistentes = asistenciaRepository.findByActividadId(actividad.getId());

            for (Persona miembro : miembros) {
                if (miembro == null || miembro.getId() == null) {
                    continue;
                }

                Optional<Asistencia> asistenciaExistente = asistenciasExistentes.stream()
                        .filter(a -> a.getMiembro().getId().equals(miembro.getId()))
                        .findFirst();

                if (asistenciaExistente.isPresent()) {
                    asistencias.add(asistenciaExistente.get());
                } else {
                    Asistencia asistenciaTemporal = Asistencia.builder()
                            .miembro(miembro)
                            .actividad(actividad)
                            .asistio(false)
                            .build();
                    asistencias.add(asistenciaTemporal);
                }
            }

            return asistencias;
        } catch (DataAccessException e) {
            throw new ServiceException("Error al preparar registro de asistencias: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceException("Error inesperado al preparar registro de asistencias: " + e.getMessage(), e);
        }
    }

    @Override
    public Asistencia convertirAsistenciaDTO(AsistenciaDTO asistenciaDTO) {
        if (asistenciaDTO == null) {
            throw new IllegalArgumentException("El DTO de asistencia no puede ser nulo");
        }

        return Asistencia.builder()
                .miembro(asistenciaDTO.miembro())
                .actividad(asistenciaDTO.actividad())
                .asistio(asistenciaDTO.asistio())
                .build();
    }

    @Override
    public List<Persona> findPersonasByRama(Rama rama) {
        try {
            if (rama == null) {
                throw new IllegalArgumentException("La rama no puede ser nula");
            }
            return personaRepository.findByRama(rama);
        } catch (DataAccessException e) {
            throw new ServiceException("Error al buscar personas por rama: " + e.getMessage(), e);
        }
    }


}