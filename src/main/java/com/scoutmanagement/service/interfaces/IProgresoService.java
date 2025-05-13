package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProgresoService {

    Optional<Progreso> findById(long id);

    void save(Progreso progreso);

    void update(Progreso progreso);

    List<Progreso> findAll();

    List<Progreso> findAllByPersona(Persona persona);

    Map<Long, Float> calcularProgresosPorEtapa(List<Etapa> etapas, Persona persona);

    Map<String, List<Reto>> prepararRetosPorEtapa(List<Etapa> etapas, Map<String, Map<Long, Boolean>> estadoRetosPorEtapa);

    Map<String, Map<Long, Boolean>> calcularEstadoRetos(List<Etapa> etapas, Persona persona);
}
