package org.icbt.onlinebillingsystempahanaedu.core.db;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 7/22/2025
 * time : 5:13 PM
 */
public class DBConnection {
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String DRIVER;
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    private DBConnection() {
    }

    static {
        try (InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties properties = new Properties();
            if (inputStream == null) {
                logger.log(Level.SEVERE, "database.properties file not found in the classpath.");
                throw new RuntimeException("database.properties file not found in the classpath.");
            }
            properties.load(inputStream);
            URL = properties.getProperty("database.url");
            USERNAME = properties.getProperty("database.username");
            PASSWORD = properties.getProperty("database.password");
            DRIVER = properties.getProperty("database.driver");

            Class.forName(DRIVER);
            logger.log(Level.INFO, "Database driver loaded successfully: " + DRIVER);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while initializing database configuration.", e);
            throw new RuntimeException("Error occurred while initializing database configuration.", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            logger.log(Level.INFO, "Database connection established successfully to URL: " + URL);
            return connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to establish database connection to URL: " + URL, e);
            throw new RuntimeException("Unable to establish database connection.", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.log(Level.INFO, "Database connection closed.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while closing database connection.", e);
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
                logger.log(Level.INFO, "Database statement closed.");
            }catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while closing database statement.", e);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
                logger.log(Level.INFO, "Database result set closed.");
            }catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while closing result set.", e);
            }
        }
    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()){
                    connection.rollback();
                    logger.log(Level.INFO, "Database connection rolled back.");
                }else {
                    logger.log(Level.INFO, "Database connection rolled back.");
                }
            }catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while rolling back database connection.", e);
            }
        }else {
            logger.log(Level.SEVERE, "Database connection rolled back.");
        }
    }
}
