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

import com.vgb.Person;
/*
 * Utility Class for reading a person from a flat data file (.csv)
 * 
 */
public class PersonLoader {

		public static final String FILE_PATH = "data/Persons.csv";
		
		/**
		 * Creates a Person object with a UUID reading a flat file
		 * @return Person
		 */
		public static Map<UUID,Person> loadPerson(){
			
			Map<UUID, Person> persons = new HashMap<UUID,Person>();
			
			
			String line = null;
			
			try(Scanner s = new Scanner(new File(FILE_PATH))){
				
				s.nextLine();
				
				
				while (s.hasNext()) {
					
					line = s.nextLine();
					
					
					if(!line.trim().isEmpty()) {
						
						Person p = null;
						String parts[] = line.split(",");
						
						UUID uuid;
						
						String uuidStr = parts[0].trim();
				        if (uuidStr.isEmpty()) {
				            throw new IllegalArgumentException("Missing UUID in CSV: " + Arrays.toString(parts));
				        }
						
						try {
						uuid = UUID.fromString(uuidStr);
						}catch(IllegalArgumentException e) {
							throw new IllegalArgumentException("Invalid UUID format: " + uuidStr);
						}
						
						String firstName = parts[1].trim();
						String lastName = parts[2].trim();
						String phone = parts[3].trim();
						List<String> emails = new ArrayList<>();
						
						for (int x = 4; x < parts.length; x++) {
					           if (!parts[x].trim().isEmpty()) {
					                emails.add(parts[x].trim());
					            }
					        }
						p = new Person(uuid, firstName, lastName, phone, emails);
						persons.put(uuid,p);
					}
					
				}
				s.close();
				
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Encountered Error on line " + line, e);
			}
			
			return persons;
		}
		
}

