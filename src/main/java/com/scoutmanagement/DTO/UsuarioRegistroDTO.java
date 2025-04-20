package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRegistroDTO(
        @NotBlank
        @Email
        String username,
        @NotBlank
        Rol rol

) {
}
