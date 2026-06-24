package model;

public class Warehouse {
	private int warehouseID;
	private String address;
	private String warehouseName;
	
	public Warehouse() {
		
	}
	public Warehouse(int warehouseID, String address, String warehouseName) {
		this.warehouseID = warehouseID;
		this.address = address;
		this.warehouseName = warehouseName;
	}
	// Getters, setters
	public int getWarehouseID() {
		return warehouseID;
	}
	public void setWarehouseID(int warehouseID) {
		this.warehouseID = warehouseID;
	}
	
	public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    @Override
    public String toString() {
        return warehouseName + " - " + address;
    }

}
