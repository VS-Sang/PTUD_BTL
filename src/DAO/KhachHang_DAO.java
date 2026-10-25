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

    public boolean themKhachHang(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KhachHang (maKhachHang, hoTen, gioiTinh, ngaySinh, "
                + "soDienThoai, email, diaChi, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection()) {
            kh.setMaKhachHang(phatSinhMaKhachHang(con));
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, kh.getMaKhachHang());
                stmt.setString(2, kh.getHoTen());
                stmt.setString(3, kh.getGioiTinh());
                if (kh.getNgaySinh() == null) {
                    stmt.setNull(4, java.sql.Types.DATE);
                } else {
                    stmt.setDate(4, new java.sql.Date(kh.getNgaySinh().getTime()));
                }
                stmt.setString(5, kh.getSoDienThoai());
                stmt.setString(6, kh.getEmail());
                stmt.setString(7, kh.getDiaChi());
                stmt.setString(8, kh.getGhiChu());
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public String phatSinhMaKhachHang() throws SQLException {
        try (Connection con = ConnectDB.getConnection()) {
            return phatSinhMaKhachHang(con);
        }
    }

    private String phatSinhMaKhachHang(Connection con) throws SQLException {
        String sql = "SELECT maKhachHang FROM KhachHang WHERE maKhachHang LIKE N'KH%'";
        int max = 0;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ma = rs.getString("maKhachHang");
                if (ma != null && ma.matches("KH\\d+")) {
                    max = Math.max(max, Integer.parseInt(ma.substring(2)));
                }
            }
        }
        return String.format(java.util.Locale.ROOT, "KH%03d", max + 1);
    }
}
