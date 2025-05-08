package com.vgb;

import java.util.UUID;

/*
 * Represents a material in the System
 */
public class Material extends Item {

	private String unit;
	private double quantity;
	private final static double TAX_RATE = 0.0715;

	/**
	 * Constructs a Material object based on the given attributes
	 * 
	 * @param uuid        Items uuid
	 * @param name        material name
	 * @param unit
	 * @param costPerUnit
	 */
	public Material(UUID uuid, String name, String unit, double costPerUnit) {
		super(uuid, name, costPerUnit);
		this.unit = unit;
	}

	/**
	 * Copy constructor for parsing in the quantity during a transaction
	 * 
	 * @param material
	 * @param quantity
	 */

	public Material(Material material, double quantity) {
		super(material.getUUID(), material.getName(), material.getPrice());
		this.quantity = quantity;
		this.unit = material.getUnit();
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public double getSubTotal() {
		return super.getPrice() * this.getQuantity();
	}

	@Override
	public double getTaxes() {

		return roundToCent(getSubTotal() * TAX_RATE);
	}

	@Override
	public double getTotal() {
		return roundToCent(getSubTotal() + getTaxes());
	}

	public double getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return String.format("\n %s (Material) %s \n %20s @ $ %s/per %s \n %70s $%s $%s $%s \n ", this.getUUID(),
				this.getName(), this.getQuantity(), this.getPrice(), this.getUnit(), " ", this.getSubTotal(),
				this.getTaxes(), this.getTotal());
	}

}
