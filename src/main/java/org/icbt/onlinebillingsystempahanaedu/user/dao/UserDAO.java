package org.icbt.onlinebillingsystempahanaedu.user.dao;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudDAO;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:10 PM
 */
public interface UserDAO extends CrudDAO<UserEntity> {
    UserEntity findByUsername(Connection connection, String username) throws SQLException, ClassNotFoundException;

    UserEntity validateUserCredentials(Connection connection, String username, String hashedPassword, String salt) throws SQLException, ClassNotFoundException;

    int countActiveUsers(Connection connection) throws SQLException, ClassNotFoundException;


}
