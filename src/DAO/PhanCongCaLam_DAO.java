package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCaLam;

public class PhanCongCaLam_DAO {

    private PhanCongCaLam docPhanCongTuResultSet(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
        nv.setHoTen(rs.getString("hoTen"));

        CaLamViec ca = new CaLamViec(rs.getString("maCa"));
        ca.setTenCa(rs.getString("tenCa"));
        ca.setGioVao(rs.getTime("gioVao"));
        ca.setGioRa(rs.getTime("gioRa"));

        String maNguoiPC = rs.getString("nguoiPhanCong");
        NhanVien nguoiPC = (maNguoiPC != null) ? new NhanVien(maNguoiPC) : null;

        return new PhanCongCaLam(
                rs.getString("maPhanCong"),
                nv,
                ca,
                rs.getDate("ngayLamViec"),
                rs.getString("trangThai"),
                rs.getTime("gioDiemDanhVao"),
                rs.getTime("gioDiemDanhRa"),
                rs.getDouble("soGioLamThem"),
                rs.getString("ghiChu"),
                nguoiPC,
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    // Lấy toàn bộ danh sách phân công ca làm (kèm họ tên NV và tên ca)
    public ArrayList<PhanCongCaLam> getAllPhanCongCaLam() {
        ArrayList<PhanCongCaLam> dsPhanCong = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen, ca.tenCa, ca.gioVao, ca.gioRa "
                + "FROM PhanCongCaLam pc "
                + "JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien "
                + "JOIN CaLamViec ca ON pc.maCa = ca.maCa "
                + "ORDER BY pc.ngayLamViec DESC, pc.maPhanCong ASC";
        try (Connection con = ConnectDB.getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                dsPhanCong.add(docPhanCongTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }

    // Lấy danh sách phân công ca làm của 1 nhân viên
    public ArrayList<PhanCongCaLam> getPhanCongTheoNhanVien(String maNhanVien) {
        ArrayList<PhanCongCaLam> dsPhanCong = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen, ca.tenCa, ca.gioVao, ca.gioRa "
                + "FROM PhanCongCaLam pc "
                + "JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien "
                + "JOIN CaLamViec ca ON pc.maCa = ca.maCa "
                + "WHERE pc.maNhanVien = ? "
                + "ORDER BY pc.ngayLamViec ASC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsPhanCong.add(docPhanCongTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }

    // Phát sinh mã phân công ca làm tiếp theo (VD: PC013)
    public String phatSinhMaPhanCong() {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maPhanCong, 3, 18))), 0) + 1 "
                + "FROM PhanCongCaLam WHERE maPhanCong LIKE 'PC[0-9]%'";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return String.format("PC%03d", rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PC001";
    }

    // Lấy mã nhân viên theo tên đăng nhập để gán người phân công
    public String getMaNhanVienTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return "NV001";
        }
        String sql = "SELECT maNhanVien FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maNV = rs.getString("maNhanVien");
                    if (maNV != null && !maNV.trim().isEmpty()) {
                        return maNV.trim();
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return "NV001";
    }

    // Thêm phân công ca làm mới
    public boolean themPhanCongCaLam(PhanCongCaLam pc) {
        String sql = "INSERT INTO PhanCongCaLam (maPhanCong, maNhanVien, maCa, ngayLamViec, trangThai, gioDiemDanhVao, gioDiemDanhRa, soGioLamThem, ghiChu, nguoiPhanCong) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, pc.getMaPhanCong());
            stmt.setString(2, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            stmt.setString(3, pc.getCaLamViec() != null ? pc.getCaLamViec().getMaCa() : null);
            stmt.setDate(4, pc.getNgayLamViec() != null ? new Date(pc.getNgayLamViec().getTime()) : null);
            stmt.setString(5, pc.getTrangThai());
            stmt.setTime(6, pc.getGioDiemDanhVao() != null ? new Time(pc.getGioDiemDanhVao().getTime()) : null);
            stmt.setTime(7, pc.getGioDiemDanhRa() != null ? new Time(pc.getGioDiemDanhRa().getTime()) : null);
            stmt.setDouble(8, pc.getSoGioLamThem());
            stmt.setString(9, pc.getGhiChu());
            stmt.setString(10, pc.getNguoiPhanCong() != null ? pc.getNguoiPhanCong().getMaNhanVien() : "NV001");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Cập nhật phân công ca làm
    public boolean capNhatPhanCongCaLam(PhanCongCaLam pc) {
        String sql = "UPDATE PhanCongCaLam SET maNhanVien = ?, maCa = ?, ngayLamViec = ?, trangThai = ?, ghiChu = ?, ngayCapNhat = GETDATE() WHERE maPhanCong = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            stmt.setString(2, pc.getCaLamViec() != null ? pc.getCaLamViec().getMaCa() : null);
            stmt.setDate(3, pc.getNgayLamViec() != null ? new Date(pc.getNgayLamViec().getTime()) : null);
            stmt.setString(4, pc.getTrangThai());
            stmt.setString(5, pc.getGhiChu());
            stmt.setString(6, pc.getMaPhanCong());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Xóa phân công ca làm theo mã
    public boolean xoaPhanCongCaLam(String maPhanCong) {
        String sql = "DELETE FROM PhanCongCaLam WHERE maPhanCong = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhanCong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm phân công ca làm theo từ khóa
    public ArrayList<PhanCongCaLam> timKiemPhanCongCaLam(String tuKhoa) {
        ArrayList<PhanCongCaLam> dsPhanCong = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen, ca.tenCa, ca.gioVao, ca.gioRa "
                + "FROM PhanCongCaLam pc "
                + "JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien "
                + "JOIN CaLamViec ca ON pc.maCa = ca.maCa "
                + "WHERE pc.maPhanCong LIKE ? OR pc.maNhanVien LIKE ? OR nv.hoTen LIKE ? OR pc.maCa LIKE ? OR ca.tenCa LIKE ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, key);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsPhanCong.add(docPhanCongTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }
}
