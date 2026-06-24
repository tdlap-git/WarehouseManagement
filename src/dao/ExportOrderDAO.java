package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import database.DatabaseConnect;
import model.ExportOrder;
import model.ExportDetail;

public class ExportOrderDAO {

    // Lấy tất cả đơn xuất kho
    public ArrayList<ExportOrder> selectAll() {
        ArrayList<ExportOrder> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ExportOrders ORDER BY ExportDate DESC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToExportOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Lấy đơn xuất theo ID
    public ExportOrder selectById(int exportID) {
        ExportOrder order = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ExportOrders WHERE ExportID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, exportID);
            rs = pst.executeQuery();

            if (rs.next()) {
                order = mapResultSetToExportOrder(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return order;
    }

    // Lấy đơn xuất theo khách hàng
    public ArrayList<ExportOrder> selectByCustomer(int customerID) {
        ArrayList<ExportOrder> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ExportOrders WHERE CustomerID = ? ORDER BY ExportDate DESC";
            pst = con.prepareStatement(sql);
            pst.setInt(1, customerID);
            rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToExportOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Lấy đơn xuất theo trạng thái
    public ArrayList<ExportOrder> selectByStatus(int status) {
        ArrayList<ExportOrder> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ExportOrders WHERE Status = ? ORDER BY ExportDate DESC";
            pst = con.prepareStatement(sql);
            pst.setInt(1, status);
            rs = pst.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToExportOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Tạo đơn xuất kho
    // Kiểm tra số lượng tồn trước khi xuất kho -> Tồn kho không đủ thì không cho xuất kho
    public int createExportOrder(ExportOrder order, ArrayList<ExportDetail> details) {
        Connection con = null;
        PreparedStatement pstOrder = null;
        PreparedStatement pstDetail = null;
        PreparedStatement pstUpdateProduct = null;
        ResultSet rs = null;
        int exportID = 0;

        try {
        	// Dùng transaction để đảm bảo các thao tác xuất kho được đồng bộ
            con = DatabaseConnect.getConnection();
            con.setAutoCommit(false);

            String sqlOrder = "INSERT INTO ExportOrders (ExportDate, Note, Status, UserID, CustomerID) VALUES (?, ?, ?, ?, ?)";
            pstOrder = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstOrder.setTimestamp(1, order.getExportDate());
            pstOrder.setString(2, order.getNote());
            pstOrder.setInt(3, order.getStatus());
            pstOrder.setInt(4, order.getUserID());
            pstOrder.setInt(5, order.getCustomerID());
            pstOrder.executeUpdate();

            rs = pstOrder.getGeneratedKeys();
            if (rs.next()) {
                exportID = rs.getInt(1);
            }

            String sqlDetail = "INSERT INTO ExportDetails (ExportID, ProductID, Quantity, ExportPrice) VALUES (?, ?, ?, ?)";
            pstDetail = con.prepareStatement(sqlDetail);

            String sqlUpdateProduct = "UPDATE Products SET Quantity = Quantity - ? WHERE ProductID = ?";
            pstUpdateProduct = con.prepareStatement(sqlUpdateProduct);

            for (ExportDetail detail : details) {
                if (!checkProductQuantity(con, detail.getProductID(), detail.getQuantity())) {
                    throw new SQLException("Insufficient stock");
                }

                pstDetail.setInt(1, exportID);
                pstDetail.setInt(2, detail.getProductID());
                pstDetail.setInt(3, detail.getQuantity());
                pstDetail.setDouble(4, detail.getExportPrice());
                pstDetail.addBatch();

                // Chỉ giảm số lượng sản phẩm trong kho nếu đơn xuất đã được duyệt
                if (order.getStatus() == 1) {
                    pstUpdateProduct.setInt(1, detail.getQuantity());
                    pstUpdateProduct.setInt(2, detail.getProductID());
                    pstUpdateProduct.addBatch();
                }
            }

            pstDetail.executeBatch();
            if (order.getStatus() == 1) {
                pstUpdateProduct.executeBatch();
            }

            // commit transaction khi thành công
            con.commit();
            return exportID;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
        return 0;
    }

    // Duyệt đơn xuất
    // Khi đơn được duyệt: Trạng thái đơn được cập nhật -> Số lượng sản phẩm trong kho giảm
    public int approveExportOrder(int exportID) {
        Connection con = null;
        PreparedStatement pstUpdate = null;
        PreparedStatement pstSelect = null;
        PreparedStatement pstUpdateProduct = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            con.setAutoCommit(false);

            String sqlUpdate = "UPDATE ExportOrders SET Status = 1 WHERE ExportID = ? AND Status = 0";
            pstUpdate = con.prepareStatement(sqlUpdate);
            pstUpdate.setInt(1, exportID);
            int result = pstUpdate.executeUpdate();

            if (result > 0) {
                String sqlSelect = "SELECT ProductID, Quantity FROM ExportDetails WHERE ExportID = ?";
                pstSelect = con.prepareStatement(sqlSelect);
                pstSelect.setInt(1, exportID);
                rs = pstSelect.executeQuery();

                String sqlUpdateProduct = "UPDATE Products SET Quantity = Quantity - ? WHERE ProductID = ?";
                pstUpdateProduct = con.prepareStatement(sqlUpdateProduct);

                while (rs.next()) {
                    int productID = rs.getInt("ProductID");
                    int quantity = rs.getInt("Quantity");
                    // Kiểm tra số lượng tồn kho trước khi cho phép xuất hàng
                    if (!checkProductQuantity(con, productID, quantity)) {
                        throw new SQLException("Insufficient stock");
                    }

                    pstUpdateProduct.setInt(1, quantity);
                    pstUpdateProduct.setInt(2, productID);
                    pstUpdateProduct.addBatch();
                }

                pstUpdateProduct.executeBatch();
                con.commit();
            }
            return result;

        } catch (Exception e) { // Có lỗi thì rollback tránh lệch dữ liệu
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
        return 0;
    }

    // Hủy đơn xuất
    public int cancelExportOrder(int exportID) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM ExportOrders WHERE ExportID = ? AND Status = 0";
            pst = con.prepareStatement(sql);
            pst.setInt(1, exportID);
            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Lấy chi tiết đơn xuất
    public ArrayList<ExportDetail> getExportDetails(int exportID) {
        ArrayList<ExportDetail> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM ExportDetails WHERE ExportID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, exportID);
            rs = pst.executeQuery();

            while (rs.next()) {
                ExportDetail detail = new ExportDetail();
                detail.setExportID(rs.getInt("ExportID"));
                detail.setProductID(rs.getInt("ProductID"));
                detail.setQuantity(rs.getInt("Quantity"));
                detail.setExportPrice(rs.getDouble("ExportPrice"));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Tổng tiền
    public double getTotalAmount(int exportID) {
        double total = 0;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT SUM(Quantity * ExportPrice) AS Total FROM ExportDetails WHERE ExportID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, exportID);
            rs = pst.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("Total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return total;
    }

    // Kiểm tra tồn kho
    private boolean checkProductQuantity(Connection con, int productID, int requiredQuantity) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT Quantity FROM Products WHERE ProductID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, productID);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("Quantity") >= requiredQuantity;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    private ExportOrder mapResultSetToExportOrder(ResultSet rs) throws SQLException {
        ExportOrder order = new ExportOrder();
        order.setExportID(rs.getInt("ExportID"));
        order.setExportDate(rs.getTimestamp("ExportDate"));
        order.setNote(rs.getString("Note"));
        order.setStatus(rs.getInt("Status"));
        order.setUserID(rs.getInt("UserID"));
        order.setCustomerID(rs.getInt("CustomerID"));
        return order;
    }
}
