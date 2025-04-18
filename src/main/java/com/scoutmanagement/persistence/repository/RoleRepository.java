package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.Rol;
import com.scoutmanagement.persistence.model.RoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity findByRoleEnum(Rol role);
}
