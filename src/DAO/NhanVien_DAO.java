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

    // Lấy toàn bộ danh sách nhân viên
    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String maNQL = rs.getString("maNguoiQuanLy");
                NhanVien nguoiQuanLy = (maNQL != null) ? new NhanVien(maNQL) : null;

                NhanVien nv = new NhanVien(
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
                dsNhanVien.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }

    // Thêm nhân viên mới
    public boolean themNhanVien(NhanVien nv) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, ngaySinh, soCMND, soDienThoai, email, diaChi, chucVu, phongBan, luongCoBan, heSoLuong, phuCap, ngayVaoLam, trangThai) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getHoTen());
            stmt.setString(3, nv.getGioiTinh());
            stmt.setDate(4, nv.getNgaySinh() != null ? new Date(nv.getNgaySinh().getTime()) : null);
            stmt.setString(5, nv.getSoCMND());
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getEmail());
            stmt.setString(8, nv.getDiaChi());
            stmt.setString(9, nv.getChucVu());
            stmt.setString(10, nv.getPhongBan());
            stmt.setDouble(11, nv.getLuongCoBan());
            stmt.setDouble(12, nv.getHeSoLuong());
            stmt.setDouble(13, nv.getPhuCap());
            stmt.setDate(14, nv.getNgayVaoLam() != null ? new Date(nv.getNgayVaoLam().getTime()) : null);
            stmt.setString(15, nv.getTrangThai());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Cập nhật thông tin nhân viên
    public boolean capNhatNhanVien(NhanVien nv) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE NhanVien SET hoTen = ?, gioiTinh = ?, ngaySinh = ?, soCMND = ?, soDienThoai = ?, email = ?, diaChi = ?, chucVu = ?, phongBan = ?, luongCoBan = ?, heSoLuong = ?, phuCap = ?, ngayVaoLam = ?, trangThai = ? WHERE maNhanVien = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getGioiTinh());
            stmt.setDate(3, nv.getNgaySinh() != null ? new Date(nv.getNgaySinh().getTime()) : null);
            stmt.setString(4, nv.getSoCMND());
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getEmail());
            stmt.setString(7, nv.getDiaChi());
            stmt.setString(8, nv.getChucVu());
            stmt.setString(9, nv.getPhongBan());
            stmt.setDouble(10, nv.getLuongCoBan());
            stmt.setDouble(11, nv.getHeSoLuong());
            stmt.setDouble(12, nv.getPhuCap());
            stmt.setDate(13, nv.getNgayVaoLam() != null ? new Date(nv.getNgayVaoLam().getTime()) : null);
            stmt.setString(14, nv.getTrangThai());
            stmt.setString(15, nv.getMaNhanVien());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Xóa nhân viên theo mã
    public boolean xoaNhanVien(String maNhanVien) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maNhanVien);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Tìm kiếm nhân viên theo từ khóa (mã, tên, SĐT)
    public ArrayList<NhanVien> timKiemNhanVien(String tuKhoa) {
        ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien WHERE maNhanVien LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ?";
            stmt = con.prepareStatement(sql);
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String maNQL = rs.getString("maNguoiQuanLy");
                NhanVien nguoiQuanLy = (maNQL != null) ? new NhanVien(maNQL) : null;

                NhanVien nv = new NhanVien(
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
                dsNhanVien.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }
}
