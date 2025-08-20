package org.icbt.onlinebillingsystempahanaedu.bill.service.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDetailsDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.impl.BillServiceImpl;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.impl.CustomerDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;
import org.icbt.onlinebillingsystempahanaedu.item.dao.impl.ItemDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:20 AM
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillServiceImplTest {

    private BillServiceImpl billService;
    private Connection connection;
    private ItemDAOImpl itemDAO;
    private CustomerDAOImpl customerDAO;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        billService = new BillServiceImpl();
        itemDAO = new ItemDAOImpl();
        customerDAO = new CustomerDAOImpl();
        connection = DBConnection.getConnection();

        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
        stmt.execute("TRUNCATE TABLE bill_details;");
        stmt.execute("TRUNCATE TABLE bills;");
        stmt.execute("TRUNCATE TABLE items;");
        stmt.execute("TRUNCATE TABLE customers;");
        stmt.execute("TRUNCATE TABLE users;");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

        stmt.execute("INSERT INTO users (id, username, password, role) VALUES (1, 'test_admin', 'p', 'ADMIN');");
        stmt.execute("INSERT INTO customers (id, cus_accountnumber, cus_name, cus_address, cus_mobile, units_consumed) " +
                "VALUES (1, 'CUST001', 'Test Customer', '123 Street', '0771112222', 50);");
        stmt.execute("INSERT INTO items (id, item_name, unit_price, quantity) VALUES (101, 'Test Pen', 50.0, 100);");
        stmt.close();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private BillDetailsDTO createDetail(int itemId, int units) {
        BillDetailsDTO detail = new BillDetailsDTO();
        detail.setItem_id(itemId);
        detail.setUnits(units);
        return detail;
    }

    @Test
    @Order(1)
    void testCreateBill_Success_UpdatesStockAndCustomerUnits() throws ClassNotFoundException, SQLException, CustomException {
        BillDetailsDTO detail = createDetail(101, 10);
        BillDTO billDTO = new BillDTO();
        billDTO.setCustomer_id(1);
        billDTO.setBillDetails(List.of(detail));

        BillDTO createdBill = billService.createBill(billDTO, 1);

        assertNotNull(createdBill);
        assertTrue(createdBill.getId() > 0);
        assertEquals(1, createdBill.getBillDetails().size());
        assertEquals(500.0, createdBill.getBillDetails().get(0).getTotal());


        ItemEntity updatedItem = itemDAO.searchById(connection, 101);
        assertEquals(90, updatedItem.getQuantity());


        CustomerEntity updatedCustomer = customerDAO.searchById(connection, 1);
        assertEquals(60, updatedCustomer.getUnits_consumed());
    }

    @Test
    @Order(2)
    void testCreateBill_FailsOnInsufficientStockAndRollsBack() throws SQLException, ClassNotFoundException {

        BillDetailsDTO detail = createDetail(101, 101);
        BillDTO billDTO = new BillDTO();
        billDTO.setCustomer_id(1);
        billDTO.setBillDetails(List.of(detail));


        CustomException exception = assertThrows(CustomException.class, () -> {
            billService.createBill(billDTO, 1);
        });
        assertEquals(CustomException.ExceptionType.INSUFFICIENT_STOCK, exception.getExceptionType());

        ItemEntity itemAfterFail = itemDAO.searchById(connection, 101);
        assertEquals(100, itemAfterFail.getQuantity());

        CustomerEntity customerAfterFail = customerDAO.searchById(connection, 1);
        assertEquals(50, customerAfterFail.getUnits_consumed());
    }
}
