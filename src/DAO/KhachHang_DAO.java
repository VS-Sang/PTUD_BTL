package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHang_DAO {

    private KhachHang docKhachHangTuResultSet(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKhachHang"),
                rs.getString("hoTen"),
                rs.getString("gioiTinh"),
                rs.getDate("ngaySinh"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                rs.getString("diaChi"),
                rs.getString("loaiKhachHang"),
                rs.getInt("diemThuong"),
                rs.getInt("tongDiemTichLuy"),
                rs.getDouble("tongChiTieu"),
                rs.getInt("soLanMuaHang"),
                rs.getTimestamp("lanMuaGanNhat"),
                rs.getString("trangThai"),
                rs.getString("ghiChu"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    public ArrayList<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY maKhachHang";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docKhachHangTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<KhachHang> timKiemKhachHang(String tuKhoa) {
        ArrayList<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? ORDER BY maKhachHang";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(docKhachHangTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
