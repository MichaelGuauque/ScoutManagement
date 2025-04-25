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
    @Column (nullable = true, columnDefinition = "VARCHAR(25)")
    private String segundoApellido;
    @Column (nullable = false, unique = true, length = 11)
    private Long numeroDeDocumento;

    @Enumerated(EnumType.STRING)
    private TipoDeDocumento tipoDeDocumento;

    @Column (nullable = true)
    private LocalDate fechaNacimiento;
    @Column (nullable = true, columnDefinition = "VARCHAR(100)")
    private String direccion;
    @Column (nullable = true, columnDefinition = "VARCHAR(15)")
    private String eps;

    @Enumerated(EnumType.STRING)
    private TipoDeSangre tipoDeSangre;
    @Column (nullable = true)
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
    @OneToOne()
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private UserEntity userEntity;
}
