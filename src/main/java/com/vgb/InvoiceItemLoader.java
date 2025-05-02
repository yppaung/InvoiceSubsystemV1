package com.vgb;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
/**
 * Utility class for loading Invoice Item from a flat file
 */
public class InvoiceItemLoader {
	
	public static final String FILE_PATH = "data/InvoiceItems.csv";
	
public static Map<UUID, List<InvoiceItem>> loadInvoiceItem(){
		
		Map<UUID, Invoice> invoices = InvoiceLoader.loadInvoice();
		Map<UUID, Item> items = ItemLoader.loadItem();
		
		Map<UUID, List<InvoiceItem>> invoiceItems = new HashMap<>();
		String line = null;
		try(Scanner s = new Scanner(new File(FILE_PATH))){
			s.nextLine();
			
			while (s.hasNext()) {
				
				line = s.nextLine();
				
				
				if(!line.trim().isEmpty()) {
					InvoiceItem invItem = null;
					String parts[] = line.split(",");
					UUID invoiceuuid = null;
					UUID itemUuid = null;
					
					String invoiceidstr = parts[0];
					String itemuuidstr = parts[1];
					char fields = parts[2].charAt(0);
					
					LocalDate startDate = null;
					LocalDate endDate = null;
					
					Invoice invoice = null;
					Item item = null;
					
					double hours = 0.0, 
							contractPrice = 0.0, 
							quantity = 0.0;
					
					itemUuid = UUID.fromString(itemuuidstr);
					
					invoiceuuid = UUID.fromString(invoiceidstr);
					
					if(parts.length == 5) {
						startDate = LocalDate.parse(parts[3]);
						endDate = LocalDate.parse(parts[4]);
						
						if(fields == 'L') {
							item = items.get(itemUuid);

							invoice = invoices.get(invoiceuuid);
							Equipment e = (Equipment) item;
							Lease l = new Lease(e, startDate, endDate);
							
							invItem = new InvoiceItem(invoice, l);							
							
						}
						
					} else if(parts.length == 4) {
						if(fields == 'M') {
							
							quantity = Double.parseDouble(parts[3]);
													
							item = items.get(itemUuid);
							invoice = invoices.get(invoiceuuid);
							
							Material m = new Material((Material) item, quantity);	
							
							invItem = new InvoiceItem(invoice, m);
							
						}else if(fields == 'C') {
							
							contractPrice = Double.parseDouble(parts[3]);
							item = items.get(itemUuid);
							invoice = invoices.get(invoiceuuid);
							
							Contract c = new Contract((Contract) item, contractPrice);
							
							invItem = new InvoiceItem(invoice, c);
							
						}else if(fields == 'R') {
							hours = Double.parseDouble(parts[3]);
							
							item = items.get(itemUuid);
							invoice = invoices.get(invoiceuuid);
							
							Rental r = new Rental((Equipment) item, hours);
							invItem = new InvoiceItem(invoice, r);
						}
						
					} else if(parts.length == 3) {
						if(fields == 'P') {
							item = (Equipment)items.get(itemUuid);
							invoice = invoices.get(invoiceuuid);
							
							invItem = new InvoiceItem(invoice, item);
						}
					}
					if (!invoiceItems.containsKey(invoiceuuid)) {
					    invoiceItems.put(invoiceuuid, new ArrayList<>());
					}

					invoiceItems.get(invoiceuuid).add(invItem);
				}
				
			}
			s.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return invoiceItems;
	}

}
