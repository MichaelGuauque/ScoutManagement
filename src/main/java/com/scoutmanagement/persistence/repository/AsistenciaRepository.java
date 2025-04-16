package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Asistencia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends CrudRepository<Asistencia, Long> {

    // Buscar todas las asistencias de una actividad
    List<Asistencia> findByActividadId(Long actividadId);

    // Buscar una asistencia específica de un miembro en una actividad
    Optional<Asistencia> findByMiembroIdAndActividadId(Long miembroId, Long actividadId);

    // Para verificar si existe registro para un miembro en una actividad
    boolean existsByMiembroIdAndActividadId(Long miembroId, Long actividadId);

}
