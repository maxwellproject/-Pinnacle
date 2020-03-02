package test.shopping.receipt.model;

import java.util.HashSet;

public class Location {
	String name;
	Double salesTaxRate;
	HashSet<String> exempts;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getSalesTaxRate() {
		return salesTaxRate;
	}
	public void setSalesTaxRate(Double salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
	}
	public HashSet<String> getExempts() {
		return exempts;
	}
	public void setExempts(HashSet<String> exempts) {
		this.exempts = exempts;
	}
	
}
