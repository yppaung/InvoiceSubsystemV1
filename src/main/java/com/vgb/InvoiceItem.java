package com.vgb;



/**
* Represents an item in an invoice.
*/
public class InvoiceItem {
	
	private Invoice invoice;
	private Item item;
	
	
	public InvoiceItem(Invoice invoice, Item item) {
		super();
		this.invoice = invoice;
		this.item = item;
	}


	public Invoice getInvoice() {
		return invoice;
	}


	public Item getItem() {
		return item;
	}
	




	
	

}
