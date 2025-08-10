package org.icbt.onlinebillingsystempahanaedu.item.dao;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudDAO;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:10 PM
 */
public interface ItemDAO extends CrudDAO<ItemEntity> {
    boolean existsByName(Connection connection, String name) throws SQLException, ClassNotFoundException;

    boolean findItemById(Connection connection, Integer id) throws SQLException, ClassNotFoundException;

    ItemEntity findByName(Connection connection, String name) throws SQLException, ClassNotFoundException;

    int getItemsCount(Connection connection) throws SQLException, ClassNotFoundException;
}
