package com.vgb.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vgb.Person;
import com.vgb.Address;
import com.vgb.Invoice;

/**
 * DataLoader class for loading entity data from the database
 * Handles Person, Address, Company, Item, Invoice and InvoiceItem entities
 */
public class DataLoader {
    
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
   
    
    /**
     * 
     * Loads data from Database 
     * 
     * @param <T> Generic that should be corresponding to the mapper base class </br>
     * Example : UUID, Company
     * @param query The SQL query that will load the required the tables from the DB
     * @param mapper The factory class required to create the relevant POJOs
     * @return Required Map to be used in ReportUtils
     */
    public <T> Map<UUID,T> loadData(String query, DataMapper<T> mapper, Connection conn) {
    	
    	try {
    	    conn = ConnectionFactory.getConnection();
    	    }catch (SQLException e) {
    	    	LOGGER.error("Bad Connection", e);
    	    }
    	
        Map<UUID, T> results = new HashMap<>();
        try {
        	ResultSet rs = DataFactory.runQuery(query);
        	
            while (rs.next()) {
            	
            	UUID uuid = UUID.fromString(rs.getString("uuid"));
                results.put(uuid, mapper.map(rs, conn));
            }
            
            rs.close();
        } catch (SQLException e) {
            LOGGER.error("Error loading data", e);
        }
        
    	try {
			if(!conn.isClosed()) {
			ConnectionFactory.closeConnection(conn);
			}
		} catch (SQLException e) {
			LOGGER.error("Closing connection no no :(",e);
			e.printStackTrace();
		}
        
        return results;
    }
    
    /**
     * Executes an sql query and groups the resulting mapped items by their corresponding {@link Invoice}.
     *
     * <p>This method assumes each row in the result set contains an {@code invoiceId} column
     * that can be used to load the full {@link Invoice} object. Each row is also mapped into an item
     * of type {@code T} using the provided {@link DataMapper}. The result is a map where each key is
     * an {@code Invoice}, and the value is a list of associated items.
     *
     * @param <T>    The type of the items
     * @param query  The SQL query to execute.
     * @param mapper The mapper used to convert each row into an object of type {@code T}.
     * @param conn   The database connection
     * @return A map grouping {@code T} items under their corresponding {@code Invoice}.
     * @throws SQLException If a database access error occurs.
     */
    public <T> Map<Invoice, List<T>> groupData(String query, DataMapper<T> mapper, Connection conn) throws SQLException {
    	 
    	    Map<Invoice, List<T>> results = new HashMap<>();

    	    try {
   	        
    	        ResultSet rs = DataFactory.runQuery(query);

    	        while (rs.next()) {
    	            Invoice invoice = IDLoader.loadInvoiceById(rs.getInt("invoiceId"), conn) ;
    	            T item = mapper.map(rs, conn);

    	            results.computeIfAbsent(invoice, k -> new ArrayList<>()).add(item);
    	        }

    	        rs.close();
    	    } catch (SQLException e) {
    	        LOGGER.error("Error grouping data", e);
    	    } finally {
    	        try {
    	            if (conn != null && !conn.isClosed()) {
    	                ConnectionFactory.closeConnection(conn);
    	            }
    	        } catch (SQLException e) {
    	            LOGGER.error("Closing connection failed", e);
    	        }
    	    }
        return results;
    }
  
    /**
     * Loads a single person record from the database
     * @param personId
     * @return Single person object
     */
    public static Person loadPersonById(int personId, Connection conn) {
        Person person = null;
        
        
        try {
                    
           
            String query = """
            		SELECT p.uuid, p.firstName, p.lastName, p.phoneNumber, e.address
            		FROM Person p JOIN Email e on e.personId = p.personId
            		WHERE p.personId = ?
            		""";
            		
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            UUID uuid = null;
            List<String> emails = new ArrayList<>();
            if (rs.next()) {
            	
            	try {
            	    String uuidStr = rs.getString("uuid");
            	    if (uuidStr != null && !uuidStr.isEmpty()) {
            	        uuid = UUID.fromString(uuidStr);
            	        
            	    } else {
            	        LOGGER.info("UUID string is null or empty");
            	    }
            	} catch (IllegalArgumentException e) {
            	    LOGGER.info("Invalid UUID format: {}", e.getMessage());
            	}
            	
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String phoneNumber = rs.getString("phoneNumber");
                
                rs.close();
                ps.close();
                
                query = """
                		SELECT address FROM Email 
                		WHERE Email.personId = ?
                		""";
                
                ps = conn.prepareStatement(query);
                ps.setInt(1, personId);
                rs = ps.executeQuery();
                
                while(rs.next()) {
                	String email = rs.getString("address");
                	emails.add(email);
                }
                
        
                person = new Person(uuid, firstName, lastName, phoneNumber, emails);
                
                ConnectionFactory.closeConnection(conn);
                
                rs.close();
                ps.close();
                
            }
            
            
            
        } catch (SQLException e) {
            LOGGER.info("SQLException: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
            	LOGGER.info("SQLException");
                throw new RuntimeException(e);
            }
        }
        
        return person;
    }
    
   
/**
 * Loads an address record from the database    
 * @param addressId
 * @param conn
 * @return
 */

    
    protected static Address loadAddressById(int addressId, Connection conn) {
    	Address address = null;
    	
    	
    	try {
    		LOGGER.info("Loading addres from DB");
    		
    		
    		String query = """
    				SELECT a.addressId, a.street, a.city, s.stateName, z.zip
    				FROM Address a
    				JOIN State s ON a.stateId = s.stateId
    				JOIN ZipCode z ON a.zipId = z.zipId
    				WHERE a.addressId = ?
    				""";
    		PreparedStatement ps = conn.prepareStatement(query);
    		ps.setInt(1, addressId);
    		ResultSet rs = ps.executeQuery();
    		
    		if (rs.next()) {
    			String street = rs.getString("street");
    			String city = rs.getString("city");
    			String state = rs.getString("stateName");
    			String zip = rs.getString("zip");
    			
    			address = new Address(street, city, state, zip);
    		}
    		
    		
    	}catch (SQLException e) {
            LOGGER.error("Error loading AN ADDRESS from database", e);
    	
    	}
    	return address;
    }
}