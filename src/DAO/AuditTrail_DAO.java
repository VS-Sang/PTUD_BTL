package DAO;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AuditTrail_DAO {
    private AuditTrail_DAO() {
    }

    public static void ghi(Connection connection, String maTaiKhoan, String hanhDong,
                           String doiTuong, String chiTiet) throws SQLException {
        String sql = "INSERT INTO AuditTrail (maTaiKhoan, hanhDong, doiTuong, chiTiet, "
                + "diaChiIP, tenMay) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maTaiKhoan);
            statement.setString(2, hanhDong);
            statement.setString(3, doiTuong);
            statement.setString(4, chiTiet);
            statement.setString(5, null);
            statement.setString(6, java.net.InetAddress.getLocalHost().getHostName());
            statement.executeUpdate();
        } catch (java.net.UnknownHostException exception) {
            throw new SQLException("Không xác định được tên máy để ghi Audit Trail.", exception);
        }
    }

    public static void ghiTheoTenDangNhap(String tenDangNhap, String hanhDong,
                                           String doiTuong, String chiTiet) throws SQLException {
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT maTaiKhoan FROM TaiKhoan WHERE tenDangNhap = ?")) {
            statement.setString(1, tenDangNhap);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ghi(connection, resultSet.getString(1), hanhDong, doiTuong, chiTiet);
                }
            }
        }
    }

    public static boolean coQuyen(String tenDangNhap, String maQuyen, String cotQuyen)
            throws SQLException {
        if (!"coQuyenXem".equals(cotQuyen) && !"coQuyenThem".equals(cotQuyen)
                && !"coQuyenSua".equals(cotQuyen) && !"coQuyenXoa".equals(cotQuyen)) {
            throw new IllegalArgumentException("Loại quyền không hợp lệ.");
        }
        String sql = "SELECT 1 FROM TaiKhoan tk JOIN PhanQuyen pq ON pq.maTaiKhoan = tk.maTaiKhoan "
                + "WHERE tk.tenDangNhap = ? AND pq.maQuyen = ? AND pq." + cotQuyen + " = 1 "
                + "AND tk.trangThai = N'Hoạt động'";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tenDangNhap);
            statement.setString(2, maQuyen);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static void ghiDangXuat(String tenDangNhap) throws SQLException {
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE LichSuDangNhap SET thoiGianDangXuat = GETDATE() "
                             + "WHERE maLichSu = (SELECT MAX(ls.maLichSu) FROM LichSuDangNhap ls "
                             + "JOIN TaiKhoan tk ON tk.maTaiKhoan = ls.maTaiKhoan "
                             + "WHERE tk.tenDangNhap = ? AND ls.ketQua = N'Thành công' "
                             + "AND ls.thoiGianDangXuat IS NULL)")) {
            statement.setString(1, tenDangNhap);
            statement.executeUpdate();
        }
        ghiTheoTenDangNhap(tenDangNhap, "ĐĂNG XUẤT", "Phiên đăng nhập",
                "Đăng xuất khỏi hệ thống");
    }
}
