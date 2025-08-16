package org.icbt.onlinebillingsystempahanaedu.item.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.DAOUtil;
import org.icbt.onlinebillingsystempahanaedu.item.dao.ItemDAO;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;

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
 * date : 8/9/2025
 * time : 1:11 PM
 */
public class ItemDAOImpl implements ItemDAO {
    public static final Logger logger = Logger.getLogger(ItemDAOImpl.class.getName());

    @Override
    public boolean existsByName(Connection connection, String name) throws SQLException, ClassNotFoundException {
        String sql = "select * from items where item_name = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql, name);
            return resultSet.next();
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean findItemById(Connection connection, Integer id) throws SQLException, ClassNotFoundException {
        String sql = "select * FROM items WHERE id = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql, id);
            return resultSet.next();
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public ItemEntity findByName(Connection connection, String name) throws SQLException, ClassNotFoundException {
        String sql = "select * from items where item_name = ? AND deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql, name);
            if(resultSet.next()){
                return mapResultSetToItemEntity(resultSet);
            }
            return null;
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public int getItemsCount(Connection connection) throws SQLException, ClassNotFoundException {
        String sql = "select count(id) from items where deleted_at IS NULL";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql);
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean add(Connection connection, ItemEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO items (item_name, unit_price, quantity) VALUES (?, ?, ?)";
        try {
            return DAOUtil.executeUpdate(connection, sql,
                    entity.getItemName(),
                    entity.getUnitPrice(),
                    entity.getQuantity()
            );
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Database error while adding item: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public List<ItemEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        List<ItemEntity> items = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM items WHERE deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        // Add search filters if present
        if (searchParams != null) {
            String searchValue = searchParams.get("search");
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                sqlBuilder.append(" AND item_name LIKE ?");
                params.add("%" + searchValue.trim() + "%");
            }
        }

        sqlBuilder.append(" ORDER BY id ASC");

        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sqlBuilder.toString(), params.toArray());
            while (resultSet.next()) {
                items.add(mapResultSetToItemEntity(resultSet));
            }
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
        return items;
    }

    @Override
    public ItemEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            throw new IllegalArgumentException("ID parameter is required and must be an Integer.");
        }

        int id = (Integer) args[0];
        String sql = "SELECT * FROM items WHERE id = ? AND deleted_at IS NULL";

        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection, sql, id);
            if (resultSet.next()) {
                return mapResultSetToItemEntity(resultSet);
            }
            return null;
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean update(Connection connection, ItemEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE items SET item_name = ?, unit_price = ?, quantity = ? WHERE id = ? AND deleted_at IS NULL";
        try {
            return DAOUtil.executeUpdate(connection, sql,
                    entity.getItemName(),
                    entity.getUnitPrice(),
                    entity.getQuantity(),
                    entity.getId()
            );
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Database error while updating item with ID " + entity.getId() + ": " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Item ID must be provided to delete.");
        }

        Integer itemId = (Integer) args[0];
        String sql = "UPDATE items SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try {
            return DAOUtil.executeUpdate(connection, sql, itemId);
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Database error while deleting item with ID " + itemId + ": " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    private ItemEntity mapResultSetToItemEntity(ResultSet resultSet) throws SQLException {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(resultSet.getInt("id"));
        itemEntity.setItemName(resultSet.getString("item_name"));
        itemEntity.setUnitPrice(resultSet.getDouble("unit_price"));
        itemEntity.setQuantity(resultSet.getInt("quantity"));
        itemEntity.setCreatedAt(resultSet.getTimestamp("created_at"));
        itemEntity.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        itemEntity.setDeletedAt(resultSet.getTimestamp("deleted_at"));
        return itemEntity;
    }
}
