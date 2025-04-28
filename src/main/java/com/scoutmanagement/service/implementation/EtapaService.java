package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.EtapaDTO;
import com.scoutmanagement.persistence.model.Etapa;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.repository.EtapaRepository;
import com.scoutmanagement.service.interfaces.IEtapaService;
import com.scoutmanagement.util.exception.ServiceException;
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
        try {
            return etapaRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Etapa no encontrada: " + e.getMessage());
        }
    }

    @Override
    public void save(EtapaDTO etapaDTO) {
        try {
            etapaRepository.save(cambiarEtapaDTO(etapaDTO));
        }catch (Exception e){
            throw new ServiceException("No se pudo guardar la etapa: " + e.getMessage());
        }
    }

    @Override
    public void update(Etapa etapa) {
        try {
            etapaRepository.save(etapa);
        } catch (Exception e) {
            throw new ServiceException("No se pudo actualizar la etapa: " + e.getMessage());
        }
    }

    @Override
    public List<Etapa> findAll() {
        try {
            return (List<Etapa>) etapaRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos: " + e.getMessage());
        }
    }

    @Override
    public List<Etapa> findAllByRama(Rama rama) {
        try{
            return (List<Etapa>) etapaRepository.findAllByRamaOrderByOrdenAsc(rama);

        } catch (Exception e) {
            throw new ServiceException("No se encontraron los datos de la rama: " + e.getMessage());
        }
    }

    @Override
    public Etapa cambiarEtapaDTO(EtapaDTO etapaDTO) {
        try{
            return new Etapa(
                    null,
                    etapaDTO.nombre(),
                    etapaDTO.orden(),
                    etapaDTO.rama());
        } catch (Exception e) {
            throw new ServiceException("No se puedo convertir el dto: " + e.getMessage());
        }
    }

}
