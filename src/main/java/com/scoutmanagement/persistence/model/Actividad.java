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

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String nombre;

    @Column(nullable = false, columnDefinition = "VARCHAR(500)")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Rama rama;

    @Column(columnDefinition = "DATE")
    private LocalDate fecha;

    @Column(nullable = true, columnDefinition = "VARCHAR(200)")
    private String ubicacion;

}
