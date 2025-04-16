package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private Persona miembro;

    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @Column(nullable = false)
    private boolean asistio;
}
