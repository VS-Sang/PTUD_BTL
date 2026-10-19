package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DonHang;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {

    private HoaDon docHoaDonTuResultSet(ResultSet rs) throws SQLException {
        String maDH = rs.getString("maDonHang");
        DonHang dh = maDH != null ? new DonHang(maDH) : null;

        String maKH = rs.getString("maKhachHang");
        KhachHang kh = null;
        if (maKH != null) {
            kh = new KhachHang(maKH);
            try {
                kh.setHoTen(rs.getString("tenKhachHang"));
            } catch (Exception ignored) {}
        }

        String maNV = rs.getString("maNhanVien");
        NhanVien nv = null;
        if (maNV != null) {
            nv = new NhanVien(maNV);
            try {
                nv.setHoTen(rs.getString("tenNhanVien"));
            } catch (Exception ignored) {}
        }

        return new HoaDon(
                rs.getString("maHoaDon"),
                dh,
                kh,
                nv,
                rs.getTimestamp("ngayLapHoaDon"),
                rs.getDouble("tongTienHang"),
                rs.getDouble("tienGiamGia"),
                rs.getDouble("tienDiem"),
                rs.getDouble("thueVAT"),
                rs.getDouble("tongThanhToan"),
                rs.getDouble("tienKhachDua"),
                rs.getDouble("tienTraLai"),
                rs.getString("phuongThucThanhToan"),
                rs.getString("soTaiKhoanNH"),
                rs.getString("tenNganHang"),
                rs.getString("maGiaoDich"),
                rs.getInt("diemCongThem"),
                rs.getString("trangThai"),
                rs.getString("ghiChu"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    public ArrayList<HoaDon> getAllHoaDon() {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT hd.*, kh.hoTen AS tenKhachHang, nv.hoTen AS tenNhanVien "
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang "
                + "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien "
                + "ORDER BY hd.ngayLapHoaDon DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docHoaDonTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
