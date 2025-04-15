package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne(targetEntity = Rama.class, optional = false)
    private Rama rama;

    @Column(columnDefinition = "DATE")
    private LocalDate fecha;

    @Column(nullable = false)
    private String ubicacion;
}
