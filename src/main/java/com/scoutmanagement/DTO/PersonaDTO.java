package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PersonaDTO(

        @NotBlank(message = "El primer nombre es obligatorio")
        String primerNombre,

        @NotBlank(message = "El segundo nombre es obligatorio")
        String segundoNombre,

        @NotBlank(message = "El primer apellido es obligatorio")
        String primerApellido,

        @NotBlank(message = "El segundo apellido es obligatorio")
        String segundoApellido,

        @NotNull(message = "El número de documento es obligatorio")
        @Positive(message = "El número de documento debe ser un número positivo")
        Long numeroDeDocumento,

        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDeDocumento tipoDeDocumento,

        @NotNull(message = "El género es obligatorio")
        Genero genero,

        @NotNull(message = "La rama es obligatoria")
        Rama rama,

        @NotNull(message = "El cargo es obligatorio")
        Cargo cargo,

        @NotBlank
        UserEntity userEntity



) {}

