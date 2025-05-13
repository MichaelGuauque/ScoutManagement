package com.scoutmanagement.service.implementation;

import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.service.interfaces.IProgresoService;
import com.scoutmanagement.service.interfaces.IRetoService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProgresoService implements IProgresoService {

    @Autowired
    private ProgresoRepository progresoRepository;
    @Autowired
    private IRetoService retoService;

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

    @Override
    public Map<Long, Float> calcularProgresosPorEtapa(List<Etapa> etapas, Persona persona) {
        try {
            Map<Long, Float> progresoPorEtapa = new HashMap<>();

            for (Etapa etapa : etapas) {
                List<Reto> retosEtapa = retoService.findAllRetosEtapa(etapa);
                List<Reto> retosCompletados = retoService.findCompletadosByPersonaAndEtapa(persona, etapa);

                float progreso = retosEtapa.isEmpty() ? 0f :
                        (float) retosCompletados.size() / retosEtapa.size() * 100;

                progresoPorEtapa.put(etapa.getId(), progreso);
            }

            return progresoPorEtapa;
        } catch (Exception e) {
            throw new ServiceException("Error al calcular los progresos: " + e.getMessage());
        }
    }

    @Override
    public Map<String, List<Reto>> prepararRetosPorEtapa(List<Etapa> etapas,Map<String, Map<Long, Boolean>> estadoRetosPorEtapa) {
        try {
            Map<String, List<Reto>> retosPorEtapa = new HashMap<>();

            for (Etapa etapa : etapas) {
                List<Reto> retosEtapa = retoService.findAllRetosEtapa(etapa);

                Map<Long, Boolean> estados = estadoRetosPorEtapa.get(etapa.getNombre());

                retosEtapa.sort((r1, r2) -> {
                    Boolean estado1 = estados.getOrDefault(r1.getId(), false);
                    Boolean estado2 = estados.getOrDefault(r2.getId(), false);
                    return Boolean.compare(estado1, estado2);
                });

                retosPorEtapa.put(etapa.getNombre(), retosEtapa);
            }
            return retosPorEtapa;
        } catch (Exception e) {
            throw new ServiceException("Error al preparar los retos por etapa: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Map<Long, Boolean>> calcularEstadoRetos(List<Etapa> etapas, Persona persona) {
        try {
            Map<String, Map<Long, Boolean>> estadoRetosPorEtapa = new HashMap<>();

            for (Etapa etapa : etapas) {
                List<Reto> retosEtapa = retoService.findAllRetosEtapa(etapa);
                List<Reto> retosCompletados = retoService.findCompletadosByPersonaAndEtapa(persona, etapa);

                Map<Long, Boolean> estados = new HashMap<>();
                for (Reto reto : retosEtapa) {
                    estados.put(reto.getId(), retosCompletados.contains(reto));
                }

                estadoRetosPorEtapa.put(etapa.getNombre(), estados);
            }

            return estadoRetosPorEtapa;
        } catch (Exception e) {
            throw new ServiceException("Error al calcular los estados de los retos: " + e.getMessage());
        }
    }
}