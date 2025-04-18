package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rol;

public record UserDTO(
        String username,
        String password,
        String newPassword,
        Rol rol
) {
}
