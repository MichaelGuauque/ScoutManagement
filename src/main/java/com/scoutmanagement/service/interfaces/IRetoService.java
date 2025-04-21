package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;

import java.util.List;
import java.util.Optional;

public interface IRetoService {

    Optional<Reto> findById(long id);
    void save(RetoDTO retoDTO);
    void update(Reto reto);
    List<Reto> findAll();
    List<Reto> findAllRetosEtapa(long idEtapa);

    Reto cambiarRetoDTO(RetoDTO retoDTO);
}
