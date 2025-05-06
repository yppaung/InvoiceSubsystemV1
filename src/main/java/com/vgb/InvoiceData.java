package com.vgb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vgb.database.ConnectionFactory;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class InvoiceData {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceData.class);;

	/**
	 * Removes all records from all tables in the database.
	 */
	public static void clearDatabase() {
		try (Connection conn = ConnectionFactory.getConnection()) {

			try (Statement disableFK = conn.createStatement()) {
				disableFK.execute("SET FOREIGN_KEY_CHECKS = 0");
			}

			String currentDb;
			try (Statement dbStmt = conn.createStatement(); ResultSet dbRs = dbStmt.executeQuery("SELECT DATABASE()")) {
				dbRs.next();
				currentDb = dbRs.getString(1);
			}

			try (Statement tableStmt = conn.createStatement();
					ResultSet rs = tableStmt
							.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '"
									+ currentDb + "'")) {
				while (rs.next()) {
					String tableName = rs.getString("table_name");
					try (Statement delStmt = conn.createStatement()) {
						delStmt.executeUpdate("DELETE FROM `" + tableName + "`");
					}
				}
			}

			try (Statement enableFK = conn.createStatement()) {
				enableFK.execute("SET FOREIGN_KEY_CHECKS = 1");
			}

			LOGGER.info("Database cleared...");

		} catch (SQLException e) {
			LOGGER.error("Failed to clear database", e);
		}
	}

	/**
	 * Method to add a person record to the database with the provided data.
	 *
	 * @param personUuid
	 * @param firstName
	 * @param lastName
	 * @param phone
	 */
	public static void addPerson(UUID personUuid, String firstName, String lastName, String phone) {

		try (Connection conn = ConnectionFactory.getConnection()) {
			if (!checkPerson(personUuid, conn)) {

				try (PreparedStatement stmt = conn.prepareStatement("""
						INSERT INTO Person (uuid, firstName, lastName, phoneNumber)
						VALUES (?, ?, ?, ?)
						""")) {
					stmt.setString(1, personUuid.toString());
					stmt.setString(2, firstName);
					stmt.setString(3, lastName);
					stmt.setString(4, phone);
					stmt.executeUpdate();

					stmt.close();
				} catch (SQLException e) {
					LOGGER.info("Error occurred adding person to database", e);
				}
			} else {
				LOGGER.info("Person already exists");
			}

			ConnectionFactory.closeConnection(conn);

		} catch (SQLException e) {
			LOGGER.info("Error occured creating connection ", e);
		}

	}

	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personUuid</code>
	 *
	 * @param personUuid
	 * @param email
	 */
	public static void addEmail(UUID personUuid, String email) {

		try (Connection conn = ConnectionFactory.getConnection()) {

			if (checkPerson(personUuid, conn)) {
				try (PreparedStatement stmt = conn.prepareStatement("""
						          INSERT INTO Email(address,personId) VALUES (?,
						(select personId from Person where uuid = ? ));
						          """)) {
					stmt.setString(1, email);
					stmt.setString(2, personUuid.toString());
					stmt.executeUpdate();

					stmt.close();
				} catch (SQLException e) {
					LOGGER.error("Error occurred adding email to database", e);
				}
			} else {
				LOGGER.warn("Person does not exist under UUID: {} ", personUuid.toString());
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds a company record to the database with the primary contact person
	 * identified by the given code.
	 *
	 * @param companyUuid
	 * @param name
	 * @param contactUuid
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */
	public static void addCompany(UUID companyUuid, UUID contactUuid, String name, String street, String city,
			String state, String zip) {

		try (Connection conn = ConnectionFactory.getConnection()) {

			
			if (!checkCompany(companyUuid, conn)) {
				if (checkPerson(contactUuid, conn)) {
					if (!checkAddress(street, city, state, zip, conn)) {

						if (!checkState(state, conn)) {
							try (PreparedStatement ps = conn.prepareStatement("""
									INSERT INTO State(stateCode) VALUES (?);
									""")) {

								ps.setString(1, state);
								ps.executeUpdate();

								ps.close();

							} catch (SQLException e) {
								LOGGER.error("Error adding State", e);
							}
						}

						if (!checkZip(zip, conn)) {
							try (PreparedStatement ps = conn.prepareStatement("""
									INSERT INTO ZipCode (zip) VALUES (?);
									""")) {

								ps.setString(1, zip);
								ps.executeUpdate();
								ps.close();

							} catch (SQLException e) {
								LOGGER.error("Error adding Zip code", e);
							}
						}

						try (PreparedStatement stmt = conn.prepareStatement("""

								INSERT INTO Address (street, city, stateId, zipId) VALUES
								(?,?,
								(SELECT stateId FROM State WHERE stateCode = ? LIMIT 1),
								(SELECT zipId FROM ZipCode WHERE zip = ? LIMIT 1) );
								""")) {

							stmt.setString(1, street);
							stmt.setString(2, city);
							stmt.setString(3, state);
							stmt.setString(4, zip);
							stmt.executeUpdate();
							stmt.close();
						} catch (SQLException e) {
							LOGGER.error("Error occure adding address for company", e);
						}
					}
					try (PreparedStatement stmt = conn.prepareStatement("""
							INSERT INTO Company (uuid,companyName,addressId,personId) Values (?, ?,
							(select addressId from Address where street = ? LIMIT 1),
								(select personId from Person where uuid = ? ));
							""")) {

						stmt.setString(1, companyUuid.toString());
						stmt.setString(2, name);
						stmt.setString(3, street);
						stmt.setString(4, contactUuid.toString());
						stmt.executeUpdate();

						stmt.close();
					} catch (SQLException e) {
						LOGGER.error("Error occurred adding company to database", e);
					}
				} else {
					LOGGER.warn("Contact does not exist under UUID: {} ", contactUuid.toString());
				}
			} else {
				LOGGER.warn("Company already exist under UUID: {}", companyUuid.toString());
			}
			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection", e);
		}

	}

	/**
	 * Adds an equipment record to the database of the given values.
	 *
	 * @param equipmentUuid
	 * @param name
	 * @param modelNumber
	 * @param retailPrice
	 */
	public static void addEquipment(UUID equipmentUuid, String name, String modelNumber, double retailPrice) {

		try (Connection conn = ConnectionFactory.getConnection()) {
			if (!checkItem(equipmentUuid, conn)) {

				try (PreparedStatement stmt = conn.prepareStatement("""
						INSERT INTO Item(uuid, itemName,itemType, itemPrice, model)
						VALUES (?, ?, ?, ?, ?)
						""")) {

					stmt.setString(1, equipmentUuid.toString());
					stmt.setString(2, name);
					stmt.setString(3, "E");
					stmt.setDouble(4, retailPrice);
					stmt.setString(5, modelNumber);
					stmt.executeUpdate();

					stmt.close();

				} catch (SQLException e) {
					LOGGER.error("addEquipment failed", e);
				}
			} else {
				LOGGER.info("Equipment Already exists");
			}
			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an material record to the database of the given values.
	 *
	 * @param materialUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
	public static void addMaterial(UUID materialUuid, String name, String unit, double pricePerUnit) {
		try (Connection conn = ConnectionFactory.getConnection()) {
			if (!checkItem(materialUuid, conn)) {

				try (PreparedStatement stmt = conn.prepareStatement("""
						INSERT INTO Item(uuid, itemName,itemPrice, itemType, unit, unitPrice)
						VALUES (?, ?, ?, ?, ?, ?)
						""")) {

					stmt.setString(1, materialUuid.toString());
					stmt.setString(2, name);
					stmt.setDouble(3, pricePerUnit);
					stmt.setString(4, "M");
					stmt.setString(5, unit);
					stmt.setDouble(6, pricePerUnit);
					stmt.executeUpdate();

					stmt.close();
				} catch (SQLException e) {
					LOGGER.error("addMaterial failed", e);
				}
			} else {
				LOGGER.info("Material Already exists");
			}
			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an contract record to the database of the given values.
	 *
	 * @param contractUuid
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
	public static void addContract(UUID contractUuid, String name, UUID servicerUuid) {
		try (Connection conn = ConnectionFactory.getConnection()) {

			if (checkItem(contractUuid, conn)) {
				LOGGER.info("Contract Already exists");
				return;
			}

			if (!checkCompany(servicerUuid, conn)) {
				LOGGER.info("Company Does not exists {}", servicerUuid);
			}

			String insertContract = """
					INSERT INTO Item(uuid, itemName,itemPrice, itemType, customerId)
					VALUES (?, ?, ?, ?, (SELECT companyId FROM Company WHERE uuid = ?))
					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertContract)) {

				stmt.setString(1, contractUuid.toString());
				stmt.setString(2, name);
				stmt.setDouble(3, 0);
				stmt.setString(4, "C");
				stmt.setString(5, servicerUuid.toString());
				stmt.executeUpdate();

				stmt.close();
			} catch (SQLException e) {
				LOGGER.error("addContract failed", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an Invoice record to the database with the given data.
	 *
	 * @param invoiceUuid
	 * @param customerUuid
	 * @param salesPersonUuid
	 * @param date
	 */
	public static void addInvoice(UUID invoiceUuid, UUID customerUuid, UUID salesPersonUuid, LocalDate date) {

		try (Connection conn = ConnectionFactory.getConnection()) {

			if (checkInvoice(invoiceUuid, conn)) {
				LOGGER.info("Invoice with UUID {} already exists", invoiceUuid);
				return;
			}

			if (!checkCompany(customerUuid, conn) || !checkPerson(salesPersonUuid, conn)) {
				LOGGER.info("At least one of the given UUIDs does not exist: \n Customer: {} | SalesPerson: {}",
						customerUuid, salesPersonUuid);
				return;
			}

			String insertInvoice = """
					    INSERT INTO Invoice (uuid, companyId, salesPersonId, invoiceDate)
					    VALUES (
					        ?,
					        (SELECT companyId FROM Company WHERE uuid = ?),
					        (SELECT personId FROM Person WHERE uuid = ?),
					        ?
					    )
					""";

			try (PreparedStatement ps = conn.prepareStatement(insertInvoice)) {

				ps.setString(1, invoiceUuid.toString());
				ps.setString(2, customerUuid.toString());
				ps.setString(3, salesPersonUuid.toString());
				ps.setString(4, date.toString());
				ps.executeUpdate();

				ps.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to add Invoice to database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an Equipment purchase record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 */
	public static void addEquipmentPurchaseToInvoice(UUID invoiceUuid, UUID itemUuid) {

		try (Connection conn = ConnectionFactory.getConnection()) {

			if (!checkInvoice(invoiceUuid, conn) || !checkItem(itemUuid, conn)) {
				LOGGER.info(
						"At least one of the UUIDs entered does not exist \n" + " Invoice UUID : {} \nItem UUID: {}",
						invoiceUuid, itemUuid);
				return;
			}

			String insertPurchase = """
					INSERT INTO InvoiceItem (uuid, invoiceId,typeEquipment,itemId)
					VALUES (?,
					(SELECT invoiceId FROM Invoice WHERE uuid = ?), ?,
					(SELECT itemId FROM Item WHERE uuid = ?))
					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertPurchase)) {

				stmt.setString(1, UUID.randomUUID().toString());
				stmt.setString(2, invoiceUuid.toString());
				stmt.setString(3, "P");
				stmt.setString(4, itemUuid.toString());
				stmt.executeUpdate();

				stmt.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to insert Purchase Invoice item to Database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an Equipment lease record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param start
	 * @param end
	 */
	public static void addEquipmentLeaseToInvoice(UUID invoiceUuid, UUID itemUuid, LocalDate start, LocalDate end) {
		try (Connection conn = ConnectionFactory.getConnection()) {

			if (!checkInvoice(invoiceUuid, conn) || !checkItem(itemUuid, conn)) {
				LOGGER.warn(
						"At least one of the UUIDs entered does not exist \n" + " Invoice UUID : {} \nItem UUID: {}",
						invoiceUuid, itemUuid);
				return;
			}

			if (start == null || end == null) {
				LOGGER.warn("Lease dates cannor be null");
				return;
			}

			String insertLease = """
						INSERT INTO InvoiceItem (uuid, invoiceId, typeEquipment, itemId, startDate, endDate)
					VALUES (?,
					    (SELECT invoiceId FROM Invoice WHERE uuid = ?),
					    ?,
					    (SELECT itemId FROM Item WHERE uuid = ?),
					    ?, ?)
					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertLease)) {

				stmt.setString(1, UUID.randomUUID().toString());
				stmt.setString(2, invoiceUuid.toString());
				stmt.setString(3, "L");
				stmt.setString(4, itemUuid.toString());
				stmt.setString(5, start.toString());
				stmt.setString(6, end.toString());
				stmt.executeUpdate();

				stmt.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to insert Lease Invoice item to Database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds an Equipment rental record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfHours
	 */
	public static void addEquipmentRentalToInvoice(UUID invoiceUuid, UUID itemUuid, double numberOfHours) {
		try (Connection conn = ConnectionFactory.getConnection()) {

			if (!checkInvoice(invoiceUuid, conn) || !checkItem(itemUuid, conn)) {
				LOGGER.warn(
						"At least one of the UUIDs entered does not exist \n" + " Invoice UUID : {} \nItem UUID: {}",
						invoiceUuid, itemUuid);
				return;
			}

			if (numberOfHours == 0) {
				LOGGER.warn("Number of hours for rental cannot be 0");
				return;
			}

			String insertRental = """
					INSERT INTO InvoiceItem (uuid, invoiceId,typeEquipment,itemId, numberOfHours)
					VALUES (?,
					(SELECT invoiceId FROM Invoice WHERE uuid = ?), ?,
					(SELECT itemId FROM Item WHERE uuid = ?),?)
					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertRental)) {

				stmt.setString(1, UUID.randomUUID().toString());
				stmt.setString(2, invoiceUuid.toString());
				stmt.setString(3, "R");
				stmt.setString(4, itemUuid.toString());
				stmt.setDouble(5, numberOfHours);
				stmt.executeUpdate();

				stmt.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to insert Rental Invoice item to Database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds a material record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param numberOfUnits
	 */
	public static void addMaterialToInvoice(UUID invoiceUuid, UUID itemUuid, int numberOfUnits) {
		try (Connection conn = ConnectionFactory.getConnection()) {

			if (!checkInvoice(invoiceUuid, conn) || !checkItem(itemUuid, conn)) {
				LOGGER.info("At least one of the UUIDs entered does not exist \n Invoice UUID : {} \nItem UUID: {}",
						invoiceUuid, itemUuid);
				return;
			}

			if (numberOfUnits == 0) {
				LOGGER.warn("Quantity cannot be 0");
			}

			String insertMaterial = """
					INSERT INTO InvoiceItem (uuid, invoiceId,typeEquipment,itemId, quantity)
					VALUES (?,
					(SELECT invoiceId FROM Invoice WHERE uuid = ?), ?,
					(SELECT itemId FROM Item WHERE uuid = ?), ?)

					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertMaterial)) {

				stmt.setString(1, UUID.randomUUID().toString());
				stmt.setString(2, invoiceUuid.toString());
				stmt.setString(3, "M");
				stmt.setString(4, itemUuid.toString());
				stmt.setInt(5, numberOfUnits);
				stmt.executeUpdate();

				stmt.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to insert Material Invoice item to Database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Adds a contract record to the given invoice.
	 *
	 * @param invoiceUuid
	 * @param itemUuid
	 * @param amount
	 */
	public static void addContractToInvoice(UUID invoiceUuid, UUID itemUuid, double amount) {
		try (Connection conn = ConnectionFactory.getConnection()) {

			if (!checkInvoice(invoiceUuid, conn) || !checkItem(itemUuid, conn)) {
				LOGGER.info(
						"At least one of the UUIDs entered does not exist \n" + " Invoice UUID : {} \nItem UUID: {}",
						invoiceUuid, itemUuid);
				return;
			}

			String insertContract = """
					INSERT INTO InvoiceItem (uuid, invoiceId,typeEquipment,itemId, price)
					VALUES (?,
					(SELECT invoiceId FROM Invoice WHERE uuid = ?), ?,
					(SELECT itemId FROM Item WHERE uuid = ?),
					?)
					""";

			try (PreparedStatement stmt = conn.prepareStatement(insertContract)) {

				stmt.setString(1, UUID.randomUUID().toString());
				stmt.setString(2, invoiceUuid.toString());
				stmt.setString(3, "C");
				stmt.setString(4, itemUuid.toString());
				stmt.setDouble(5, amount);
				stmt.executeUpdate();

				stmt.close();

			} catch (SQLException e) {
				LOGGER.error("Failed to insert Contract Invoice item to Database", e);
			}

			ConnectionFactory.closeConnection(conn);
		} catch (SQLException e) {
			LOGGER.error("Bad Connection");
		}

	}

	/**
	 * Checks to see if a person exists in the database
	 * 
	 * @param personUuid
	 * @param conn
	 * @return returns True if a person exist
	 */
	private static boolean checkPerson(UUID personUuid, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM Person WHERE uuid = ?")) {
			stmt.setString(1, personUuid.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking if person exists", e);
			return false;
		}
	}

	/**
	 * Validates whether and address exist in database
	 * 
	 * @param street
	 * @param city
	 * @param conn
	 * @return True if address exist
	 */
	private static boolean checkAddress(String street, String city, String state, String zip, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("""
				SELECT addressId
				FROM Address a
				JOIN State s ON a.stateId = s.stateId
				JOIN ZipCode z ON z.zipId = a.zipId
				WHERE a.street = ?
				  AND a.city = ?
				  AND s.stateCode = ?
				  AND z.zip = ?
					LIMIT 1""")) {
			stmt.setString(1, street);
			stmt.setString(2, city);
			stmt.setString(3, state);
			stmt.setString(4, zip);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking if person exists", e);
			return false;
		}
	}

	/**
	 * Validates whether a company exists in database
	 * 
	 * @param companyUuid
	 * @param conn
	 * @return True if company exist
	 */
	private static boolean checkCompany(UUID companyUuid, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM Company where uuid = ?")) {
			stmt.setString(1, companyUuid.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Company exists");
			return false;
		}
	}

	/**
	 * Validates whether an Item exists in database
	 * 
	 * @param itemUuid
	 * @param conn
	 * @return True if item exists
	 */
	private static boolean checkItem(UUID itemUuid, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("""
				SELECT uuid FROM Item WHERE uuid = ?
				""")) {
			stmt.setString(1, itemUuid.toString());

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking equipment", e);
		}
		return false;
	}

	/**
	 * Validates whether an Invoice exists in Database
	 * 
	 * @param invoiceUuid
	 * @param conn
	 * @return True of and Invoice exist
	 */
	private static boolean checkInvoice(UUID invoiceUuid, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("""
				SELECT uuid FROM Invoice WHERE uuid = ?
				""")) {
			stmt.setString(1, invoiceUuid.toString());

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking invoice", e);
		}
		return false;
	}

	private static boolean checkState(String stateCode, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("""
				SELECT stateId FROM State WHERE stateCode = ? LIMIT 1
				""")) {
			stmt.setString(1, stateCode);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking stateId", e);
		}
		return false;
	}

	private static boolean checkZip(String zipCode, Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement("""
				SELECT zipId FROM ZipCode WHERE zip = ? LIMIT 1
				""")) {
			stmt.setString(1, zipCode);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.warn("Error checking zipCode", e);
		}
		return false;
	}

}
