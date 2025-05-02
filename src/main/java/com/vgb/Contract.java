package com.vgb;

import java.util.UUID;

/**
 * Represents a contract available in the system.
 */
public class Contract extends Item {
	
	  
	  private double contractprice;

	  /**
	     * Constructs a Contract item with the given attributes
	     * 
	     * @param uuid        The UUID of the contract
	     * @param name        The name/description of the contract
	     * @param companyUuid The UUID of the company that VGB subcontracts with
	     */
	    public Contract(UUID uuid, String name, double contractPrice, Company customer) {
	        super(uuid, name, contractPrice, customer);
	        this.contractprice = contractPrice;
	    }
	    
	    /**
	     * Copy ConstrUctor for passing in the contractPrice 
	     * @param contract
	     * @param contractPrice
	     * @param customer 
	     */

	    public Contract(Contract contract, double contractPrice) {
	        super(contract.getUUID(), contract.getName(), contractPrice, contract.getCustomer());
	        this.contractprice = contractPrice;
	        
	    }

	    @Override
	    public double getTaxes() {
	        return 0;
	    }

	    @Override
	    public double getTotal() {
	        return contractprice;
	    }

		@Override
		public double getSubTotal() {
			return contractprice;
		}
		
		@Override
		public String toString() {
			return String.format("%s (Contract) %s \n %70s $%s $%s $%12s\n %s \n ", this.getUUID(), this.getName(), " ",
					this.getSubTotal(), this.getTaxes(), this.getTotal(), this.getCustomer().getName());
		}
	}
