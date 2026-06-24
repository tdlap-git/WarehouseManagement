package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import database.DatabaseConnect;
import model.Customer;

public class CustomerDAO {

    // Danh sách khách hàng
    public ArrayList<Customer> selectAll() {
        ArrayList<Customer> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Customers";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setCustomerName(rs.getString("CustomerName"));
                c.setPhone(rs.getString("Phone"));
                c.setAddress(rs.getString("Address"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm khách hàng
    public int insert(Customer c) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "INSERT INTO Customers (CustomerName, Phone, Address) VALUES (?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, c.getCustomerName());
            pst.setString(2, c.getPhone());
            pst.setString(3, c.getAddress());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Cập nhật khách hàng
    public int update(Customer c) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "UPDATE Customers SET CustomerName=?, Phone=?, Address=? WHERE CustomerID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, c.getCustomerName());
            pst.setString(2, c.getPhone());
            pst.setString(3, c.getAddress());
            pst.setInt(4, c.getCustomerID());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Xóa khách hàng
    public int delete(int id) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Customers WHERE CustomerID=?";
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

    // Lấy khách hàng theo ID
    public Customer selectById(int id) {
        Customer c = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Customers WHERE CustomerID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                c = new Customer();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setCustomerName(rs.getString("CustomerName"));
                c.setPhone(rs.getString("Phone"));
                c.setAddress(rs.getString("Address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return c;
    }
}
