package model;
import java.sql.Timestamp;

public class ExportOrder {
    private int exportID;
    private Timestamp exportDate;
    private String note;
    private int status;
    private int userID;
    private int customerID;

    public ExportOrder() {
    }

    public ExportOrder(int exportID, Timestamp exportDate, String note, int status, int userID, int customerID) {
        this.exportID = exportID;
        this.exportDate = exportDate;
        this.note = note;
        this.status = status;
        this.userID = userID;
        this.customerID = customerID;
    }

    // Getters, setters
    public int getExportID() { 
    	return exportID; 
    }
    public void setExportID(int exportID) { 
    	this.exportID = exportID; 
    }

    public Timestamp getExportDate() { 
    	return exportDate; 
    }
    public void setExportDate(Timestamp exportDate) { 
    	this.exportDate = exportDate; 
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

    public int getCustomerID() { 
    	return customerID; 
    }
    public void setCustomerID(int customerID) { 
    	this.customerID = customerID; 
    }
    
    @Override
    public String toString() {
        return "ExportOrder #" + exportID + " (" + exportDate + ")";
    }
}