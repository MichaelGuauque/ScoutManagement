package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.Cargo;
import com.scoutmanagement.persistence.model.Rama;
import com.scoutmanagement.persistence.model.Rol;
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
public class PersonaActualizacionDTO extends PersonaBaseDTO {

    private Rol rol;
}

