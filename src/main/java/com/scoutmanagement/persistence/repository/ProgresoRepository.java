package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Progreso;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgresoRepository extends CrudRepository<Progreso, Long> {
}
