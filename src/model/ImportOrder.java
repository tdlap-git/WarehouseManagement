package model;
import java.sql.Timestamp; 

public class ImportOrder {
    private int importID;
    private Timestamp importDate;
    private String note;
    private int status;
    private int userID;
    private int supplierID;

    public ImportOrder() {
    }

    public ImportOrder(int importID, Timestamp importDate, String note, int status, int userID, int supplierID) {
        this.importID = importID;
        this.importDate = importDate;
        this.note = note;
        this.status = status;
        this.userID = userID;
        this.supplierID = supplierID;
    }

    // Getters, setters
    public int getImportID() { 
    	return importID; 
    }
    public void setImportID(int importID) { 
    	this.importID = importID; 
    }

    public Timestamp getImportDate() { 
    	return importDate; 
    }
    public void setImportDate(Timestamp importDate) { 
    	this.importDate = importDate; 
    }

    public String getNote() { 
    	return note; 
    }
    public void setNote(String note) { 
    	this.note = note; 
    }

    public int getStatus() { 
    	return status; 
    }
    public void setStatus(int status) { 
    	this.status = status; 
    }

    public int getUserID() { 
    	return userID; 
    }
    public void setUserID(int userID) { 
    	this.userID = userID; 
    }

    public int getSupplierID() { 
    	return supplierID; 
    }
    public void setSupplierID(int supplierID) { 
    	this.supplierID = supplierID; 
    }

    @Override
    public String toString() {
        return "ImportOrder #" + importID + " (" + importDate + ")";
    }
}