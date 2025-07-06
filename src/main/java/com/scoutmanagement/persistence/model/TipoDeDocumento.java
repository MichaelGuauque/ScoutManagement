package com.scoutmanagement.persistence.model;

import lombok.Getter;

@Getter
public enum TipoDeDocumento {
    CC("Cédula de Ciudadanía"),
    TI("Tarjeta de Identidad"),
    CE("Cédula de Extranjería"),
    RC("Registro Civil");

    private final String descripcion;

    TipoDeDocumento(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getDescripcion() {
        return descripcion;
    }

}
