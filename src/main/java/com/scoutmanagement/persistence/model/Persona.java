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
@Table(name = "person")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String primerNombre;
    @Column (nullable = true, columnDefinition = "VARCHAR(25)")
    private String segundoNombre;
    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String primerApellido;
    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String segundoApellido;
    @Column (nullable = false, unique = true, length = 10)
    private Long numeroDocumento;

    @Column (nullable = false)
    private LocalDate fechaNacimiento;
    @Column (nullable = false, columnDefinition = "VARCHAR(30)")
    private String direccion;
    @Column (nullable = false, columnDefinition = "VARCHAR(15)")
    private String eps;

    @Column (nullable = false, columnDefinition = "VARCHAR(3)")
    private String tipoSangre;
    @Column (nullable = false)
    private boolean tomaMedicamentos;
    @Column (nullable = true, columnDefinition = "VARCHAR(400)")
    private String especificacionMedicamentos;
    @Column (nullable = true, columnDefinition = "VARCHAR(400)")
    private String especificacionAlergiasYRestricciones;

    @Enumerated(EnumType.STRING)
    private Genero genero;
    @Enumerated(EnumType.STRING)
    private Rama rama;
    @Enumerated(EnumType.STRING)
    private Cargo cargo;
    @ManyToOne(targetEntity = Responsable.class)
    private Responsable responsable;
    @Column (nullable = false)
    private String usuario;
}
