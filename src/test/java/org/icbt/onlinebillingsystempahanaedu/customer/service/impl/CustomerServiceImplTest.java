package org.icbt.onlinebillingsystempahanaedu.customer.service.impl;

import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:18 AM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceImplTest {

    private CustomerServiceImpl customerService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        customerService = new CustomerServiceImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;").execute();
        connection.prepareStatement("TRUNCATE TABLE customers;").execute();
        connection.prepareStatement("TRUNCATE TABLE users;").execute();
        connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1;").execute();

        connection.prepareStatement(
                "INSERT INTO users (id, username, password, role) VALUES (1, 'test_admin', 'pass', 'ADMIN');"
        ).execute();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private CustomerDTO createCustomer(String accNum, String name, String phone) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCus_AccountNumber(accNum);
        dto.setCus_Name(name);
        dto.setCus_Mobile(phone);
        dto.setCus_Address("123 Test Street, Colombo");
        dto.setUnits_consumed(0);
        return dto;
    }

    @Test
    @Order(1)
    void testAdd_Success() throws Exception {
        CustomerDTO customer = createCustomer("CUST001", "Amal Perera", "0771234567");
        assertTrue(customerService.add(customer));
    }

    @Test
    @Order(2)
    void testAdd_FailsOnDuplicateAccountNumber() throws Exception {
        customerService.add(createCustomer("CUST002", "Kamal Silva", "0711111111"));

        CustomException ex = assertThrows(CustomException.class,
                () -> customerService.add(createCustomer("CUST002", "Nimal Fonseka", "0722222222"))
        );
        assertEquals(CustomException.ExceptionType.CUSTOMER_ALREADY_EXISTS, ex.getExceptionType());
    }

    @Test
    @Order(3)
    void testAdd_FailsOnDuplicatePhoneNumber() throws Exception {
        customerService.add(createCustomer("CUST003", "Sunil Bandara", "0765555555"));

        CustomException ex = assertThrows(CustomException.class,
                () -> customerService.add(createCustomer("CUST004", "Kasun Jayasuriya", "0765555555"))
        );
        assertEquals(CustomException.ExceptionType.CUSTOMER_ALREADY_EXISTS, ex.getExceptionType());
    }

    @Test
    @Order(4)
    void testUpdate_Success() throws Exception {
        customerService.add(createCustomer("CUST005", "Initial Name", "0701234567"));
        CustomerDTO existing = customerService.findByAccountNumber("CUST005");

        existing.setCus_Name("Updated Name");
        existing.setCus_Address("456 New Road, Galle");

        assertTrue(customerService.update(existing));

        CustomerDTO updated = customerService.searchById(existing.getCus_Id());
        assertEquals("Updated Name", updated.getCus_Name());
        assertEquals("456 New Road, Galle", updated.getCus_Address());
    }

    @Test
    @Order(5)
    void testUpdate_FailsForNonExistentCustomer() {
        CustomerDTO ghost = createCustomer("CUST999", "Ghost User", "0700000000");

        CustomException ex = assertThrows(CustomException.class,
                () -> customerService.update(ghost)
        );
        assertEquals(CustomException.ExceptionType.CUSTOMER_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @Order(6)
    void testUpdate_FailsWhenChangingToExistingPhone() throws Exception {
        customerService.add(createCustomer("CUST006", "User One", "0777777777"));
        customerService.add(createCustomer("CUST007", "User Two", "0778888888"));

        CustomerDTO toUpdate = customerService.findByAccountNumber("CUST007");
        toUpdate.setCus_Mobile("0777777777"); // Duplicate phone

        CustomException ex = assertThrows(CustomException.class,
                () -> customerService.update(toUpdate)
        );
        assertEquals(CustomException.ExceptionType.CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS, ex.getExceptionType());
    }

    @Test
    @Order(7)
    void testDelete_Success() throws Exception {
        customerService.add(createCustomer("CUST008", "ToDelete", "0713334444"));
        CustomerDTO customer = customerService.findByAccountNumber("CUST008");

        assertTrue(customerService.delete(1, String.valueOf(customer.getCus_Id())));

        CustomException ex = assertThrows(CustomException.class,
                () -> customerService.searchById(customer.getCus_Id())
        );
        assertEquals(CustomException.ExceptionType.CUSTOMER_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @Order(8)
    void testFindByMobile_NotFound() throws Exception {
        CustomerDTO dto = customerService.findByMobile("0719999999");
        assertNull(dto, "Non-existing mobile should return null.");
    }

    @Test
    @Order(9)
    void testGetAll_EmptyListInitially() throws Exception {
        assertTrue(customerService.getAll(new HashMap<>()).isEmpty());
    }
}