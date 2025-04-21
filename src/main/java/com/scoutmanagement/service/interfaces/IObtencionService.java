package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Obtencion;

import java.util.List;
import java.util.Optional;

public interface IObtencionService {
    Optional<Obtencion> findById(long id);
    void save(Obtencion obtencion);
    void update(Obtencion obtencion);
    List<Obtencion> findAllByPersona(long idPersona);
    List<Obtencion> findAll();
}
