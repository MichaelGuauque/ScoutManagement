package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;

import java.util.List;
import java.util.Optional;

public interface IProgresoService {

    Optional<Progreso> findById(long id);
    void save(Progreso progreso);
    void update(Progreso progreso);
    List<Progreso> findAll();
    List<Progreso> findAllByPersona(Persona persona);
}
