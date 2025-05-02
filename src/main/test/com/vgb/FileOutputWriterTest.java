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

public class FileOutputWriterTest {

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

    @Test
    public void testWriteToFileJson() throws IOException {
        // Prepare data
        Map<UUID, Person> persons = PersonLoader.loadPerson();
        Map<UUID, Company> companies = CompanyLoader.loadCompany();
        Map<UUID, Item> items = ItemLoader.loadItem();

        // Write files using FileOutputWriter
        FileOutputWriter.writeToFile(DataConverter.convertToJson(persons), PERSON_JSON);
        FileOutputWriter.writeToFile(DataConverter.convertToJson(companies), COMPANY_JSON);
        FileOutputWriter.writeToFile(DataConverter.convertToJson(items), ITEM_JSON);

        // Check if the files are created and not empty
        checkFileExistsAndNotEmpty(PERSON_JSON);
        checkFileExistsAndNotEmpty(COMPANY_JSON);
        checkFileExistsAndNotEmpty(ITEM_JSON);

        // Check if expected content is in the files
        checkFileContent(PERSON_JSON, "Sula");
        checkFileContent(COMPANY_JSON, "Podcat");
        checkFileContent(ITEM_JSON, "box");
    }

    @Test
    public void testWriteToFileXml() throws IOException {
        // Prepare data
        Map<UUID, Person> persons = PersonLoader.loadPerson();
        Map<UUID, Company> companies = CompanyLoader.loadCompany();
        Map<UUID, Item> items = ItemLoader.loadItem();

        // Write files using FileOutputWriter
        FileOutputWriter.writeToFile(DataConverter.convertToXml(persons), PERSON_XML);
        FileOutputWriter.writeToFile(DataConverter.convertToXml(companies), COMPANY_XML);
        FileOutputWriter.writeToFile(DataConverter.convertToXml(items), ITEM_XML);

        // Check if the files are created and not empty
        checkFileExistsAndNotEmpty(PERSON_XML);
        checkFileExistsAndNotEmpty(COMPANY_XML);
        checkFileExistsAndNotEmpty(ITEM_XML);

        // Check if expected content is in the files
        checkFileContent(PERSON_XML, "Sula");
        checkFileContent(COMPANY_XML, "Podcat");
        checkFileContent(ITEM_XML, "box");
    }

    private void checkFileExistsAndNotEmpty(String filePath) {
        File file = new File(filePath);
        assertTrue(file.exists(), filePath + " does not exist.");
        assertTrue(file.length() > 0, filePath + " is empty.");
    }

    private void checkFileContent(String filePath, String expectedContent) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        assertTrue(content.contains(expectedContent), "Expected content not found in " + filePath);
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
