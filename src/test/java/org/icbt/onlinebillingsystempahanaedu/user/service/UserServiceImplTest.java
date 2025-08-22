package org.icbt.onlinebillingsystempahanaedu.user.service;

import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * author : Niwanthi
 * date : 8/20/2025
 * time : 8:04 AM
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    private UserServiceImpl userService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userService = new UserServiceImpl();
        connection = DBConnection.getConnection();

        connection.prepareStatement("DELETE FROM users").executeUpdate();
        connection.prepareStatement("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();

        UserDTO admin = createTestUserDTO("admin", "adminpass", Role.ADMIN);
        userService.add(admin);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private UserDTO createTestUserDTO(String username, String password, Role role) {
        UserDTO user = new UserDTO();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    @Test
    @Order(1)
    void testAdd_Success() throws Exception {
        UserDTO user = createTestUserDTO("testuser", "password123", Role.USER);
        boolean result = userService.add(user);
        assertTrue(result, "User should be added successfully");

        UserDTO fetched = userService.findByUsername("testuser");
        assertNotNull(fetched, "User should be found");
        assertEquals("testuser", fetched.getUsername());
        assertNotEquals("password123", fetched.getPassword(), "Password should be hashed");
    }

    @Test
    @Order(2)
    void testAdd_FailsOnDuplicateUsername() throws Exception {
        UserDTO user1 = createTestUserDTO("duplicateuser", "password123", Role.USER);
        userService.add(user1);

        UserDTO user2 = createTestUserDTO("duplicateuser", "anotherpass", Role.ADMIN);
        CustomException ex = assertThrows(CustomException.class, () -> userService.add(user2));
        assertEquals(CustomException.ExceptionType.USER_ALREADY_EXISTS, ex.getExceptionType());
    }

    @Test
    @Order(3)
    void testSearchById_Success() throws Exception {
        UserDTO user = createTestUserDTO("searchuser", "pass123", Role.USER);
        userService.add(user);

        UserDTO fetched = userService.searchById(2);
        assertNotNull(fetched, "User should be fetched by ID");
        assertEquals("searchuser", fetched.getUsername());
    }

    @Test
    @Order(4)
    void testSearchById_UserNotFound() {
        assertThrows(CustomException.class, () -> userService.searchById(99));
    }

    @Test
    @Order(5)
    void testUpdate_Success() throws Exception {
        UserDTO user = createTestUserDTO("updateuser", "oldpass", Role.USER);
        userService.add(user);

        UserDTO toUpdate = userService.findByUsername("updateuser");
        toUpdate.setPassword("newpass");

        boolean result = userService.update(toUpdate);
        assertTrue(result, "User should be updated");

        UserDTO updated = userService.searchById(toUpdate.getId());
        assertNotEquals("oldpass", updated.getPassword(), "Password should be re-hashed");
    }

    @Test
    @Order(6)
    void testDelete_Success() throws Exception {
        UserDTO user = createTestUserDTO("deleteuser", "pass123", Role.USER);
        userService.add(user);

        UserDTO added = userService.findByUsername("deleteuser");
        assertNotNull(added, "User should exist after add");
        assertTrue(added.getId() > 1, "User ID must not be initial admin");

        boolean result = userService.delete(added.getId());
        assertTrue(result, "User should be deleted");

        assertThrows(CustomException.class, () -> userService.searchById(added.getId()));
    }

    @Test
    @Order(7)
    void testDelete_FailsOnInitialAdmin() {
        CustomException ex = assertThrows(CustomException.class, () -> userService.delete(1));
        assertEquals(CustomException.ExceptionType.UNAUTHORIZED_ACCESS, ex.getExceptionType());
    }

    @Test
    @Order(8)
    void testLoginUser_SuccessAndFailure() throws Exception {
        UserDTO user = createTestUserDTO("loginuser", "mypassword", Role.USER);
        userService.add(user);

        UserDTO loggedIn = userService.loginUser("loginuser", "mypassword");
        assertNotNull(loggedIn);
        assertEquals("loginuser", loggedIn.getUsername());

        assertThrows(SQLException.class, () -> userService.loginUser("loginuser", "wrongpass"));
        assertThrows(SQLException.class, () -> userService.loginUser("nouser", "anypass"));
    }

    @Test
    @Order(9)
    void testGetAll_Success() throws Exception {
        userService.add(createTestUserDTO("user1", "pass1", Role.USER));
        userService.add(createTestUserDTO("user2", "pass2", Role.ADMIN));

        List<UserDTO> allUsers = userService.getAll(new HashMap<>());
        assertNotNull(allUsers);
        assertEquals(3, allUsers.size(), "Should return 3 users including admin");
    }
}
