package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    private NhanVien docNhanVienTuResultSet(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("hoTen"),
                rs.getString("gioiTinh"),
                rs.getDate("ngaySinh"),
                rs.getString("soCMND"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                rs.getString("diaChi"),
                rs.getString("chucVu"),
                rs.getString("phongBan"),
                rs.getDouble("luongCoBan"),
                rs.getString("trangThai")
        );
    }

    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNhanVien";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docNhanVienTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public NhanVien getNhanVienTheoMa(String maNhanVien) {
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return docNhanVienTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<NhanVien> timKiemNhanVien(String tuKhoa) {
        ArrayList<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien LIKE ? OR hoTen LIKE ? OR soCMND LIKE ? OR soDienThoai LIKE ? ORDER BY maNhanVien";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, key);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(docNhanVienTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public String phatSinhMaNhanVien() {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maNhanVien, 3, 18))), 0) + 1 "
                + "FROM NhanVien WHERE maNhanVien LIKE 'NV[0-9]%'";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return String.format("NV%03d", rs.getInt(1));
            }
        } catch (SQLException ignored) {
        }
        return "NV001";
    }

    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, ngaySinh, soCMND, soDienThoai, "
                + "email, diaChi, chucVu, phongBan, luongCoBan, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getHoTen());
            stmt.setString(3, nv.getGioiTinh());
            stmt.setDate(4, new Date(nv.getNgaySinh().getTime()));
            stmt.setString(5, nv.getSoCMND());
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getEmail() != null && !nv.getEmail().isEmpty() ? nv.getEmail() : null);
            stmt.setString(8, nv.getDiaChi());
            stmt.setString(9, nv.getChucVu());
            stmt.setString(10, nv.getPhongBan());
            stmt.setDouble(11, nv.getLuongCoBan());
            stmt.setString(12, nv.getTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET hoTen = ?, gioiTinh = ?, ngaySinh = ?, soCMND = ?, soDienThoai = ?, "
                + "email = ?, diaChi = ?, chucVu = ?, phongBan = ?, luongCoBan = ?, trangThai = ?, "
                + "ngayNghiViec = CASE WHEN ? = N'Đã nghỉ việc' THEN ISNULL(ngayNghiViec, GETDATE()) ELSE NULL END, "
                + "ngayCapNhat = GETDATE() WHERE maNhanVien = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getGioiTinh());
            stmt.setDate(3, new Date(nv.getNgaySinh().getTime()));
            stmt.setString(4, nv.getSoCMND());
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getEmail() != null && !nv.getEmail().isEmpty() ? nv.getEmail() : null);
            stmt.setString(7, nv.getDiaChi());
            stmt.setString(8, nv.getChucVu());
            stmt.setString(9, nv.getPhongBan());
            stmt.setDouble(10, nv.getLuongCoBan());
            stmt.setString(11, nv.getTrangThai());
            stmt.setString(12, nv.getTrangThai());
            stmt.setString(13, nv.getMaNhanVien());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean xoaNhanVien(String maNhanVien) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // --- Quản lý danh mục Phòng ban & Chức vụ ---
    private void khoiTaoBangDanhMuc(Connection con) throws SQLException {
        String sql = "IF OBJECT_ID('PhongBan', 'U') IS NULL BEGIN "
                + "CREATE TABLE PhongBan (tenPhongBan NVARCHAR(50) PRIMARY KEY); "
                + "INSERT INTO PhongBan VALUES (N'Bán hàng'), (N'Kế toán'), (N'Kho'), (N'Quản lý'); END; "
                + "IF OBJECT_ID('ChucVu', 'U') IS NULL BEGIN "
                + "CREATE TABLE ChucVu (tenChucVu NVARCHAR(50) PRIMARY KEY); "
                + "INSERT INTO ChucVu VALUES (N'Nhân viên'), (N'Nhân viên kho'), (N'Quản lý'), (N'Thu ngân'); END;";
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }

    public List<String> layDanhSachDanhMuc(boolean laPhongBan) {
        List<String> ds = new ArrayList<>();
        String sql = laPhongBan ? "SELECT tenPhongBan FROM PhongBan ORDER BY tenPhongBan"
                                : "SELECT tenChucVu FROM ChucVu ORDER BY tenChucVu";
        try (Connection con = ConnectDB.getConnection()) {
            khoiTaoBangDanhMuc(con);
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            return laPhongBan ? Arrays.asList("Bán hàng", "Kế toán", "Kho", "Quản lý")
                              : Arrays.asList("Nhân viên", "Nhân viên kho", "Quản lý", "Thu ngân");
        }
        return ds;
    }

    public boolean themDanhMuc(boolean laPhongBan, String ten) {
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String sql = "INSERT INTO " + bang + " VALUES (?)";
        try (Connection con = ConnectDB.getConnection()) {
            khoiTaoBangDanhMuc(con);
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ten.trim());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean suaDanhMuc(boolean laPhongBan, String tenCu, String tenMoi) {
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String cot = laPhongBan ? "tenPhongBan" : "tenChucVu";
        String cotNV = laPhongBan ? "phongBan" : "chucVu";
        try (Connection con = ConnectDB.getConnection()) {
            khoiTaoBangDanhMuc(con);
            try (PreparedStatement u1 = con.prepareStatement("UPDATE " + bang + " SET " + cot + " = ? WHERE " + cot + " = ?");
                 PreparedStatement u2 = con.prepareStatement("UPDATE NhanVien SET " + cotNV + " = ? WHERE " + cotNV + " = ?")) {
                u1.setString(1, tenMoi.trim());
                u1.setString(2, tenCu.trim());
                if (u1.executeUpdate() <= 0) return false;
                u2.setString(1, tenMoi.trim());
                u2.setString(2, tenCu.trim());
                u2.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean xoaDanhMuc(boolean laPhongBan, String ten) {
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String cot = laPhongBan ? "tenPhongBan" : "tenChucVu";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement("DELETE FROM " + bang + " WHERE " + cot + " = ?")) {
            stmt.setString(1, ten.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
