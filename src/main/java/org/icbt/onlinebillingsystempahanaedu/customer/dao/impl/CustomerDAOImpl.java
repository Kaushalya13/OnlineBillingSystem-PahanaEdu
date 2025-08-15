package org.icbt.onlinebillingsystempahanaedu.customer.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.DAOUtil;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.CustomerDAO;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;

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
 * date : 7/20/2025
 * time : 11:31 PM
 */
public class CustomerDAOImpl implements CustomerDAO {
    private static final Logger logger = Logger.getLogger(CustomerDAOImpl.class.getName());

    public boolean add(Connection connection, CustomerEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO customers (cus_Name, cus_Address, cus_Mobile, cus_AccountNumber, units_consumed) VALUES (?, ?, ?, ?, ?)";

        try {
            return DAOUtil.executeUpdate(connection, sql,
                    entity.getCus_Name(),
                    entity.getCus_Address(),
                    entity.getCus_Mobile(),
                    entity.getCus_AccountNumber(),
                    entity.getUnits_consumed()
            );
        } catch (CustomException e){
            logger.log(Level.SEVERE, "Database error while adding Customer: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }

    }

    @Override
    public List<CustomerEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        List<CustomerEntity> customers = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM customers WHERE deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (searchParams != null) {
            String searchValue = searchParams.get("search");
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                sql.append(" AND (cus_Name LIKE ? OR cus_Mobile LIKE ? OR cus_AccountNumber LIKE ?)");
                params.add("%" + searchValue.trim() + "%");
                params.add("%" + searchValue.trim() + "%");
                params.add("%" + searchValue.trim() + "%");
            }
        }

        sql.append(" ORDER BY id ASC");

        ResultSet resultSet = null;
        try {
            if (!params.isEmpty()) {
                resultSet = DAOUtil.executeQuery(connection, sql.toString(), params.toArray());
            } else {
                resultSet = DAOUtil.executeQuery(connection, sql.toString());
            }

            while (resultSet.next()) {
                customers.add(mapResultSetToCustomerEntity(resultSet));
            }
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
        return customers;
    }

    @Override
    public CustomerEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            throw new IllegalArgumentException("ID parameter is required and must be an Integer.");
        }
        int id = (Integer) args[0];
        String sql = "SELECT * FROM customers WHERE id = ? AND deleted_at IS NULL";

        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, id);
            if (resultSet.next()) {
                return mapResultSetToCustomerEntity(resultSet);
            }
            return null;
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean update(Connection connection, CustomerEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE customers SET cus_Name = ?, cus_Address = ?, cus_Mobile = ?, cus_AccountNumber = ?, units_consumed = ? WHERE id = ? AND deleted_at IS NULL";
        try {
            return DAOUtil.executeUpdate(
                    connection,sql,
                    entity.getCus_Name(),
                    entity.getCus_Address(),
                    entity.getCus_Mobile(),
                    entity.getCus_AccountNumber(),
                    entity.getUnits_consumed(),
                    entity.getCus_Id()
            );
        }catch (CustomException e){
            logger.log(Level.SEVERE, "Database error while updating Customer: " + entity.getCus_Id() + ":" + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Customer ID must be provided to delete.");
        }
        Integer customerId = (Integer) args[0];
        String sql = "UPDATE customers SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";
        try {
            return DAOUtil.executeUpdate(connection, sql, customerId);
        } catch (RuntimeException e){
            logger.log(Level.SEVERE, "Database error while deleting Customer: " + customerId + ":" + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public boolean existsById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM customers WHERE id = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql, args);
            return resultSet.next();
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean existsByAccountNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM customers WHERE cus_AccountNumber = ? AND deleted_at IS NULL";
        String accountNumber = (String) args[0];
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, accountNumber);
            return resultSet.next();
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean existsByPhoneNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        String sql = "SELECT 1 FROM customers WHERE cus_Mobile = ? AND deleted_at IS NULL";
        String phoneNumber = (String) args[0];
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, phoneNumber);
            return resultSet.next();
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public CustomerEntity findByAccountNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM customers WHERE cus_AccountNumber = ? AND deleted_at IS NULL";
        String accountNumber = (String) args[0];
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, accountNumber);
            if (resultSet.next()) {
                return mapResultSetToCustomerEntity(resultSet);
            }
            return null;
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    private CustomerEntity mapResultSetToCustomerEntity(ResultSet resultSet) throws SQLException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCus_Id(resultSet.getInt("id"));
        customerEntity.setCus_Name(resultSet.getString("cus_Name"));
        customerEntity.setCus_Address(resultSet.getString("cus_Address"));
        customerEntity.setCus_Mobile(resultSet.getString("cus_Mobile"));
        customerEntity.setCus_AccountNumber(resultSet.getString("cus_AccountNumber"));
        customerEntity.setUnits_consumed(resultSet.getInt("units_consumed"));
        customerEntity.setCreatedAt(resultSet.getTimestamp("created_at"));
        customerEntity.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        customerEntity.setDeletedAt(resultSet.getTimestamp("deleted_at"));
        return customerEntity;
    }
}
