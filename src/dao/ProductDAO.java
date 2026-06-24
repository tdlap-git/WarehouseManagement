package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnect;
import model.Product;

public class ProductDAO {

    // Lấy tất cả sản phẩm
    public ArrayList<Product> selectAll() {
        ArrayList<Product> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Products ORDER BY ProductID DESC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm sản phẩm 
    public int insert(Product p) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            
            String sql = "INSERT INTO Products (ProductName, CategoryID, Unit, Price, Quantity, WarehouseID, SupplierID) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);

            pst.setString(1, p.getProductName());

            if (p.getCategoryID() == 0) {
                pst.setNull(2, java.sql.Types.INTEGER);
            } else {
                pst.setInt(2, p.getCategoryID());
            }

            pst.setString(3, p.getUnit());
            pst.setDouble(4, p.getPrice());
            pst.setInt(5, p.getQuantity());

            if (p.getWarehouseID() == 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, p.getWarehouseID());
            }

            // Set tham số cho SupplierID
            if (p.getSupplierID() == 0) {
                pst.setNull(7, java.sql.Types.INTEGER);
            } else {
                pst.setInt(7, p.getSupplierID());
            }

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Cập nhật sản phẩm 
    public int update(Product p) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            
            String sql = "UPDATE Products SET ProductName=?, CategoryID=?, Unit=?, Price=?, Quantity=?, WarehouseID=?, SupplierID=? WHERE ProductID=?";
            pst = con.prepareStatement(sql);

            pst.setString(1, p.getProductName());

            if (p.getCategoryID() == 0) {
                pst.setNull(2, java.sql.Types.INTEGER);
            } else {
                pst.setInt(2, p.getCategoryID());
            }

            pst.setString(3, p.getUnit());
            pst.setDouble(4, p.getPrice());
            pst.setInt(5, p.getQuantity());

            if (p.getWarehouseID() == 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, p.getWarehouseID());
            }

            
            if (p.getSupplierID() == 0) {
                pst.setNull(7, java.sql.Types.INTEGER);
            } else {
                pst.setInt(7, p.getSupplierID());
            }

            pst.setInt(8, p.getProductID());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Xóa sản phẩm
    public int delete(int id) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Products WHERE ProductID=?";
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

    // Tìm kiếm theo tên 
    public ArrayList<Product> searchByKeyword(String keyword) {
        ArrayList<Product> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Products WHERE ProductName LIKE ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");

            rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Lấy sản phẩm theo ID 
    public Product selectById(int id) {
        Product p = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Products WHERE ProductID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next()) {
                p = mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return p;
    }

    // Lấy danh sách sản phẩm theo ID Nhà cung cấp
    public ArrayList<Product> selectBySupplierId(int supplierId) {
        ArrayList<Product> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Products WHERE SupplierID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, supplierId);
            rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductID(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));

        int cID = rs.getInt("CategoryID");
        if (rs.wasNull()) {
            p.setCategoryID(0);
        } else {
            p.setCategoryID(cID);
        }

        p.setUnit(rs.getString("Unit"));
        p.setPrice(rs.getDouble("Price"));
        p.setQuantity(rs.getInt("Quantity"));

        int wID = rs.getInt("WarehouseID");
        if (rs.wasNull()) {
            p.setWarehouseID(0);
        } else {
            p.setWarehouseID(wID);
        }

        int sID = rs.getInt("SupplierID");
        if (rs.wasNull()) {
            p.setSupplierID(0);
        } else {
            p.setSupplierID(sID);
        }

        return p;
    }
}