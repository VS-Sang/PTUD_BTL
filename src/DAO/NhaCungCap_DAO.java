package DAO;

import connectDB.ConnectDB;
import entity.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class NhaCungCap_DAO {

    public ArrayList<NhaCungCap> getAllNhaCungCap() {
        ArrayList<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap ORDER BY maNhaCungCap";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("maNhaCungCap"),
                        rs.getString("tenNhaCungCap"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("nguoiLienHe"),
                        rs.getString("maSoThue"),
                        rs.getString("soTaiKhoanNH"),
                        rs.getString("tenNganHang"),
                        rs.getDouble("congNo"),
                        rs.getString("trangThai"),
                        rs.getString("ghiChu"),
                        rs.getTimestamp("ngayTao"),
                        rs.getTimestamp("ngayCapNhat")
                );
                list.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public NhaCungCap getNhaCungCapById(String maNCC) {
        String sql = "SELECT * FROM NhaCungCap WHERE maNhaCungCap = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhaCungCap(
                            rs.getString("maNhaCungCap"),
                            rs.getString("tenNhaCungCap"),
                            rs.getString("soDienThoai"),
                            rs.getString("email"),
                            rs.getString("diaChi"),
                            rs.getString("nguoiLienHe"),
                            rs.getString("maSoThue"),
                            rs.getString("soTaiKhoanNH"),
                            rs.getString("tenNganHang"),
                            rs.getDouble("congNo"),
                            rs.getString("trangThai"),
                            rs.getString("ghiChu"),
                            rs.getTimestamp("ngayTao"),
                            rs.getTimestamp("ngayCapNhat")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
