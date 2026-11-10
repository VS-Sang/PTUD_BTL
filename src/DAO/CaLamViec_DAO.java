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

    public ArrayList<CaLamViec> getAllCaLamViec() {
        ArrayList<CaLamViec> ds = new ArrayList<>();
        String sql = "SELECT * FROM CaLamViec ORDER BY maCa";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new CaLamViec(
                        rs.getString("maCa"),
                        rs.getString("tenCa"),
                        rs.getTime("gioVao"),
                        rs.getTime("gioRa"),
                        rs.getString("moTa"),
                        rs.getString("trangThai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public String phatSinhMaCa() {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maCa, 3, 18))), 0) + 1 "
                + "FROM CaLamViec WHERE maCa LIKE 'CA[0-9]%'";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return String.format("CA%03d", rs.getInt(1));
            }
        } catch (Exception ignored) {
        }
        return "CA001";
    }

    public boolean themCaLamViec(CaLamViec ca) {
        String sql = "INSERT INTO CaLamViec (maCa, tenCa, gioVao, gioRa, moTa, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getMaCa());
            stmt.setString(2, ca.getTenCa());
            stmt.setTime(3, new Time(ca.getGioVao().getTime()));
            stmt.setTime(4, new Time(ca.getGioRa().getTime()));
            stmt.setString(5, ca.getMoTa());
            stmt.setString(6, ca.getTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean capNhatCaLamViec(CaLamViec ca) {
        String sql = "UPDATE CaLamViec SET tenCa = ?, gioVao = ?, gioRa = ?, moTa = ?, trangThai = ? WHERE maCa = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getTenCa());
            stmt.setTime(2, new Time(ca.getGioVao().getTime()));
            stmt.setTime(3, new Time(ca.getGioRa().getTime()));
            stmt.setString(4, ca.getMoTa());
            stmt.setString(5, ca.getTrangThai());
            stmt.setString(6, ca.getMaCa());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean xoaCaLamViec(String maCa) {
        String sql = "DELETE FROM CaLamViec WHERE maCa = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maCa);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
