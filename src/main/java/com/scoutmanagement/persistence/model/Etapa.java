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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"orden", "rama"})
        }
)
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int orden;
    @Column(name = "rama")
    @Enumerated(EnumType.STRING)
    private Rama rama;
}
