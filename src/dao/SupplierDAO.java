package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import database.DatabaseConnect;
import model.Supplier;

public class SupplierDAO {

    // Lấy danh sách nhà cung cấp
    public ArrayList<Supplier> selectAll() {
        ArrayList<Supplier> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Suppliers";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierID(rs.getInt("SupplierID"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setPhone(rs.getString("Phone"));
                s.setAddress(rs.getString("Address"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm
    public int insert(Supplier s) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "INSERT INTO Suppliers (SupplierName, Phone, Address) VALUES (?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, s.getSupplierName());
            pst.setString(2, s.getPhone());
            pst.setString(3, s.getAddress());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Sửa
    public int update(Supplier s) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "UPDATE Suppliers SET SupplierName=?, Phone=?, Address=? WHERE SupplierID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, s.getSupplierName());
            pst.setString(2, s.getPhone());
            pst.setString(3, s.getAddress());
            pst.setInt(4, s.getSupplierID());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Xóa (đã bỏ -1)
    public int delete(int id) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Suppliers WHERE SupplierID=?";
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

    // Lấy nhà cung cấp theo ID
    public Supplier selectById(int id) {
        Supplier s = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Suppliers WHERE SupplierID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                s = new Supplier();
                s.setSupplierID(rs.getInt("SupplierID"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setPhone(rs.getString("Phone"));
                s.setAddress(rs.getString("Address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return s;
    }
}
