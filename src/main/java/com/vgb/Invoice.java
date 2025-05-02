package com.vgb;

import java.time.LocalDate;
import java.util.List;

import java.util.UUID;
/**
 * Represents an Invoice in the System
 */
public class Invoice {

	private UUID invoiceUUID;
    private Company customer;
    private Person salesperson;
    private LocalDate invoiceDate;
    private List<InvoiceItem> invoiceItems;
    
    /**
     * Constructs an Invoice based on the given attributes
     * @param invoiceUUID Invoice
     * @param customer Company the transaction was done to
     * @param salesperson Person who carried out the sale
     * @param invoiceDate date of the invoice
     */
	public Invoice(UUID invoiceUUID, Company customer, Person salesperson, LocalDate invoiceDate) {
		super();
		this.invoiceUUID = invoiceUUID;
		this.customer = customer;
		this.salesperson = salesperson;
		this.invoiceDate = invoiceDate;
		
		
	}
	
	/**
	 * Constructs a mapped Invoice with the list of items belonging to that invoice
	 * @param invoice
	 * @param invoiceItem
	 */
	public Invoice(Invoice invoice, List<InvoiceItem> invoiceItem) {
		this(invoice.getInvoiceUUID(), invoice.getCustomer(), invoice.getSalesperson(), invoice.getInvoiceDate());
		this.invoiceItems = invoiceItem;
	}
	
	
	public UUID getInvoiceUUID() {
		return invoiceUUID;
	}
	public Company getCustomer() {
		return customer;
	}
	public Person getSalesperson() {
		return salesperson;
	}
	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}


	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	/**
	 * Returns the sum of Totals in an invoice
	 */
	public double grandTotal(List<InvoiceItem> invoiceItem){
		
		double total = 0.0;
		for(InvoiceItem i : invoiceItem) {
			if(i != null) {
			total += i.getItem().getTotal();
			}
		}
		
		return total;
		
	}
	
	/**
	 * Returns the sum of SubTotals in an invoice
	 */
public double grandSubTotal(List<InvoiceItem> invoiceItem){
		
		double total = 0.0;
		for(InvoiceItem i : invoiceItem) {
			if (i != null) {
			total += i.getItem().getSubTotal();
			}
		}
		
		return total;
		
	}
/**
 * Returns the sum of taxes in an invoice
 */
public double grandTaxTotal(List<InvoiceItem> invoiceItem){
	
	double total = 0.0;
	for(InvoiceItem i : invoiceItem) {
		if(i != null) {
		total += i.getItem().getTaxes();
		}
	}
	
	return total;
	
}

public String itemList(List<InvoiceItem> invoiceItem) {
	StringBuilder report = new StringBuilder();

for(InvoiceItem it : invoiceItem) {
	if(it != null) {
		report.append( it.getItem().toString());
	}

}

report.append(String.format("\nInvoice Total %57s -------------------------"
		+ " \n %70s $%s $%s $%s\n", " ",
" ",grandSubTotal(invoiceItem),
grandTaxTotal(invoiceItem),
grandTotal(invoiceItem)));

return report.toString();
					
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Invoice invoice = (Invoice) o;
    return invoiceUUID.equals(invoice.invoiceUUID);
}

@Override
public int hashCode() {
    return invoiceUUID.hashCode();
}

	
	@Override
	public String toString() {
		String contactStr = (this.getSalesperson() != null) ? this.getSalesperson().toString() : "N/A";
		
		return String.format("Invoice ID: %s \n \n"
				+ "Customer: %s \n"
				+ "Salesperson: %s \n", this.getInvoiceUUID(), this.getCustomer().toString(),
				contactStr);
	}
    
	
    
}
