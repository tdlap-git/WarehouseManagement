package model;

public class ExportDetail {
    private int exportID;
    private int productID;
    private int quantity;
    private double exportPrice;

    public ExportDetail() {
    }

    public ExportDetail(int exportID, int productID, int quantity, double exportPrice) {
        this.exportID = exportID;
        this.productID = productID;
        this.quantity = quantity;
        this.exportPrice = exportPrice;
    }

    // Getters, setters
    public int getExportID() { 
    	return exportID; 
    }
    public void setExportID(int exportID) { 
    	this.exportID = exportID; 
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

    public double getExportPrice() { 
    	return exportPrice; 
    }
    public void setExportPrice(double exportPrice) { 
    	this.exportPrice = exportPrice; 
    }
    
    // Tính thành tiền
    public double getTotalAmount() {
        return quantity * exportPrice;
    }
    
    @Override
    public String toString() {
        return "ProductID: " + productID + ", Quantity: " + quantity + ", Price: " + exportPrice;
    }
}