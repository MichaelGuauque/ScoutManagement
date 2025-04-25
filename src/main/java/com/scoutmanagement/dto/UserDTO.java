package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.Rol;

public record UserDTO(
        String username,
        String password,
        String newPassword,
        Rol rol
) {
}
