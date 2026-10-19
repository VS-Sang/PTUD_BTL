package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.CaLamViec;

public class CaLamViec_DAO {

    // Lấy toàn bộ danh sách ca làm việc
    public ArrayList<CaLamViec> getAllCaLamViec() {
        ArrayList<CaLamViec> dsCaLam = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM CaLamViec";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                CaLamViec ca = new CaLamViec(
                        rs.getString("maCa"),
                        rs.getString("tenCa"),
                        rs.getTime("gioVao"),
                        rs.getTime("gioRa"),
                        rs.getString("moTa"),
                        rs.getString("trangThai"),
                        rs.getTimestamp("ngayTao")
                );
                dsCaLam.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCaLam;
    }

    // Thêm ca làm việc mới
    public boolean themCaLamViec(CaLamViec ca) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "INSERT INTO CaLamViec (maCa, tenCa, gioVao, gioRa, moTa, trangThai) VALUES(?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, ca.getMaCa());
            stmt.setString(2, ca.getTenCa());
            stmt.setTime(3, ca.getGioVao() != null ? new Time(ca.getGioVao().getTime()) : null);
            stmt.setTime(4, ca.getGioRa() != null ? new Time(ca.getGioRa().getTime()) : null);
            stmt.setString(5, ca.getMoTa());
            stmt.setString(6, ca.getTrangThai());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Cập nhật thông tin ca làm việc
    public boolean capNhatCaLamViec(CaLamViec ca) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "UPDATE CaLamViec SET tenCa = ?, gioVao = ?, gioRa = ?, moTa = ?, trangThai = ? WHERE maCa = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, ca.getTenCa());
            stmt.setTime(2, ca.getGioVao() != null ? new Time(ca.getGioVao().getTime()) : null);
            stmt.setTime(3, ca.getGioRa() != null ? new Time(ca.getGioRa().getTime()) : null);
            stmt.setString(4, ca.getMoTa());
            stmt.setString(5, ca.getTrangThai());
            stmt.setString(6, ca.getMaCa());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Xóa ca làm việc theo mã
    public boolean xoaCaLamViec(String maCa) {
        PreparedStatement stmt = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "DELETE FROM CaLamViec WHERE maCa = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maCa);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Tìm kiếm ca làm việc theo từ khóa (mã ca, tên ca)
    public ArrayList<CaLamViec> timKiemCaLamViec(String tuKhoa) {
        ArrayList<CaLamViec> dsCaLam = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM CaLamViec WHERE maCa LIKE ? OR tenCa LIKE ?";
            stmt = con.prepareStatement(sql);
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CaLamViec ca = new CaLamViec(
                        rs.getString("maCa"),
                        rs.getString("tenCa"),
                        rs.getTime("gioVao"),
                        rs.getTime("gioRa"),
                        rs.getString("moTa"),
                        rs.getString("trangThai"),
                        rs.getTimestamp("ngayTao")
                );
                dsCaLam.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCaLam;
    }
}
