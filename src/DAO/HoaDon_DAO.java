package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import connectDB.ConnectDB;
import entity.ChiTietDonHang;
import entity.DonHang;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {

    private HoaDon docHoaDonTuResultSet(ResultSet rs) throws SQLException {
        String maDH = rs.getString("maDonHang");
        DonHang dh = maDH != null ? new DonHang(maDH) : null;

        String maKH = rs.getString("maKhachHang");
        KhachHang kh = null;
        if (maKH != null) {
            kh = new KhachHang(maKH);
            try {
                kh.setHoTen(rs.getString("tenKhachHang"));
            } catch (Exception ignored) {}
        }

        String maNV = rs.getString("maNhanVien");
        NhanVien nv = null;
        if (maNV != null) {
            nv = new NhanVien(maNV);
            try {
                nv.setHoTen(rs.getString("tenNhanVien"));
            } catch (Exception ignored) {}
        }

        return new HoaDon(
                rs.getString("maHoaDon"),
                dh,
                kh,
                nv,
                rs.getTimestamp("ngayLapHoaDon"),
                rs.getDouble("tongTienHang"),
                rs.getDouble("tienGiamGia"),
                rs.getDouble("tienDiem"),
                rs.getDouble("thueVAT"),
                rs.getDouble("tongThanhToan"),
                rs.getDouble("tienKhachDua"),
                rs.getDouble("tienTraLai"),
                rs.getString("phuongThucThanhToan"),
                rs.getString("soTaiKhoanNH"),
                rs.getString("tenNganHang"),
                rs.getString("maGiaoDich"),
                rs.getInt("diemCongThem"),
                rs.getString("trangThai"),
                rs.getString("ghiChu"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    public ArrayList<HoaDon> getAllHoaDon() {
        ArrayList<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT hd.*, kh.hoTen AS tenKhachHang, nv.hoTen AS tenNhanVien "
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang "
                + "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien "
                + "ORDER BY hd.ngayLapHoaDon DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docHoaDonTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public String taoHoaDon(String maNhanVien, String maKhachHang,
                            List<ChiTietDonHang> chiTiet, String phuongThucThanhToan,
                            double tienKhachDua) throws SQLException {
        return taoHoaDon(maNhanVien, maKhachHang, chiTiet, phuongThucThanhToan,
                tienKhachDua, 0, 0, null);
    }

    public String taoHoaDon(String maNhanVien, String maKhachHang,
                            List<ChiTietDonHang> chiTiet, String phuongThucThanhToan,
                            double tienKhachDua, double phanTramGiamGia,
                            double thueVAT, String ghiChu) throws SQLException {
        if (chiTiet == null || chiTiet.isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất một sản phẩm.");
        }
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Không xác định được nhân viên lập hóa đơn.");
        }
        if (tienKhachDua < 0) {
            throw new IllegalArgumentException("Tiền khách đưa không hợp lệ.");
        }
        if (Double.isNaN(tienKhachDua) || Double.isInfinite(tienKhachDua)) {
            throw new IllegalArgumentException("Tiền khách đưa không hợp lệ.");
        }
        if (Double.isNaN(phanTramGiamGia) || Double.isInfinite(phanTramGiamGia)
                || phanTramGiamGia < 0 || phanTramGiamGia > 100) {
            throw new IllegalArgumentException("Khuyến mãi phải nằm trong khoảng 0 đến 100%.");
        }
        if (Double.isNaN(thueVAT) || Double.isInfinite(thueVAT)
                || thueVAT < 0 || thueVAT > 100) {
            throw new IllegalArgumentException("Thuế VAT phải nằm trong khoảng 0 đến 100%.");
        }
        if (!"Tiền mặt".equals(phuongThucThanhToan)
                && !"Chuyển khoản".equals(phuongThucThanhToan)
                && !"Thẻ tín dụng".equals(phuongThucThanhToan)
                && !"Ví điện tử".equals(phuongThucThanhToan)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        }

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maDonHang = phatSinhMa(con, "DonHang", "maDonHang", "DH");
                String maHoaDon = phatSinhMa(con, "HoaDon", "maHoaDon", "HD");
                Set<String> maSanPhamDaThem = new HashSet<>();
                List<ChiTietDonHang> chiTietDaXacNhan = new ArrayList<>();
                double tongTienHang = 0;

                for (ChiTietDonHang item : chiTiet) {
                    if (item == null || item.getSanPham() == null
                            || item.getSanPham().getMaSanPham() == null
                            || item.getSoLuong() <= 0
                            || !maSanPhamDaThem.add(item.getSanPham().getMaSanPham())) {
                        throw new IllegalArgumentException("Danh sách sản phẩm trong hóa đơn không hợp lệ.");
                    }

                    String maSanPham = item.getSanPham().getMaSanPham();
                    double donGia;
                    int soLuongTon;
                    String trangThai;
                    String sqlSanPham = "SELECT giaBan, soLuongTon, trangThai FROM SanPham "
                            + "WITH (UPDLOCK, ROWLOCK) WHERE maSanPham = ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlSanPham)) {
                        stmt.setString(1, maSanPham);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Không tìm thấy sản phẩm " + maSanPham + ".");
                            }
                            donGia = rs.getDouble("giaBan");
                            soLuongTon = rs.getInt("soLuongTon");
                            trangThai = rs.getString("trangThai");
                        }
                    }

                    if (!"Đang kinh doanh".equals(trangThai)) {
                        throw new SQLException("Sản phẩm " + maSanPham + " hiện không kinh doanh.");
                    }
                    if (soLuongTon < item.getSoLuong()) {
                        throw new SQLException("Sản phẩm " + maSanPham + " chỉ còn "
                                + soLuongTon + " trong kho.");
                    }
                    if (Math.abs(donGia - item.getDonGia()) > 0.001) {
                        throw new SQLException("Giá sản phẩm " + maSanPham
                                + " đã thay đổi. Vui lòng tạo lại hóa đơn.");
                    }

                    double thanhTien = donGia * item.getSoLuong();
                    tongTienHang += thanhTien;
                    chiTietDaXacNhan.add(new ChiTietDonHang(
                            new DonHang(maDonHang), item.getSanPham(), item.getSoLuong(),
                            donGia, 0, thanhTien, null));

                    String sqlCapNhatKho = "UPDATE SanPham SET soLuongTon = soLuongTon - ?, "
                            + "trangThai = CASE WHEN soLuongTon - ? = 0 "
                            + "THEN N'Hết hàng' ELSE trangThai END, ngayCapNhat = GETDATE() "
                            + "WHERE maSanPham = ? AND soLuongTon >= ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlCapNhatKho)) {
                        stmt.setInt(1, item.getSoLuong());
                        stmt.setInt(2, item.getSoLuong());
                        stmt.setString(3, maSanPham);
                        stmt.setInt(4, item.getSoLuong());
                        if (stmt.executeUpdate() != 1) {
                            throw new SQLException("Tồn kho sản phẩm " + maSanPham
                                    + " vừa thay đổi. Vui lòng thử lại.");
                        }
                    }
                }

                double tienGiamGia = Math.round(tongTienHang * phanTramGiamGia / 100);
                double tienVAT = Math.round((tongTienHang - tienGiamGia) * thueVAT / 100);
                double tongThanhToan = tongTienHang - tienGiamGia + tienVAT;
                double tienTraLai = tienKhachDua - tongThanhToan;
                if (tienTraLai < 0) {
                    throw new IllegalArgumentException("Tiền khách đưa chưa đủ thanh toán.");
                }

                String sqlDonHang = "INSERT INTO DonHang (maDonHang, maKhachHang, maNhanVien, "
                        + "ngayDatHang, ngayGiaoHang, diaChiGiaoHang, tongTienHang, tienGiamGia, "
                        + "diemSuDung, tienDiem, phiVanChuyen, thueVAT, tongThanhToan, trangThai, ghiChu) "
                        + "VALUES (?, ?, ?, GETDATE(), GETDATE(), N'Mua trực tiếp tại cửa hàng', "
                        + "?, ?, 0, 0, 0, ?, ?, N'Đã hoàn thành', ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlDonHang)) {
                    stmt.setString(1, maDonHang);
                    stmt.setString(2, maKhachHang);
                    stmt.setString(3, maNhanVien);
                    stmt.setDouble(4, tongTienHang);
                    stmt.setDouble(5, tienGiamGia);
                    stmt.setDouble(6, tienVAT);
                    stmt.setDouble(7, tongThanhToan);
                    stmt.setString(8, ghiChu);
                    stmt.executeUpdate();
                }

                String sqlChiTiet = "INSERT INTO ChiTietDonHang "
                        + "(maDonHang, maSanPham, soLuong, donGia, giamGia, thanhTien, ghiChu) "
                        + "VALUES (?, ?, ?, ?, 0, ?, NULL)";
                try (PreparedStatement stmt = con.prepareStatement(sqlChiTiet)) {
                    for (ChiTietDonHang item : chiTietDaXacNhan) {
                        stmt.setString(1, maDonHang);
                        stmt.setString(2, item.getSanPham().getMaSanPham());
                        stmt.setInt(3, item.getSoLuong());
                        stmt.setDouble(4, item.getDonGia());
                        stmt.setDouble(5, item.getThanhTien());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                String sqlHoaDon = "INSERT INTO HoaDon (maHoaDon, maDonHang, maKhachHang, "
                        + "maNhanVien, ngayLapHoaDon, tongTienHang, tienGiamGia, tienDiem, thueVAT, "
                        + "tongThanhToan, tienKhachDua, tienTraLai, phuongThucThanhToan, "
                        + "diemCongThem, trangThai, ghiChu) "
                        + "VALUES (?, ?, ?, ?, GETDATE(), ?, ?, 0, ?, ?, ?, ?, ?, 0, "
                        + "N'Đã thanh toán', ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlHoaDon)) {
                    stmt.setString(1, maHoaDon);
                    stmt.setString(2, maDonHang);
                    stmt.setString(3, maKhachHang);
                    stmt.setString(4, maNhanVien);
                    stmt.setDouble(5, tongTienHang);
                    stmt.setDouble(6, tienGiamGia);
                    stmt.setDouble(7, tienVAT);
                    stmt.setDouble(8, tongThanhToan);
                    stmt.setDouble(9, tienKhachDua);
                    stmt.setDouble(10, tienTraLai);
                    stmt.setString(11, phuongThucThanhToan);
                    stmt.setString(12, ghiChu);
                    stmt.executeUpdate();
                }

                if (maKhachHang != null) {
                    String sqlKhachHang = "UPDATE KhachHang SET tongChiTieu = tongChiTieu + ?, "
                            + "soLanMuaHang = soLanMuaHang + 1, lanMuaGanNhat = GETDATE(), "
                            + "ngayCapNhat = GETDATE() WHERE maKhachHang = ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlKhachHang)) {
                        stmt.setDouble(1, tongThanhToan);
                        stmt.setString(2, maKhachHang);
                        if (stmt.executeUpdate() != 1) {
                            throw new SQLException("Không tìm thấy khách hàng đã chọn.");
                        }
                    }
                }

                con.commit();
                return maHoaDon;
            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private String phatSinhMa(Connection con, String table, String column, String prefix)
            throws SQLException {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(" + column
                + ", 3, 18))), 0) + 1 FROM " + table + " WITH (TABLOCKX, HOLDLOCK)";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                throw new SQLException("Không thể phát sinh mã cho " + table + ".");
            }
            return prefix + String.format("%03d", rs.getInt(1));
        }
    }
}
