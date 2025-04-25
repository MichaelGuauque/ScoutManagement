package com.scoutmanagement.persistence.model;

import lombok.*;

@Getter
public enum Cargo {
    JEFE_MANADA("Jefe de Manada", Rol.ADULTO),
    JEFE_TROPA("Jefe de Tropa", Rol.ADULTO),
    JEFE_COMUNIDAD("Jefe de Comunidad", Rol.ADULTO),
    JEFE_CLAN("Jefe de Clan", Rol.ADULTO),

    LOBATO("Lobato", Rol.JOVEN),
    SCOUT("Scout", Rol.JOVEN),
    CAMINANTE("Caminante", Rol.JOVEN),
    ROVER("Rover", Rol.JOVEN);

    private final String label;
    private final Rol rolAsociado;

    Cargo(String label, Rol rolAsociado) {
        this.label = label;
        this.rolAsociado = rolAsociado;
    }

    public String getLabel() {
        return label;
    }

    public Rol getRolAsociado() {
        return rolAsociado;
    }

    @Override
    public String toString() {
        return label;
    }
}
