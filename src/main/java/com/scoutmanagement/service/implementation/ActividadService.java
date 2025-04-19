package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.service.interfaces.IActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService implements IActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Override
    public List<Actividad> findAllActividad() {
        return (List<Actividad>) actividadRepository.findAll();
    }

    @Override
    public void crearActividad(ActividadDTO actividadDTO) {
        actividadRepository.save(cambiarActividadDTO(actividadDTO));
    }

    @Override
    public Actividad modificarActividad(Actividad actividad) {
        if (actividadRepository.existsById(actividad.getId())) {
            return actividadRepository.save(actividad);
        }
        throw new IllegalArgumentException("La actividad con ID " + actividad.getId() + " no existe.");
    }

    @Override
    public void eliminarActividad(Long id) {
        actividadRepository.deleteById(id);
    }

    @Override
    public Optional<Actividad> findByNombre(String nombre) {
        return actividadRepository.findByNombre(nombre);
    }

    @Override
    public Actividad cambiarActividadDTO(ActividadDTO actividadDTO) {
        Actividad actividad = Actividad.builder()
                .nombre(actividadDTO.nombre())
                .descripcion(actividadDTO.descripcion())
                .rama(actividadDTO.rama())
                .fecha(actividadDTO.fecha())
                .ubicacion(actividadDTO.ubicacion())
                .build();
        return actividad;
    }

    @Override
    public List<Actividad> findAllActividadesOrdenadas() {
        return (List<Actividad>) actividadRepository.findAllOrderByFechaAsc();
    }

    @Override
    public List<Actividad> findAllByRama(Rama rama) {
        return actividadRepository.findByRama(rama);
    }

}
