package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rama;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EtapaDTO(
        @NotBlank
        String nombre,
        @NotNull
        int orden,
        @NotNull
        Rama rama) {
}
