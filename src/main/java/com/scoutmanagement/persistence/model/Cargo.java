package com.scoutmanagement.persistence.model;

import lombok.*;

@Getter
public enum Cargo {

    JEFE_MANADA(RoleEnum.ADMIN),
    JEFE_TROPA(RoleEnum.ADMIN),
    JEFE_COMUNIDAD(RoleEnum.ADMIN),
    JEFE_CLAN(RoleEnum.ADMIN),

    LOBATO(RoleEnum.USER),
    SCOUT(RoleEnum.USER),
    CAMINANTE(RoleEnum.USER),
    ROVER(RoleEnum.USER);

    private final RoleEnum rol;

    Cargo(RoleEnum rol){
        this.rol=rol;
    }
}
