package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.persistence.repository.RetoRepository;
import com.scoutmanagement.service.interfaces.IRetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RetoService implements IRetoService {

    @Autowired
    private RetoRepository retoRepository;
    @Autowired
    private EtapaRepository etapaRepository;

    @Override
    public Optional<Reto> findById(long id) {
        return retoRepository.findById(id);
    }

    @Override
    public void save(RetoDTO retoDTO) {
        retoRepository.save(cambiarRetoDTO(retoDTO));
    }

    @Override
    public void update(Reto reto) {
        retoRepository.save(reto);
    }

    @Override
    public List<Reto> findAll() {
        return
                (List<Reto>) retoRepository.findAll();
    }

    @Override
    public List<Reto> findAllRetosEtapa(Etapa etapa) {

        return (List<Reto>) retoRepository.findAllRetosByEtapa(etapa);
    }

    @Override
    public Reto cambiarRetoDTO(RetoDTO retoDTO) {
        Etapa etapa = etapaRepository.findByNombre(retoDTO.etapa());
        return new Reto(null,
                retoDTO.numero(),
                retoDTO.descripcion(),
                etapa);
    }
}
