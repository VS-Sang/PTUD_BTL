package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DanhMuc;
import entity.SanPham;

public class SanPham_DAO {

    private SanPham docSanPhamTuResultSet(ResultSet rs) throws SQLException {
        DanhMuc dm = new DanhMuc(rs.getString("maDanhMuc"));
        try {
            dm.setTenDanhMuc(rs.getString("tenDanhMuc"));
        } catch (Exception ignored) {}

        return new SanPham(
                rs.getString("maSanPham"),
                rs.getString("tenSanPham"),
                dm,
                rs.getString("moTa"),
                rs.getString("thuongHieu"),
                rs.getString("xuatXu"),
                rs.getString("donViTinh"),
                rs.getDouble("giaNhap"),
                rs.getDouble("giaBan"),
                rs.getInt("soLuongTon"),
                rs.getInt("soLuongToiThieu"),
                rs.getString("maSKU"),
                rs.getString("maVach"),
                rs.getDouble("trongLuong"),
                rs.getString("kichThuoc"),
                rs.getString("hinhAnh"),
                rs.getString("trangThai"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc "
                + "ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docSanPhamTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<SanPham> timKiemSanPham(String tuKhoa) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc "
                + "WHERE sp.maSanPham LIKE ? OR sp.tenSanPham LIKE ? OR sp.thuongHieu LIKE ? "
                + "ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(docSanPhamTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
