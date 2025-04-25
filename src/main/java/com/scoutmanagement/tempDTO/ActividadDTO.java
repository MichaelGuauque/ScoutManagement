package com.scoutmanagement.tempDTO;

import com.scoutmanagement.persistence.model.Rama;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ActividadDTO(

        @NotBlank
        String nombre,
        @NotBlank
        String descripcion,
        @NotNull
        Rama rama,
        @NotNull
        LocalDate fecha,

        String ubicacion
) {
}

