package com.scoutmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioDTO(
        @NotBlank
        @Email
        String username,
        @NotBlank
        String password

) {
}
