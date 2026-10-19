package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public final class ConnectDB {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanHang;"
                    + "encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    // Chỉ hiện hộp thoại báo lỗi 1 lần, tránh bật liên tục
    private static boolean daBaoLoi = false;

    private ConnectDB() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            baoLoi("Không tìm thấy SQL Server JDBC Driver.\n"
                    + "Hãy thêm lib/sqljdbc42.jar vào thư viện của project.");
            throw new SQLException("Không tìm thấy SQL Server JDBC Driver.", e);
        } catch (SQLException e) {
            baoLoi("Không kết nối được SQL Server:\n" + e.getMessage());
            throw e;
        }
    }

    private static void baoLoi(String thongBao) {
        if (!daBaoLoi) {
            daBaoLoi = true;
            JOptionPane.showMessageDialog(null, thongBao, "Lỗi kết nối CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
