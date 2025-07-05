package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoRepository extends CrudRepository<Progreso, Long> {
    List<Progreso> findAllByPersona(Persona persona);

    List<Progreso> findByPersonaAndEstadoTrue(Persona persona);

    Optional<Progreso> findByPersonaAndReto(Persona persona, Reto reto);

    void deleteByRetoId(Long retoId);
}
