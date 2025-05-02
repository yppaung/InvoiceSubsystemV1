package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test suite for VGB invoice system.
 */
public class InvoiceTests {

    public static final double TOLERANCE = 0.001;

    // Common test objects
    private Person salesperson;
    private Person contactPerson;
    private Address companyAddress;
    private Company customer;
    private UUID invoiceUUID;
    private Invoice invoice;
    private LocalDate invoiceDate;

    @BeforeEach
    public void setUp() {
        // Create a salesperson
        List<String> salesEmails = new ArrayList<>(Arrays.asList("john.doe@vgb.com"));
        salesperson = new Person(UUID.randomUUID(), "John", "Doe", "555-1234", salesEmails);

        // Create a contact person for the company
        List<String> contactEmails = new ArrayList<>(Arrays.asList("contact@abccorp.com"));
        contactPerson = new Person(UUID.randomUUID(), "Jane", "Smith", "555-5678", contactEmails);

        // Create an address for the company
        companyAddress = new Address("123 Business St", "Metropolis", "NY", "10001");

        // Create a customer company
        customer = new Company(UUID.randomUUID(), "ABC Corporation", contactPerson, companyAddress);

        // Create a random UUID for the invoice
        invoiceUUID = UUID.randomUUID();

        // Set a specific date for testing
        invoiceDate = LocalDate.of(2025, 4, 1);

        // Initialize invoice
        invoice = new Invoice(invoiceUUID, customer, salesperson, invoiceDate);
    }

    /**
     * Tests the subtotal, tax total, and grand total values of an invoice in
     * the VGB system with 3 different types of items.
     */
    @Test
    public void testInvoice01() {
        // 1. Create test instances of 3 different types of invoice items

        // Equipment item
        UUID equipmentUUID = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentUUID, "Heavy Machinery", "HM-2000", 5000.00);
        InvoiceItem equipmentItem = new InvoiceItem(invoice, equipment);

        // Material item
        UUID materialUUID = UUID.randomUUID();
        Material material = new Material(materialUUID, "Construction Steel", "kg", 25.00);
        Material materialWithQuantity = new Material(material, 100); // 100 kg of steel
        InvoiceItem materialItem = new InvoiceItem(invoice, materialWithQuantity);

        // Contract item
        UUID contractUUID = UUID.randomUUID();
        Contract contract = new Contract(contractUUID, "Maintenance Agreement", 2500.00, customer);
        InvoiceItem contractItem = new InvoiceItem(invoice, contract);

        // 2. Create an instance of invoice and add these 3 items to it
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        invoiceItems.add(equipmentItem);
        invoiceItems.add(materialItem);
        invoiceItems.add(contractItem);

        Invoice invoiceWithItems = new Invoice(invoice, invoiceItems);

        // 3. Calculate the expected values 
        double expectedSubtotal = equipment.getSubTotal() + materialWithQuantity.getSubTotal() + contract.getSubTotal();
        double expectedTaxTotal = equipment.getTaxes() + materialWithQuantity.getTaxes() + contract.getTaxes();
        double expectedGrandTotal = equipment.getTotal() + materialWithQuantity.getTotal() + contract.getTotal();

        // Actual values from the invoice
        double actualSubtotal = invoiceWithItems.grandSubTotal(invoiceItems);
        double actualTaxTotal = invoiceWithItems.grandTaxTotal(invoiceItems);
        double actualGrandTotal = invoiceWithItems.grandTotal(invoiceItems);

        // Assertions for calculations
        assertEquals(expectedSubtotal, actualSubtotal, TOLERANCE);
        assertEquals(expectedTaxTotal, actualTaxTotal, TOLERANCE);
        assertEquals(expectedGrandTotal, actualGrandTotal, TOLERANCE);

        // Ensure that the string representation contains ALL necessary elements
        String invoiceString = invoiceWithItems.toString();
        
        String itemList = invoiceWithItems.itemList(invoiceItems);

        // Check invoice identification information
        assertTrue(invoiceString.contains(invoiceUUID.toString()));

