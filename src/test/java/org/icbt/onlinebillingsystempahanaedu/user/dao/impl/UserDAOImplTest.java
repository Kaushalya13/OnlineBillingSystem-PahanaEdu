package org.icbt.onlinebillingsystempahanaedu.user.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.user.entity.UserEntity;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/19/2025
 * time : 12:22 PM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOImplTest {

    private UserDAOImpl userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userDAO = new UserDAOImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("DELETE FROM users").executeUpdate();
        connection.prepareStatement("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private UserEntity createTestUser(String username, Role role) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("testpassword");
        user.setRole(role);
        return user;
    }

    @Test
    @Order(1)
    void testAddAndFindByUsername() throws Exception {
        UserEntity user = createTestUser("johndoe", Role.USER);

        boolean result = userDAO.add(connection, user);
        assertTrue(result, "User should be added successfully");

        UserEntity found = userDAO.findByUsername(connection, "johndoe");
        assertNotNull(found);
        assertEquals("johndoe", found.getUsername());
        assertEquals(Role.USER, found.getRole());
    }

    @Test
    @Order(2)
    void testUpdate() throws Exception {
        userDAO.add(connection, createTestUser("janedoe", Role.USER));
        UserEntity added = userDAO.findByUsername(connection, "janedoe");

        added.setUsername("janedoe_updated");
        added.setRole(Role.ADMIN);
        boolean result = userDAO.update(connection, added);

        assertTrue(result);

        UserEntity updated = userDAO.searchById(connection, added.getId());
        assertEquals("janedoe_updated", updated.getUsername());
        assertEquals(Role.ADMIN, updated.getRole());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    @Order(3)
    void testDelete() throws Exception {
        userDAO.add(connection, createTestUser("todelete", Role.USER));
        UserEntity added = userDAO.findByUsername(connection, "todelete");

        boolean result = userDAO.delete(connection, added.getId());
        assertTrue(result);

        UserEntity deleted = userDAO.searchById(connection, added.getId());
        assertNull(deleted, "Deleted user should not be returned");
    }

    @Test
    @Order(4)
    void testGetAll() throws Exception {
        userDAO.add(connection, createTestUser("user1", Role.USER));
        userDAO.add(connection, createTestUser("admin1", Role.ADMIN));
        userDAO.add(connection, createTestUser("user2", Role.USER));

        List<UserEntity> allUsers = userDAO.getAll(connection, null);
        assertEquals(3, allUsers.size());
    }


    @Test
    @Order(5)
    void testGetAllWithSearch() throws Exception {
        userDAO.add(connection, createTestUser("test_user_alpha", Role.USER));
        userDAO.add(connection, createTestUser("test_admin_beta", Role.ADMIN));
        userDAO.add(connection, createTestUser("another_user_gamma", Role.USER));

        Map<String, String> searchParams = Map.of("search", "user");
        List<UserEntity> found = userDAO.getAll(connection, searchParams);

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(u -> u.getUsername().equals("test_user_alpha")));
        assertTrue(found.stream().anyMatch(u -> u.getUsername().equals("another_user_gamma")));
    }

    @Test
    @Order(6)
    void testCountActiveUsers() throws Exception {
        userDAO.add(connection, createTestUser("count_user_1", Role.USER));
        userDAO.add(connection, createTestUser("count_user_2", Role.USER));
        userDAO.add(connection, createTestUser("count_admin_1", Role.ADMIN));

        int count = userDAO.countActiveUsers(connection);
        assertEquals(3, count);
    }

}
