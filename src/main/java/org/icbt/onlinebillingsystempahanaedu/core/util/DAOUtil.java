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

    //Execute an sql UPDATE , INSERT or DELETE statement.
    public static boolean executeUpdate(Connection connection, String sql, Object... args) {
        try (PreparedStatement preparedStatement  = connection.prepareStatement(sql)){
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
                return preparedStatement .executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Update/Insert/Delete Error: " + sql + " - " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    //Execute an SQL SELECT statement
    public static ResultSet executeQuery(Connection connection, String sql, Object... args) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject((i + 1),args[i]);
            }
            return preparedStatement.executeQuery();
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL SELECT Error: " + sql + " - " + e.getMessage(), e);
            //close the statement if an error occurs before returning ResultSet
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }catch (SQLException e1) {
                    logger.log(Level.WARNING, "SQL CLOSE Error: " + sql + " - " + e.getMessage(), e);
                }
            }
            throw new RuntimeException(e);
        }
    }


    public static <T> T executeSql(Connection connection, String sql, Object... args) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject((i + 1), args[i]);
            }

            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                return (T) preparedStatement.executeQuery();
            }

            return (T) (Boolean) (preparedStatement.executeUpdate() > 0);
        }
    }
}
