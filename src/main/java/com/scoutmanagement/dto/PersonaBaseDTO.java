package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.Cargo;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.model.TipoDeDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public abstract class PersonaBaseDTO {

    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;

    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    private String primerApellido;

    private String segundoApellido;

    @NotNull(message = "El número de documento es obligatorio")
    @Positive(message = "El número de documento debe ser un número positivo")
    private Long numeroDeDocumento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDeDocumento tipoDeDocumento;

    @NotNull(message = "La rama es obligatoria")
    private Rama rama;

    @NotNull(message = "El cargo es obligatorio")
    private Cargo cargo;


}
