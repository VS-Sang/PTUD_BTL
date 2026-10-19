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

    private CaLamViec docCaLamViecTuResultSet(ResultSet rs) throws SQLException {
        return new CaLamViec(
                rs.getString("maCa"),
                rs.getString("tenCa"),
                rs.getTime("gioVao"),
                rs.getTime("gioRa"),
                rs.getString("moTa"),
                rs.getString("trangThai"),
                rs.getTimestamp("ngayTao")
        );
    }

    // Lấy toàn bộ danh sách ca làm việc
    public ArrayList<CaLamViec> getAllCaLamViec() {
        ArrayList<CaLamViec> dsCaLam = new ArrayList<>();
        String sql = "SELECT * FROM CaLamViec ORDER BY maCa";
        try (Connection con = ConnectDB.getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                dsCaLam.add(docCaLamViecTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCaLam;
    }

    // Thêm ca làm việc mới
    public boolean themCaLamViec(CaLamViec ca) {
        String sql = "INSERT INTO CaLamViec (maCa, tenCa, gioVao, gioRa, moTa, trangThai) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getMaCa());
            stmt.setString(2, ca.getTenCa());
            stmt.setTime(3, ca.getGioVao() != null ? new Time(ca.getGioVao().getTime()) : null);
            stmt.setTime(4, ca.getGioRa() != null ? new Time(ca.getGioRa().getTime()) : null);
            stmt.setString(5, ca.getMoTa());
            stmt.setString(6, ca.getTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin ca làm việc
    public boolean capNhatCaLamViec(CaLamViec ca) {
        String sql = "UPDATE CaLamViec SET tenCa = ?, gioVao = ?, gioRa = ?, moTa = ?, trangThai = ? WHERE maCa = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getTenCa());
            stmt.setTime(2, ca.getGioVao() != null ? new Time(ca.getGioVao().getTime()) : null);
            stmt.setTime(3, ca.getGioRa() != null ? new Time(ca.getGioRa().getTime()) : null);
            stmt.setString(4, ca.getMoTa());
            stmt.setString(5, ca.getTrangThai());
            stmt.setString(6, ca.getMaCa());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa ca làm việc theo mã
    public boolean xoaCaLamViec(String maCa) {
        String sql = "DELETE FROM CaLamViec WHERE maCa = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maCa);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm ca làm việc theo từ khóa (mã ca, tên ca)
    public ArrayList<CaLamViec> timKiemCaLamViec(String tuKhoa) {
        ArrayList<CaLamViec> dsCaLam = new ArrayList<>();
        String sql = "SELECT * FROM CaLamViec WHERE maCa LIKE ? OR tenCa LIKE ? ORDER BY maCa";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsCaLam.add(docCaLamViecTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCaLam;
    }
}
