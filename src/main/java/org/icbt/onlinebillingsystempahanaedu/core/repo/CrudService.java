package org.icbt.onlinebillingsystempahanaedu.core.repo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * author : Niwanthi
 * date : 7/23/2025
 * time : 7:26 PM
 */
public interface CrudService <T extends SuperDTO> extends SuperService{
    boolean add(T dto) throws SQLException, ClassNotFoundException;

    T searchById(Object... args) throws SQLException, ClassNotFoundException;

    List<T> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException;

    boolean update(T dto) throws SQLException, ClassNotFoundException;

    boolean delete(Object... args) throws SQLException, ClassNotFoundException;

}
