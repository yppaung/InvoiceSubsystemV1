package com.vgb;

import java.util.List;
import java.util.Map;

/**
 * Contains Printing functions for the three reports
 */
public class InvoiceReport {
     /**
     * Prints Invoice Summary report ordered by total (highest to lowest)
     */
    public static String printInvoiceSummaryByTotal() {
        StringBuilder report = new StringBuilder();
        
        report.append("+-------------------------------------------------------------------------+\n");
        report.append("| Invoices by Total                                                       |\n");
        report.append("+-------------------------------------------------------------------------+\n");
        report.append(String.format("%-40s %-30s %s\n", "Invoice", "Customer", "Total"));
        
        Map<Invoice, List<InvoiceItem>> invoiceItems = ReportUtils.populateInvoice();
        
        SortedListBST<Invoice> sortedInvoices = ReportUtils.getInvoicesByTotal();
        
        for (Invoice invoice : sortedInvoices) {
            List<InvoiceItem> items = invoiceItems.get(invoice);
            double total = invoice.grandTotal(items);
            
            report.append(String.format("%-40s %-30s $%10.2f\n", 
                    invoice.getInvoiceUUID(), 
                    invoice.getCustomer().getName(), 
                    total));
        }
        
        return report.toString();
    }
    
    /**
     * Prints Invoice Summary report ordered by customer name
     */
    public static String printInvoiceSummaryByCustomer() {
        StringBuilder report = new StringBuilder();
        
        report.append("+-------------------------------------------------------------------------+\n");
        report.append("| Invoices by Customer                                                    |\n");
        report.append("+-------------------------------------------------------------------------+\n");
        report.append(String.format("%-40s %-30s %s\n", "Invoice", "Customer", "Total"));
        
        Map<Invoice, List<InvoiceItem>> invoiceItems = ReportUtils.populateInvoice();
        
        SortedListBST<Invoice> sortedInvoices = ReportUtils.getInvoicesByCustomerName();
        
        for (Invoice invoice : sortedInvoices) {
            List<InvoiceItem> items = invoiceItems.get(invoice);
            double total = invoice.grandTotal(items);
            
            report.append(String.format("%-40s %-30s $%10.2f\n", 
                    invoice.getInvoiceUUID(), 
                    invoice.getCustomer().getName(), 
                    total));
        }
        
        return report.toString();
    }
    
    /**
     * Prints company summary report ordered by total invoice amount
     */
    public static String printCompanySummary() {
        StringBuilder report = new StringBuilder();
        
        report.append("+-------------------------------------------------------------------------+\n");
        report.append("| Customer Invoice Totals                                                 |\n");
        report.append("+-------------------------------------------------------------------------+\n");
        report.append(String.format("%-40s %-20s %s\n", "Customer", "Number of Invoices", "Total"));
        
        Map<Company, Double> companyTotals = ReportUtils.calculateCompanyTotals();
        Map<Company, Integer> companyInvoiceCounts = ReportUtils.calculateCompanyInvoiceCount();
        
        SortedListBST<Company> sortedCompanies = ReportUtils.getCompaniesByTotal();
        
        for (Company company : sortedCompanies) {
            double totalAmount = companyTotals.getOrDefault(company, 0.0);
            int invoiceCount = companyInvoiceCounts.getOrDefault(company, 0);
            
            report.append(String.format("%-40s %-20d $%10.2f\n", 
                    company.getName(), 
                    invoiceCount, 
                    totalAmount));
        }
        
        return report.toString();
    }
    
    
    /**
     * Prints all three required summary reports 
     */
    public static String printAllSummaryReports() {
        StringBuilder report = new StringBuilder();
        
        report.append(printInvoiceSummaryByTotal());
        report.append("\n\n");
        report.append(printInvoiceSummaryByCustomer());
        report.append("\n\n");
        report.append(printCompanySummary());
        
        return report.toString();
    }
    
    public static String printInvoice() {
	    
    	StringBuilder report = new StringBuilder();
    			
    			Map<Invoice,List<InvoiceItem>> invoicesReport = ReportUtils.populateInvoice();
    			
    		    for (Map.Entry<Invoice, List<InvoiceItem>> pair : invoicesReport.entrySet()) {
    		    	List<InvoiceItem> items = pair.getValue();
    		    	
    				report.append(pair.getKey().toString());
    				report.append(String.format("Items(%s)", pair.getValue().size()));
    				report.append("\n----------------------------------------------------------------------\n");
    				
    				report.append(String.format("\n %70s %s %s %s \n", " ", "SUBTOTAL", "TAX", "TOTAL"));
    				
    				report.append(pair.getKey().itemList(items));
    				report.append("\n+=====================================================================+ \n");
    			}
    		return report.toString();

    	}
    
    public static void main(String[] args) {
        System.out.println(InvoiceReport.printAllSummaryReports());
        System.out.println(InvoiceReport.printInvoice());
     
			
		
 }
    
    

}	
