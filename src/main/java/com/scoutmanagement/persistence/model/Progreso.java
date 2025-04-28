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
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean estado;
    @ManyToOne(targetEntity = Persona.class)
    private Persona persona;
    @ManyToOne(targetEntity = Reto.class)
    private Reto reto;
}
