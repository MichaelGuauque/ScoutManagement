package com.scoutmanagement.tempDTO;

import com.scoutmanagement.persistence.model.Actividad;
import com.scoutmanagement.persistence.model.Persona;
import jakarta.validation.constraints.NotNull;

public record AsistenciaDTO(

        @NotNull
        Persona miembro,
        @NotNull
        Actividad actividad,
        @NotNull
        Boolean asistio
) {
}
