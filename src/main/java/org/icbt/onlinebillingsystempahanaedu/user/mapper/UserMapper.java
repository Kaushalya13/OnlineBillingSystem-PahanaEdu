package org.icbt.onlinebillingsystempahanaedu.user.mapper;

import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:09 PM
 */
public class UserMapper {
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
        userEntity.setCreatedAt(userDTO.getCreatedAt());
        userEntity.setUpdatedAt(userDTO.getUpdatedAt());
        userEntity.setDeletedAt(userDTO.getDeletedAt());
        
        return userEntity;
    }

    //Convert Entity to DTO
    public static UserDTO convertUserEntityToUserDTO(UserEntity userEntity) {
        if (userEntity == null){
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setPassword(userEntity.getPassword());
        userDTO.setRole(userEntity.getRole());
        userDTO.setCreatedAt(userEntity.getCreatedAt());
        userDTO.setUpdatedAt(userEntity.getUpdatedAt());
        userDTO.setDeletedAt(userEntity.getDeletedAt());

        return userDTO;
    }

    public static List<UserDTO> convertUserEntityToUserDTOList(List<UserEntity> userEntityList) {
        List<UserDTO> userDTOList = new ArrayList<>();
        if (userEntityList != null){
            for (UserEntity userEntity : userEntityList) {
                userDTOList.add(convertUserEntityToUserDTO(userEntity));
            }
        }
        return userDTOList;
    }


}
