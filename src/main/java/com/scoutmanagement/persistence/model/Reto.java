package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Reto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private String descripcion;
    @ManyToOne(targetEntity = Etapa.class)
    private Etapa etapa;
}
