package org.icbt.onlinebillingsystempahanaedu.customer.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.CustomerDAO;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.impl.CustomerDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;
import org.icbt.onlinebillingsystempahanaedu.customer.mapper.CustomerMapper;
import org.icbt.onlinebillingsystempahanaedu.customer.service.CustomerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/20/2025
 * time : 11:34 PM
 */
public class CustomerServiceImpl implements CustomerService {
    private final CustomerDAO customerDAO;
    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());

    public CustomerServiceImpl() {
        this.customerDAO = new CustomerDAOImpl();
    }

    @Override
    public CustomerDTO findByAccountNumber(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length < 1 || !(args[0] instanceof String)) {
            throw new IllegalArgumentException("Search by AccountNumber requires customer AccountNumber (String).");
        }

        Connection connection = null;
        String accountNumber = (String) args[0];

        try {
            connection = DBConnection.getConnection();
            CustomerEntity entity = customerDAO.findByAccountNumber(connection, accountNumber);

            if (entity == null) {
                logger.log(Level.WARNING,"Customer accountNumber " + accountNumber + " not found.");
                return null;
            }

            logger.log(Level.INFO,"Customer accountNumber " + accountNumber + " found.");
            return CustomerMapper.convertCustomerEntityToCustomerDTO(entity);
        }catch (SQLException e) {
            logger.log(Level.SEVERE,"SQL Exception: " + e.getMessage());
            throw e;
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean add(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.log(Level.INFO,"New Customer adding",dto.getCus_Name());

            if (customerDAO.existsByAccountNumber(connection, dto.getCus_AccountNumber())) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_ALREADY_EXISTS);
            }

            if (customerDAO.existsByPhoneNumber(connection, dto.getCus_Mobile())){
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_ALREADY_EXISTS);
            }

            CustomerEntity customerEntity = CustomerMapper.convertCustomerToCustomerEntity(dto);

            boolean added = customerDAO.add(connection, customerEntity);

            if (added) {
                connection.commit();
                logger.log(Level.INFO,"Customer added successfully",dto.getCus_Name());
                return true;
            }else {
                connection.rollback();
                logger.log(Level.INFO,"Customer add failed",dto.getCus_Name());
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_CREATION_FAILED);
            }
        }catch (SQLException e) {
            DBConnection.closeConnection(connection);
            logger.log(Level.SEVERE,"SQL Exception: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public CustomerDTO searchById(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            logger.warning("Invalid searchById parameters. Expected an Integer ID.");
            return null;
        }

        Integer id = (Integer) args[0];
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            CustomerEntity entity = customerDAO.searchById(connection, id);
            if (entity == null) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_NOT_FOUND);
            }
            return CustomerMapper.convertCustomerEntityToCustomerDTO(entity);
        }catch (SQLException e) {
            logger.log(Level.SEVERE,"SQL Exception: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<CustomerDTO> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            List<CustomerEntity> entities = customerDAO.getAll(connection, searchParams);
            return CustomerMapper.convertCustomerEntityListToCustomerDTOList(entities);
        }catch (SQLException e) {
            logger.log(Level.SEVERE,"Error while getting customers: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean update(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.log(Level.INFO,"Customer updating",dto.getCus_Id());

            CustomerEntity existingCustomer = customerDAO.searchById(connection, dto.getCus_Id());
            if (existingCustomer == null) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_NOT_FOUND);
            }

            if (!existingCustomer.getCus_Mobile().equals(dto.getCus_Mobile())) {
                if (customerDAO.existsByPhoneNumber(connection, dto.getCus_Mobile())) {
                    throw new CustomException(CustomException.ExceptionType.CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS);
                }
            }
            dto.setCus_Id(existingCustomer.getCus_Id());

            CustomerEntity updateEntity = CustomerMapper.convertCustomerToCustomerEntity(dto);
            boolean idUpdated = customerDAO.update(connection, updateEntity);
            if (idUpdated) {
                connection.commit();
                logger.log(Level.INFO,"Customer updated successfully",dto.getCus_Id());
                return true;
            }else {
                connection.rollback();
                logger.log(Level.INFO,"Customer update failed",dto.getCus_Id());
                throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
            }
        }catch (SQLException e) {
            DBConnection.closeConnection(connection);
            logger.log(Level.SEVERE, "Error during customer updating for Id" + dto.getCus_Id());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean delete(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length < 2 || !(args[0] instanceof Integer) || !(args[1] instanceof String)) {
            throw new IllegalArgumentException("Delete requires deleter ID (Integer) and customer ID (String).");
        }

        String customerIdStr = (String) args[1];
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.log(Level.INFO, "Attempting to soft delete Customer ID " + customerIdStr);

            int customerId = Integer.parseInt(customerIdStr);

            if (!customerDAO.existsById(connection, customerId)) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_NOT_FOUND);
            }

            boolean isDeleted = customerDAO.delete(connection, customerId);
            if (isDeleted) {
                connection.commit();
                logger.log(Level.INFO, "Customer deleted successfully: " + customerId);
                return true;
            } else {
                connection.rollback();
                logger.log(Level.INFO, "Customer delete failed: " + customerId);
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_DELETION_FAILED);
            }
        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Error during customer deleting for Id" + customerIdStr);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }
}
