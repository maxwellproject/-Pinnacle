package test.shopping.receipt.model;

import java.util.LinkedList;

public class Purchase {
	Location location;
	LinkedList<Product> products;
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public LinkedList<Product> getProducts() {
		return products;
	}
	public void setProducts(LinkedList<Product> products) {
		this.products = products;
	}
	
}
