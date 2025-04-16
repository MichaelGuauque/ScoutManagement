package com.scoutmanagement.persistence.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column (nullable = false, columnDefinition = "VARCHAR(50)")
    private String nombres;
    @Column (nullable = false, columnDefinition = "VARCHAR(50)")
    private String apellidos;
    @Column (nullable = false, unique = true, length = 10)
    private long numeroDocumento;
    @Column (nullable = false, length = 10)
    private long telefono;
}
