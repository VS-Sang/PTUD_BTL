package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    private NhanVien docNhanVienTuResultSet(ResultSet rs) throws SQLException {
        String maNQL = rs.getString("maNguoiQuanLy");
        NhanVien nguoiQuanLy = (maNQL != null) ? new NhanVien(maNQL) : null;

        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("hoTen"),
                rs.getString("gioiTinh"),
                rs.getDate("ngaySinh"),
                rs.getString("soCMND"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                rs.getString("diaChi"),
                rs.getString("hinhAnh"),
                rs.getString("chucVu"),
                rs.getString("phongBan"),
                rs.getDouble("luongCoBan"),
                rs.getDouble("heSoLuong"),
                rs.getDouble("phuCap"),
                rs.getDate("ngayVaoLam"),
                rs.getDate("ngayNghiViec"),
                rs.getString("trangThai"),
                nguoiQuanLy,
                rs.getString("ghiChu"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    // Lấy toàn bộ danh sách nhân viên
    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNhanVien";
        try (Connection con = ConnectDB.getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                dsNhanVien.add(docNhanVienTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }

    // Lấy nhân viên theo mã
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

    // Phát sinh mã nhân viên tiếp theo (VD: NV011)
    public String phatSinhMaNhanVien() {
        String sql = "SELECT TOP 1 maNhanVien FROM NhanVien ORDER BY maNhanVien DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("maNhanVien"); // VD: NV010
                if (lastId != null && lastId.startsWith("NV")) {
                    int num = Integer.parseInt(lastId.substring(2)) + 1;
                    return String.format("NV%03d", num);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NV001";
    }

    // Thêm nhân viên mới
    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, ngaySinh, soCMND, soDienThoai, email, diaChi, chucVu, phongBan, luongCoBan, heSoLuong, phuCap, ngayVaoLam, trangThai) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String cmnd = nv.getSoCMND();
            if (cmnd == null || cmnd.trim().isEmpty()) {
                // Tạo số CCCD/CMND mặc định từ SĐT nếu form không nhập
                cmnd = "079" + nv.getSoDienThoai().replaceAll("\\D", "");
                if (cmnd.length() > 12) {
                    cmnd = cmnd.substring(0, 12);
                }
            }
            Date ngaySinh = nv.getNgaySinh() != null
                    ? new Date(nv.getNgaySinh().getTime())
                    : Date.valueOf("1995-01-01");
            Date ngayVaoLam = nv.getNgayVaoLam() != null
                    ? new Date(nv.getNgayVaoLam().getTime())
                    : new Date(System.currentTimeMillis());

            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getHoTen());
            stmt.setString(3, nv.getGioiTinh());
            stmt.setDate(4, ngaySinh);
            stmt.setString(5, cmnd);
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getEmail() != null && !nv.getEmail().trim().isEmpty() ? nv.getEmail().trim() : null);
            stmt.setString(8, nv.getDiaChi());
            stmt.setString(9, nv.getChucVu());
            stmt.setString(10, nv.getPhongBan());
            stmt.setDouble(11, nv.getLuongCoBan());
            stmt.setDouble(12, nv.getHeSoLuong() > 0 ? nv.getHeSoLuong() : 1.0);
            stmt.setDouble(13, nv.getPhuCap() >= 0 ? nv.getPhuCap() : 0);
            stmt.setDate(14, ngayVaoLam);
            stmt.setString(15, nv.getTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin nhân viên
    public boolean capNhatNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET hoTen = ?, gioiTinh = ?, ngaySinh = ?, soDienThoai = ?, email = ?, diaChi = ?, chucVu = ?, phongBan = ?, luongCoBan = ?, trangThai = ? WHERE maNhanVien = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getGioiTinh());
            stmt.setDate(3, nv.getNgaySinh() != null ? new Date(nv.getNgaySinh().getTime()) : Date.valueOf("1995-01-01"));
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getEmail() != null && !nv.getEmail().trim().isEmpty() ? nv.getEmail().trim() : null);
            stmt.setString(6, nv.getDiaChi());
            stmt.setString(7, nv.getChucVu());
            stmt.setString(8, nv.getPhongBan());
            stmt.setDouble(9, nv.getLuongCoBan());
            stmt.setString(10, nv.getTrangThai());
            stmt.setString(11, nv.getMaNhanVien());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhân viên theo mã
    public boolean xoaNhanVien(String maNhanVien) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm nhân viên theo từ khóa (mã, tên, SĐT)
    public ArrayList<NhanVien> timKiemNhanVien(String tuKhoa) {
        ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? ORDER BY maNhanVien";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsNhanVien.add(docNhanVienTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }
}
