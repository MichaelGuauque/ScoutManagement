package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Rama;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapaRepository extends CrudRepository<Etapa, Long> {
    List<Etapa> findAllByRamaOrderByOrdenAsc(Rama rama);

    Etapa findByNombre(String nombre);
}
