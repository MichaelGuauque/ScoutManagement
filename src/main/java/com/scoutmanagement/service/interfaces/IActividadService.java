package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;

import java.util.List;
import java.util.Optional;

public interface IActividadService {

    public Optional<Actividad> findById(Long id);

    public List<Actividad> findAllActividad();

    public void crearActividad(ActividadDTO actividadDTO);

    public Actividad modificarActividad(Actividad actividad);

    public void eliminarActividad(Long id);

    public List<Actividad> findAllByRama(Rama rama);

    public Optional<Actividad> findByNombre(String nombre);

    Actividad cambiarActividadDTO(ActividadDTO actividadDTO);

    List<Actividad> findAllActividadesOrdenadas();

}
