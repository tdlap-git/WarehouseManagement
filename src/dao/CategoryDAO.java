package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import database.DatabaseConnect;
import model.Category;

public class CategoryDAO {

    // Lấy danh sách danh mục
    public ArrayList<Category> selectAll() {
        ArrayList<Category> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Categories";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryID(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm danh mục
    public int insert(Category c) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "INSERT INTO Categories (CategoryName) VALUES (?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, c.getCategoryName());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Cập nhật danh mục
    public int update(Category c) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "UPDATE Categories SET CategoryName=? WHERE CategoryID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, c.getCategoryName());
            pst.setInt(2, c.getCategoryID());

            return pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return 0;
    }

    // Xóa danh mục
    public int delete(int id) {
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Categories WHERE CategoryID=?";
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
