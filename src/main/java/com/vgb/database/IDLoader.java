package com.vgb.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vgb.Address;
import com.vgb.Company;
import com.vgb.Invoice;
import com.vgb.Person;
import com.vgb.factory.LoadAddress;
import com.vgb.factory.LoadPerson;



/**
 * A generic service layer class used to load a single record from the database by ID.
 *
 * @param <T> The type of object being loaded. This type must be supported by the provided {@link DataMapper}.
 *
 * <p>This class delegates the mapping of the {@link ResultSet} to the {@link DataMapper} implementation
 * provided during instantiation.
 */
public class IDLoader <T> {
    private final DataMapper<T> mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(IDLoader.class);
    

    public IDLoader(DataMapper<T> mapper) {
        this.mapper = mapper;
    }
    
    public T loadById(String query, int id, Connection conn) {
    	T entity = null;
    	
    	
    	try {
    		
    		PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
    		
            if(rs.next()) {
    			entity = mapper.map(rs, conn);
    		}
            rs.close();
            ps.close();
    		
    	}catch(SQLException e) {
    		LOGGER.error("Something bad happening loading generic by ID :(", e);
    	}
    	
    	return entity;
    	
    }
    
    
    public T loadById(String query, int invoiceId, int invoiceItemId, Connection conn) {
    	T entity = null;
    	
    	
    	try {
    		
    		PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, invoiceId);
            ps.setInt(2, invoiceItemId);
            ResultSet rs = ps.executeQuery();
    		
            if(rs.next()) {
    			entity = mapper.map(rs, conn);
    		}
            rs.close();
            ps.close();
    		
    	}catch(SQLException e) {
    		LOGGER.error("Something bad happening loading generic by ID :(", e);
    	}
    	
    	return entity;
    	
    }
    
    public T loadById(String query, UUID uuid, Connection conn) {
    	T entity = null;
    	
    	try {
    		
    		PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
    		
            if(rs.next()) {
    			entity = mapper.map(rs, conn);
    		}
            rs.close();
            ps.close();
    		
    	}catch(SQLException e) {
    		LOGGER.error("Something bad happening loading generic by ID :(", e);
    	}
    	
    	return entity;
    	
    }
    
    /**
     * Loads a {@link Person} from the database using their unique ID.
     *
     * <p>This method uses {@link IDLoader} with a {@link LoadPerson} mapper to execute a SQL query
     * that joins the {@code Person} and {@code Email} tables and maps the result into a {@code Person} object.
     *
     * @param id   The unique ID of the person to load.
     * @param conn The active database connection to use.
     * @return The {@code Person} object if found, or {@code null} if no matching record exists.
     */

    public static Person loadPersonById(int id, Connection conn) {
    	
    	Person p = null;
    	IDLoader<Person> pLoader = new IDLoader<>(new LoadPerson());
    	p = pLoader.loadById("""
				SELECT p.uuid, p.firstName, p.lastName, p.phoneNumber, e.address
            		FROM Person p JOIN Email e on e.personId = p.personId
            		WHERE p.personId = ?
				""", id, conn);
    	
    	return p;
    	
    }
    
    
    public static Address loadAddressById(int id, Connection conn) {
    	Address a = null;
    	
    	IDLoader<Address> aLoader = new IDLoader<>(new LoadAddress());
    	
    	a = aLoader.loadById("""
          		SELECT a.addressId, a.street, a.city, s.stateCode, z.zip
				FROM Address a
				JOIN State s ON a.stateId = s.stateId
				JOIN ZipCode z ON a.zipId = z.zipId
				WHERE a.addressId = ?
          		""",id,conn);
    	
    	return a;
    }
    
    public static Company loadCompanyById(int companyId, Connection conn) {
    	Person contact = null;
    	Address address = null;
    	Company company = null;

    	
    	try {
    		
    		String query = """
    				SELECT uuid, companyName, personId, addressId
    				FROM Company
    				WHERE companyId = ?
    				""";
    		  PreparedStatement ps = conn.prepareStatement(query);
              ps.setInt(1, companyId);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) {
              UUID uuid = UUID.fromString(rs.getString("uuid"));
              String name = rs.getString("companyName");
              int personId = rs.getInt("personId");
              int addressId = rs.getInt("addressId");
              
              contact = IDLoader.loadPersonById(personId, conn);

              address = IDLoader.loadAddressById(addressId, conn);
              
              company = new Company(uuid, name, contact, address);
              } else {
                  LOGGER.error( "No company found with companyId = " + companyId);
              }
              
              
              
    	}catch(SQLException e) {
    		LOGGER.error("COMPANY NOT LOADING BY ID", e);
    	}
            return company;
            
    }
    
    public static Invoice loadInvoiceById(int id, Connection conn) {
  
    	UUID uuid = null;
    	Company customer = null;
    	Person salesPerson = null;
    	LocalDate invoiceDate = null;
    	try {
    		String query = """
    				SELECT i.invoiceId, i.uuid, i.companyId, i.salesPersonId, i.invoiceDate
    				FROM Invoice i 
    				WHERE invoiceId = ?
    				""";
    		PreparedStatement ps = conn.prepareStatement(query);
    		ps.setInt(1,id);
    		ResultSet rs = ps.executeQuery();
    		
    		if(rs.next()) {
    		uuid = UUID.fromString(rs.getString("i.uuid"));
    		
    		int customerId = rs.getInt("i.companyId");
    		int personId = rs.getInt("i.salesPersonId");
    		
    		customer = IDLoader.loadCompanyById(customerId, conn);
    		
    		salesPerson = IDLoader.loadPersonById(personId, conn);
    		
    		try {
    			invoiceDate = LocalDate.parse(rs.getString("invoiceDate"));
    			} catch(DateTimeParseException e) {
    				throw new RuntimeException(e);
    			}
    		}	
    	}catch(SQLException e) {
    		LOGGER.error("Bad Connection invoice by id", e);
    	}
    
    	
    	return new Invoice(uuid, customer, salesPerson, invoiceDate);
    }
    
}
