package org.icbt.onlinebillingsystempahanaedu.user.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.util.DAOUtil;
import org.icbt.onlinebillingsystempahanaedu.user.dao.UserDAO;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:15 PM
 */
public class UserDAOImpl implements UserDAO {
    private static final Logger log = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public List<UserEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        List<UserEntity> userList = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(
                    connection,
                    "SELECT * FROM users WHERE deleted_at IS NULL"
            );

            while (resultSet.next()) {
                userList.add(mapResultSetToUserEntity(resultSet));
            }
        } finally {
            DBConnection.closeResultSet(resultSet);
        }

        return userList;
    }


    @Override
    public UserEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("User ID must be provided.");
        }

        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(
                    connection,
                    "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL",
                    args[0]
            );

            if (resultSet.next()) {
                return mapResultSetToUserEntity(resultSet);
            }
            return null;
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
    }


    @Override
    public boolean add(Connection connection, UserEntity entity) throws SQLException, ClassNotFoundException {
        return DAOUtil.executeUpdate(
                connection,
                "INSERT INTO users (username,password,role) VALUES (?,?,?)",
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole().name()
        );
    }

    @Override
    public boolean update(Connection connection, UserEntity entity) throws SQLException, ClassNotFoundException {
        return DAOUtil.executeUpdate(
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
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("User ID must be provided for deletion.");
        }

        return DAOUtil.executeUpdate(
                connection,
                "UPDATE users SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL",
                args[0]
        );
    }

    @Override
    public UserEntity findByUsername(Connection connection, String username) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE username = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, username);
            if (resultSet.next()) {
                return mapResultSetToUserEntity(resultSet);
            }
            return null;
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public UserEntity validateUserCredentials(Connection connection, String username, String hashedPassword, String salt) throws SQLException, ClassNotFoundException {
        return findByUsername(connection, username);
    }

    @Override
    public int countActiveUsers(Connection connection) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(id) FROM users WHERE deleted_at IS NULL";
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    private UserEntity mapResultSetToUserEntity(ResultSet resultSet) throws SQLException {
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

}
