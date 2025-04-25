package com.scoutmanagement.service.interfaces;

import com.scoutmanagement.tempDTO.UserDTO;
import com.scoutmanagement.tempDTO.UserRegistroDTO;
import com.scoutmanagement.persistence.model.UserEntity;

import java.util.Optional;

public interface IUserEntity {
    void save(UserEntity userEntity);

    void updatePassword(String username, String oldPassword, String newPassword);

    UserEntity cambioUserDTO(UserRegistroDTO userDTO);

    Optional<UserEntity> findByEmail(UserDTO userDTO);

    Optional<UserEntity> findById(long id);
}
