package com.scoutmanagement.dto;

public record ResponsablePersonaDTO(
        String nombresAcudiente,
        String apellidosAcudiente,
        Long numeroDocumentoResponsable,
        String direccion,
        Long telefonoResponsable
) {
}
