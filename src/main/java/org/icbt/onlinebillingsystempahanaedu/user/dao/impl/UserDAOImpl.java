package org.icbt.onlinebillingsystempahanaedu.user.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
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
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public List<UserEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        List<UserEntity> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        if (searchParams != null) {
            String searchValue = searchParams.get("search");
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                sql.append(" AND (username LIKE ? OR role LIKE ?)");
                params.add("%" + searchValue.trim() + "%");
                params.add("%" + searchValue.trim() + "%");
            }
        }

        sql.append(" ORDER BY id ASC");

        ResultSet resultSet = null;

        try {
            if (!params.isEmpty()) {
                resultSet = DAOUtil.executeQuery(connection, sql.toString(), params.toArray());
            }else {
                resultSet = DAOUtil.executeQuery(connection, sql.toString());
            }

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
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            throw new IllegalArgumentException("ID parameter is required and must be an Integer.");
        }
        int id = (Integer) args[0];
        String sql = "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection,sql,id);
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
        String sql = "INSERT INTO users (username,password,role) VALUES (?,?,?)";

        try {
            return DAOUtil.executeUpdate(
                    connection, sql,
                    entity.getUsername(),
                    entity.getPassword(),
                    entity.getRole().name()
            );
        }catch (CustomException e){
            logger.log(Level.SEVERE,"Database error while adding User :"+ e.getMessage(),e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public boolean update(Connection connection, UserEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE users SET username=?,password=?,role=?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try {
            return DAOUtil.executeUpdate(
                    connection, sql,
                    entity.getUsername(),
                    entity.getPassword(),
                    entity.getRole().name(),
                    entity.getId()
            );
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Database error while Updating User :" + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("User ID must be provided to delete.");
        }

        Integer userId;
        try {
            userId = (Integer) args[0];
        }catch (CustomException e){
            throw new IllegalArgumentException("User ID must be provided to delete.");
        }

        String sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try {
            return DAOUtil.executeUpdate(connection,sql,userId);
        }catch (CustomException e){
            logger.log(Level.SEVERE,"Database error while Deleting User :"+ userId + ":" + e.getMessage(),e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
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
