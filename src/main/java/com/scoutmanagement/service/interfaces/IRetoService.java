package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;

import java.util.List;
import java.util.Optional;

public interface IRetoService {

    Optional<Reto> findById(long id);

    void save(RetoDTO retoDTO);

    void update(Reto reto);

    List<Reto> findAll();

    List<Reto> findAllRetosEtapa(Etapa etapa);

    Reto cambiarRetoDTO(RetoDTO retoDTO);
}
