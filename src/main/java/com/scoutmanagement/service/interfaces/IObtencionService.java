package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;

import java.util.List;
import java.util.Optional;

public interface IObtencionService {
    Optional<Obtencion> findById(long id);
    void save(Obtencion obtencion);
    void update(Obtencion obtencion);
    List<Obtencion> findAllByPersona(Persona persona);
    List<Obtencion> findAll();
}
