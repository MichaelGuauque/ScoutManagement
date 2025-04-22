package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Etapa;

public record RetoDTO(
        int numero,
        String etapa,
        String descripcion) {
}
