package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.service.interfaces.IProgresoService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgresoService implements IProgresoService {

    @Autowired
    private ProgresoRepository progresoRepository;

    @Override
    public Optional<Progreso> findById(long id) {
        try {
            return progresoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Progreso no encontrado: " + e.getMessage());
        }
    }

    @Override
    public void save(Progreso progreso) {
        try {
            progresoRepository.save(progreso);
        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar el progreso: " + e.getMessage());
        }
    }

    @Override
    public void update(Progreso progreso) {
        try {
            progresoRepository.save(progreso);
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar el progreso: " + e.getMessage());
        }
    }

    @Override
    public List<Progreso> findAll() {
        try {
            return (List<Progreso>) progresoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos: " + e.getMessage());
        }

    }

    @Override
    public List<Progreso> findAllByPersona(Persona persona) {
        try {
            return (List<Progreso>) progresoRepository.findAllByPersona(persona);
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos de la persona: " + e.getMessage());
        }
    }
}
