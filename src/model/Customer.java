package model;

public class Customer {
    private int customerID;
    private String customerName;
    private String phone; 
    private String address;

    public Customer() {
    }

    public Customer(int customerID, String customerName, String phone, String address) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
    }

    // Getters, setters
    public int getCustomerID() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
        return customerName;
    }
    
}