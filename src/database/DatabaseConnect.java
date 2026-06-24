package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnect {
	// Kết nối database SQL Server
	private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseManagement;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456";

    public static Connection getConnection() {
        Connection conn = null;
        try {
        	// JDBC Driver cho SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Kết nối thất bại!");
            e.printStackTrace();
        }
        return conn;
    }
    
    // Đóng JDBC theo thứ tự: ResultSet -> PreparedStatement -> Connection.
    public static void closeConnection(Connection c, PreparedStatement p, ResultSet r) {
        try {
            if (r != null) r.close();
            if (p != null) p.close();
            if (c != null) c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (getConnection() != null) {
            System.out.println("Kết nối thành công tới SQL Server!");
        }
    }	

}
