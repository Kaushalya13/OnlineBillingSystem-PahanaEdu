package org.icbt.onlinebillingsystempahanaedu.user.service;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudService;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;

import java.sql.SQLException;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:16 PM
 */
public interface UserService extends CrudService<UserDTO> {

    UserDTO loginUser(String username, String password) throws SQLException, ClassNotFoundException;

}
