package com.vgb.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generic interface for mapping rows from a {@link ResultSet} to Java objects.
 * <p>
 * This interface is used to create loosely-coupled implementations
 * that dynamically load and map data from different database tables into corresponding
 * system objects.
 * </p>
 *
 * @param <T> the type of object to map each row into
 * 
 * @see DataFactory
 */
public interface DataMapper <T> {
	
	/**
	 * 
	 * Creates a single object by mapping to a single row in {@link ResultSet} of type {@code T} </br>
	 * Loose coupled implementation to dynamically load tables from the database 
	 * 
	 * @param rs Result set after executing a query using {@link DataFactory}
	 * @param conn Connection used
	 */
	T map(ResultSet rs, Connection conn) throws SQLException;
	
}
