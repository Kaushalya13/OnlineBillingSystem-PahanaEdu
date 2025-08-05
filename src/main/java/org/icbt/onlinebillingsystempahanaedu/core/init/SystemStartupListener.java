package org.icbt.onlinebillingsystempahanaedu.core.init;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.icbt.onlinebillingsystempahanaedu.core.constant.Role;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/5/2025
 * time : 12:47 PM
 */
public class SystemStartupListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(SystemStartupListener.class.getName());
    private UserService userService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "System startup initiated. Verifying existing users...");

        userService = new UserServiceImpl();

        try {
            if (userService.getAll(null).isEmpty()) {
                LOGGER.log(Level.INFO, "No users found. Creating default admin user...");

                UserDTO adminUser = new UserDTO();
                adminUser.setUsername("admin");
                adminUser.setPassword("admin123");
                adminUser.setRole(Role.ADMIN);

                boolean isCreated = userService.add(adminUser);
                if (isCreated) {
                    LOGGER.log(Level.INFO, "Default admin user 'admin' created successfully.");
                } else {
                    LOGGER.log(Level.SEVERE, "Failed to create default admin user.");
                }
            } else {
                LOGGER.log(Level.INFO, "Users already exist. Skipping admin user creation.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "System startup failed: " + e.getMessage(), e);
            throw new RuntimeException("System startup failed due to error.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "System shutdown initiated.");
    }
}
