package org.icbt.onlinebillingsystempahanaedu.user.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;

import java.io.IOException;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(req.getParameter("username"));
            userDTO.setPassword(req.getParameter("password"));
            userDTO.setRole(Role.valueOf(req.getParameter("role").toUpperCase()));


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
