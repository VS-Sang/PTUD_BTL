package DAO;

import connectDB.ConnectDB;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapKho_DAO {

    public String sinhMaPhieuNhap() {
        String sql = "SELECT TOP 1 maPhieuNhap FROM PhieuNhapKho ORDER BY maPhieuNhap DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastMa = rs.getString("maPhieuNhap");
                int num = Integer.parseInt(lastMa.replaceAll("[^0-9]", "")) + 1;
                return String.format("PN%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PN001";
    }

    public boolean themPhieuNhap(PhieuNhapKho pn, List<ChiTietPhieuNhap> chiTietList) throws SQLException {
        if (pn == null || chiTietList == null || chiTietList.isEmpty()) {
            throw new IllegalArgumentException("Thông tin phiếu nhập không hợp lệ.");
        }

        String sqlPN = "INSERT INTO PhieuNhapKho (maPhieuNhap, maNhaCungCap, maNhanVien, ngayNhap, "
                + "tongSoLuong, tongTienNhap, trangThai, ghiChu, ngayTao, ngayCapNhat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        String sqlCT = "INSERT INTO ChiTietPhieuNhap (maPhieuNhap, maSanPham, soLuongNhap, donGiaNhap, "
                + "thanhTien, ngaySanXuat, hanSuDung, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Check if database trigger trg_ChiTietPhieuNhap_Insert is enabled
        boolean triggerActive = false;
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement psCheck = con.prepareStatement(
                     "SELECT is_disabled FROM sys.triggers WHERE name = 'trg_ChiTietPhieuNhap_Insert'")) {
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    triggerActive = !rs.getBoolean("is_disabled");
                }
            }
        } catch (Exception ignored) {}

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. Insert PhieuNhapKho
                try (PreparedStatement ps = con.prepareStatement(sqlPN)) {
                    ps.setString(1, pn.getMaPhieuNhap());
                    ps.setString(2, pn.getNhaCungCap() != null ? pn.getNhaCungCap().getMaNhaCungCap() : null);
                    ps.setString(3, pn.getNhanVien() != null ? pn.getNhanVien().getMaNhanVien() : null);
                    ps.setTimestamp(4, pn.getNgayNhap() != null ? new Timestamp(pn.getNgayNhap().getTime()) : new Timestamp(System.currentTimeMillis()));
                    ps.setInt(5, pn.getTongSoLuong());
                    ps.setDouble(6, pn.getTongTienNhap());
                    ps.setString(7, pn.getTrangThai() != null ? pn.getTrangThai() : "Đã nhập");
                    ps.setString(8, pn.getGhiChu());
                    ps.executeUpdate();
                }

                // 2. Insert ChiTietPhieuNhap
                try (PreparedStatement ps = con.prepareStatement(sqlCT)) {
                    for (ChiTietPhieuNhap ct : chiTietList) {
                        ps.setString(1, pn.getMaPhieuNhap());
                        ps.setString(2, ct.getSanPham().getMaSanPham());
                        ps.setInt(3, ct.getSoLuongNhap());
                        ps.setDouble(4, ct.getDonGiaNhap());
                        ps.setDouble(5, ct.getThanhTien());
                        if (ct.getNgaySanXuat() != null) {
                            ps.setDate(6, new java.sql.Date(ct.getNgaySanXuat().getTime()));
                        } else {
                            ps.setNull(6, Types.DATE);
                        }
                        if (ct.getHanSuDung() != null) {
                            ps.setDate(7, new java.sql.Date(ct.getHanSuDung().getTime()));
                        } else {
                            ps.setNull(7, Types.DATE);
                        }
                        ps.setString(8, ct.getGhiChu());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // 3. Cập nhật tồn kho nếu trigger không kích hoạt
                if (!triggerActive) {
                    String sqlUpdateStock = "UPDATE SanPham SET soLuongTon = soLuongTon + ?, ngayCapNhat = GETDATE() WHERE maSanPham = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdateStock)) {
                        for (ChiTietPhieuNhap ct : chiTietList) {
                            ps.setInt(1, ct.getSoLuongNhap());
                            ps.setString(2, ct.getSanPham().getMaSanPham());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
