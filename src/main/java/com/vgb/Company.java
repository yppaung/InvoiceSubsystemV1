package com.vgb;

import java.util.UUID;
/*
 * Represents a single company in the System
 * 
 */
public class Company {

	
	private UUID uuid;
	private String name;
	private Person contact;
	private Address address;
	
	/**
	 * Constructs a company object based on the given attributes
	 * @param uuid
	 * @param name
	 * @param contact
	 * @param address
	 */
	public Company(UUID uuid, String name, Person contact, Address address) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.contact = contact;
		this.address = address;
	}


	public UUID getUuid() {
		return uuid;
	}


	public String getName() {
		return name;
	}


	public Person getContact() {
		return contact;
	}


	public Address getAddress() {
		return address;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if((obj == null) || getClass() != obj.getClass()) return false;
		Company company = (Company) obj;
		return uuid.equals(company.uuid);
	}
	
	@Override
	public int hashCode() {
	    return uuid.hashCode();  
	}
	
	@Override
	public String toString() {
		
		return String.format("%s (%s) \n %s \n \n %30s", this.getName(), this.getUuid(),
				this.getContact().toString(), this.getAddress().toString());
	}


	
	
}
