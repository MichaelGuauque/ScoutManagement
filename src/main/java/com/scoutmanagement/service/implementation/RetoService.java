package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Progreso;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.persistence.repository.ProgresoRepository;
import com.scoutmanagement.persistence.repository.RetoRepository;
import com.scoutmanagement.service.interfaces.IRetoService;
import com.scoutmanagement.util.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RetoService implements IRetoService {

    @Autowired
    private RetoRepository retoRepository;
    @Autowired
    private EtapaRepository etapaRepository;
    @Autowired
    private ProgresoRepository progresoRepository;

    @Override
    public Optional<Reto> findById(long id) {
        try {
            return retoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Reto no encontrado: " + e.getMessage());
        }
    }

    @Override
    public void save(RetoDTO retoDTO) {
        try {
            Optional<Reto> retoOptional = retoRepository.findRetoByNumero(retoDTO.numero());
            if (retoOptional.isPresent()) {
                throw new ServiceException("El reto con numero " + retoDTO.numero() + " ya existe");
            }else {
                retoRepository.save(cambiarRetoDTO(retoDTO));
            }

        } catch (Exception e) {
            throw new ServiceException("No se pudo guardar el reto: " + e.getMessage());
        }
    }

    @Override
    public void update(Reto reto) {
        try {
            retoRepository.save(reto);
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar el reto: " + e.getMessage());
        }

    }

    @Override
    public List<Reto> findAll() {
        try {
            return (List<Reto>) retoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos: " + e.getMessage());
        }
    }

    @Override
    public List<Reto> findAllRetosEtapa(Etapa etapa) {
        try {
            return (List<Reto>) retoRepository.findAllRetosByEtapa(etapa);
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los retos por etapa: " + e.getMessage());
        }
    }

    @Override
    public Reto cambiarRetoDTO(RetoDTO retoDTO) {
        try {
            return new Reto(null,
                    retoDTO.numero(),
                    retoDTO.descripcion(),
                    etapaRepository.findByNombre(retoDTO.etapa()));
        } catch (Exception e) {
            throw new ServiceException("No se puedo convertir el dto: " + e.getMessage());
        }

    }



    @Override
    public List<Reto> findCompletadosByPersonaAndEtapa(Persona persona, Etapa etapa) {
        List<Progreso> progresos = progresoRepository.findByPersonaAndEstadoTrue(persona);

        return progresos.stream()
                .map(Progreso::getReto)
                .filter(reto -> reto.getEtapa().equals(etapa))
                .collect(Collectors.toList());
    }

}
