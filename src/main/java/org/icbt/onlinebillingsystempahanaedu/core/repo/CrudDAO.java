package org.icbt.onlinebillingsystempahanaedu.core.repo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 6:46 PM
 */
public interface CrudDAO<T extends SuperEntity> extends SuperDAO{
    boolean add(Connection connection, T entity) throws SQLException, ClassNotFoundException;

    List<T> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException;

    T searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException;

    boolean update(Connection connection, T entity) throws SQLException, ClassNotFoundException;

    boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException;
}
