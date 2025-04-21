package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Obtencion;
import com.scoutmanagement.persistence.repository.ObtencionRepository;
import com.scoutmanagement.service.interfaces.IObtencionService;
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
        return obtencionRepository.findById(id);
    }

    @Override
    public void save(Obtencion obtencion) {
        obtencionRepository.save(obtencion);
    }

    @Override
    public void update(Obtencion obtencion) {
        obtencionRepository.save(obtencion);
    }

    @Override
    public List<Obtencion> findAllByPersona(long idPersona) {
        return (List<Obtencion>) obtencionRepository.findAllByPersona(idPersona);
    }

    @Override
    public List<Obtencion> findAll() {
        return (List<Obtencion>) obtencionRepository.findAll();
    }
}
