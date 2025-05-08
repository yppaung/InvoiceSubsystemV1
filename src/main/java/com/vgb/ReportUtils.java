package com.vgb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vgb.database.ConnectionFactory;
import com.vgb.database.DataLoader;
import com.vgb.factory.LoadCompany;
import com.vgb.factory.LoadInvoiceItem;

/**
 * Utility class to populate and calculate totals of the invoices
 */
public class ReportUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportUtils.class);

	/**
	 * Distributes Invoices and invoiceItems to another map comparing UUID Strings
	 * 
	 * @return matched invoiceItems per Invoice
	 */
	public static Map<Invoice, List<InvoiceItem>> populateInvoice() {
		DataLoader dl = new DataLoader();

		Map<Invoice, List<InvoiceItem>> invoiceItemMap = null;
		try (Connection conn = ConnectionFactory.getConnection()) {
			invoiceItemMap = dl.groupData("""
					SELECT invoiceItemId,
					    i.uuid,
					    i.invoiceId,
					    invItem.itemId,
					    typeEquipment,
					    price,
					    startDate,
					    endDate,
					    quantity,
					    numberOfHours
					    FROM Invoice i LEFT JOIN InvoiceItem invItem
					    ON i.invoiceId = invItem.invoiceId
					""", new LoadInvoiceItem(), conn);
		} catch (SQLException e) {
			LOGGER.error("Populating invoice connection error", e);
		}

		return invoiceItemMap;
	}

	/**
	 * Gets invoices sorted by total (highest to lowest).
	 * 
	 * @return A sorted list of invoices by total amount.
	 */
	public static SortedListBST<Invoice> getInvoicesByTotal() {
		Map<Invoice, List<InvoiceItem>> invoiceItems = populateInvoice();

		SortedListBST<Invoice> sortedInvoices = new SortedListBST<>(
				new InvoiceComparators.InvoiceByTotalComparator(invoiceItems));

		for (Invoice invoice : invoiceItems.keySet()) {
			sortedInvoices.add(invoice);
		}

		return sortedInvoices;
	}

	/**
	 * Gets invoices sorted by customer name.
	 * 
	 * @return A sorted list of invoices by customer name.
	 */
	public static SortedListBST<Invoice> getInvoicesByCustomerName() {
		Map<Invoice, List<InvoiceItem>> invoiceItems = populateInvoice();

		SortedListBST<Invoice> sortedInvoices = new SortedListBST<>(
				new InvoiceComparators.InvoiceByCustomerNameComparator());

		for (Invoice invoice : invoiceItems.keySet()) {
			sortedInvoices.add(invoice);
		}

		return sortedInvoices;
	}

	/**
	 * Calculates the total amount per company.
	 * 
	 * @return Map of Company to total invoice amount.
	 */
	public static Map<Company, Double> calculateCompanyTotals() {
		Map<Invoice, List<InvoiceItem>> invoiceItems = populateInvoice();

		DataLoader dl = new DataLoader();
		Map<UUID, Company> companies = null;

		try (Connection conn = ConnectionFactory.getConnection()) {
			companies = dl.loadData("""
					SELECT uuid, companyName, personId, addressId
					FROM Company
					""", new LoadCompany(), conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection", e);
		}

		Map<Company, Double> companyInvoiceTotals = new HashMap<>();

		for (Company company : companies.values()) {
			companyInvoiceTotals.put(company, 0.0);
		}

		for (Map.Entry<Invoice, List<InvoiceItem>> entry : invoiceItems.entrySet()) {
			Invoice invoice = entry.getKey();
			List<InvoiceItem> items = entry.getValue();

			Company company = companies.get(invoice.getCustomer().getUuid());

			if (company == null)
				continue;

			double invoiceTotal = invoice.grandTotal(items);
			companyInvoiceTotals.put(company, companyInvoiceTotals.getOrDefault(company, 0.0) + invoiceTotal);
		}

		return companyInvoiceTotals;
	}

	/**
	 * Gets companies sorted by total invoice amount.
	 * 
	 * @return A sorted list of companies by total invoice amount.
	 */
	public static SortedListBST<Company> getCompaniesByTotal() {
		Map<Company, Double> companyTotals = calculateCompanyTotals();

		SortedListBST<Company> sortedCompanies = new SortedListBST<>(
				new InvoiceComparators.CompanyByTotalComparator(companyTotals));

		for (Company company : companyTotals.keySet()) {
			sortedCompanies.add(company);
		}

		return sortedCompanies;
	}

	/**
	 * Calculates the count of invoices per company.
	 * 
	 * @return Map of Company to the number of invoices.
	 */
	public static Map<Company, Integer> calculateCompanyInvoiceCount() {
		Map<Invoice, List<InvoiceItem>> invoiceItems = populateInvoice();
		DataLoader dl = new DataLoader();

		Map<UUID, Company> companies = null;
		try (Connection conn = ConnectionFactory.getConnection()) {
			companies = dl.loadData("""
					SELECT uuid, companyName, personId, addressId
					FROM Company
					    """, new LoadCompany(), conn);
		} catch (SQLException e) {
			LOGGER.error("calculating company bad connection");
		}

		Map<Company, Integer> companyInvoiceCounts = new HashMap<>();

		for (Company company : companies.values()) {
			companyInvoiceCounts.put(company, 0);
		}

		for (Map.Entry<Invoice, List<InvoiceItem>> entry : invoiceItems.entrySet()) {
			Invoice invoice = entry.getKey();

			Company company = companies.get(invoice.getCustomer().getUuid());
			if (company == null)
				continue;

			companyInvoiceCounts.put(company, companyInvoiceCounts.getOrDefault(company, 0) + 1);
		}
		return companyInvoiceCounts;
	}

	/**
	 * Generates a company summary report of company totals and counts for each
	 * company.
	 * 
	 * @return Formatted string with company summary information
	 */
	public static String companySummary() {
		StringBuilder report = new StringBuilder();

		Map<Company, Double> companyTotals = calculateCompanyTotals();
		Map<Company, Integer> count = calculateCompanyInvoiceCount();

		SortedListBST<Company> sortedCompanies = getCompaniesByTotal();

		for (Company company : sortedCompanies) {
			double totalAmount = companyTotals.get(company);
			int invoiceCount = count.getOrDefault(company, 0);

			report.append(
					String.format("%s \n %70s %s %8s $%s\n", company.getName(), " ", invoiceCount, "", totalAmount));
		}

		double companyTotal = Math.round(companyTotals.values().stream().mapToDouble(d -> d).sum());
		int invoiceCountTotal = count.values().stream().mapToInt(d -> d).sum();
		report.append(String.format("%95s" + "\n%70s %s %8s $%s\n", "------------------------", " ", invoiceCountTotal,
				" ", companyTotal));

		return report.toString();
	}

}
