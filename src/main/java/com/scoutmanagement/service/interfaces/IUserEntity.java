package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.DTO.UserDTO;
import com.scoutmanagement.DTO.UserRegistroDTO;
import com.scoutmanagement.persistence.model.UserEntity;

import java.util.Optional;

public interface IUserEntity {
    void save(UserEntity userEntity);

    void updatePassword(String username, String oldPassword, String newPassword);

    UserEntity cambioUserDTO(UserRegistroDTO userDTO);

    Optional<UserEntity> findByEmail(UserDTO userDTO);

    Optional<UserEntity> findById(long id);

    Optional<UserEntity> findByEmail(String username);
}
