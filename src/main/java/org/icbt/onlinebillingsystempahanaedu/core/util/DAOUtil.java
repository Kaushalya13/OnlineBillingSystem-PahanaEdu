package org.icbt.onlinebillingsystempahanaedu.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:59 PM
 */
public class DAOUtil {
    private static final Logger logger = Logger.getLogger(DAOUtil.class.getName());

    private DAOUtil() {
    }

    public static boolean executeUpdate(Connection connection, String sql, Object... args) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Update/Insert/Delete Error: " + sql + " - " + e.getMessage(), e);
            throw new RuntimeException("Error executing update statement", e);
        }
    }

    public static ResultSet executeQuery(Connection connection, String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject((i + 1), args[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL SELECT Error: " + sql + " - " + e.getMessage(), e);
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e1) {
                    logger.log(Level.WARNING, "Error closing PreparedStatement after query error: " + e1.getMessage(), e1);
                }
            }
            throw new RuntimeException("Error executing query statement", e);
        }
    }
}
