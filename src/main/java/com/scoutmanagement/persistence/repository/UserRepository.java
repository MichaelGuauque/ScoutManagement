package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long>{
    Optional<UserEntity> findUserEntityByUsername(String username);
}
