package com.scoutmanagement.service.implementation;

import com.scoutmanagement.tempDTO.AsistenciaDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.persistence.repository.AsistenciaRepository;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsistenciaService implements IAsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public List<Asistencia> findByActividad(Long actividadId) {
        return asistenciaRepository.findByActividadId(actividadId);
    }

    @Override
    public List<Asistencia> findByActividadOrdenado(Long actividadId) {
        return asistenciaRepository.findByActividadOrdenado(actividadId);
    }

    @Override
    @Transactional
    public Asistencia registrarAsistencia(AsistenciaDTO asistenciaDTO) {
        // Verificar si ya existe un registro para ese miembro en esa actividad
        Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByMiembroIdAndActividadId(
                asistenciaDTO.miembro().getId(),
                asistenciaDTO.actividad().getId());

        Asistencia asistencia;
        if (asistenciaExistente.isPresent()) {
            // Actualizar el registro existente
            asistencia = asistenciaExistente.get();
            asistencia.setAsistio(asistenciaDTO.asistio());
        } else {
            // Crear un nuevo registro
            asistencia = convertirAsistenciaDTO(asistenciaDTO);
        }

        return asistenciaRepository.save(asistencia);
    }

    @Override
    @Transactional
    public void registrarAsistenciasMasivas(Long actividadId, Map<Long, Boolean> asistenciasPorMiembro) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + actividadId));

        asistenciasPorMiembro.forEach((miembroId, asistio) -> {
            Persona miembro = personaRepository.findById(miembroId)
                    .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado con ID: " + miembroId));

            Optional<Asistencia> asistenciaExistente = asistenciaRepository.findByMiembroIdAndActividadId(miembroId, actividadId);

            Asistencia asistencia;
            if (asistenciaExistente.isPresent()) {
                asistencia = asistenciaExistente.get();
                asistencia.setAsistio(asistio);
            } else {
                asistencia = Asistencia.builder()
                        .miembro(miembro)
                        .actividad(actividad)
                        .asistio(asistio)
                        .build();
            }

            asistenciaRepository.save(asistencia);
        });
    }

    @Override
    public boolean existeAsistencia(Long miembroId, Long actividadId) {
        return asistenciaRepository.existsByMiembroIdAndActividadId(miembroId, actividadId);
    }

    @Override
    public Asistencia findByMiembroAndActividad(Long miembroId, Long actividadId) {
        return asistenciaRepository.findByMiembroIdAndActividadId(miembroId, actividadId).orElse(null);
    }

    @Override
    public List<Asistencia> prepararRegistroAsistencias(Actividad actividad, List<Persona> miembros) {
        List<Asistencia> asistencias = new ArrayList<>();

        // Obtener asistencias existentes para esta actividad
        List<Asistencia> asistenciasExistentes = asistenciaRepository.findByActividadId(actividad.getId());

        // Preparar una nueva lista de asistencias
        for (Persona miembro : miembros) {
            // Buscar si ya existe un registro para este miembro en esta actividad
            Optional<Asistencia> asistenciaExistente = asistenciasExistentes.stream()
                    .filter(a -> a.getMiembro().getId().equals(miembro.getId()))
                    .findFirst();

            if (asistenciaExistente.isPresent()) {
                // Si ya existe, usar ese registro
                asistencias.add(asistenciaExistente.get());
            } else {
                // Si no existe, crear uno nuevo con asistencia en false por defecto
                Asistencia nuevaAsistencia = Asistencia.builder()
                        .miembro(miembro)
                        .actividad(actividad)
                        .asistio(false)
                        .build();
                asistencias.add(nuevaAsistencia);
            }
        }

        return asistencias;
    }

    @Override
    public Asistencia convertirAsistenciaDTO(AsistenciaDTO asistenciaDTO) {
        return Asistencia.builder()
                .miembro(asistenciaDTO.miembro())
                .actividad(asistenciaDTO.actividad())
                .asistio(asistenciaDTO.asistio())
                .build();
    }

    @Override
    @Transactional
    public List<Asistencia> crearAsistenciasAutomaticas(Actividad actividad) {
        // Obtener todos los miembros de la misma rama que la actividad
        List<Persona> miembrosRama = findPersonasByRama(actividad.getRama());

        // Crear registros de asistencia para cada miembro
        List<Asistencia> asistencias = miembrosRama.stream()
                .map(miembro -> Asistencia.builder()
                        .miembro(miembro)
                        .actividad(actividad)
                        .asistio(false) // Inicialmente todos están como no asistentes
                        .build())
                .collect(Collectors.toList());

        // Guardar todos los registros en la base de datos
        return guardarAsistenciasMasivas(asistencias);
    }

    @Override
    public List<Persona> findPersonasByRama(Rama rama) {
        return personaRepository.findByRama(rama);
    }

    @Override
    @Transactional
    public List<Asistencia> guardarAsistenciasMasivas(List<Asistencia> asistencias) {
        List<Asistencia> asistenciasGuardadas = new ArrayList<>();

        for (Asistencia asistencia : asistencias) {
            asistenciasGuardadas.add(asistenciaRepository.save(asistencia));
        }

        return asistenciasGuardadas;
    }
}
