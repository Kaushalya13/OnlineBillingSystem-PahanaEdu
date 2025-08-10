package org.icbt.onlinebillingsystempahanaedu.item.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.item.dao.ItemDAO;
import org.icbt.onlinebillingsystempahanaedu.item.dao.impl.ItemDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;
import org.icbt.onlinebillingsystempahanaedu.item.mapper.ItemMapper;
import org.icbt.onlinebillingsystempahanaedu.item.service.ItemService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:15 PM
 */
public class ItemServiceImpl implements ItemService {
    private final ItemDAO itemDAO;
    private static final Logger logger = Logger.getLogger(ItemServiceImpl.class.getName());

    public ItemServiceImpl() {
        this.itemDAO = new ItemDAOImpl();
    }

    @Override
    public ItemDTO findByName(String name) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            ItemEntity entity = itemDAO.findByName(connection, name);

            if (entity == null) {
                logger.log(Level.WARNING, "Item not found with name: {0}", name);
                return null;
            }

            logger.log(Level.INFO, "Item found with name: {0}", name);
            return ItemMapper.convertItemEntityToItemDTO(entity);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during search by name: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public boolean add(ItemDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.log(Level.INFO, "Adding new item: {0}", dto.getItemName());

            // Check if the item already exists
            if (itemDAO.existsByName(connection, dto.getItemName())) {
                throw new CustomException(CustomException.ExceptionType.ITEM_ALREADY_EXISTS);
            }

            // Convert DTO to Entity
            ItemEntity itemEntity = ItemMapper.convertItemDTOToItemEntity(dto);

            // Attempt to insert into DB
            boolean added = itemDAO.add(connection, itemEntity);

            if (added) {
                connection.commit();
                logger.log(Level.INFO, "Item successfully added: {0}", dto.getItemName());
                return true;
            } else {
                connection.rollback();
                logger.log(Level.WARNING, "Failed to add item: {0}. Transaction rolled back.", dto.getItemName());
                throw new CustomException(CustomException.ExceptionType.ITEM_CREATION_FAILED);
            }

        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Error while adding item: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public ItemDTO searchById(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            logger.warning("Invalid searchById parameters. Expected an Integer ID.");
            return null;
        }

        Integer id = (Integer) args[0];
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            logger.log(Level.INFO, "Searching for item with ID: {0}", id);

            ItemEntity entity = itemDAO.searchById(connection, id);

            if (entity == null) {
                logger.log(Level.WARNING, "No item found for ID: {0}", id);
                return null;
            }

            return ItemMapper.convertItemEntityToItemDTO(entity);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while searching item by ID: " + e.getMessage(), e);
            throw e; // rethrow so caller can handle
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public List<ItemDTO> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        List<ItemDTO> resultList = new ArrayList<>();

        try {
            connection = DBConnection.getConnection();
            logger.info("Fetching all items with search parameters: " + searchParams);

            List<ItemEntity> entities = itemDAO.getAll(connection, searchParams);

            if (entities != null && !entities.isEmpty()) {
                resultList = ItemMapper.convertItemEntityListToItemDTOList(entities);
                logger.info("Retrieved " + resultList.size() + " items from database.");
            } else {
                logger.warning("No items found for given search parameters.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while retrieving all items: " + e.getMessage(), e);
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }

        return resultList;
    }


    @Override
    public boolean update(ItemDTO dto) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.info("Starting update for item ID: " + dto.getId() + ", Name: " + dto.getItemName());

            // Check if item exists
            ItemEntity existingItem = itemDAO.searchById(connection, dto.getId());
            if (existingItem == null) {
                throw new CustomException(CustomException.ExceptionType.ITEM_NOT_FOUND);
            }

            // Check name uniqueness (excluding current item)
            if (!existingItem.getItemName().equals(dto.getItemName())) {
                if (itemDAO.existsByName(connection, dto.getItemName())) {
                    throw new CustomException(CustomException.ExceptionType.ITEM_ALREADY_EXISTS);
                }
            }

            // Convert DTO to Entity and update
            ItemEntity updatedEntity = ItemMapper.convertItemDTOToItemEntity(dto);
            boolean isUpdated = itemDAO.update(connection, updatedEntity);

            if (isUpdated) {
                connection.commit();
                logger.info("Item updated successfully: ID=" + dto.getId() + ", Name: " + dto.getItemName());
                return true;
            } else {
                connection.rollback();
                logger.warning("Update operation returned false. Rolled back transaction for item ID: " + dto.getId());
                throw new CustomException(CustomException.ExceptionType.ITEM_UPDATE_FAILED);
            }

        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Error during item update for ID=" + dto.getId() + ": " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public boolean delete(Object... args) throws SQLException, ClassNotFoundException {
        if (args.length < 2 || !(args[0] instanceof Integer) || !(args[1] instanceof Integer)) {
            throw new IllegalArgumentException("Delete requires deleter ID (Integer) and item ID (Integer).");
        }
        Integer deletedByUserId = (Integer) args[0];
        Integer itemId = (Integer) args[1];

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.info("Attempting to soft delete item ID: " + itemId + " by user ID: " + deletedByUserId);

            // Check if item exists
            if (!itemDAO.findItemById(connection, itemId)) {
                throw new CustomException(CustomException.ExceptionType.ITEM_NOT_FOUND);  // Stop if item does not exist
            }

            // Perform delete (soft delete)
            boolean isDeleted = itemDAO.delete(connection, deletedByUserId, itemId);

            if (isDeleted) {
                connection.commit();
                logger.info("Item soft deleted successfully: ID " + itemId);
                return true;
            } else {
                connection.rollback();
                logger.warning("Failed to soft delete item: ID " + itemId + ". Rolled back transaction.");
                throw new CustomException(CustomException.ExceptionType.ITEM_DELETION_FAILED);
            }

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            logger.severe("Database error during item soft delete: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR); // propagate the exception
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public int getItemsCount() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            int count = itemDAO.getItemsCount(connection);
            return count;
        } catch (SQLException e) {
            logger.severe("Database error during getItemsCount: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);  // Rethrow the SQLException
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @Override
    public boolean restockItem(Integer itemId, int quantityToAdd) throws SQLException, ClassNotFoundException {
        if (itemId == null || quantityToAdd <= 0) {
            logger.warning("Invalid input for restockItem: itemId=" + itemId + ", quantityToAdd=" + quantityToAdd);
            return false;
        }

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.info("Attempting to restock item ID: " + itemId + " by " + quantityToAdd + " units.");

            ItemEntity existingItem = itemDAO.searchById(connection, itemId);
            if (existingItem == null) {
                throw new CustomException(CustomException.ExceptionType.ITEM_NOT_FOUND);
            }

            int newQuantity = existingItem.getQuantity() + quantityToAdd;

            // Prepare DTO for update
            ItemDTO updateDto = new ItemDTO();
            updateDto.setId(itemId);
            updateDto.setItemName(existingItem.getItemName());
            updateDto.setUnitPrice(existingItem.getUnitPrice());
            updateDto.setQuantity(newQuantity);

            // Convert DTO to entity and update
            ItemEntity updatedEntity = ItemMapper.convertItemDTOToItemEntity(updateDto);
            boolean isRestocked = itemDAO.update(connection, ItemMapper.convertItemDTOToItemEntity(updateDto));

            if (isRestocked) {
                connection.commit();
                logger.info("Item ID " + itemId + " restocked successfully. New quantity: " + newQuantity);
                return true;
            } else {
                connection.rollback();
                logger.warning("Failed to restock item ID " + itemId + ", rolling back.");
                throw new CustomException(CustomException.ExceptionType.ITEM_DELETION_FAILED);
            }
        } catch (SQLException e) {
            DBConnection.rollback(connection);
            logger.severe("Database error during restockItem: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

}
