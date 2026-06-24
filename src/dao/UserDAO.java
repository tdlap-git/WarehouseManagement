package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import database.DatabaseConnect;
import model.User;

public class UserDAO {

    // Check đăng nhập
    public User checkLogin(String username, String password) {
        User user = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Users WHERE UserName = ? AND Password = ? AND Status = 1";
            
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password); 
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return user;
    }

    // Lấy danh sách nhân viên (acc quản lý)
    public ArrayList<User> selectAll() {
        ArrayList<User> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Users";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return list;
    }

    // Thêm nhân viên (acc quản lý)
    public int insert(User user) {
        int result = 0;
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            
            String sql = "INSERT INTO Users (UserName, Password, Role, FullName, Status, WarehouseID) VALUES (?, ?, ?, ?, ?, ?)";
            
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getFullName());
            pst.setInt(5, user.getStatus());
            
            if (user.getWarehouseID() == 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, user.getWarehouseID());
            }
            
            result = pst.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return result;
    }

    // Cập nhật thông tin
    public int update(User user) {
        int result = 0;
        Connection con = null;
        PreparedStatement pst = null;

        try {
            con = DatabaseConnect.getConnection();
            
            String sql = "UPDATE Users SET UserName=?, Password=?, Role=?, FullName=?, Status=?, WarehouseID=? WHERE UserID=?";
            
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getFullName());
            pst.setInt(5, user.getStatus());
            
            if (user.getWarehouseID() == 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, user.getWarehouseID());
            }
            
            pst.setInt(7, user.getUserID());
            
            result = pst.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return result;
    }
    
    // Xóa tài khoản
    public int delete(int id) {
        int result = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "DELETE FROM Users WHERE UserID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            result = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, null);
        }
        return result;
    }

    // Lấy tài khoản theo ID 
    public User selectById(int id) {
        User user = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnect.getConnection();
            String sql = "SELECT * FROM Users WHERE UserID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnect.closeConnection(con, pst, rs);
        }
        return user;
    }

    
    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUserName(rs.getString("UserName"));
        user.setPassword(rs.getString("Password"));
        user.setRole(rs.getString("Role"));
        user.setFullName(rs.getString("FullName"));
        user.setStatus(rs.getInt("Status"));
        
        
        int wID = rs.getInt("WarehouseID");
        if (rs.wasNull()) {
            user.setWarehouseID(0);
        } else {
            user.setWarehouseID(wID);
        }
        return user;
    }
}