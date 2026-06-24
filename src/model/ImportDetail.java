package model;

public class ImportDetail {
    private int importID;
    private int productID;
    private int quantity;
    private double importPrice;

    public ImportDetail() {
    }

    public ImportDetail(int importID, int productID, int quantity, double importPrice) {
        this.importID = importID;
        this.productID = productID;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    // Getters, setters
    public int getImportID() { 
    	return importID; 
    }
    public void setImportID(int importID) { 
    	this.importID = importID; 
    }

    public int getProductID() { 
    	return productID; 
    }
    public void setProductID(int productID) { 
    	this.productID = productID; 
    }

    public int getQuantity() { 
    	return quantity; 
    }
    public void setQuantity(int quantity) { 
    	this.quantity = quantity; 
    }

    public double getImportPrice() { 
    	return importPrice; 
    }
    public void setImportPrice(double importPrice) { 
    	this.importPrice = importPrice; 
    }
    
    // Tính thành tiền
    public double getTotalAmount() {
        return quantity * importPrice;
    }
    
    @Override
    public String toString() {
        return "ProductID: " + productID + ", Quantity: " + quantity + ", Price: " + importPrice;
    }
    
}