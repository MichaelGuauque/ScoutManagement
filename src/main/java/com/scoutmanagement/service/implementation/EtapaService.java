package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.service.interfaces.IEtapaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtapaService implements IEtapaService {

    @Autowired
    private EtapaRepository etapaRepository;

    @Override
    public Optional<Etapa> findById(long id) {
        return etapaRepository.findById(id);
    }

    @Override
    public void save(Etapa etapa) {
        etapaRepository.save(etapa);
    }

    @Override
    public void update(Etapa etapa) {
        etapaRepository.save(etapa);
    }

    @Override
    public List<Etapa> findAll() {
        return (List<Etapa>) etapaRepository.findAll();
    }

    @Override
    public List<Etapa> findAllByRama(Rama rama) {
        return (List<Etapa>) etapaRepository.findAllByRamaOrderByOrdenAsc(rama);
    }
}
