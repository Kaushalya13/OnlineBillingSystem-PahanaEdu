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
    boolean existsById(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

    boolean existsByUsername(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

}
