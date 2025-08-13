package org.icbt.onlinebillingsystempahanaedu.user.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:17 PM
 */
@WebServlet(name = "UserController",urlPatterns = "/users")
public class UserController extends HttpServlet {
    private UserService userService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

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


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                session.invalidate();
                logger.log(Level.INFO, "User '" + username + "' logged out successfully.");
            }
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter.");
        }
    }

}
