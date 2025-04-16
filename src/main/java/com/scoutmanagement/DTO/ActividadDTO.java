package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rama;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ActividadDTO (
        @NotBlank
        String nombre,
        @NotBlank
        String descripcion,
        @NotBlank
        Rama rama,
        @NotBlank
        LocalDate fecha,

        String ubicacion
){
}

