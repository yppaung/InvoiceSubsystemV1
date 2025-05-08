package com.vgb.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import com.vgb.Company;
import com.vgb.Invoice;
import com.vgb.Person;
import com.vgb.database.DataMapper;
import com.vgb.database.IDLoader;
/**
 * Factory class for instantiating {@link Invoice} objects.
 * <p>
 * Expects a {@link ResultSet} with the following columns:
 * <ul>
 *   <li>uuid</li>
 *   <li>salesPersonId</li>
 *   <li>companyId</li>
 *   <li>invoiceDate</li>
 * </ul>
 */
public class LoadInvoice implements DataMapper<Invoice> {

	/**
	 * 
	 * Creates a single <code>Invoice</code> object using DataMappers generic method
	 * Loose coupled implementation to dynamically load tables from the database 
	 * 
	 * @param The Result set after executing a query using DataFactory
	 */
	@Override
	public Invoice map(ResultSet rs, Connection conn) throws SQLException {
		
		UUID uuid = UUID.fromString(rs.getString("uuid"));
		int salesPersonId = rs.getInt("salesPersonId");
		int companyId = rs.getInt("companyId");
		LocalDate invoiceDate = null;
		Person salesPerson = null;
		Company customer = null;
		
		try {
		invoiceDate = LocalDate.parse(rs.getString("invoiceDate"));
		} catch(DateTimeParseException e) {
			throw new RuntimeException(e);
		}
		
   
        salesPerson = IDLoader.loadPersonById(salesPersonId, conn);
        customer = IDLoader.loadCompanyById(companyId,conn);
        
		
		return new Invoice(uuid,customer, salesPerson, invoiceDate);
	}
	
}
