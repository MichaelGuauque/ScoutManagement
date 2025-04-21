package com.scoutmanagement.service.implementation;

import com.scoutmanagement.DTO.RetoDTO;
import com.scoutmanagement.persistence.model.Reto;
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
        return (List<Reto>) retoRepository.findAll();
    }

    @Override
    public List<Reto> findAllRetosEtapa(long idEtapa) {
        return (List<Reto>) retoRepository.findAllRetosByEtapa(idEtapa);
    }

    @Override
    public Reto cambiarRetoDTO(RetoDTO retoDTO) {
        return new Reto(null,
                retoDTO.numero(),
                retoDTO.descripcion(),
                retoDTO.etapa());
    }
}
