package org.icbt.onlinebillingsystempahanaedu.item.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:16 AM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceImplTest {

    private ItemServiceImpl itemService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        itemService = new ItemServiceImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;").execute();
        connection.prepareStatement("TRUNCATE TABLE items;").execute();
        connection.prepareStatement("TRUNCATE TABLE users;").execute();

        connection.prepareStatement(
                "INSERT INTO users (id, username, password, role) " +
                        "VALUES (1, 'test_admin', 'pass', 'ADMIN');"
        ).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private ItemDTO createTestItem(String name, Double price, int stock) {
        ItemDTO dto = new ItemDTO();
        dto.setItemName(name);
        dto.setUnitPrice(price);
        dto.setQuantity(stock);
        return dto;
    }

    @Test
    @Order(1)
    void testAdd_Success() throws Exception {
        ItemDTO dto = createTestItem("Laptop", Double.valueOf("1200.50"), 10);
        boolean result = itemService.add(dto);

        assertTrue(result);
        ItemDTO saved = itemService.findByName("Laptop");
        assertNotNull(saved);
        assertEquals("Laptop", saved.getItemName());
    }

    @Test
    @Order(2)
    void testAdd_FailsWhenItemAlreadyExists() throws Exception {
        itemService.add(createTestItem("Pen", Double.valueOf("10.00"), 100));

        CustomException ex = assertThrows(CustomException.class,
                () -> itemService.add(createTestItem("Pen", Double.valueOf("12.00"), 50))
        );

        assertEquals(CustomException.ExceptionType.ITEM_ALREADY_EXISTS, ex.getExceptionType());
    }

    @Test
    @Order(3)
    void testFindByName_ReturnsNullIfNotFound() throws Exception {
        ItemDTO dto = itemService.findByName("GhostItem");
        assertNull(dto);
    }

    @Test
    @Order(4)
    void testSearchById_Success() throws Exception {
        itemService.add(createTestItem("Book", Double.valueOf("45.00"), 20));
        ItemDTO dto = itemService.findByName("Book");

        ItemDTO found = itemService.searchById(dto.getId());
        assertNotNull(found);
        assertEquals("Book", found.getItemName());
    }

    @Test
    @Order(5)
    void testSearchById_InvalidParams_ReturnsNull() throws Exception {
        assertNull(itemService.searchById());
        assertNull(itemService.searchById("wrong-type"));
    }

    @Test
    @Order(6)
    void testGetAll_ReturnsList() throws Exception {
        itemService.add(createTestItem("Item1", Double.valueOf("5.00"), 5));
        itemService.add(createTestItem("Item2", Double.valueOf("15.00"), 10));

        List<ItemDTO> items = itemService.getAll(new HashMap<>());
        assertTrue(items.size() >= 2);
    }

    @Test
    @Order(7)
    void testUpdate_Success() throws Exception {
        itemService.add(createTestItem("Chair", Double.valueOf("300.00"), 3));
        ItemDTO dto = itemService.findByName("Chair");

        dto.setItemName("Office Chair");
        dto.setQuantity(5);

        boolean updated = itemService.update(dto);
        assertTrue(updated);

        ItemDTO updatedItem = itemService.findByName("Office Chair");
        assertEquals(5, updatedItem.getQuantity());
    }

    @Test
    @Order(8)
    void testUpdate_FailsWhenItemNotFound() {
        ItemDTO dto = createTestItem("NonExistent", Double.valueOf("50.00"), 5);
        dto.setId(999);

        CustomException ex = assertThrows(CustomException.class,
                () -> itemService.update(dto)
        );

        assertEquals(CustomException.ExceptionType.ITEM_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @Order(9)
    void testDelete_Success() throws Exception {
        itemService.add(createTestItem("DeleteMe", Double.valueOf("12.00"), 2));
        ItemDTO dto = itemService.findByName("DeleteMe");

        boolean deleted = itemService.delete(1, String.valueOf(dto.getId()));
        assertTrue(deleted);
    }

    @Test
    @Order(10)
    void testDelete_FailsWhenNotFound() {
        CustomException ex = assertThrows(CustomException.class,
                () -> itemService.delete(1, "999")
        );
        assertEquals(CustomException.ExceptionType.ITEM_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @Order(11)
    void testGetItemsCount() throws Exception {
        itemService.add(createTestItem("CountItem1", Double.valueOf("20.00"), 1));
        itemService.add(createTestItem("CountItem2", Double.valueOf("30.00"), 2));

        int count = itemService.getItemsCount();
        assertTrue(count >= 2);
    }
}
