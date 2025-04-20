package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Persona;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends CrudRepository<Persona, Long> {


}
