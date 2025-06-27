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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"numero", "etapa"})})
public class Reto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int numero;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    @ManyToOne(targetEntity = Etapa.class)
    @JoinColumn(name = "etapa", nullable = false)
    private Etapa etapa;
}
