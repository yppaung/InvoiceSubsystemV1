package com.vgb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;
/**
 * Utility class for writing Converted data files and Reports to a text file
 */
public class FileOutputWriter {

	
    /**
     * Writes converted data files
     * 
     * @param content Converted objects
     * @param filePath
     * @throws IOException Throws if an error occurs while writing
     */
    public static void writeToFile(String content, String filePath) throws IOException {
        
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        
        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write(content);
        }
    }
       
    /*
     * 
     * Writes company totals to a text file
     */
    
	public static void writeReportsToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            
            
            writer.write("==========================================\n");
            writer.write("Company Summary\n");
            writer.write("==========================================\n\n");

            Map<Company, Double> totals = ReportUtils.calculateCompanyTotals();
            Map<Company, Integer> counts = ReportUtils.calculateCompanyInvoiceCount();
            for (Company company : totals.keySet()) {
                writer.write(String.format("%s \n%70s %s %8s $%.2f\n",
                        company.getName(), " ", counts.getOrDefault(company, 0), "", totals.get(company)));
            }

           
            writer.write("\n+----------------------------------------------------------------------------------------+\n");
            writer.write("| Summary Report - By Total                                                              |\n");
            writer.write("+----------------------------------------------------------------------------------------+\n");
            writer.write(String.format("%-40s %-30s %10s %12s %12s\n", "Invoice #", "Customer", "Num Items", "Tax", "Total"));

            Map<Invoice, double[]> invoiceSummary = ReportUtils.invoiceSummary();
            double grandTotal = 0, totalTax = 0;
            int totalItems = 0;

            for (Map.Entry<Invoice, double[]> entry : invoiceSummary.entrySet()) {
                Invoice invoice = entry.getKey();
                double[] values = entry.getValue();

                int itemCount = (int) values[0];
                double tax = values[1];
                double total = values[2];

                grandTotal += total;
                totalTax += tax;
                totalItems += itemCount;

                writer.write(String.format("%-40s %-30s %10d %12.2f %12.2f\n", 
                    invoice.getInvoiceUUID(), 
                    invoice.getCustomer().getName(), 
                    itemCount, 
                    tax, 
                    total));
            }

            writer.write("+----------------------------------------------------------------------------------------+\n");
            writer.write(String.format("%-72s %10d %12.2f %12.2f\n", " ", totalItems, totalTax, grandTotal));

            writer.write(InvoiceReport.printInvoice());
            
            writer.flush(); 

            System.out.println("Reports successfully written to " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing reports to file: " + e.getMessage());
        }
    }
}