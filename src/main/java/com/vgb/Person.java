
package com.vgb;

import java.util.List;
import java.util.UUID;
/**
 * Represents a Person in the system
 */
public class Person {
	
	private UUID uuid;
	private String firstName;
	private String lastName;
	private String phone;
	private List<String> emails;
	
	/**
	 * Constructs a person based on the given attributes
	 * @param uuid
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param emails
	 */
	public Person(UUID uuid, String firstName, String lastName, String phone, List<String> emails) {
		super();
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.emails = emails;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhone() {
		return phone;
	}

	public List<String> getEmails() {
		return emails;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s (%s) \n %30s \n %s", this.getLastName(), this.getFirstName(),
				this.getUuid(), this.getEmails(), this.getPhone());
	}
	
	
	
}
