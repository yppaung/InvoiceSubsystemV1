package com.vgb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections. Handles connection creation
 * and safe closure with logging.
 */

public abstract class ConnectionFactory {

	private static final String URL = "jdbc:mysql://nuros.unl.edu/rsamarasinghe2";
	private static final String USERNAME = "rsamarasinghe2";
	private static final String PASSWORD = "mohmao4Coaha";

	public static Connection getConnection() throws SQLException {
		Connection connection = null;
		try {

			connection = DriverManager.getConnection(ConnectionFactory.URL, ConnectionFactory.USERNAME,
					ConnectionFactory.PASSWORD);

		} catch (SQLException e) {

			throw new SQLException("Error while connecting to the database", e);
		}
		return connection;
	}

	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();

			} catch (SQLException e) {

			}
		}
	}

}
