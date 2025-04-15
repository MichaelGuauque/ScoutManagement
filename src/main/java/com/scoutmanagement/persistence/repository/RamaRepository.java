package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Rama;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RamaRepository extends CrudRepository<Rama, Long> {
}
