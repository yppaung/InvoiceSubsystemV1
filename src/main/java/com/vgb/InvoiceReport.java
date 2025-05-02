package com.vgb;


import java.util.List;
import java.util.Map;

/*
 * Contains Printing functions for the three reports
 * 
 */
public class InvoiceReport {  
    


    /**
     * 
     * Prints a detailed report of Invoice total
     * Invoice Sub totals, Invoice Tax Totals, Invoice Grand Totals
     */
    
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
	/**
	 * Prints company summary report
	 */
	public static String printCompanySummary() {
	    
		StringBuilder report = new StringBuilder();
		
		report.append("==========================================\n"
				+ "Company Summary\n"
				+ "==========================================\n");
		
		report.append(String.format("%70s %s %s %s\n", " ", "#Invoices", "", "Grand Total"));
	    
		report.append(ReportUtils.companySummary());
		
		return report.toString();
		
	}
	
	/**
	 * Prints Invoice Summary report
	 */
	
	public static String printInvoiceSummary() {
		StringBuilder report = new StringBuilder();
	    Map<Invoice, double[]> summary = ReportUtils.invoiceSummary();
	    
	    report.append("+----------------------------------------------------------------------------------------+ \n");
	    report.append("| Summary Report - By Total                                                              | \n");
	    report.append("+----------------------------------------------------------------------------------------+ \n");
	    report.append(String.format("%-40s %-30s %10s %12s %12s \n", "Invoice #", "Customer", "Num Items", "Tax", "Total"));

	    double grandTotal = 0;
	    double totalTax = 0;
	    int totalItems = 0;

	    for (Map.Entry<Invoice, double[]> entry : summary.entrySet()) {
	        Invoice invoice = entry.getKey();
	        double[] values = entry.getValue();

	        int itemCount = (int) values[0];
	        double tax = values[1];
	        double total = values[2];

	        grandTotal += total;
	        totalTax += tax;
	        totalItems += itemCount;

	        report.append(String.format("%-40s %-30s %10d %12.2f %12.2f \n", 
	            invoice.getInvoiceUUID(), 
	            invoice.getCustomer().getName(), 
	            itemCount, 
	            tax, 
	            total));
	    }

	    report.append("+----------------------------------------------------------------------------------------+ \n");
	    report.append(String.format("%-72s %10d %12.2f %12.2f", " ", totalItems, totalTax, grandTotal));
	    
	    return report.toString();
	}

	
	public static void main(String[] args) {
		
		System.out.println(InvoiceReport.printInvoice());
		System.out.println(InvoiceReport.printInvoiceSummary());
		System.out.println(InvoiceReport.printCompanySummary());	
		

	
	}
	
	
}	

