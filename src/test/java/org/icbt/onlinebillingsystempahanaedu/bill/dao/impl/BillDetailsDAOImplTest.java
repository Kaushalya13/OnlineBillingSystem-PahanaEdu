package org.icbt.onlinebillingsystempahanaedu.bill.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillDetailsEntity;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillDetailsDAOImplTest {

    private BillDetailsDAOImpl billDetailsDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        billDetailsDAO = new BillDetailsDAOImpl();
        connection = DBConnection.getConnection();

        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
        stmt.execute("TRUNCATE TABLE bill_details;");
        stmt.execute("TRUNCATE TABLE Bills;");
        stmt.execute("TRUNCATE TABLE items;");
        stmt.execute("TRUNCATE TABLE customers;");
        stmt.execute("TRUNCATE TABLE users;");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

        stmt.execute("INSERT INTO users (id, username, password, role) VALUES (1, 'test_admin', 'p', 'ADMIN');");
        stmt.execute("INSERT INTO customers " +
                "(id, cus_Name, cus_Address, cus_Mobile, cus_AccountNumber, units_consumed) " +
                "VALUES (1, 'Test Customer', '123 Street', '0771112222', 'CUST001', 0);");
        stmt.execute("INSERT INTO items (id, item_name, unit_price, quantity) VALUES (101, 'Test Pen', 50.00, 100);");
        stmt.execute("INSERT INTO items (id, item_name, unit_price, quantity) VALUES (102, 'Test Book', 250.00, 50);");
        stmt.execute("INSERT INTO bills (id, customer_id, total_amount) VALUES (1, 1, 350.00);");
        stmt.close();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private BillDetailsEntity createTestDetail(int billId, int itemId, String itemName, double unitPrice, int units) {
        BillDetailsEntity detail = new BillDetailsEntity();
        detail.setBill_id(billId);
        detail.setItem_id(itemId);
        detail.setItem_name_at_sale(itemName);
        detail.setUnit_price_at_sale(unitPrice);
        detail.setUnits(units);
        detail.setTotal(unitPrice * units);
        return detail;
    }

    @Test
    @Order(1)
    void testSaveBillDetailsAndFindByBillId() throws SQLException {
        BillDetailsEntity detail1 = createTestDetail(1, 101, "Test Pen", 50.0, 2);
        BillDetailsEntity detail2 = createTestDetail(1, 102, "Test Book", 250.0, 1);
        List<BillDetailsEntity> details = List.of(detail1, detail2);

        assertDoesNotThrow(() -> billDetailsDAO.saveBillDetails(connection, details));

        List<BillDetailsEntity> foundDetails = billDetailsDAO.findBillDetailsByBillId(connection, 1);

        assertNotNull(foundDetails, "The list should not be null");
        assertEquals(2, foundDetails.size(), "Should retrieve exactly 2 items");

        BillDetailsEntity pen = foundDetails.stream().filter(d -> d.getItem_id() == 101).findFirst().orElse(null);
        assertNotNull(pen);
        assertEquals(2, pen.getUnits());
        assertEquals(100.0, pen.getTotal());

        BillDetailsEntity book = foundDetails.stream().filter(d -> d.getItem_id() == 102).findFirst().orElse(null);
        assertNotNull(book);
        assertEquals(1, book.getUnits());
        assertEquals(250.0, book.getTotal());
    }

    @Test
    @Order(2)
    void testAddSingleBillDetail() throws SQLException, ClassNotFoundException {
        BillDetailsEntity detail = createTestDetail(1, 101, "Test Pen", 50.0, 3);

        boolean result = billDetailsDAO.add(connection, detail);

        assertTrue(result, "add() should return true on successful insert");

        List<BillDetailsEntity> all = billDetailsDAO.findBillDetailsByBillId(connection, 1);
        assertEquals(1, all.size());
        assertEquals(150.0, all.get(0).getTotal());
    }
}