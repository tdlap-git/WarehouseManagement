package model;

public class Supplier {
    private int supplierID;
    private String supplierName;
    private String phone;
    private String address;

    public Supplier() {
    }

    public Supplier(int supplierID, String supplierName, String phone, String address) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.phone = phone;
        this.address = address;
    }

    // Getters, setters
    public int getSupplierID() {
        return supplierID;
    }
    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return supplierName; 
    }
}