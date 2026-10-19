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

    // Lấy toàn bộ danh sách phân công ca làm
    public ArrayList<PhanCongCaLam> getAllPhanCongCaLam() {
        ArrayList<PhanCongCaLam> dsPhanCong = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM PhanCongCaLam";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String maNV = rs.getString("maNhanVien");
                String maCa = rs.getString("maCa");
                String maNguoiPC = rs.getString("nguoiPhanCong");

                PhanCongCaLam pc = new PhanCongCaLam(
                        rs.getString("maPhanCong"),
                        maNV != null ? new NhanVien(maNV) : null,
                        maCa != null ? new CaLamViec(maCa) : null,
                        rs.getDate("ngayLamViec"),
                        rs.getString("trangThai"),
                        rs.getTime("gioDiemDanhVao"),
                        rs.getTime("gioDiemDanhRa"),
                        rs.getDouble("soGioLamThem"),
                        rs.getString("ghiChu"),
                        maNguoiPC != null ? new NhanVien(maNguoiPC) : null,
                        rs.getTimestamp("ngayTao"),
                        rs.getTimestamp("ngayCapNhat")
                );
                dsPhanCong.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }

    // Thêm phân công ca làm mới
    public boolean themPhanCongCaLam(PhanCongCaLam pc) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "INSERT INTO PhanCongCaLam (maPhanCong, maNhanVien, maCa, ngayLamViec, trangThai, gioDiemDanhVao, gioDiemDanhRa, soGioLamThem, ghiChu, nguoiPhanCong) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, pc.getMaPhanCong());
            stmt.setString(2, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            stmt.setString(3, pc.getCaLamViec() != null ? pc.getCaLamViec().getMaCa() : null);
            stmt.setDate(4, pc.getNgayLamViec() != null ? new Date(pc.getNgayLamViec().getTime()) : null);
            stmt.setString(5, pc.getTrangThai());
            stmt.setTime(6, pc.getGioDiemDanhVao() != null ? new Time(pc.getGioDiemDanhVao().getTime()) : null);
            stmt.setTime(7, pc.getGioDiemDanhRa() != null ? new Time(pc.getGioDiemDanhRa().getTime()) : null);
            stmt.setDouble(8, pc.getSoGioLamThem());
            stmt.setString(9, pc.getGhiChu());
            stmt.setString(10, pc.getNguoiPhanCong() != null ? pc.getNguoiPhanCong().getMaNhanVien() : null);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Cập nhật phân công ca làm
    public boolean capNhatPhanCongCaLam(PhanCongCaLam pc) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE PhanCongCaLam SET maNhanVien = ?, maCa = ?, ngayLamViec = ?, trangThai = ?, gioDiemDanhVao = ?, gioDiemDanhRa = ?, soGioLamThem = ?, ghiChu = ?, nguoiPhanCong = ? WHERE maPhanCong = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            stmt.setString(2, pc.getCaLamViec() != null ? pc.getCaLamViec().getMaCa() : null);
            stmt.setDate(3, pc.getNgayLamViec() != null ? new Date(pc.getNgayLamViec().getTime()) : null);
            stmt.setString(4, pc.getTrangThai());
            stmt.setTime(5, pc.getGioDiemDanhVao() != null ? new Time(pc.getGioDiemDanhVao().getTime()) : null);
            stmt.setTime(6, pc.getGioDiemDanhRa() != null ? new Time(pc.getGioDiemDanhRa().getTime()) : null);
            stmt.setDouble(7, pc.getSoGioLamThem());
            stmt.setString(8, pc.getGhiChu());
            stmt.setString(9, pc.getNguoiPhanCong() != null ? pc.getNguoiPhanCong().getMaNhanVien() : null);
            stmt.setString(10, pc.getMaPhanCong());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Xóa phân công ca làm theo mã
    public boolean xoaPhanCongCaLam(String maPhanCong) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "DELETE FROM PhanCongCaLam WHERE maPhanCong = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhanCong);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Tìm kiếm phân công ca làm theo mã phân công, mã nhân viên hoặc mã ca
    public ArrayList<PhanCongCaLam> timKiemPhanCongCaLam(String tuKhoa) {
        ArrayList<PhanCongCaLam> dsPhanCong = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM PhanCongCaLam WHERE maPhanCong LIKE ? OR maNhanVien LIKE ? OR maCa LIKE ?";
            stmt = con.prepareStatement(sql);
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String maNV = rs.getString("maNhanVien");
                String maCa = rs.getString("maCa");
                String maNguoiPC = rs.getString("nguoiPhanCong");

                PhanCongCaLam pc = new PhanCongCaLam(
                        rs.getString("maPhanCong"),
                        maNV != null ? new NhanVien(maNV) : null,
                        maCa != null ? new CaLamViec(maCa) : null,
                        rs.getDate("ngayLamViec"),
                        rs.getString("trangThai"),
                        rs.getTime("gioDiemDanhVao"),
                        rs.getTime("gioDiemDanhRa"),
                        rs.getDouble("soGioLamThem"),
                        rs.getString("ghiChu"),
                        maNguoiPC != null ? new NhanVien(maNguoiPC) : null,
                        rs.getTimestamp("ngayTao"),
                        rs.getTimestamp("ngayCapNhat")
                );
                dsPhanCong.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }
}
