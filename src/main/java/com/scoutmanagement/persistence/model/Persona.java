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
    private Long idPersona;

    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String priNombre;
    @Column (nullable = true, columnDefinition = "VARCHAR(25)")
    private String segNombre;
    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String priApellido;
    @Column (nullable = false, columnDefinition = "VARCHAR(25)")
    private String segApellido;
    @Column (nullable = false, unique = true)
    private Long numeroDocumento;

    @Column (nullable = false)
    private LocalDate fechaNacimiento;
    @Column (nullable = false, columnDefinition = "VARCHAR(30)")
    private String correo;
    @Column (nullable = false, columnDefinition = "VARCHAR(30)")
    private String direccion;
    @Column (nullable = false, columnDefinition = "VARCHAR(15)")
    private String eps;

    @Column (nullable = false)
    private String tipoSangre;
    @Column (nullable = false)
    private boolean tomaMedicamentos;
    @Column (nullable = true, columnDefinition = "VARCHAR(400)")
    private String especificacionMedicamentos;
    @Column (nullable = true, columnDefinition = "VARCHAR(400)")
    private String alergiasYRestr;

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
    private UserEntity usuario;
}
