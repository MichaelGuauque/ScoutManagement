package com.scoutmanagement.persistence.repository;

import com.scoutmanagement.persistence.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{
    Optional<UserEntity> findUserEntityByUsername(String username);
}
