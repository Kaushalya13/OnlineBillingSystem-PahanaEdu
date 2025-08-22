package org.icbt.onlinebillingsystempahanaedu.bill.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillEntity;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:19 AM
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillDAOImplTest {

    private BillDAOImpl billDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        billDAO = new BillDAOImpl();
        connection = DBConnection.getConnection();

        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
        stmt.execute("TRUNCATE TABLE bill_details;");
        stmt.execute("TRUNCATE TABLE bills;");
        stmt.execute("TRUNCATE TABLE customers;");
        stmt.execute("TRUNCATE TABLE users;");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

        stmt.execute("INSERT INTO users (id, username, password, role) VALUES (1, 'test_admin', 'p', 'ADMIN');");
        stmt.execute("INSERT INTO customers (id, cus_Name, cus_Address, cus_Mobile, cus_AccountNumber, units_consumed) " +
                "VALUES (1, 'Test Customer', '123 Street', '0771112222', 'ACC001', 0);");
        stmt.close();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private BillEntity createTestBill() {
        BillEntity entity = new BillEntity();
        entity.setCustomer_id(1);
        entity.setTotal_amount(1500.00);
        return entity;
    }

    @Test
    @Order(1)
    void testCreateBillAndSearchById() throws Exception {
        BillEntity bill = createTestBill();

        int generatedId = billDAO.createBill(connection, bill);
        assertTrue(generatedId > 0, "createBill should return generated ID");

        BillEntity found = billDAO.searchById(connection, generatedId);
        assertNotNull(found, "Bill should be found by its ID");
        assertEquals(1, found.getCustomer_id());
        assertEquals(1500.00, found.getTotal_amount());
    }

    @Test
    @Order(2)
    void testAdd() throws Exception {
        BillEntity bill = createTestBill();
        boolean result = billDAO.add(connection, bill);

        assertTrue(result, "add() should return true on success");
        List<BillEntity> all = billDAO.getAll(connection, null);
        assertEquals(1, all.size(), "There should be one bill in DB after add()");
    }

    @Test
    @Order(3)
    void testGetAll() throws Exception {
        billDAO.add(connection, createTestBill());
        billDAO.add(connection, createTestBill());

        List<BillEntity> all = billDAO.getAll(connection, null);
        assertEquals(2, all.size(), "getAll should return all non-deleted bills");
    }

    @Test
    @Order(4)
    void testSearchById_NotFound() throws Exception {
        BillEntity result = billDAO.searchById(connection, 999);
        assertNull(result, "searchById should return null if bill does not exist");
    }

    @Test
    @Order(5)
    void testDelete() throws Exception {
        int billId = billDAO.createBill(connection, createTestBill());
        assertTrue(billId > 0);

        boolean deleted = billDAO.delete(connection, billId);
        assertTrue(deleted, "delete() should return true when successful");

        BillEntity afterDelete = billDAO.searchById(connection, billId);
        assertNull(afterDelete, "Deleted bill should not be found");
    }

    @Test
    @Order(6)
    void testDelete_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> billDAO.delete(connection));
    }

    @Test
    @Order(7)
    void testUpdate_NotSupported() {
        BillEntity bill = createTestBill();
        assertThrows(UnsupportedOperationException.class, () -> billDAO.update(connection, bill));
    }
}
