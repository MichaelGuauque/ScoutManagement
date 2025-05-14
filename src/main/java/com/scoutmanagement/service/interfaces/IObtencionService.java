package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IObtencionService {
    Optional<Obtencion> findById(long id);

    void save(Obtencion obtencion);

    void update(Obtencion obtencion);

    List<Obtencion> findAllByPersona(Persona persona);

    List<Obtencion> findAll();

    Set<Long> findIdEtapasObtenidasByPersona(Persona persona);

    Optional<Obtencion> findByPersona(Persona persona);

    List<Obtencion> ultimasObtenciones(Persona persona);

}
