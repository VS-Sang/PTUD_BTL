package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCaLam;

public class PhanCongCaLam_DAO {

    public ArrayList<PhanCongCaLam> getAllPhanCongCaLam() {
        ArrayList<PhanCongCaLam> ds = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen, ca.tenCa, ca.gioVao, ca.gioRa "
                + "FROM PhanCongCaLam pc "
                + "JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien "
                + "JOIN CaLamViec ca ON pc.maCa = ca.maCa "
                + "ORDER BY pc.ngayLamViec DESC, pc.maPhanCong ASC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
                nv.setHoTen(rs.getString("hoTen"));

                CaLamViec ca = new CaLamViec(rs.getString("maCa"));
                ca.setTenCa(rs.getString("tenCa"));
                ca.setGioVao(rs.getTime("gioVao"));
                ca.setGioRa(rs.getTime("gioRa"));

                String maNguoiPC = rs.getString("nguoiPhanCong");
                ds.add(new PhanCongCaLam(
                        rs.getString("maPhanCong"),
                        nv,
                        ca,
                        rs.getDate("ngayLamViec"),
                        rs.getString("trangThai"),
                        rs.getString("ghiChu"),
                        maNguoiPC != null ? new NhanVien(maNguoiPC) : null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public String phatSinhMaPhanCong() {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maPhanCong, 3, 18))), 0) + 1 "
                + "FROM PhanCongCaLam WHERE maPhanCong LIKE 'PC[0-9]%'";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return String.format("PC%03d", rs.getInt(1));
            }
        } catch (Exception ignored) {
        }
        return "PC001";
    }

    public String getMaNhanVienTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return "NV001";
        }
        String sql = "SELECT maNhanVien FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getString("maNhanVien") != null) {
                    return rs.getString("maNhanVien").trim();
                }
            }
        } catch (SQLException ignored) {
        }
        return "NV001";
    }

    public boolean themPhanCongCaLam(PhanCongCaLam pc) {
        String sql = "INSERT INTO PhanCongCaLam (maPhanCong, maNhanVien, maCa, ngayLamViec, trangThai, ghiChu, nguoiPhanCong) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, pc.getMaPhanCong());
            stmt.setString(2, pc.getNhanVien().getMaNhanVien());
            stmt.setString(3, pc.getCaLamViec().getMaCa());
            stmt.setDate(4, new Date(pc.getNgayLamViec().getTime()));
            stmt.setString(5, pc.getTrangThai());
            stmt.setString(6, pc.getGhiChu());
            stmt.setString(7, pc.getNguoiPhanCong() != null ? pc.getNguoiPhanCong().getMaNhanVien() : "NV001");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean capNhatPhanCongCaLam(PhanCongCaLam pc) {
        String sql = "UPDATE PhanCongCaLam SET maNhanVien = ?, maCa = ?, ngayLamViec = ?, trangThai = ?, "
                + "ghiChu = ?, ngayCapNhat = GETDATE() WHERE maPhanCong = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, pc.getNhanVien().getMaNhanVien());
            stmt.setString(2, pc.getCaLamViec().getMaCa());
            stmt.setDate(3, new Date(pc.getNgayLamViec().getTime()));
            stmt.setString(4, pc.getTrangThai());
            stmt.setString(5, pc.getGhiChu());
            stmt.setString(6, pc.getMaPhanCong());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean xoaPhanCongCaLam(String maPhanCong) {
        String sql = "DELETE FROM PhanCongCaLam WHERE maPhanCong = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhanCong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
