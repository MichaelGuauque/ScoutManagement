package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.AsistenciaDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Asistencia;
import com.scoutmanagement.persistence.model.Persona;
import com.scoutmanagement.persistence.model.Rama;

import java.util.List;
import java.util.Map;

public interface IAsistenciaService {
    
    /**
     * Obtiene todas las asistencias registradas para una actividad
     * @param actividadId ID de la actividad
     * @return Lista de asistencias
     */
    List<Asistencia> findByActividad(Long actividadId);
    
    /**
     * Crea o actualiza un registro de asistencia
     * @param asistenciaDTO DTO con la información de asistencia
     * @return La asistencia registrada
     */
    Asistencia registrarAsistencia(AsistenciaDTO asistenciaDTO);
    
    /**
     * Registra asistencias en lote para una actividad
     * @param actividadId ID de la actividad
     * @param asistenciasPorMiembro Mapa con el ID del miembro como clave y el estado de asistencia como valor
     */
    void registrarAsistenciasMasivas(Long actividadId, Map<Long, Boolean> asistenciasPorMiembro);
    
    /**
     * Verifica si existe un registro de asistencia para un miembro en una actividad
     * @param miembroId ID del miembro
     * @param actividadId ID de la actividad
     * @return true si existe, false si no
     */
    boolean existeAsistencia(Long miembroId, Long actividadId);
    
    /**
     * Obtiene la asistencia de un miembro a una actividad
     * @param miembroId ID del miembro
     * @param actividadId ID de la actividad
     * @return Asistencia si existe, o null
     */
    Asistencia findByMiembroAndActividad(Long miembroId, Long actividadId);
    
    /**
     * Prepara el registro de asistencias para una actividad
     * @param actividad La actividad para la que se registrarán asistencias
     * @param miembros Lista de miembros disponibles para la actividad
     * @return Lista de asistencias preparada (con asistio=false por defecto)
     */
    List<Asistencia> prepararRegistroAsistencias(Actividad actividad, List<Persona> miembros);

    /**
     * Convierte un DTO de asistencia a una entidad Asistencia
     * @param asistenciaDTO DTO con la información de asistencia
     * @return Entidad Asistencia
     */
    Asistencia convertirAsistenciaDTO(AsistenciaDTO asistenciaDTO);
    
    /**
     * Crea automáticamente registros de asistencia para todos los miembros de la rama
     * correspondiente a una actividad recién creada
     * @param actividad Actividad recién creada
     * @return Lista de asistencias creadas
     */
    List<Asistencia> crearAsistenciasAutomaticas(Actividad actividad);
    
    /**
     * Obtiene todas las personas que pertenecen a una rama específica
     * @param rama Rama de los miembros a buscar
     * @return Lista de personas pertenecientes a la rama
     */
    List<Persona> findPersonasByRama(Rama rama);
    
    /**
     * Guarda una lista de asistencias en la base de datos
     * @param asistencias Lista de asistencias a guardar
     * @return Lista de asistencias guardadas
     */
    List<Asistencia> guardarAsistenciasMasivas(List<Asistencia> asistencias);
    
    /**
     * Obtiene todas las asistencias para una actividad ordenadas por ID de miembro
     * @param actividadId ID de la actividad
     * @return Lista de asistencias ordenadas
     */
    List<Asistencia> findByActividadOrdenado(Long actividadId);
}
