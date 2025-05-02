package com.vgb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataConverterTest {

    private static final String PERSON_JSON = "output/persons.json";
    private static final String ITEM_JSON = "output/items.json";
    private static final String COMPANY_JSON = "output/companies.json";

    private static final String PERSON_XML = "output/persons.xml";
    private static final String ITEM_XML = "output/items.xml";
    private static final String COMPANY_XML = "output/companies.xml";

    @BeforeEach
    public void clean() {
        deleteTestFiles();
    }

    /**
     * Tests conversion and writing to JSON for Person data.
     */
    @Test
    public void testConvertToJsonPerson() throws IOException {
        Map<UUID, Person> persons = PersonLoader.loadPerson();
        String json = DataConverter.convertToJson(persons);
        
        // Write to file
        FileOutputWriter.writeToFile(json, PERSON_JSON);

        // Verify that the file is written and has content
        File jsonFile = new File(PERSON_JSON);
        assertTrue(jsonFile.exists(), "JSON file was not created.");
        assertTrue(jsonFile.length() > 0, "JSON file is empty.");

        // Verify that specific content exists
        String jsonContent = new String(Files.readAllBytes(Paths.get(PERSON_JSON)));
        assertTrue(jsonContent.contains("Sula"), "Expected first name not found in JSON.");
    }

    /**
     * Tests conversion and writing to JSON for Item data.
     */
    @Test
    public void testConvertToJsonItem() throws IOException {
        Map<UUID, Item> items = ItemLoader.loadItem();
        String json = DataConverter.convertToJson(items);
        
        // Write to file
        FileOutputWriter.writeToFile(json, ITEM_JSON);

        // Verify that the file is written and has content
        File jsonFile = new File(ITEM_JSON);
        assertTrue(jsonFile.exists(), "JSON file was not created.");
        assertTrue(jsonFile.length() > 0, "JSON file is empty.");

        // Verify that specific content exists
        String jsonContent = new String(Files.readAllBytes(Paths.get(ITEM_JSON)));
        assertTrue(jsonContent.contains("box"), "Expected item name not found in JSON.");
    }

    /**
     * Tests conversion and writing to JSON for Company data.
     */
    @Test
    public void testConvertToJsonCompany() throws IOException {
        Map<UUID, Company> companies = CompanyLoader.loadCompany();
        String json = DataConverter.convertToJson(companies);
        
        // Write to file
        FileOutputWriter.writeToFile(json, COMPANY_JSON);

        // Verify that the file is written and has content
        File jsonFile = new File(COMPANY_JSON);
        assertTrue(jsonFile.exists(), "JSON file was not created.");
        assertTrue(jsonFile.length() > 0, "JSON file is empty.");

        // Verify that specific content exists
        String jsonContent = new String(Files.readAllBytes(Paths.get(COMPANY_JSON)));
        assertTrue(jsonContent.contains("Podcat"), "Expected company name not found in JSON.");
    }

    /**
     * Tests conversion and writing to XML for Person data.
     */
    @Test
    public void testConvertToXmlPerson() throws IOException {
        Map<UUID, Person> persons = PersonLoader.loadPerson();
        String xml = DataConverter.convertToXml(persons);
        
        // Write to file
        FileOutputWriter.writeToFile(xml, PERSON_XML);

        // Verify that the file is written and has content
        File xmlFile = new File(PERSON_XML);
        assertTrue(xmlFile.exists(), "XML file was not created.");
        assertTrue(xmlFile.length() > 0, "XML file is empty.");

        // Verify that specific content exists
        String xmlContent = new String(Files.readAllBytes(Paths.get(PERSON_XML)));
        assertTrue(xmlContent.contains("Sula"), "Expected first name not found in XML.");
    }

    /**
     * Tests conversion and writing to XML for Item data.
     */
    @Test
    public void testConvertToXmlItem() throws IOException {
        Map<UUID, Item> items = ItemLoader.loadItem();
        String xml = DataConverter.convertToXml(items);
        
        // Write to file
        FileOutputWriter.writeToFile(xml, ITEM_XML);

        // Verify that the file is written and has content
        File xmlFile = new File(ITEM_XML);
        assertTrue(xmlFile.exists(), "XML file was not created.");
        assertTrue(xmlFile.length() > 0, "XML file is empty.");

        // Verify that specific content exists
        String xmlContent = new String(Files.readAllBytes(Paths.get(ITEM_XML)));
        assertTrue(xmlContent.contains("box"), "Expected item name not found in XML.");
    }

    /**
     * Tests conversion and writing to XML for Company data.
     */
    @Test
    public void testConvertToXmlCompany() throws IOException {
        Map<UUID, Company> companies = CompanyLoader.loadCompany();
        String xml = DataConverter.convertToXml(companies);
        
        // Write to file
        FileOutputWriter.writeToFile(xml, COMPANY_XML);

        // Verify that the file is written and has content
        File xmlFile = new File(COMPANY_XML);
        assertTrue(xmlFile.exists(), "XML file was not created.");
        assertTrue(xmlFile.length() > 0, "XML file is empty.");

        // Verify that specific content exists
        String xmlContent = new String(Files.readAllBytes(Paths.get(COMPANY_XML)));
        assertTrue(xmlContent.contains("Podcat"), "Expected company name not found in XML.");
    }

    private void deleteTestFiles() {
        deleteFile(PERSON_JSON);
        deleteFile(ITEM_JSON);
        deleteFile(COMPANY_JSON);
        deleteFile(PERSON_XML);
        deleteFile(ITEM_XML);
        deleteFile(COMPANY_XML);
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
