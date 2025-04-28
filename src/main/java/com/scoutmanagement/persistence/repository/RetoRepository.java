package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetoRepository extends CrudRepository<Reto, Long> {
    List<Reto> findAllRetosByEtapa(Etapa etapa);
}
