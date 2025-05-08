package com.vgb;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Provides comparators for sorting invoices and companies.
 */
public class InvoiceComparators {

	/**
	 * Comparator for sorting invoices by total amount (highest to lowest)
	 */
	public static class InvoiceByTotalComparator implements Comparator<Invoice> {
		private final Map<Invoice, List<InvoiceItem>> invoiceItems;

		public InvoiceByTotalComparator(Map<Invoice, List<InvoiceItem>> invoiceItems) {
			this.invoiceItems = invoiceItems;
		}

		@Override
		public int compare(Invoice inv1, Invoice inv2) {
			double total1 = inv1.grandTotal(invoiceItems.get(inv1));
			double total2 = inv2.grandTotal(invoiceItems.get(inv2));

			int result = Double.compare(total2, total1);

			if (result == 0) {
				return inv1.getInvoiceUUID().compareTo(inv2.getInvoiceUUID());
			}

			return result;
		}
	}

	/**
	 * Comparator for sorting invoices by customer name alphabetically.
	 */
	public static class InvoiceByCustomerNameComparator implements Comparator<Invoice> {
		@Override
		public int compare(Invoice inv1, Invoice inv2) {
			int result = inv1.getCustomer().getName().compareTo(inv2.getCustomer().getName());

			if (result == 0) {
				return inv1.getInvoiceUUID().compareTo(inv2.getInvoiceUUID());
			}

			return result;
		}
	}

	/**
	 * Comparator for sorting companies by total invoice amounts
	 * 
	 */
	public static class CompanyByTotalComparator implements Comparator<Company> {
		private final Map<Company, Double> companyTotals;

		public CompanyByTotalComparator(Map<Company, Double> companyTotals) {
			this.companyTotals = companyTotals;
		}

		@Override
		public int compare(Company comp1, Company comp2) {
			double total1 = companyTotals.getOrDefault(comp1, 0.0);
			double total2 = companyTotals.getOrDefault(comp2, 0.0);

			int result = Double.compare(total1, total2);

			if (result == 0) {
				return comp1.getUuid().toString().compareTo(comp2.getUuid().toString());
			}

			return result;
		}
	}
}