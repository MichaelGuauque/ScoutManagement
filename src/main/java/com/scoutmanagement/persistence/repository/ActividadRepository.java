package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Actividad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ActividadRepository extends CrudRepository<Actividad, Long> {

    Optional<Actividad> findActividadByNombre(String nombre);
    Optional<Actividad> findByFecha(LocalDate fecha);
}
