package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;

import java.util.List;
import java.util.Optional;

public interface IActividadService {

    public List<Actividad> findAllActividad();

    public void crearActividad(Actividad actividad);

    public Actividad modificarActividad(Actividad actividad);

    public void eliminarActividad(Long id);

    public List<Actividad> findAllByRama(Rama rama);

    public Optional<Actividad> findByNombre(String nombre);

}
