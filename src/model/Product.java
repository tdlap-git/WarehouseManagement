package model;

public class Product {
    private int productID;
    private String productName;
    private int categoryID;
    private String unit;
    private double price;
    private int quantity;
    private int warehouseID;
    private int supplierID;

    public Product() {
    }

    public Product(int productID, String productName, int categoryID, String unit, double price, int quantity, int warehouseID, int supplierID) {
        this.productID = productID;
        this.productName = productName;
        this.categoryID = categoryID;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.warehouseID = warehouseID;
        this.supplierID = supplierID;
    }

    // Getters, setters
    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getWarehouseID() {
        return warehouseID;
    }
    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }
    
    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    @Override
    public String toString() {
        return productID + " - " + productName;
    }
}