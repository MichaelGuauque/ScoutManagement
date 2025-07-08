package com.scoutmanagement.dto;

public record InformacionPersonaDTO(
        String primerNombre,
        String segundoNombre,
        String primerApellido,
        String segundoApellido,
        Long numeroDeDocumento,
        String alergias,
        String tipoDeSangre,
        String eps,
        boolean medicamentos,
        String especificacionMedicamentos,
        String primerContacto,
        long numeroPrimerContacto,
        String segundoContacto,
        long numeroSegundoContacto
) {
}