        // Check customer details
        assertTrue(invoiceString.contains("ABC Corporation"));
        assertTrue(invoiceString.contains(customer.getUuid().toString()));

        // Check salesperson details
        assertTrue(invoiceString.contains("Doe") && invoiceString.contains("John"));
        assertTrue(invoiceString.contains(salesperson.getUuid().toString()));

        // Check for items information
        assertTrue( itemList.contains("Heavy Machinery") && itemList.contains("HM-2000"));
        assertTrue(itemList .contains("Construction Steel") && itemList.contains("kg"));
        assertTrue(itemList.contains("Maintenance Agreement"));

        // Check for financial values
        assertTrue(itemList.contains(String.valueOf(expectedSubtotal)));
        assertTrue(itemList.contains(String.valueOf(expectedTaxTotal)));
        assertTrue(itemList.contains(String.valueOf(expectedGrandTotal)));
    }

    /**
     * Tests the subtotal, tax total, and grand total values of an invoice in
     * the VGB system with the remaining 2 types of items.
     */
    @Test
    public void testInvoice02() {
        // 1. Create test instances of the other 2 types of invoice items

        // Rental item
        UUID equipmentUUID = UUID.randomUUID();
        Equipment baseEquipment = new Equipment(equipmentUUID, "Excavator", "EX-500", 80000.00);
        Rental rental = new Rental(baseEquipment, 40); // 40 hours rental
        InvoiceItem rentalItem = new InvoiceItem(invoice, rental);

        // Lease item
        UUID leaseEquipmentUUID = UUID.randomUUID();
        Equipment leaseEquipment = new Equipment(leaseEquipmentUUID, "Office Space", "OS-100", 200000.00);
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31); // 365 days lease
        Lease lease = new Lease(leaseEquipment, startDate, endDate);
        InvoiceItem leaseItem = new InvoiceItem(invoice, lease);

        // 2. Create an instance of invoice and add these 2 items to it
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        invoiceItems.add(rentalItem);
        invoiceItems.add(leaseItem);

        Invoice invoiceWithItems = new Invoice(invoice, invoiceItems);

        // 3. Calculate the expected values using the methods from our classes
        double expectedSubtotal = rental.getSubTotal() + lease.getSubTotal();
        double expectedTaxTotal = rental.getTaxes() + lease.getTaxes();
        double expectedGrandTotal = rental.getTotal() + lease.getTotal();

        // Get actual values from the invoice
        double actualSubtotal = invoiceWithItems.grandSubTotal(invoiceItems);
        double actualTaxTotal = invoiceWithItems.grandTaxTotal(invoiceItems);
        double actualGrandTotal = invoiceWithItems.grandTotal(invoiceItems);

        // Assertions for calculations
        assertEquals(expectedSubtotal, actualSubtotal, TOLERANCE);
        assertEquals(expectedTaxTotal, actualTaxTotal, TOLERANCE);
        assertEquals(expectedGrandTotal, actualGrandTotal, TOLERANCE);

        // Comprehensive string representation assertions
        String invoiceString = invoiceWithItems.toString();
        
        String itemList = invoiceWithItems.itemList(invoiceItems);

        // Check invoice identification information
        assertTrue(invoiceString.contains(invoiceUUID.toString()));

        // Check customer information
        assertTrue(invoiceString.contains("ABC Corporation"));
        assertTrue(invoiceString.contains(customer.getUuid().toString()));

        // Check salesperson information
        assertTrue(invoiceString.contains("Doe") && invoiceString.contains("John"));
        assertTrue(invoiceString.contains(salesperson.getUuid().toString()));

        // Check for items information
        assertTrue(itemList.contains("Excavator") && itemList.contains("EX-500"));
        assertTrue(itemList.contains("Office Space") && (itemList.contains("2025-01-01") || invoiceString.contains("2025-12-31")));

        // Check for financial values
        assertTrue(itemList.contains(String.valueOf(expectedSubtotal)));
        assertTrue(itemList.contains(String.valueOf( expectedTaxTotal)));
        assertTrue(itemList.contains(String.valueOf(expectedGrandTotal)));
    }
}
