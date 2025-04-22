package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.service.interfaces.IProgresoService;
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
        return progresoRepository.findById(id);
    }

    @Override
    public void save(Progreso progreso) {
        progresoRepository.save(progreso);
    }

    @Override
    public void update(Progreso progreso) {
        progresoRepository.save(progreso);
    }

    @Override
    public List<Progreso> findAll() {
        return (List<Progreso>) progresoRepository.findAll();
    }

    @Override
    public List<Progreso> findAllByPersona(Persona persona) {
        return (List<Progreso>) progresoRepository.findAllByPersona(persona);
    }
}
