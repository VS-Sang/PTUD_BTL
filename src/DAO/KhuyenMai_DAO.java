package DAO;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    /** Lấy tất cả khuyến mãi từ database */
    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, moTa, loaiKhuyenMai, "
                + "giaTriGiam, giaTriGiamToiDa, donHangToiThieu, "
                + "soLuongToiDa, soLuongDaSuDung, ngayBatDau, ngayKetThuc, "
                + "apDungChoTatCa, trangThai, nguoiTao, ngayTao, ngayCapNhat "
                + "FROM KhuyenMai ORDER BY ngayTao DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy khuyến mãi theo mã */
    public KhuyenMai getKhuyenMaiById(String maKhuyenMai) {
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, moTa, loaiKhuyenMai, "
                + "giaTriGiam, giaTriGiamToiDa, donHangToiThieu, "
                + "soLuongToiDa, soLuongDaSuDung, ngayBatDau, ngayKetThuc, "
                + "apDungChoTatCa, trangThai, nguoiTao, ngayTao, ngayCapNhat "
                + "FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Thêm khuyến mãi mới vào database. @return true nếu thành công */
    public boolean themKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai "
                + "(maKhuyenMai, tenKhuyenMai, moTa, loaiKhuyenMai, "
                + "giaTriGiam, giaTriGiamToiDa, donHangToiThieu, "
                + "soLuongToiDa, ngayBatDau, ngayKetThuc, "
                + "apDungChoTatCa, trangThai, nguoiTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKhuyenMai());
            ps.setString(3, km.getMoTa());
            ps.setString(4, km.getLoaiKhuyenMai());
            ps.setDouble(5, km.getGiaTriGiam());
            if (km.getGiaTriGiamToiDa() <= 0)
                ps.setNull(6, Types.DECIMAL);
            else
                ps.setDouble(6, km.getGiaTriGiamToiDa());
            ps.setDouble(7, km.getDonHangToiThieu());
            if (km.getSoLuongToiDa() <= 0)
                ps.setNull(8, Types.INTEGER);
            else
                ps.setInt(8, km.getSoLuongToiDa());
            ps.setTimestamp(9, new Timestamp(km.getNgayBatDau().getTime()));
            ps.setTimestamp(10, new Timestamp(km.getNgayKetThuc().getTime()));
            ps.setBoolean(11, km.isApDungChoTatCa());
            ps.setString(12, km.getTrangThai());
            if (km.getNguoiTao() != null)
                ps.setString(13, km.getNguoiTao().getMaNhanVien());
            else
                ps.setNull(13, Types.NVARCHAR);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Xóa khuyến mãi sau khi gỡ các liên kết lịch sử, giữ nguyên tổng tiền đơn hàng. */
    public boolean xoaKhuyenMai(String maKhuyenMai) throws SQLException {
        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE DonHang SET maKhuyenMai = NULL WHERE maKhuyenMai = ?")) {
                    ps.setString(1, maKhuyenMai);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM KhuyenMai_SanPham WHERE maKhuyenMai = ?")) {
                    ps.setString(1, maKhuyenMai);
                    ps.executeUpdate();
                }
                int deleted;
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?")) {
                    ps.setString(1, maKhuyenMai);
                    deleted = ps.executeUpdate();
                }
                con.commit();
                return deleted > 0;
            } catch (SQLException | RuntimeException exception) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }

    /** Tự động sinh mã khuyến mãi mới theo dạng KM001, KM002, ... */
    public String sinhMaKhuyenMai() {
        String sql = "SELECT TOP 1 maKhuyenMai FROM KhuyenMai ORDER BY maKhuyenMai DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastMa = rs.getString("maKhuyenMai");
                int num = Integer.parseInt(lastMa.replaceAll("[^0-9]", "")) + 1;
                return String.format("KM%03d", num);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return "KM001";
    }

    private KhuyenMai mapResultSet(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
        km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
        km.setMoTa(rs.getString("moTa"));
        km.setLoaiKhuyenMai(rs.getString("loaiKhuyenMai"));
        km.setGiaTriGiam(rs.getDouble("giaTriGiam"));
        km.setGiaTriGiamToiDa(rs.getDouble("giaTriGiamToiDa"));
        km.setDonHangToiThieu(rs.getDouble("donHangToiThieu"));
        km.setSoLuongToiDa(rs.getInt("soLuongToiDa"));
        km.setSoLuongDaSuDung(rs.getInt("soLuongDaSuDung"));
        Timestamp tsBD = rs.getTimestamp("ngayBatDau");
        if (tsBD != null) km.setNgayBatDau(new java.util.Date(tsBD.getTime()));
        Timestamp tsKT = rs.getTimestamp("ngayKetThuc");
        if (tsKT != null) km.setNgayKetThuc(new java.util.Date(tsKT.getTime()));
        km.setApDungChoTatCa(rs.getBoolean("apDungChoTatCa"));
        km.setTrangThai(rs.getString("trangThai"));
        Timestamp tsTao = rs.getTimestamp("ngayTao");
        if (tsTao != null) km.setNgayTao(new java.util.Date(tsTao.getTime()));
        Timestamp tsCN = rs.getTimestamp("ngayCapNhat");
        if (tsCN != null) km.setNgayCapNhat(new java.util.Date(tsCN.getTime()));
        return km;
    }
}
