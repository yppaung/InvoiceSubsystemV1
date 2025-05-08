package com.vgb.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vgb.Address;
import com.vgb.database.DataMapper;

/**
 * Factory class for instantiating {@link Address} objects.
 * <p>
 * Expects a {@link ResultSet} with the following columns:
 * <ul>
 *   <li>street</li>
 *   <li>city</li>
 *   <li>stateName</li>
 *   <li>zip</li>
 * </ul>
 */
public class LoadAddress implements DataMapper<Address> {
	
	
	@Override
	public Address map(ResultSet rs, Connection conn) throws SQLException {
		
		String street = rs.getString("street");
		String city = rs.getString("city");
		String state = rs.getString("stateCode");
		String zip = rs.getString("zip");
		
		return new Address(street, city, state, zip);
	}
	
	

}
