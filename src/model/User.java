package model;

public class User {
    private int userID;
    private String userName;
    private String password;
    private String role;
    private String fullName;
    private int status; 
    private int warehouseID;

    public User() {
    }

    public User(int userID, String userName, String password, String role, String fullName, int status, int warehouseID) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.status = status;
        this.warehouseID = warehouseID;
    }
    // Getters, setters

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public int getWarehouseID() {
        return warehouseID;
    }
    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    @Override 
    public String toString() {
        return "User [ID=" + userID + ", Username=" + userName + ", Role=" + role + ", Name=" + fullName + ", status=" + status + "]";
    }
}