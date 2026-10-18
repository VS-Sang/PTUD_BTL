package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectDB {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanHang;"
                    + "encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    private ConnectDB() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Không tìm thấy SQL Server JDBC Driver.", exception);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
