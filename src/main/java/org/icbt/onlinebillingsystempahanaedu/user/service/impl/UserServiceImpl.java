package org.icbt.onlinebillingsystempahanaedu.user.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.user.converter.UserConverter;
import org.icbt.onlinebillingsystempahanaedu.user.dao.UserDAO;
import org.icbt.onlinebillingsystempahanaedu.user.dao.impl.UserDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
        logger.info("add user (username) : " + dto.getUsername());
        if (userDAO.existsById(connection,dto.getId())){
            throw new SQLException("Cannot add user: ID already exists.");
        }
        if (userDAO.existsByUsername(connection,dto.getUsername())){
            throw new SQLException("Cannot add user: Username already exists");
        }

        boolean isSuccess = userDAO.add(connection,UserConverter.convertUserToUserEntity(dto));
        if (!isSuccess){
            throw new SQLException("Add user failed");
        }
        return true;
    }

    @Override
    public UserDTO searchById(Object... args) throws SQLException, ClassNotFoundException {
        logger.info("search user by id : " + args[0]);
        UserDTO userDTO = UserConverter.convertUserEntityToUserDTO(userDAO.searchById(connection,args[0]));
        if (userDTO == null){
            throw new SQLException("User not found");
        }
        return userDTO;
    }

    @Override
    public List<UserDTO> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        logger.info("search all users  : " + searchParams.get("username"));
        return UserConverter.convertUserEntityToUserDTOList(userDAO.getAll(connection,searchParams));
    }

    @Override
    public boolean update(UserDTO dto) throws SQLException, ClassNotFoundException {
        logger.info("update user (username) : " + dto.getUsername());
        // 1. Check if the user ID exists before updating
        if (!userDAO.existsById(connection,dto.getId())){
            throw new SQLException("Cannot update user: ID does not exist.");
        }

        //Proceed with update
        boolean updateSuccess = userDAO.update(connection,UserConverter.convertUserToUserEntity(dto));
        if (!updateSuccess){
            throw new SQLException("Update user failed");
        }
        return true;
    }

    @Override
    public boolean delete(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("User ID must be provided to delete.");
        }

        Object id = args[0];
        logger.info("delete user (user id) : " );

        // Check if the user exists before deleting
        if (!userDAO.existsById(connection, id)) {
            throw new SQLException("Cannot delete user: ID does not exist.");
        }

        // Call delete method in DAO
        boolean deleteSuccess = userDAO.delete(connection, id);
        if (!deleteSuccess) {
            throw new SQLException("Delete user failed");
        }

        return true;
    }
}
