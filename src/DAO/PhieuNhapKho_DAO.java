package DAO;

import connectDB.ConnectDB;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.SanPham;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhieuNhapKho_DAO {

    public String sinhMaPhieuNhap() {
        String sql = "SELECT TOP 1 maPhieuNhap FROM PhieuNhapKho ORDER BY maPhieuNhap DESC";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastMa = rs.getString("maPhieuNhap");
                int num = Integer.parseInt(lastMa.replaceAll("[^0-9]", "")) + 1;
                return String.format("PN%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "PN001";
    }

    public boolean themPhieuNhap(PhieuNhapKho pn, List<ChiTietPhieuNhap> chiTietList) throws SQLException {
        if (pn == null || chiTietList == null || chiTietList.isEmpty()) {
            throw new IllegalArgumentException("Thông tin phiếu nhập không hợp lệ.");
        }
        if (pn.getMaPhieuNhap() == null || pn.getMaPhieuNhap().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu nhập không được để trống.");
        }
        if (pn.getNhaCungCap() == null || pn.getNhaCungCap().getMaNhaCungCap() == null
                || pn.getNgayNhap() == null) {
            throw new IllegalArgumentException("Phiếu nhập phải có nhà cung cấp và ngày nhập.");
        }
        if (pn.getNhanVien() == null || pn.getNhanVien().getMaNhanVien() == null
                || pn.getNhanVien().getMaNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có nhân viên lập phiếu.");
        }
        Set<String> maSanPhamDaThem = new HashSet<>();
        for (ChiTietPhieuNhap ct : chiTietList) {
            if (ct == null || ct.getSanPham() == null
                    || ct.getSanPham().getMaSanPham() == null
                    || ct.getSanPham().getMaSanPham().trim().isEmpty()
                    || ct.getSoLuongNhap() <= 0
                    || ct.getDonGiaNhap() < 0
                    || !maSanPhamDaThem.add(ct.getSanPham().getMaSanPham())) {
                throw new IllegalArgumentException(
                        "Chi tiết phiếu nhập phải có sản phẩm hợp lệ và không trùng sản phẩm.");
            }
        }
        boolean denNgayNhap = !new java.sql.Date(pn.getNgayNhap().getTime())
                .toLocalDate().isAfter(LocalDate.now());
        pn.setTrangThai(denNgayNhap ? "Đã nhập" : "Đang nhập");

        String sqlPN = "INSERT INTO PhieuNhapKho (maPhieuNhap, maNhaCungCap, maNhanVien, ngayNhap, "
                + "tongSoLuong, tongTienNhap, trangThai, ghiChu, ngayTao, ngayCapNhat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        String sqlCT = "INSERT INTO ChiTietPhieuNhap (maPhieuNhap, maSanPham, soLuongNhap, donGiaNhap, "
                + "thanhTien, ngaySanXuat, hanSuDung, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                boolean triggerActive;
                try (PreparedStatement psCheck = con.prepareStatement(
                        "SELECT is_disabled FROM sys.triggers "
                                + "WHERE name = 'trg_ChiTietPhieuNhap_Insert'")) {
                    try (ResultSet rs = psCheck.executeQuery()) {
                        triggerActive = rs.next() && !rs.getBoolean("is_disabled");
                    }
                }

                // 1. Insert PhieuNhapKho
                try (PreparedStatement ps = con.prepareStatement(sqlPN)) {
                    ps.setString(1, pn.getMaPhieuNhap());
                    ps.setString(2, pn.getNhaCungCap() != null ? pn.getNhaCungCap().getMaNhaCungCap() : null);
                    ps.setString(3, pn.getNhanVien() != null ? pn.getNhanVien().getMaNhanVien() : null);
                    ps.setTimestamp(4, pn.getNgayNhap() != null ? new Timestamp(pn.getNgayNhap().getTime()) : new Timestamp(System.currentTimeMillis()));
                    ps.setInt(5, pn.getTongSoLuong());
                    ps.setDouble(6, pn.getTongTienNhap());
                    ps.setString(7, pn.getTrangThai() != null ? pn.getTrangThai() : "Đã nhập");
                    ps.setString(8, pn.getGhiChu());
                    ps.executeUpdate();
                }

                // 2. Insert ChiTietPhieuNhap
                try (PreparedStatement ps = con.prepareStatement(sqlCT)) {
                    for (ChiTietPhieuNhap ct : chiTietList) {
                        ps.setString(1, pn.getMaPhieuNhap());
                        ps.setString(2, ct.getSanPham().getMaSanPham());
                        ps.setInt(3, ct.getSoLuongNhap());
                        ps.setDouble(4, ct.getDonGiaNhap());
                        ps.setDouble(5, ct.getThanhTien());
                        if (ct.getNgaySanXuat() != null) {
                            ps.setDate(6, new java.sql.Date(ct.getNgaySanXuat().getTime()));
                        } else {
                            ps.setNull(6, Types.DATE);
                        }
                        if (ct.getHanSuDung() != null) {
                            ps.setDate(7, new java.sql.Date(ct.getHanSuDung().getTime()));
                        } else {
                            ps.setNull(7, Types.DATE);
                        }
                        ps.setString(8, ct.getGhiChu());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // Trigger handles eligible receipts; this is the fallback when it is absent or disabled.
                if (!triggerActive && denNgayNhap) {
                    String sqlUpdateStock = "UPDATE SanPham SET soLuongTon = soLuongTon + ?, "
                            + "ngayCapNhat = GETDATE() WHERE maSanPham = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdateStock)) {
                        for (ChiTietPhieuNhap ct : chiTietList) {
                            ps.setInt(1, ct.getSoLuongNhap());
                            ps.setString(2, ct.getSanPham().getMaSanPham());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException exception) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }

    public List<SanPham> getSanPhamNhapKho() throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        String sql = "SELECT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON dm.maDanhMuc = sp.maDanhMuc "
                + "WHERE sp.trangThai IN (N'Đang kinh doanh', N'Hết hàng') "
                + "ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sanPhamList.add(new SanPham(
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            new entity.DanhMuc(rs.getString("maDanhMuc")),
                            rs.getString("moTa"),
                            rs.getString("thuongHieu"),
                            rs.getString("xuatXu"),
                            rs.getString("donViTinh"),
                            rs.getDouble("giaNhap"),
                            rs.getDouble("giaBan"),
                            rs.getInt("soLuongTon"),
                            rs.getInt("soLuongToiThieu"),
                            rs.getString("maSKU"),
                            rs.getString("maVach"),
                            rs.getDouble("trongLuong"),
                            rs.getString("kichThuoc"),
                            rs.getString("hinhAnh"),
                            rs.getString("trangThai"),
                            rs.getTimestamp("ngayTao"),
                            rs.getTimestamp("ngayCapNhat")));
                }
            }
        }
        return sanPhamList;
    }

    public List<SanPham> getSanPhamTheoNhaCungCap(String maNhaCungCap) throws SQLException {
        if (maNhaCungCap == null || maNhaCungCap.trim().isEmpty()) {
            return getSanPhamNhapKho();
        }
        List<SanPham> sanPhamList = new ArrayList<>();
        String sql = "SELECT DISTINCT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON dm.maDanhMuc = sp.maDanhMuc "
                + "LEFT JOIN NhaCungCap ncc ON ncc.maNhaCungCap = ? "
                + "WHERE sp.trangThai IN (N'Đang kinh doanh', N'Hết hàng') "
                + "AND ("
                + "  sp.maSanPham IN ("
                + "    SELECT ctpn.maSanPham FROM ChiTietPhieuNhap ctpn "
                + "    JOIN PhieuNhapKho pnk ON pnk.maPhieuNhap = ctpn.maPhieuNhap "
                + "    WHERE pnk.maNhaCungCap = ?"
                + "  ) "
                + "  OR (ncc.tenNhaCungCap IS NOT NULL AND sp.thuongHieu IS NOT NULL AND sp.thuongHieu <> '' "
                + "      AND (CHARINDEX(LOWER(sp.thuongHieu), LOWER(ncc.tenNhaCungCap)) > 0 "
                + "           OR CHARINDEX(LOWER(ncc.tenNhaCungCap), LOWER(sp.thuongHieu)) > 0)) "
                + "  OR (ncc.maNhaCungCap IN ('NCC003', 'NCC004') AND sp.thuongHieu NOT IN ('Apple', 'Samsung', 'Xiaomi')) "
                + ") ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhaCungCap);
            stmt.setString(2, maNhaCungCap);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sanPhamList.add(new SanPham(
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            new entity.DanhMuc(rs.getString("maDanhMuc")),
                            rs.getString("moTa"),
                            rs.getString("thuongHieu"),
                            rs.getString("xuatXu"),
                            rs.getString("donViTinh"),
                            rs.getDouble("giaNhap"),
                            rs.getDouble("giaBan"),
                            rs.getInt("soLuongTon"),
                            rs.getInt("soLuongToiThieu"),
                            rs.getString("maSKU"),
                            rs.getString("maVach"),
                            rs.getDouble("trongLuong"),
                            rs.getString("kichThuoc"),
                            rs.getString("hinhAnh"),
                            rs.getString("trangThai"),
                            rs.getTimestamp("ngayTao"),
                            rs.getTimestamp("ngayCapNhat")));
                }
            }
        }
        if (sanPhamList.isEmpty()) {
            return getSanPhamNhapKho();
        }
        return sanPhamList;
    }

    public int capNhatPhieuNhapDenHan() throws SQLException {
        String sql = "DECLARE @PhieuDenHan TABLE (maPhieuNhap NVARCHAR(20) PRIMARY KEY); "
                + "UPDATE PhieuNhapKho SET trangThai = N'Đã nhập', ngayCapNhat = GETDATE() "
                + "OUTPUT inserted.maPhieuNhap INTO @PhieuDenHan "
                + "WHERE trangThai = N'Đang nhập' "
                + "AND CAST(ngayNhap AS DATE) <= CAST(GETDATE() AS DATE); "
                + "UPDATE sp SET sp.soLuongTon = sp.soLuongTon + nhap.soLuongNhap, "
                + "sp.ngayCapNhat = GETDATE() FROM SanPham sp "
                + "JOIN (SELECT ct.maSanPham, SUM(ct.soLuongNhap) AS soLuongNhap "
                + "FROM ChiTietPhieuNhap ct JOIN @PhieuDenHan denHan "
                + "ON denHan.maPhieuNhap = ct.maPhieuNhap "
                + "GROUP BY ct.maSanPham) nhap ON nhap.maSanPham = sp.maSanPham";

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                int affectedProducts;
                try (Statement stmt = con.createStatement()) {
                    boolean hasResult = stmt.execute(sql);
                    affectedProducts = 0;
                    while (hasResult || stmt.getUpdateCount() != -1) {
                        if (hasResult) {
                            try (ResultSet rs = stmt.getResultSet()) {
                                while (rs.next()) {
                                    affectedProducts = rs.getInt(1);
                                }
                            }
                        } else if (stmt.getUpdateCount() >= 0) {
                            affectedProducts = stmt.getUpdateCount();
                        }
                        hasResult = stmt.getMoreResults();
                    }
                }
                con.commit();
                return affectedProducts;
            } catch (SQLException exception) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }
}
