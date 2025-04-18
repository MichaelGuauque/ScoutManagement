package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends CrudRepository<Actividad, Long> {

    Optional<Actividad> findByNombre(String nombre);
    Optional<Actividad> findByFecha(LocalDate fecha);
    List<Actividad> findByRama(Rama rama);

    @Query("SELECT a FROM Actividad a ORDER BY a.fecha ASC")
    List<Actividad> findAllOrderByFechaAsc();
}
