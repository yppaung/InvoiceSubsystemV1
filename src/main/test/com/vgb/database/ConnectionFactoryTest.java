package com.vgb.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionFactoryTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactoryTest.class);

    @Test
    public void testGetConnectionSuccess() {
        LOGGER.error("Starting test: testGetConnectionSuccess");

        try (Connection connection = ConnectionFactory.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            LOGGER.error("Connection obtained and verified successfully.");
        } catch (SQLException e) {
            LOGGER.error("Failed to get connection in testGetConnectionSuccess", e);
            fail("Exception thrown while getting connection: " + e.getMessage());
        }

        LOGGER.info("Finished test: testGetConnectionSuccess");
    }

    @Test
    public void testCloseConnection() {
        LOGGER.info("Starting test: testCloseConnection");

        try {
            Connection connection = ConnectionFactory.getConnection();
            assertNotNull(connection, "Connection should not be null");

            ConnectionFactory.closeConnection(connection);
            assertTrue(connection.isClosed(), "Connection should be closed");
            LOGGER.info("Connection closed successfully in test.");
        } catch (SQLException e) {
            LOGGER.error("Exception during testCloseConnection", e);
            fail("Exception thrown during connection close test: " + e.getMessage());
        }

        LOGGER.info("Finished test: testCloseConnection");
    }
}
