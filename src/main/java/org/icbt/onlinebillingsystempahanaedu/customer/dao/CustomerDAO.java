package org.icbt.onlinebillingsystempahanaedu.customer.dao;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudDAO;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * author : Niwanthi
 * date : 7/20/2025
 * time : 11:31 PM
 */
public interface CustomerDAO extends CrudDAO<CustomerEntity> {
    boolean existsById(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

    boolean existsByAccountNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

    boolean existsByPhoneNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

    CustomerEntity findByAccountNumber(Connection connection, Object... args) throws SQLException, ClassNotFoundException;
}

