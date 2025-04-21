package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Etapa;

public record RetoDTO(
        int numero,
        Etapa etapa,
        String descripcion) {
}
