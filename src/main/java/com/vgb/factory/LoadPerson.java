package com.vgb.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.vgb.Person;
import com.vgb.database.DataMapper;

/**
 * Factory class for instantiating {@link Person} objects.
 * <p>
 * Expects a {@link ResultSet} with the following columns:
 * <ul>
 *   <li>uuid</li>
 *   <li>firstName</li>
 *   <li>lastName</li>
 *   <li>phoneNumber</li>
 *   <li>address</li>
 * </ul>
 */
public class LoadPerson implements DataMapper<Person>{

	
	@Override
	public Person map(ResultSet rs, Connection conn) throws SQLException {
		
		UUID uuid = UUID.fromString(rs.getString("uuid"));
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String phone = rs.getString("phoneNumber");
        
        String emailsStr = rs.getString("address");
        List<String> emails = emailsStr != null && !emailsStr.isEmpty() ? 
                Arrays.asList(emailsStr.split(",")) : new ArrayList<>();
		
		return new Person(uuid,firstName, lastName, phone, emails);
	}

	
}
