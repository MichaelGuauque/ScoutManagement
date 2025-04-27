package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService implements IActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Override
    public Optional<Actividad> findById(Long id) {
        return actividadRepository.findById(id);
    }

    @Override
    public List<Actividad> findAllActividad() {
        return (List<Actividad>) actividadRepository.findAll();
    }

    @Override
    @Transactional
    public void crearActividad(ActividadDTO actividadDTO) {
        try {
            Actividad nuevaActividad = actividadRepository.save(cambiarActividadDTO(actividadDTO));
        } catch (Exception e) {
            throw new ServiceException("Actividad no creada." + e.getMessage());
        }
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
        return Actividad.builder()
                .nombre(actividadDTO.nombre())
                .descripcion(actividadDTO.descripcion())
                .rama(actividadDTO.rama())
                .fecha(actividadDTO.fecha())
                .ubicacion(actividadDTO.ubicacion())
                .build();
    }

    @Override
    public List<Actividad> findAllActividadesOrdenadas() {
        return actividadRepository.findAllOrderByFechaAsc();
    }

    @Override
    public List<Actividad> findAllByRama(Rama rama) {
        return actividadRepository.findByRama(rama);
    }
}