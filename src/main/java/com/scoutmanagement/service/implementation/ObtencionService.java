package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.repository.ObtencionRepository;
import com.scoutmanagement.service.interfaces.IObtencionService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ObtencionService implements IObtencionService {

    @Autowired
    private ObtencionRepository obtencionRepository;

    @Override
    public Optional<Obtencion> findById(long id) {
        try {
            return obtencionRepository.findById(id);

        } catch (Exception e) {
            throw new ServiceException("Obtención no encontrada: " + e.getMessage());
        }
    }

    @Override
    public void save(Obtencion obtencion) {
        try {
            obtencionRepository.save(obtencion);
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar la obtención: " + e.getMessage());
        }
    }

    @Override
    public void update(Obtencion obtencion) {
        try {
            obtencionRepository.save(obtencion);
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar la obtención: " + e.getMessage());
        }
    }

    @Override
    public List<Obtencion> findAllByPersona(Persona persona) {
        try {
            return (List<Obtencion>) obtencionRepository.findAllByPersona(persona);
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos de la persona: " + e.getMessage());
        }
    }

    @Override
    public List<Obtencion> findAll() {
        try {
            return (List<Obtencion>) obtencionRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos: " + e.getMessage());
        }
    }

    @Override
    public Set<Long> findIdEtapasObtenidasByPersona(Persona persona) {
        try {
            List<Obtencion> obtenciones = obtencionRepository.findAllByPersona(persona);
            return obtenciones.stream()
                    .map(o -> o.getEtapa().getId())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new ServiceException("Error al obtener las etapas obtenidas: " + e.getMessage());
        }
    }

    @Override
    public Optional<Obtencion> findByPersona(Persona persona) {
        return obtencionRepository.findByPersona(persona);
    }

    @Override
    public List<Obtencion> ultimasObtenciones(Persona persona) {
        List<Obtencion> obtencionesByRama = new ArrayList<>();
        List<Obtencion> obtenciones = obtencionRepository.findAllByPersona(persona);
        for (Obtencion obtencion : obtenciones) {
            if (obtencion.getEtapa().getRama().equals(persona.getRama())) {
                obtencionesByRama.add(obtencion);
            }
        }
        return obtencionesByRama;
    }

}
