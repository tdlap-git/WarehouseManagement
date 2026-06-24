package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import database.DatabaseConnect;
import model.Warehouse;

public class WarehouseDAO {

    // Lấy danh sách kho
    public ArrayList<Warehouse> selectAll() {
        ArrayList<Warehouse> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Warehouse";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setWarehouseID(rs.getInt("WarehouseID"));
                w.setWarehouseName(rs.getString("WarehouseName"));
                w.setAddress(rs.getString("Address"));
                list.add(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm kho
    public int insert(Warehouse w) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "INSERT INTO Warehouse (WarehouseName, Address) VALUES (?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, w.getWarehouseName());
            pst.setString(2, w.getAddress());

            return pst.executeUpdate(); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Cập nhật kho
    public int update(Warehouse w) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "UPDATE Warehouse SET WarehouseName=?, Address=? WHERE WarehouseID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, w.getWarehouseName());
            pst.setString(2, w.getAddress());
            pst.setInt(3, w.getWarehouseID());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Xóa kho
    public int delete(int id) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Warehouse WHERE WarehouseID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }
}
