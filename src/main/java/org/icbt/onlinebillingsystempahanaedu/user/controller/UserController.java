package org.icbt.onlinebillingsystempahanaedu.user.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.SendResponse;
import org.icbt.onlinebillingsystempahanaedu.core.validation.CustomValidation;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:17 PM
 */
@WebServlet(name = "UserController", urlPatterns = "/users")
public class UserController extends HttpServlet {
    private UserService userService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    private Integer getUserIdFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return (Integer) session.getAttribute("userId");
        }
        return null;
    }

    private String getUserRoleFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("role") != null) {
            return (String) session.getAttribute("role");
        }
        return null;
    }

    private Map<String, Object> userDTOtoMap(UserDTO userDTO) {
        if (userDTO == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("id", userDTO.getId());
        map.put("username", userDTO.getUsername());
        map.put("role", userDTO.getRole().name());
        map.put("createdAt", userDTO.getCreatedAt());
        map.put("updatedAt", userDTO.getUpdatedAt());
        map.put("deletedAt", userDTO.getDeletedAt());

        return map;
    }

    private String safeDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error decoding URL-encoded parameter: " + e.getMessage());
            return "";
        }
    }

    private Map<String, String> parseUrlEncodedBody(HttpServletRequest req) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            if (body != null && !body.isEmpty()) {
                Arrays.stream(body.split("&"))
                        .forEach(pair -> {
                            String[] keyValue = pair.split("=", 2);
                            if (keyValue.length == 2) {
                                String key = safeDecode(keyValue[0]);
                                String value = safeDecode(keyValue[1]);
                                params.put(key, value);
                            }
                        });
            }
        }
        return params;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equals(action)) {

            System.out.println("on add user method on User controller (POST)");

            Integer currentUserId = getUserIdFromSession(req);
            String currentUserRole = getUserRoleFromSession(req);

            if (currentUserId == null || !"ADMIN".equals(currentUserRole)) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of("message", "Unauthorized: Only admin can add users."));
                return;
            }

            try {
                UserDTO dto = new UserDTO();
                dto.setUsername(req.getParameter("username"));
                dto.setPassword(req.getParameter("password"));
                dto.setRole(Role.valueOf(req.getParameter("role").toUpperCase()));


                if (dto.getUsername() == null || dto.getUsername().trim().isEmpty() ||
                        dto.getPassword() == null || dto.getPassword().trim().isEmpty() ||
                        dto.getRole() == null) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Missing required user fields (username, password, role)."));
                    return;
                }

                try {
                    CustomValidation.validateUser(dto);
                } catch (CustomException e) {
                    logger.warning("User Validation Failed: " + e.getMessage());
                    SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                            Map.of("message", "Invalid data", "errors", e.getMessage()));
                    return;
                }

                boolean addedUser = userService.add(dto);
                if (addedUser) {
                    UserDTO newlyAddedUser = userService.findByUsername(dto.getUsername());
                    SendResponse.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "User added successfully", "user", userDTOtoMap(newlyAddedUser)));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "User addition failed unexpectedly."));
                }
            } catch (CustomException e) {
                logger.log(Level.WARNING, "Business error during user add: " + e.getExceptionType().name() + " - " + e.getMessage());
                String errorMessage;
                int statusCode = HttpServletResponse.SC_BAD_REQUEST;
                switch (e.getExceptionType()) {
                    case USER_ALREADY_EXISTS:
                        errorMessage = "User with this username already exists.";
                        break;
                    case USER_CREATION_FAILED:
                        errorMessage = "Failed to create user due to a system error.";
                        statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                        break;
                    case DATABASE_ERROR:
                        errorMessage = "A database error occurred during user addition.";
                        statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                        break;
                    default:
                        errorMessage = "An unexpected business error occurred: " + e.getExceptionType().name();
                        break;
                }
                SendResponse.sendJson(resp, statusCode, Map.of("message", errorMessage));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error during POST /users (add user): " + e.getMessage(), e);
                SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error during user addition."));
            }
        } else {

            System.out.println("on Login user method on User controller (POST)");

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            System.out.println("on User Login on UserController");
            System.out.println("username: " + username);
            System.out.println("password: " + password);

            try {
                UserDTO authenticatedUser = userService.loginUser(username, password);

                if (authenticatedUser != null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("username", authenticatedUser.getUsername());
                    session.setAttribute("role", authenticatedUser.getRole().name());
                    session.setAttribute("userId", authenticatedUser.getId());

                    logger.log(Level.INFO, "User '" + username + "' logged in successfully with role: " + authenticatedUser.getRole().name());
                    resp.sendRedirect("dashboard.jsp");
                } else {
                    req.setAttribute("error", "Invalid username or password.");
                    logger.log(Level.WARNING, "Login failed for username: " + username + " - Invalid credentials.");
                    req.getRequestDispatcher("index.jsp").forward(req, resp);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Database error during login for username: " + username + " - " + e.getMessage(), e);
                req.setAttribute("error", "A database error occurred. Please try again later.");
                req.getRequestDispatcher("index.jsp").forward(req, resp);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error during login for username: " + username + " - " + e.getMessage(), e);
                req.setAttribute("error", "An unexpected error occurred. Please try again.");
                req.getRequestDispatcher("index.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String searchParam = req.getParameter("search");
        String action = req.getParameter("action");

        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                session.invalidate();
                logger.log(Level.INFO, "User '" + username + "' logged out successfully.");
            }
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        try {
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int userId = Integer.parseInt(idParam);
                    UserDTO user = userService.searchById(userId);

                    if (user != null) {
                        SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of("user", userDTOtoMap(user)));
                    } else {
                        SendResponse.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "User not found with ID: " + userId));
                    }
                } catch (NumberFormatException e) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid user ID format."));
                }
            } else {
                Map<String, String> searchParams = new HashMap<>();
                if (searchParam != null && !searchParam.trim().isEmpty()) {
                    searchParams.put("search", searchParam.trim());
                }

                List<Map<String, Object>> userList = userService.getAll(searchParams)
                        .stream()
                        .map(this::userDTOtoMap)
                        .collect(Collectors.toList());

                SendResponse.sendJson(resp, HttpServletResponse.SC_OK, userList);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during GET /users: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "An internal server error occurred."));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> params = parseUrlEncodedBody(req);
        String action = params.get("action");

        if ("update".equals(action)) {
            if (!"ADMIN".equals(getUserRoleFromSession(req))) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Forbidden: Only admins can update users."));
                return;
            }

            try {
                UserDTO dto = new UserDTO();
                dto.setId(Integer.parseInt(params.get("id")));
                dto.setUsername(params.get("username"));
                dto.setRole(Role.valueOf(params.get("role").toUpperCase()));
                dto.setPassword(params.get("password"));

                boolean isUpdated = userService.update(dto);

                if (isUpdated) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "User updated successfully."));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "User update failed unexpectedly."));
                }
            } catch (NumberFormatException e) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid user ID provided."));
            } catch (CustomException e) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", e.getMessage()));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during PUT /users (update): " + e.getMessage(), e);
                SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "An internal server error occurred."));
            }
        } else {
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid action for PUT request."));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"ADMIN".equals(getUserRoleFromSession(req))) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Forbidden: Only admins can delete users."));
            return;
        }

        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            boolean isDeleted = userService.delete(userId);

            if (isDeleted) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "User deleted successfully."));
            } else {
                SendResponse.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "User not found or could not be deleted."));
            }
        } catch (NumberFormatException e) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid user ID provided."));
        } catch (CustomException e) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during DELETE /users: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "An internal server error occurred."));
        }
    }

}
