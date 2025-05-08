package com.vgb.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.vgb.Address;
import com.vgb.Company;
import com.vgb.Person;

/**
 * Utility class for loading and Constucting Company objects from a flat file
 */
public class CompanyLoader {
	
	
	
	
	public static final String FILE_PATH = "data/Companies.csv";
	
	public static Map<UUID, Company> loadCompany(){
		Map<UUID, Person> persons = PersonLoader.loadPerson();
		
		Map<UUID, Company> companies = new HashMap<UUID, Company>();
		
		
		String line = null;
		
		try(Scanner s = new Scanner(new File(FILE_PATH))){
			
			s.nextLine();
			
			while (s.hasNext()) {
				
				line = s.nextLine();
				
				
				if(!line.trim().isEmpty()) {
					
					Company c = null;
					Person contactPerson = null;
					String parts[] = line.split(",");
					
					UUID uuid;
					UUID contactUuid;
					
					String uuidStr = parts[0].trim();
					String contactstr = parts[1].trim();
			        if (uuidStr.isEmpty() || contactstr.isEmpty()) {
			            throw new IllegalArgumentException("Missing UUID in CSV: " + Arrays.toString(parts));
			        }
					
					try {
					uuid = UUID.fromString(uuidStr);
					}catch(IllegalArgumentException e) {
						throw new IllegalArgumentException("Invalid UUID format: " + uuidStr);
					}
					
					try {
						contactUuid = UUID.fromString(contactstr);
						contactPerson = persons.get(contactUuid);						
						}catch(IllegalArgumentException e) {
							throw new IllegalArgumentException("Invalid UUID format: " + contactstr);
						}
					
					String name = parts[2].trim();
					Address address = new Address(parts[3], parts[4], parts[5], parts[6]);
					List<String> emails = new ArrayList<>();
					
					for (int x = 4; x < parts.length; x++) {
				           if (!parts[x].trim().isEmpty()) {
				                emails.add(parts[x].trim());
				            }
				        }
					c = new Company(uuid, name, contactPerson, address);
					companies.put(uuid,c);
				}
				
			}
			s.close();
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Encountered Error on line " + line, e);
		}
		
		return companies;
	}

}
