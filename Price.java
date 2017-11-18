package models;

public class Price {
	private double price;
	private double lowestPrice;
	private double highestPrice;
	
    
	//double currentPrice;
	//double lowest; 
    //double highest;
	public Price(double price, double lowestPrice, double highestPrice) {
		this.price = price;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public double getLowestPrice() {
		return this.lowestPrice;
	}
	
	public double getHighestPrice() {
		return this.highestPrice;
	}
}