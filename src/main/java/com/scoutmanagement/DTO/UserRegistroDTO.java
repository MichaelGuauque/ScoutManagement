package com.scoutmanagement.DTO;

import com.scoutmanagement.persistence.model.Rol;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistroDTO{
    private String username;
    private Rol rol;
}


