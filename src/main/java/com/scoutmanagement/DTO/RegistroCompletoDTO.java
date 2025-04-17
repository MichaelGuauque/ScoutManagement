package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroCompletoDTO(

        @NotBlank
        String primerNombre,
        @NotBlank
        String segundoNombre,
        @NotBlank
        String primerApellido,
        @NotBlank
        String segundoApellido,
        @NotNull
        Long numeroDeDocumento,
        @NotNull
        TipoDeDocumento tipoDeDocumento,
        @NotNull
        Genero genero,
        @NotNull
        Rama rama,
        @NotNull
        Cargo cargo,

        // Usuario
        @Email @NotBlank String username,
        @NotNull Rol rol
) {}

