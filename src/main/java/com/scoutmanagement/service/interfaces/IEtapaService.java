package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.EtapaDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Rama;

import java.util.List;
import java.util.Optional;

public interface IEtapaService {

    Optional<Etapa> findById(long id);

    void save(EtapaDTO etapaDTO);

    void update(Etapa etapa);

    List<Etapa> findAll();

    List<Etapa> findAllByRama(Rama rama);

    Etapa cambiarEtapaDTO(EtapaDTO etapaDTO);
}
