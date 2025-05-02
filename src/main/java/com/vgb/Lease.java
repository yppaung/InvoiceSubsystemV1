package com.vgb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


/**
 * Represents a Lease Transaction of an Equipment
 * Contains the logic for calculating the lease values of an Equipment 
 */

public class Lease extends Equipment {

	private LocalDate startDate;
	private LocalDate endDate;
	private static final double FLAT_TAX = 1500.00;
	
	/**
	 * Constructs a Lease Object based on the given attributes
	 * @param equipment Equipments attributes
	 * @param startDate start of lease
	 * @param endDate end of lease
	 */
	public Lease(Equipment equipment, LocalDate startDate, LocalDate endDate) {
		super(equipment.getUUID(), equipment.getName(), equipment.getModelName(), equipment.getPrice());
		this.startDate = startDate;
		this.endDate = endDate;
	}

	

	/*
	 * returns the difference of startDate and endDate
	 * 
	 */
	public double calculateDays() {	
		return ChronoUnit.DAYS.between(getStartDate(), getEndDate()) + 1;
	}
	
	@Override
	public double getSubTotal() {	     
	    double years = calculateDays() / 365.0;
	    return roundToCent((years / 5) * super.getPrice() * 1.5);
	}
	
	@Override
	public double getTaxes() {
		
		if (getSubTotal() > 12500.00) {
			return FLAT_TAX;
		}
		return 0;
	}
	
	@Override
	public double getTotal() {
		return roundToCent(getSubTotal() + getTaxes());
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}
	
	
	@Override
	public String toString() {
		return String.format("\n %s (Lease) \n"
				+ "%20s | %s - %s (%s) days \n %70s $%s $%s $%s", this.getUUID(), this.getName(),this.getStartDate() , this.getEndDate(),
				this.calculateDays(), " ", this.getSubTotal(), this.getTaxes(), this.getTotal());
	}
	
}
