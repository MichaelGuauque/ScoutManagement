package com.scoutmanagement.persistence.model;

import lombok.Getter;



public enum TipoDeSangre {
    O_POSITIVO("O+"),
    O_NEGATIVO("O-"),
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-");

    private final String alias;

    TipoDeSangre(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return alias;
    }
}
