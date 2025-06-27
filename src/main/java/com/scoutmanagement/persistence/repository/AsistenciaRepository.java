package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Asistencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    // Guardar una lista de asistencias en batch
    @Query("SELECT a FROM Asistencia a JOIN FETCH a.miembro WHERE a.actividad.id = :actividadId ORDER BY a.miembro.id")
    List<Asistencia> findByActividadOrdenado(@Param("actividadId") Long actividadId);


}
