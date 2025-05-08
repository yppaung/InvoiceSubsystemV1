package com.vgb;

import java.util.UUID;

/**
 * Represents a Rental Transaction available in the System Extends equipment
 */
public class Rental extends Equipment {

	private double hours;
	private static final double TAX = 0.0438;

	/**
	 * Constructs a Rental object with given attributes
	 * 
	 * @param uuid      Equipments UUID
	 * @param name      Equipments Name
	 * @param modelName Equipments model name
	 * @param price     Equipments price
	 * @param hours     Number of hours rented
	 */
	public Rental(UUID uuid, String name, String modelName, double price, double hours) {
		super(uuid, name, modelName, price);
		this.hours = hours;
	}

	public Rental(Equipment equipment, double hours) {
		super(equipment.getUUID(), equipment.getName(), equipment.getModelName(), equipment.getPrice());
		this.hours = hours;
	}

	public double calculateRate() {
		return super.getPrice() * 0.001;
	}

	@Override
	public double getTaxes() {
		return roundToCent(getSubTotal() * TAX);

	}

	@Override
	public double getSubTotal() {
		return roundToCent(getHours() * calculateRate());
	}

	@Override
	public double getTotal() {
		return roundToCent(getTaxes() + getSubTotal());
	}

	public double getHours() {
		return hours;
	}

	public String toString() {
		return String.format("\n %s (Rental) %s-%s \n %s hours @ %s/per hour \n %70s $%s $%s $%s \n", this.getUUID(),
				this.getName(), this.getModelName(), this.getHours(), this.calculateRate(), " ", this.getSubTotal(),
				this.getTaxes(), this.getTotal());
	}
}
