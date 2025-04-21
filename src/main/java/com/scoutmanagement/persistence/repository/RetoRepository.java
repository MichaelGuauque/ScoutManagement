package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Reto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetoRepository extends CrudRepository<Reto, Long> {
}
