package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Obtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean estado;
    private LocalDate fecha;
    @ManyToOne(targetEntity = Persona.class)
    private Persona persona;
    @ManyToOne(targetEntity = Etapa.class)
    private Etapa etapa;
}