package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.TipoDeSangre;

public record InformacionPersonaDTO(
        String primerNombre,
        String segundoNombre,
        String primerApellido,
        String segundoApellido,
        Long numeroDeDocumento,
        String alergias,
        String tipoDeSangre,
        String eps,
        String medicamentos,
        String especificacionMedicamentos,
        String primerContacto,
        long numeroPrimerContacto,
        String segundoContacto,
        long numeroSegundoContacto
) {
}
