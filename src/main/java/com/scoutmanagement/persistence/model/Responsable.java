package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Responsable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    private String nombres;
    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    private String apellidos;
    @Column(nullable = true, unique = true, length = 10)
    private Long numeroDocumento;
    @Column(nullable = true, length = 10)
    private long telefono;
    @OneToMany(mappedBy = "responsable")
    private List<Persona> personas;

}
