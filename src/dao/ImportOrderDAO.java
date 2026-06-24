package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.DatabaseConnect;
import model.ImportDetail;
import model.ImportOrder;

public class ImportOrderDAO {

    // Lấy toàn bộ đơn nhập
    public ArrayList<ImportOrder> selectAll() {
        ArrayList<ImportOrder> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ImportOrders ORDER BY ImportDate DESC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToImportOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Lấy đơn nhập theo ID
    public ImportOrder selectById(int importID) {
        ImportOrder order = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ImportOrders WHERE ImportID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, importID);
            rs = pst.executeQuery();
            if (rs.next()) {
                order = mapResultSetToImportOrder(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return order;
    }

    // Lấy đơn nhập theo nhà cung cấp
    public ArrayList<ImportOrder> selectBySupplier(int supplierID) {
        ArrayList<ImportOrder> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ImportOrders WHERE SupplierID = ? ORDER BY ImportDate DESC";
            pst = con.prepareStatement(sql);
            pst.setInt(1, supplierID);
            rs = pst.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToImportOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Tạo đơn nhập mới
    // > 0 : tạo thành công (ImportID)
    // = 0 : thất bại
    public int createImportOrder(ImportOrder order, ArrayList<ImportDetail> details) {
        Connection con = null;
        PreparedStatement pstOrder = null;
        PreparedStatement pstDetail = null;
        PreparedStatement pstUpdateProduct = null;
        ResultSet rs = null;
        int importID = 0;

        try {
        	// Dùng transaction để tạo đơn được đồng bộ 
            con = DatabaseConnect.getConnection();
            con.setAutoCommit(false);

            String sqlOrder =
                "INSERT INTO ImportOrders (ImportDate, Note, Status, UserID, SupplierID) VALUES (?, ?, ?, ?, ?)";
            pstOrder = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstOrder.setTimestamp(1, order.getImportDate());
            pstOrder.setString(2, order.getNote());
            pstOrder.setInt(3, order.getStatus());
            pstOrder.setInt(4, order.getUserID());
            pstOrder.setInt(5, order.getSupplierID());
            pstOrder.executeUpdate();

            rs = pstOrder.getGeneratedKeys();
            if (rs.next()) {
                importID = rs.getInt(1);
            }

            String sqlDetail =
                "INSERT INTO ImportDetails (ImportID, ProductID, Quantity, ImportPrice) VALUES (?, ?, ?, ?)";
            pstDetail = con.prepareStatement(sqlDetail);

            String sqlUpdateProduct =
                "UPDATE Products SET Quantity = Quantity + ? WHERE ProductID = ?";
            pstUpdateProduct = con.prepareStatement(sqlUpdateProduct);

            for (ImportDetail d : details) {
                pstDetail.setInt(1, importID);
                pstDetail.setInt(2, d.getProductID());
                pstDetail.setInt(3, d.getQuantity());
                pstDetail.setDouble(4, d.getImportPrice());
                pstDetail.addBatch();

                // Chỉ cập nhật số lượng sản phẩm nếu đơn nhập đã được duyệt
                if (order.getStatus() == 1) {
                    pstUpdateProduct.setInt(1, d.getQuantity());
                    pstUpdateProduct.setInt(2, d.getProductID());
                    pstUpdateProduct.addBatch();
                }
            }

            pstDetail.executeBatch();
            if (order.getStatus() == 1) {
                pstUpdateProduct.executeBatch();
            }

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstOrder != null) pstOrder.close();
                if (pstDetail != null) pstDetail.close();
                if (pstUpdateProduct != null) pstUpdateProduct.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return importID;
    }

    // Duyệt đơn nhập
    public boolean approveImportOrder(int importID) {
        Connection con = null;
        PreparedStatement pstUpdate = null;
        PreparedStatement pstSelect = null;
        PreparedStatement pstUpdateProduct = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            con.setAutoCommit(false);

            String sqlUpdate =
                "UPDATE ImportOrders SET Status = 1 WHERE ImportID = ? AND Status = 0";
            pstUpdate = con.prepareStatement(sqlUpdate);
            pstUpdate.setInt(1, importID);

            if (pstUpdate.executeUpdate() == 0) {
                return false;
            }

            String sqlSelect =
                "SELECT ProductID, Quantity FROM ImportDetails WHERE ImportID = ?";
            pstSelect = con.prepareStatement(sqlSelect);
            pstSelect.setInt(1, importID);
            rs = pstSelect.executeQuery();

            String sqlUpdateProduct =
                "UPDATE Products SET Quantity = Quantity + ? WHERE ProductID = ?";
            pstUpdateProduct = con.prepareStatement(sqlUpdateProduct);

            while (rs.next()) {
                pstUpdateProduct.setInt(1, rs.getInt("Quantity"));
                pstUpdateProduct.setInt(2, rs.getInt("ProductID"));
                pstUpdateProduct.addBatch();
            }

            pstUpdateProduct.executeBatch();
            con.commit(); // Commit transaction khi thành công
            return true;

        } catch (Exception e) { // // Nếu có lỗi, rollback để đảm bảo data không bị lệch
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstUpdate != null) pstUpdate.close();
                if (pstSelect != null) pstSelect.close();
                if (pstUpdateProduct != null) pstUpdateProduct.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Hủy đơn nhập (chỉ khi chưa duyệt)
    public boolean cancelImportOrder(int importID) {
        Connection con = null;
        PreparedStatement pstDetail = null;
        PreparedStatement pstOrder = null;

        try {
            con = DatabaseConnect.getConnection();
            con.setAutoCommit(false);

            String sqlDeleteDetail = "DELETE FROM ImportDetails WHERE ImportID = ?";
            pstDetail = con.prepareStatement(sqlDeleteDetail);
            pstDetail.setInt(1, importID);
            pstDetail.executeUpdate();

            String sqlDeleteOrder =
                "DELETE FROM ImportOrders WHERE ImportID = ? AND Status = 0";
            pstOrder = con.prepareStatement(sqlDeleteOrder);
            pstOrder.setInt(1, importID);

            if (pstOrder.executeUpdate() == 0) {
                return false;
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DatabaseConnect.closeConnection(con, pstOrder, null);
            if (pstDetail != null) {
                try { pstDetail.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // Lấy chi tiết sản phẩm của một đơn nhập
    public ArrayList<ImportDetail> getImportDetails(int importID) {
        ArrayList<ImportDetail> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ImportDetails WHERE ImportID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, importID);
            rs = pst.executeQuery();
            while (rs.next()) {
                ImportDetail detail = new ImportDetail();
                detail.setImportID(rs.getInt("ImportID"));
                detail.setProductID(rs.getInt("ProductID"));
                detail.setQuantity(rs.getInt("Quantity"));
                detail.setImportPrice(rs.getDouble("ImportPrice"));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    
    private ImportOrder mapResultSetToImportOrder(ResultSet rs) throws SQLException {
        ImportOrder order = new ImportOrder();
        order.setImportID(rs.getInt("ImportID"));
        order.setImportDate(rs.getTimestamp("ImportDate"));
        order.setNote(rs.getString("Note"));
        order.setStatus(rs.getInt("Status"));
        order.setUserID(rs.getInt("UserID"));
        order.setSupplierID(rs.getInt("SupplierID"));
        return order;
    }
}
