package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.RetoDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Reto;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.persistence.repository.RetoRepository;
import com.scoutmanagement.service.interfaces.IRetoService;
import com.scoutmanagement.util.exception.ServiceException;
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
        try {
            return retoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Reto no encontrado: " + e.getMessage());
        }
    }

    @Override
    public void save(RetoDTO retoDTO) {
        try {
            retoRepository.save(cambiarRetoDTO(retoDTO));
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
}
