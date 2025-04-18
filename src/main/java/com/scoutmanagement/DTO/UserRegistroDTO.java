package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rol;

public record UserRegistroDTO(
        String username,
        Rol rol
) {
}
