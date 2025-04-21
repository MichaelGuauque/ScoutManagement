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
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String orden;
    @Column(name = "etapa")
    @Enumerated(EnumType.STRING)
    private Rama rama;
}
