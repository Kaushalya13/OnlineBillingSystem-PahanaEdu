package org.icbt.onlinebillingsystempahanaedu.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 7:59 PM
 */
public class DAOUtil {
    public static <T> T executeQueryOrUpdate(Connection connection, String sql, Object... args) throws SQLException {

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
