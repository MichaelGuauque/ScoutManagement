package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.ActividadRepository;
import com.scoutmanagement.service.interfaces.IActividadService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public void crearActividad(ActividadDTO actividadDTO) {
        try {
             actividadRepository.save(cambiarActividadDTO(actividadDTO));
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

    public List<Actividad> filtrarYOrdenarActividadesPorTab(List<Actividad> actividades, Rama rama, String tab, LocalDate hoy) {
        return actividades.stream()
                .filter(actividad -> rama == null || actividad.getRama().equals(rama))
                .filter(actividad -> tab.equals("pasadas")
                        ? actividad.getFecha().isBefore(hoy)
                        : !actividad.getFecha().isBefore(hoy))
                .sorted(tab.equals("pasadas")
                        ? Comparator.comparing(Actividad::getFecha).reversed()
                        : Comparator.comparing(Actividad::getFecha))
                .collect(Collectors.toList());
    }

    public List<Actividad> paginarActividades(List<Actividad> actividades, int page, int size) {
        return actividades.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public Map<Long, Boolean> encontrarActividadMasProxima(List<Actividad> actividades,int page, String tab) {
        Map<Long, Boolean> actividadEsMasProxima = new HashMap<>();
        if ("proximas".equals(tab) && page == 0) {
            actividades.stream()
                    .min(Comparator.comparing(Actividad::getFecha))
                    .ifPresent(actividad -> actividadEsMasProxima.put(actividad.getId(), true));
        }
        return actividadEsMasProxima;
    }

}