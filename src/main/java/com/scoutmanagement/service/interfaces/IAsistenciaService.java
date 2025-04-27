package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.AsistenciaDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;

import java.util.List;
import java.util.Map;

public interface IAsistenciaService {



    Asistencia registrarAsistencia(AsistenciaDTO asistenciaDTO);


    void registrarAsistenciasMasivas(Long actividadId, Map<Long, Boolean> asistenciasPorMiembro);




    List<Asistencia> prepararRegistroAsistencias(Actividad actividad, List<Persona> miembros);


    Asistencia convertirAsistenciaDTO(AsistenciaDTO asistenciaDTO);



    List<Persona> findPersonasByRama(Rama rama);



    List<Asistencia> findByActividadOrdenado(Long actividadId);
}
