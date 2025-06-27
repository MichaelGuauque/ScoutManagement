package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.MiembroRamaDTO;
import com.scoutmanagement.persistence.model.Persona;

import java.util.List;

public interface IMiembroService {

    List<MiembroRamaDTO> mostrarMiembrosRama(Persona personaSesion);
}
