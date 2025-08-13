package org.icbt.onlinebillingsystempahanaedu.user.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.util.PasswordSecurityUtil;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;
import org.icbt.onlinebillingsystempahanaedu.user.mapper.UserMapper;
import org.icbt.onlinebillingsystempahanaedu.user.dao.UserDAO;
import org.icbt.onlinebillingsystempahanaedu.user.dao.impl.UserDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:17 PM
 */

public class UserServiceImpl implements UserService {
    private final Connection connection = DBConnection.getConnection();
    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    public UserServiceImpl(){
        userDAO = new UserDAOImpl();
    }

    @Override
    public boolean add(UserDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            if (userDAO.findByUsername(connection, dto.getUsername()) != null) {
                throw new SQLException("Username already exists");
            }

            String hashedPassword = PasswordSecurityUtil.hashPassword(dto.getPassword());
            dto.setPassword(hashedPassword);

            UserEntity userEntity = UserMapper.convertUserToUserEntity(dto);
            boolean isAdded = userDAO.add(connection, userEntity);

            if (!isAdded) {
                connection.rollback();
                throw new SQLException("Add user failed");
            }

            connection.commit();
            logger.info("User added successfully: " + dto.getUsername());
            return true;

        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Failed to add user: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public UserDTO searchById(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("User ID must be provided.");
        }

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            logger.info("Searching user by ID: " + args[0]);

            UserEntity userEntity = userDAO.searchById(connection, args[0]);
            if (userEntity == null) {
                throw new SQLException("User not found with ID: " + args[0]);
            }

            return UserMapper.convertUserEntityToUserDTO(userEntity);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during searchById: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<UserDTO> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            logger.info("Fetching users with search params: " + searchParams);

            List<UserEntity> userEntities = userDAO.getAll(connection, searchParams);
            return UserMapper.convertUserEntityToUserDTOList(userEntities);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during getAll users: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public boolean update(UserDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            UserEntity existingUser = userDAO.searchById(connection, dto.getId());
            if (existingUser == null) {
                throw new SQLException("User not found for update");
            }

            if (dto.getPassword() != null && !dto.getPassword().isEmpty() &&
                    !PasswordSecurityUtil.verifyPassword(dto.getPassword(), existingUser.getPassword())) {
                String hashedPassword = PasswordSecurityUtil.hashPassword(dto.getPassword());
                dto.setPassword(hashedPassword);
            } else {
                dto.setPassword(existingUser.getPassword());
            }

            UserEntity userEntity = UserMapper.convertUserToUserEntity(dto);
            boolean isUpdated = userDAO.update(connection, userEntity);

            if (!isUpdated) {
                connection.rollback();
                throw new SQLException("Update user failed");
            }

            connection.commit();
            logger.info("User updated successfully: " + dto.getUsername());
            return true;

        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Failed to update user: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public boolean delete(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length < 2 || !(args[0] instanceof Integer) || !(args[1] instanceof Integer)) {
            throw new IllegalArgumentException("Delete requires user ID and deletedBy user ID.");
        }

        Integer userId = (Integer) args[0];
        Integer deletedBy = (Integer) args[1];

        final int INITIAL_ADMIN_ID = 1;
        if (userId.equals(INITIAL_ADMIN_ID)) {
            logger.warning("Attempt to delete initial admin user (ID: " + INITIAL_ADMIN_ID + ") was blocked.");
            throw new SQLException("Unauthorized: Cannot delete the initial admin user.");
        }

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            UserEntity existingUser = userDAO.searchById(connection, userId);
            if (existingUser == null) {
                throw new SQLException("User not found for deletion.");
            }

            boolean isDeleted = userDAO.delete(connection, userId);
            if (!isDeleted) {
                connection.rollback();
                throw new SQLException("User deletion failed.");
            }

            connection.commit();
            logger.info("User deleted successfully: ID " + userId);
            return true;

        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Failed to delete user: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public UserDTO loginUser(String username, String password) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();

            UserEntity userEntity = userDAO.findByUsername(connection, username);
            if (userEntity == null) {
                logger.warning("Login failed: Username not found -> " + username);
                throw new SQLException("Invalid username or password");
            }

            boolean isValid = PasswordSecurityUtil.verifyPassword(password, userEntity.getPassword());
            if (!isValid) {
                logger.warning("Login failed: Invalid password for username -> " + username);
                throw new SQLException("Invalid username or password");
            }

            logger.info("User logged in successfully: " + username);
            return UserMapper.convertUserEntityToUserDTO(userEntity);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during user login: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

}
