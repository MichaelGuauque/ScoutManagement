package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.repository.ObtencionRepository;
import com.scoutmanagement.service.interfaces.IObtencionService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
