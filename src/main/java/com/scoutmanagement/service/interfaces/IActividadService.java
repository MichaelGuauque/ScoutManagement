package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.dto.ActividadDTO;
import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Rama;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IActividadService {

    public Optional<Actividad> findById(Long id);

    public List<Actividad> findAllActividad();

    public void crearActividad(ActividadDTO actividadDTO);

    public Actividad modificarActividad(Actividad actividad);

    public void eliminarActividad(Long id);

    public List<Actividad> findAllByRama(Rama rama);

    public Optional<Actividad> findByNombre(String nombre);

    Actividad cambiarActividadDTO(ActividadDTO actividadDTO);

    List<Actividad> findAllActividadesOrdenadas();

    public List<Actividad> filtrarYOrdenarActividadesPorTab(List<Actividad> actividades, Rama rama, String tab, LocalDate hoy, LocalDate fechaFiltro);

    public List<Actividad> paginarActividades(List<Actividad> actividades, int page, int size);

    public Map<Long, Boolean> encontrarActividadMasProxima(List<Actividad> actividades, int page, String tab);

}
