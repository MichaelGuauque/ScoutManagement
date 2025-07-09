package com.scoutmanagement.dto;

import com.scoutmanagement.persistence.model.Obtencion;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MiembroRamaDTO {
    private String nombreCompleto;
    private String cargo;
    private String imagen;
    private List<Obtencion> insignias;
}
