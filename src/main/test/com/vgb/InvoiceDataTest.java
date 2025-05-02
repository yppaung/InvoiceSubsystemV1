package com.vgb;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


import org.junit.jupiter.api.Test;
import com.vgb.database.ConnectionFactory;

public class InvoiceDataTest {
	
	@Test
	public void testAddPersonSuccessfully() throws SQLException {
	    UUID testUuid = UUID.randomUUID();
	    String firstName = "John";
	    String lastName = "Doe";
	    String phone = "1234567890";

	    // Call the method
	    InvoiceData.addPerson(testUuid, firstName, lastName, phone);

	    // Verify person was inserted
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("""
	                select p.uuid, p.firstName, p.lastName, p.phoneNumber
					from Person p where p.uuid = ?
	                 """)) {
	        stmt.setString(1, testUuid.toString());

	        try (ResultSet rs = stmt.executeQuery()) {
	            assertTrue(rs.next(), "Person should exist in database after insertion");
	            assertEquals(firstName, rs.getString("firstName"));
	            assertEquals(lastName, rs.getString("lastName"));
	            assertEquals(phone, rs.getString("phoneNumber"));
	        }
	    }
	}
	
	@Test
	public void testDuplicatePerson() throws SQLException {
	    UUID testUuid = UUID.fromString("4c56e3b0-c80b-47a3-acfb-bafb512606ad");
	    String firstName = "John";
	    String lastName = "Doe";
	    String phone = "1234567890";

	    // Call the method
	    InvoiceData.addPerson(testUuid, firstName, lastName, phone);

	    // Verify person was inserted
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("""
	                select p.uuid, p.firstName, p.lastName, p.phoneNumber
					from Person p where p.uuid = ?
	                 """)) {
	        stmt.setString(1, testUuid.toString());

	        try (ResultSet rs = stmt.executeQuery()) {
	            assertTrue(rs.next(), "Person should exist in database after insertion");
	            assertEquals(firstName, rs.getString("firstName"));
	            assertEquals(lastName, rs.getString("lastName"));
	            assertEquals(phone, rs.getString("phoneNumber"));
	        }
	    }
	}
	
	@Test
	public void testAddEmailToPerson() throws SQLException {
	    UUID testUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
	    String testEmail = "john.doe@example.com";

	    
	    InvoiceData.addPerson(testUuid, "John", "Doe", "1234567890");

	    
	    InvoiceData.addEmail(testUuid, testEmail);

	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("""
	            SELECT e.address 
	            FROM Email e
	            JOIN Person p ON p.personId = e.personId
	            WHERE p.uuid = ?
	        """)) {
	        stmt.setString(1, testUuid.toString());

	        try (ResultSet rs = stmt.executeQuery()) {
	            boolean emailFound = false;
	            while (rs.next()) {
	                if (testEmail.equalsIgnoreCase(rs.getString("address"))) {
	                    emailFound = true;
	                    break;
	                }
	            }
	            assertTrue(emailFound, "Email should be found for the given person UUID.");
	        }
	    }
	}
	
	@Test
	public void testEmailEdge() throws SQLException {
	    UUID testUuid = UUID.fromString("11111111-1111-1111-1111-111111111112");
	    String testEmail = "john.doe@example.com";
	    
	    InvoiceData.addEmail(testUuid, testEmail);

	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("""
	            SELECT e.address 
	            FROM Email e
	            JOIN Person p ON p.personId = e.personId
	            WHERE p.uuid = ?
	        """)) {
	        stmt.setString(1, testUuid.toString());

	        try (ResultSet rs = stmt.executeQuery()) {
	            boolean emailFound = false;
	            while (rs.next()) {
	                if (testEmail.equalsIgnoreCase(rs.getString("address"))) {
	                    emailFound = true;
	                    break;
	                }
	            }
	            assertFalse(emailFound, "Email should be found for the given person UUID.");
	        }
	    }
	}

	
	@Test
	public void testAddCompanySuccessfully() throws SQLException {
	    UUID contactUuid = UUID.randomUUID();
	    UUID companyUuid =UUID.randomUUID();
	    String firstName = "Alice";
	    String lastName = "Smith";
	    String phone = "9876543210";

	    // Add the person first
	    InvoiceData.addPerson(contactUuid, firstName, lastName, phone);

	    // Assume valid address state/zip data already exists in ZipCode/State tables
	    String companyName = "TechCorp";
	    String street = "456 Elm St";
	    String city = "Omaha";
	    String state = "Nebraska";
	    String zip = "68102";

	    InvoiceData.addCompany(companyUuid, contactUuid, companyName, street, city, state, zip);

	    // Verify insertion
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("""
	            SELECT c.uuid, c.companyName, a.street, a.city
	            FROM Company c
	            JOIN Address a ON c.addressId = a.addressId
	            WHERE c.uuid = ?
	        """)) {
	        stmt.setString(1, companyUuid.toString());
	        try (ResultSet rs = stmt.executeQuery()) {
	            assertTrue(rs.next(), "Company should exist in the database");
	            assertEquals(companyName, rs.getString("companyName"));
	            assertEquals(street, rs.getString("street"));
	            assertEquals(city, rs.getString("city"));
	        }
	    }
	}
	
	@Test
	public void testAddCompanyWithInvalidContactUuid() throws SQLException {
	    UUID invalidContactUuid = UUID.randomUUID(); // Not added to Person
	    UUID companyUuid = UUID.randomUUID();

	    // Pre-insert the address manually
	    String street = "789 Oak St";
	    String city = "Lincoln";
	    String state = "Nebraska";
	    String zip = "55401";

	    try (Connection conn = ConnectionFactory.getConnection()) {
	        // Insert state and zip if not already there
	        try (PreparedStatement stateStmt = conn.prepareStatement("INSERT IGNORE INTO State (stateName) VALUES (?)")) {
	            stateStmt.setString(1, state);
	            stateStmt.executeUpdate();
	        }

	        try (PreparedStatement zipStmt = conn.prepareStatement("""
	            INSERT IGNORE INTO ZipCode (zip)
	            VALUES (?)
	        """)) {
	            zipStmt.setString(1, zip);
	            zipStmt.executeUpdate();
	        }

	        try (PreparedStatement addressStmt = conn.prepareStatement("""
	            INSERT IGNORE INTO Address (street, city, stateId, zipId)
	            VALUES (?, ?, 
	                (SELECT stateId FROM State WHERE stateName = ?),
	                (SELECT zipId FROM ZipCode WHERE zip = ?)
	            )
	        """)) {
	            addressStmt.setString(1, street);
	            addressStmt.setString(2, city);
	            addressStmt.setString(3, state);
	            addressStmt.setString(4, zip);
	            addressStmt.executeUpdate();
	        }
	    }

	    // Now attempt to add the company with a non-existent person
	    InvoiceData.addCompany(companyUuid, invalidContactUuid, "GhostCorp", street, city, state, zip);

	    // Verify that the company was NOT inserted
	    try (Connection conn = ConnectionFactory.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Company WHERE uuid = ?")) {
	        stmt.setString(1, companyUuid.toString());
	        try (ResultSet rs = stmt.executeQuery()) {
	            assertFalse(rs.next(), "Company should not be added with invalid contact UUID");
	        }
	    }
	}


}
