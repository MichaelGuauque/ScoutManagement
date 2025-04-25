package com.scoutmanagement.tempDTO;

import com.scoutmanagement.persistence.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PersonaRegistroDTO {
    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;

    @NotBlank(message = "El segundo nombre es obligatorio")
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    private String primerApellido;

    @NotBlank(message = "El segundo apellido es obligatorio")
    private String segundoApellido;

    @NotNull(message = "El número de documento es obligatorio")
    @Positive(message = "El número de documento debe ser un número positivo")
    private Long numeroDeDocumento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDeDocumento tipoDeDocumento;

    @NotNull(message = "El género es obligatorio")
    private Genero genero;

    @NotNull(message = "La rama es obligatoria")
    private Rama rama;

    @NotNull(message = "El cargo es obligatorio")
    private Cargo cargo;

    @NotBlank
    private UserEntity userEntity;

    public PersonaRegistroDTO(PersonaConUsuarioDTO personaConUsuarioDTO) {
        this.primerNombre = personaConUsuarioDTO.getPrimerNombre();
        this.segundoNombre = personaConUsuarioDTO.getSegundoNombre();
        this.primerApellido = personaConUsuarioDTO.getPrimerApellido();
        this.segundoApellido = personaConUsuarioDTO.getSegundoApellido();
        this.numeroDeDocumento = personaConUsuarioDTO.getNumeroDeDocumento();
        this.tipoDeDocumento = personaConUsuarioDTO.getTipoDeDocumento();
        this.genero = personaConUsuarioDTO.getGenero();
        this.rama = personaConUsuarioDTO.getRama();
        this.cargo = personaConUsuarioDTO.getCargo();
    }
}
