package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Obtencion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObtencionRepository extends CrudRepository<Obtencion, Long> {
    List<Obtencion> findAllByPersona(long idPersona);
}
