package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Etapa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtapaRepository extends CrudRepository<Etapa, Long> {
}
