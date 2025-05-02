package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * JUnit test suite for VGB invoice system.
 */
public class EntityTests {

    public static final double TOLERANCE = 0.001;

    /**
     * Test for Equipment entity.
     */
    
    // TODO: Fix failures
    @Test
    public void testEquipment() {
        // Data values
        UUID uuid = UUID.randomUUID();
        String name = "Backhoe 3000";
        String model = "BH30X2";
        double cost = 95125.0;

        // Create an instance of equipment with the data values
        Equipment equipment = new Equipment(uuid, name, model, cost);

        // Expected cost and tax values
        double expectedCost = 95125.0;
        double expectedTax = Math.round(cost * 0.0525 * 100.0) / 100.0;
        double expectedTotal = cost + expectedTax;

        
        // Invoke methods to determine the cost/tax
        double actualCost = equipment.getPrice();
        double actualTax = equipment.getTaxes();
        double actualTotal = equipment.getTotal();
        

        // Use assertEquals with the TOLERANCE to compare
        assertEquals(expectedCost, actualCost, TOLERANCE);
        
        assertEquals(expectedTax, actualTax, TOLERANCE);
        
        assertEquals(expectedTotal, actualTotal, TOLERANCE);
        String s = equipment.toString();
       
        // Ensure that the string representation contains necessary elements
        
        
        assertTrue(s.contains("Backhoe 3000"));
        assertTrue(s.contains("BH30X2"));
        assertTrue(s.contains("95125.0"));
        
    }

    /**
     * Test for Lease entity.
     */
    @Test
    public void testLease() {
        // Data values
        Equipment equipment = new Equipment(UUID.randomUUID(), "Excavator", "EX45", 95125.00);
        // Define the lease period
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 1);

        // Create a Lease object
        Lease lease = new Lease(equipment, startDate, endDate);

        // Assert the calculated values
        assertEquals(95125.00, lease.getPrice(), 0.01);  // 50% markup on the amortized cost
        assertEquals(1500.00, lease.getTaxes(), 0.01);  // Flat tax applied
        assertEquals(70537.29, lease.getTotal(), 0.01);  // Total including tax

        
        String s = lease.toString();
        assertTrue(s.contains("Excavator"));
        assertTrue(s.contains("1500.0")); // Tax
        assertTrue(s.contains("69037.29")); // Agreement
        assertTrue(s.contains("70537.29")); // Total
    }

    /**
     * Test for Rental entity.
     */
    @Test
    public void testRental() {
        // Data values
        Equipment equipment = new Equipment(UUID.randomUUID(), "Excavator", "EX45", 95125.00);
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 1, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 5, 2, 9, 0);

        // Create an instance of rental
        Rental rental = new Rental(equipment, ChronoUnit.HOURS.between(startTime, endTime));

        // Calculate expected values (assuming hourly rental rate)
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        double expectedAgreement = (equipment.getPrice() * 0.001) ; // Price per hour with a factor
        double expectedSubtotal = expectedAgreement * hours;
        double expectedTax = Math.round(equipment.getPrice() * 0.0438 * 100.0) / 100.0;
 
        double expectedTotal = (Math.round((expectedSubtotal + expectedTax)* 100.0)/100.0);

        // Invoke methods to determine the agreement, tax, and total
        double actualAgreement = rental.calculateRate();
        double actualTax = rental.getTaxes();
        double actualTotal = rental.getTotal();
        String s = rental.toString();
        
      
        // Use assertEquals with the TOLERANCE to compare
        assertEquals(expectedAgreement, actualAgreement, TOLERANCE);
        
        assertEquals(expectedTax, actualTax, TOLERANCE);
       
        assertEquals(expectedTotal, actualTotal, TOLERANCE);
        
        
        assertTrue(s.contains("Excavator"));
        assertTrue(s.contains("2378.13")); // Agreement
        assertTrue(s.contains("4166.47")); // Tax
        assertTrue(s.contains("6544.6")); // Total
    }

    /**
     * Test for Material entity.
     */
    @Test
    public void testMaterial() {
        // Data values
        UUID uuid = UUID.randomUUID();
        String name = "Steel Beam";
        String unit = "meters";
        double costPerUnit = 50.0;
        double quantity = 10;

        // Create an instance of material
        Material material = new Material(uuid, name, unit, costPerUnit);
        Material materialWithQuantity = new Material(material, quantity);

        // Expected values
        
        double expectedTax = (costPerUnit * quantity)  * 0.0715;
        double expectedTotal = (costPerUnit * quantity) + expectedTax;
        // Invoke methods to determine the total and tax
        double actualTotal = materialWithQuantity.getTotal();
        double actualTax = materialWithQuantity.getTaxes();

        // Use assertEquals with the TOLERANCE to compare
        assertEquals(expectedTotal, actualTotal, TOLERANCE);
        assertEquals(expectedTax, actualTax, TOLERANCE);

        String s = materialWithQuantity.toString();
        assertTrue(s.contains("Steel Beam"));
        assertTrue(s.contains("50.0")); // Cost per unit
        assertTrue(s.contains("meters")); // Unit
        assertTrue(s.contains("535.75")); // Total
    }

    /**
     * Test for Contract entity.
     */
    @Test
    public void testContract() {
        // Data values
        UUID uuid = UUID.randomUUID();
        String name = "Construction Agreement";

        // Create a company
        List<String> ball = new ArrayList<>();
        ball.add("Gmail.com");
        Person person = new Person(UUID.fromString("7af2d8f9-d09e-4992-a41d-3bec9ed2aa31"), "Might", "KMS", "60527", ball);
        Address address = new Address("Blal", "NE", "hdh", "asdas");
        Company company = new Company(UUID.randomUUID(), "ABC Construction", person, address);
        double price = 20000.0;

        // Create an instance of contract
        Contract contract = new Contract(uuid, name, price, company);

        // Expected total value
        double expectedTotal = price;

        // Invoke method to determine the total
        double actualTotal = contract.getTotal();

        // Use assertEquals with the TOLERANCE to compare
        assertEquals(expectedTotal, actualTotal, TOLERANCE);

        String s = contract.toString();
       
        
        assertTrue(s.contains("Construction Agreement"));
        assertTrue(s.contains("ABC Construction")); // Company name
        assertTrue(s.contains("20000.0")); // Price since no tax
    }
}
