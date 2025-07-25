package org.icbt.onlinebillingsystempahanaedu.user.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.util.DAOUtil;
import org.icbt.onlinebillingsystempahanaedu.user.dao.UserDAO;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:15 PM
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public List<UserEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DAOUtil.executeQueryOrUpdate(
                connection,
                "SELECT * FROM users WHERE deleted_at IS NULL"
        );
        List<UserEntity> list = new ArrayList<>();
        while (resultSet.next()) {
            UserEntity user = new UserEntity();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setCreatedAt(resultSet.getTimestamp("created_at"));
            user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            user.setDeletedAt(resultSet.getTimestamp("deleted_at"));
            list.add(user);
        }

        return list;
    }


    @Override
    public UserEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DAOUtil.executeQueryOrUpdate(
                connection,
                "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL",
                args[0]
        );
        if (resultSet.next()) {
            UserEntity user = new UserEntity();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setCreatedAt(resultSet.getTimestamp("created_at"));
            user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            user.setDeletedAt(resultSet.getTimestamp("deleted_at"));
            return user;
        }
        return null;
    }

    @Override
    public boolean add(Connection connection, UserEntity entity) throws SQLException, ClassNotFoundException {
        return DAOUtil.executeQueryOrUpdate(
                connection,
                "INSERT INTO users (username,password,role) VALUES (?,?,?)",
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole().name()
        );
    }

    @Override
    public boolean update(Connection connection, UserEntity entity) throws SQLException, ClassNotFoundException {
        return DAOUtil.executeQueryOrUpdate(
                connection,
                "UPDATE users SET username = ?, password = ?, role = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL",
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole().name(),
                entity.getId()
        );
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        return DAOUtil.executeQueryOrUpdate(
                connection,
                "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL",
                args[0]
        );
    }

    @Override
    public boolean existsById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DAOUtil.executeQueryOrUpdate(
                connection,
                "SELECT 1 FROM users WHERE id = ? AND deleted_at IS NULL",
                args[0]
        );
        return resultSet.next();
    }

    @Override
    public boolean existsByUsername(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DAOUtil.executeQueryOrUpdate(
                connection,
                "SELECT 1 FROM users WHERE username = ? AND deleted_at IS NULL",
                args[0]
        );
        return resultSet.next();
    }
}
