package com.scoutmanagement.service.implementation;

import com.scoutmanagement.dto.MiembroRamaDTO;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.repository.PersonaRepository;
import com.scoutmanagement.service.interfaces.IAsistenciaService;
import com.scoutmanagement.service.interfaces.IMiembroService;
import com.scoutmanagement.service.interfaces.IObtencionService;
import com.scoutmanagement.service.interfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MiembroService implements IMiembroService {

    @Autowired
    private IAsistenciaService asistenciaService;
    @Autowired
    private IObtencionService obtencionService;
    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public List<MiembroRamaDTO> mostrarMiembrosRama(Persona personaSesion) {
        List<MiembroRamaDTO> miembrosRama = new ArrayList<>();
        List<Persona> miembros = personaRepository.findByUserEntityActivoTrueAndRama(personaSesion.getRama());
        for (Persona miembro : miembros) {
            miembrosRama.add(MiembroRamaDTO.builder()
                    .nombreCompleto(miembro.getNombreCompleto())
                    .cargo(miembro.getCargo().toString())
                    .insignias(obtencionService.ultimasObtenciones(miembro))
                    .build());
        }
        return miembrosRama;
    }
}
