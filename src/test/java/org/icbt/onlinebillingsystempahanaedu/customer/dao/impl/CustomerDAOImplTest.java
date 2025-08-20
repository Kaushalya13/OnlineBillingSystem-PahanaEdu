package org.icbt.onlinebillingsystempahanaedu.customer.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:17 AM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerDAOImplTest {

    private CustomerDAOImpl customerDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        customerDAO = new CustomerDAOImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;").execute();
        connection.prepareStatement("TRUNCATE TABLE customers;").execute();
        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1;").execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private CustomerEntity createCustomer(String accNum, String name, String phone) {
        CustomerEntity customer = new CustomerEntity();
        customer.setCus_AccountNumber(accNum);
        customer.setCus_Name(name);
        customer.setCus_Mobile(phone);
        customer.setCus_Address("123 Test Road");
        customer.setUnits_consumed(0);
        return customer;
    }

    @Test
    @Order(1)
    void testAddAndFindByAccountNumber() throws SQLException, ClassNotFoundException {
        CustomerEntity customer = createCustomer("ACC001", "John Doe", "0771234567");
        assertTrue(customerDAO.add(connection, customer));

        CustomerEntity found = customerDAO.findByAccountNumber(connection, "ACC001");
        assertNotNull(found);
        assertEquals("John Doe", found.getCus_Name());
        assertEquals("0771234567", found.getCus_Mobile());
    }

    @Test
    @Order(2)
    void testUpdateCustomer() throws SQLException, ClassNotFoundException {
        CustomerEntity customer = createCustomer("ACC002", "Jane Doe", "0712223333");
        customerDAO.add(connection, customer);

        CustomerEntity added = customerDAO.findByAccountNumber(connection, "ACC002");
        added.setCus_Name("Jane Smith");
        added.setCus_Mobile("0714445555");
        added.setUnits_consumed(150);

        assertTrue(customerDAO.update(connection, added));

        CustomerEntity updated = customerDAO.searchById(connection, added.getCus_Id());
        assertEquals("Jane Smith", updated.getCus_Name());
        assertEquals("0714445555", updated.getCus_Mobile());
        assertEquals(150, updated.getUnits_consumed());
    }

    @Test
    @Order(3)
    void testDeleteCustomer() throws SQLException, ClassNotFoundException {
        CustomerEntity customer = createCustomer("ACC003", "Delete Me", "0765554444");
        customerDAO.add(connection, customer);

        CustomerEntity added = customerDAO.findByAccountNumber(connection, "ACC003");
        assertTrue(customerDAO.delete(connection, added.getCus_Id()));

        assertNull(customerDAO.searchById(connection, added.getCus_Id()));
    }

    @Test
    @Order(4)
    void testExistsByAccountNumberAndPhoneNumber() throws SQLException, ClassNotFoundException {
        customerDAO.add(connection, createCustomer("ACC004", "Exist Test", "0751112222"));

        assertTrue(customerDAO.existsByAccountNumber(connection, "ACC004"));
        assertFalse(customerDAO.existsByAccountNumber(connection, "ACC999"));

        assertTrue(customerDAO.existsByPhoneNumber(connection, "0751112222"));
        assertFalse(customerDAO.existsByPhoneNumber(connection, "0700000000"));
    }

    @Test
    @Order(5)
    void testGetAllWithSearch() throws SQLException, ClassNotFoundException {
        customerDAO.add(connection, createCustomer("CUST01", "Kamal Silva", "0771111111"));
        customerDAO.add(connection, createCustomer("CUST02", "Nimal Perera", "0712222222"));
        customerDAO.add(connection, createCustomer("CUST03", "Sunil Silva", "0763333333"));

        Map<String, String> searchParams = Map.of("search", "Silva");
        List<CustomerEntity> result = customerDAO.getAll(connection, searchParams);

        assertEquals(2, result.size());
    }

    @Test
    @Order(6)
    void testFindByPhoneNumber() throws SQLException, ClassNotFoundException {
        customerDAO.add(connection, createCustomer("ACC005", "Phone Test", "0719876543"));

        CustomerEntity found = customerDAO.findByPhoneNumber(connection, "0719876543");
        assertNotNull(found);
        assertEquals("Phone Test", found.getCus_Name());

        CustomerEntity notFound = customerDAO.findByPhoneNumber(connection, "0700000000");
        assertNull(notFound);
    }

    @Test
    @Order(7)
    void testExistsById() throws SQLException, ClassNotFoundException {
        customerDAO.add(connection, createCustomer("ACC006", "ID Exist Test", "0761112222"));
        CustomerEntity added = customerDAO.findByAccountNumber(connection, "ACC006");

        assertTrue(customerDAO.existsById(connection, added.getCus_Id()));
        assertFalse(customerDAO.existsById(connection, 9999));
    }
}
