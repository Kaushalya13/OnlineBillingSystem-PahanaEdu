package org.icbt.onlinebillingsystempahanaedu.item.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:15 AM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemDAOImplTest {

    private ItemDAOImpl itemDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        itemDAO = new ItemDAOImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;").execute();
        connection.prepareStatement("TRUNCATE TABLE items;").execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private ItemEntity createTestItem(String name, double price, int qty) {
        ItemEntity item = new ItemEntity();
        item.setItemName(name);
        item.setUnitPrice(price);
        item.setQuantity(qty);
        return item;
    }

    @Test
    @Order(1)
    void testAddAndFindByName() throws Exception {
        ItemEntity item = createTestItem("Test Pen", 50.0, 100);

        boolean added = itemDAO.add(connection, item);
        assertTrue(added);

        ItemEntity found = itemDAO.findByName(connection, "Test Pen");
        assertNotNull(found);
        assertEquals("Test Pen", found.getItemName());
        assertEquals(50.0, found.getUnitPrice());
    }

    @Test
    @Order(2)
    void testExistsByName() throws Exception {
        ItemEntity item = createTestItem("Book", 200.0, 20);
        itemDAO.add(connection, item);

        boolean exists = itemDAO.existsByName(connection, "Book");
        assertTrue(exists);

        boolean notExists = itemDAO.existsByName(connection, "Nonexistent");
        assertFalse(notExists);
    }

    @Test
    @Order(3)
    void testFindItemById() throws Exception {
        ItemEntity item = createTestItem("Eraser", 20.0, 200);
        itemDAO.add(connection, item);

        List<ItemEntity> all = itemDAO.getAll(connection, null);
        int itemId = all.get(0).getId();

        boolean found = itemDAO.findItemById(connection, itemId);
        assertTrue(found);

        boolean notFound = itemDAO.findItemById(connection, 999);
        assertFalse(notFound);
    }

    @Test
    @Order(4)
    void testSearchById() throws Exception {
        ItemEntity item = createTestItem("Marker", 100.0, 50);
        itemDAO.add(connection, item);

        List<ItemEntity> all = itemDAO.getAll(connection, null);
        int id = all.get(0).getId();

        ItemEntity found = itemDAO.searchById(connection, id);
        assertNotNull(found);
        assertEquals("Marker", found.getItemName());
    }

    @Test
    @Order(5)
    void testUpdate() throws Exception {
        ItemEntity item = createTestItem("Pencil", 10.0, 500);
        itemDAO.add(connection, item);

        List<ItemEntity> all = itemDAO.getAll(connection, null);
        ItemEntity toUpdate = all.get(0);

        toUpdate.setItemName("Updated Pencil");
        toUpdate.setUnitPrice(15.0);
        toUpdate.setQuantity(300);

        boolean updated = itemDAO.update(connection, toUpdate);
        assertTrue(updated);

        ItemEntity updatedEntity = itemDAO.searchById(connection, toUpdate.getId());
        assertEquals("Updated Pencil", updatedEntity.getItemName());
        assertEquals(15.0, updatedEntity.getUnitPrice());
        assertEquals(300, updatedEntity.getQuantity());
    }

    @Test
    @Order(6)
    void testDelete() throws Exception {
        ItemEntity item = createTestItem("Glue", 60.0, 40);
        itemDAO.add(connection, item);

        List<ItemEntity> all = itemDAO.getAll(connection, null);
        int id = all.get(0).getId();

        boolean deleted = itemDAO.delete(connection, id);
        assertTrue(deleted);

        ItemEntity shouldBeNull = itemDAO.searchById(connection, id);
        assertNull(shouldBeNull);
    }

    @Test
    @Order(7)
    void testGetAllWithSearch() throws Exception {
        itemDAO.add(connection, createTestItem("Blue Pen", 30.0, 10));
        itemDAO.add(connection, createTestItem("Red Pen", 40.0, 20));
        itemDAO.add(connection, createTestItem("Notebook", 120.0, 5));

        List<ItemEntity> found = itemDAO.getAll(connection, Map.of("search", "Pen"));
        assertEquals(2, found.size());
    }

    @Test
    @Order(8)
    void testGetItemsCount() throws Exception {
        itemDAO.add(connection, createTestItem("A", 10.0, 1));
        itemDAO.add(connection, createTestItem("B", 20.0, 2));
        itemDAO.add(connection, createTestItem("C", 30.0, 3));

        int count = itemDAO.getItemsCount(connection);
        assertEquals(3, count);
    }
}