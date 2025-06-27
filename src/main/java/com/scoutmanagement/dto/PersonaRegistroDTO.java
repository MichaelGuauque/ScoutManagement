package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.*;
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
public class PersonaRegistroDTO extends PersonaBaseDTO {

    @NotNull(message = "El usuario es obligatorio")
    private UserRegistroDTO usuario;

}
