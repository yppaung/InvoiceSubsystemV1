package com.vgb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import com.vgb.database.DataMapper;
import com.vgb.database.IDLoader;

/**
 * Factory class for instantiating {@link InvoiceItem} objects.
 * <p>
 * Expects a {@link ResultSet} with the following columns:
 * <ul>
 *   <li>invoiceItemId</li>
 *   <li>invoiceId</li>
 *   <li>itemId</li>
 *   <li>typeEquipment</li>
 *   <li>price</li>
 *   <li>startDate</li>
 *   <li>endDate</li>
 *   <li>quantity</li>
 *   <li>numberOfHours</li>
 * </ul>
 */
public class LoadInvoiceItem implements DataMapper<InvoiceItem>{

	@Override
	public InvoiceItem map(ResultSet rs, Connection conn) throws SQLException {

	    LoadItem mapper = new LoadItem();
	    IDLoader<Item> service = new IDLoader<>(mapper);
	    
	    char EQType = rs.getString("typeEquipment").charAt(0);
	    Item item = service.loadById(
	        """
	    		SELECT 
	    		uuid,
	    		itemName,
	    		itemPrice,
	    		itemType,
	    		model,
	    		unit,
	    		unitPrice,
	    		customerId
	    		FROM Item WHERE itemId = ?
	    	""",
	        rs.getInt("itemId"), conn
	    );

	    LoadInvoice map = new LoadInvoice();
	    IDLoader<Invoice> invoiceLoader = new IDLoader<>(map);

	    Invoice inv = invoiceLoader.loadById(
	        """
	    		SELECT invoiceId,
	    		uuid,
	    		companyId,
	    		salesPersonId,
	    		invoiceDate
	    		FROM Invoice WHERE invoiceId = ?
	    		""",
	        rs.getInt("invoiceId"), conn
	    );

	    switch (EQType) {
	        case 'L':
	            LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
	            LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
	            return new InvoiceItem(inv, new Lease((Equipment) item, startDate, endDate));

	        case 'R':
	            double hours = rs.getDouble("numberOfHours");
	            return new InvoiceItem(inv, new Rental((Equipment) item, hours));

	        case 'P':
	            return new InvoiceItem(inv, item);

	        case 'M':
	            double quantity = rs.getDouble("quantity");
	            return new InvoiceItem(inv, new Material((Material) item, quantity));

	        case 'C':
	            double contractPrice = rs.getDouble("price");
	            return new InvoiceItem(inv, new Contract((Contract) item, contractPrice));

	        default:
	            throw new SQLException("Unknown Equipment Type: " + EQType);
	    }
	}

}
