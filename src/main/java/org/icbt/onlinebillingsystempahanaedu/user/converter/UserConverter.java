package org.icbt.onlinebillingsystempahanaedu.user.converter;

import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:09 PM
 */
public class UserConverter {
    // Convert DTO to Entity
    public static UserEntity convertUserToUserEntity(UserDTO userDTO) {
        if (userDTO == null){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDTO.getId());
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setRole(userDTO.getRole());
        
        return userEntity;
    }
}
