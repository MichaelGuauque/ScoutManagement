package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends CrudRepository<Persona, Long> {
    
    // Buscar personas por rama
    List<Persona> findByRama(Rama rama);
    boolean existsByNumeroDeDocumento(Long numeroDeDocumento);

    @Query("SELECT p FROM Persona p WHERE str(p.cargo) LIKE 'JEFE_%'")
    List<Persona> findJefes();



}
