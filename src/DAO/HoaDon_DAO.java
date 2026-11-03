package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connectDB.ConnectDB;
import entity.ChiTietDonHang;
import entity.DonHang;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;

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

    public HoaDon getHoaDonByMa(String maHoaDon) throws SQLException {
        String sql = "SELECT hd.*, kh.hoTen AS tenKhachHang, nv.hoTen AS tenNhanVien "
                + "FROM HoaDon hd LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang "
                + "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien WHERE hd.maHoaDon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy hóa đơn.");
                }
                return docHoaDonTuResultSet(rs);
            }
        }
    }

    public ArrayList<ChiTietDonHang> getChiTietHoaDon(String maHoaDon) throws SQLException {
        ArrayList<ChiTietDonHang> chiTiet = new ArrayList<>();
        String sql = "SELECT ct.maDonHang, ct.maSanPham, sp.tenSanPham, ct.soLuong, "
                + "ct.donGia, ct.giamGia, ct.thanhTien, ct.ghiChu "
                + "FROM ChiTietDonHang ct JOIN SanPham sp ON sp.maSanPham = ct.maSanPham "
                + "JOIN HoaDon hd ON hd.maDonHang = ct.maDonHang "
                + "WHERE hd.maHoaDon = ? ORDER BY ct.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SanPham sanPham = new SanPham(rs.getString("maSanPham"));
                    sanPham.setTenSanPham(rs.getString("tenSanPham"));
                    sanPham.setGiaBan(rs.getDouble("donGia"));
                    chiTiet.add(new ChiTietDonHang(new DonHang(rs.getString("maDonHang")),
                            sanPham, rs.getInt("soLuong"), rs.getDouble("donGia"),
                            rs.getDouble("giamGia"), rs.getDouble("thanhTien"),
                            rs.getString("ghiChu")));
                }
            }
        }
        return chiTiet;
    }

    public String getMaKhuyenMaiHoaDon(String maHoaDon) throws SQLException {
        String sql = "SELECT dh.maKhuyenMai FROM HoaDon hd "
                + "JOIN DonHang dh ON dh.maDonHang = hd.maDonHang WHERE hd.maHoaDon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy hóa đơn.");
                }
                return rs.getString("maKhuyenMai");
            }
        }
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
        return taoHoaDon(maNhanVien, maKhachHang, chiTiet, phuongThucThanhToan,
                tienKhachDua, null, -1, phanTramGiamGia, thueVAT, ghiChu);
    }

    public String taoHoaDon(String maNhanVien, String maKhachHang,
                            List<ChiTietDonHang> chiTiet, String phuongThucThanhToan,
                            double tienKhachDua, String maKhuyenMai, double tienGiamGiaTrucTiep,
                            double thueVAT, String ghiChu) throws SQLException {
        return taoHoaDon(maNhanVien, maKhachHang, chiTiet, phuongThucThanhToan,
                tienKhachDua, maKhuyenMai, tienGiamGiaTrucTiep, 0, thueVAT, ghiChu);
    }

    public void capNhatThuNganHoaDon(String maHoaDon, String maNhanVien) throws SQLException {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()
                || maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn và nhân viên phải được chọn.");
        }

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maDonHang;
                try (PreparedStatement stmt = con.prepareStatement(
                        "SELECT maDonHang FROM HoaDon WITH (UPDLOCK, HOLDLOCK) WHERE maHoaDon = ?")) {
                    stmt.setString(1, maHoaDon);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy hóa đơn cần cập nhật.");
                        }
                        maDonHang = rs.getString("maDonHang");
                    }
                }
                try (PreparedStatement stmt = con.prepareStatement(
                        "UPDATE HoaDon SET maNhanVien = ?, ngayCapNhat = GETDATE() WHERE maHoaDon = ?")) {
                    stmt.setString(1, maNhanVien);
                    stmt.setString(2, maHoaDon);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể cập nhật nhân viên trên hóa đơn.");
                    }
                }
                try (PreparedStatement stmt = con.prepareStatement(
                        "UPDATE DonHang SET maNhanVien = ?, ngayCapNhat = GETDATE() WHERE maDonHang = ?")) {
                    stmt.setString(1, maNhanVien);
                    stmt.setString(2, maDonHang);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể đồng bộ nhân viên trên đơn hàng.");
                    }
                }
                con.commit();
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

    public void capNhatNoiDungHoaDon(String maHoaDon, String maKhachHang,
                                    List<ChiTietDonHang> chiTiet,
                                    String phuongThucThanhToan, double tienKhachDua,
                                    String ghiChu) throws SQLException {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()
                || chiTiet == null || chiTiet.isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất một sản phẩm.");
        }
        if (!"Tiền mặt".equals(phuongThucThanhToan)
                && !"Chuyển khoản".equals(phuongThucThanhToan)
                && !"Thẻ tín dụng".equals(phuongThucThanhToan)
                && !"Ví điện tử".equals(phuongThucThanhToan)
                && !"Kết hợp".equals(phuongThucThanhToan)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        }
        if (Double.isNaN(tienKhachDua) || Double.isInfinite(tienKhachDua)
                || tienKhachDua < 0) {
            throw new IllegalArgumentException("Tiền khách đưa không hợp lệ.");
        }

        Map<String, Integer> soLuongMoi = new LinkedHashMap<>();
        for (ChiTietDonHang item : chiTiet) {
            if (item == null || item.getSanPham() == null
                    || item.getSanPham().getMaSanPham() == null
                    || item.getSoLuong() <= 0
                    || soLuongMoi.put(item.getSanPham().getMaSanPham(), item.getSoLuong()) != null) {
                throw new IllegalArgumentException("Danh sách sản phẩm trong hóa đơn không hợp lệ.");
            }
        }

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maDonHang;
                String maKhachHangCu;
                String maKhuyenMai;
                String trangThai;
                double tongThanhToanCu;
                double tongTienHangCu;
                double tienGiamCu;
                double thueCu;
                String sqlHoaDonCu = "SELECT hd.maDonHang, hd.maKhachHang, hd.trangThai, "
                        + "hd.tongThanhToan, hd.tongTienHang, hd.tienGiamGia, hd.thueVAT, "
                        + "dh.maKhuyenMai FROM HoaDon hd WITH (UPDLOCK, HOLDLOCK) "
                        + "JOIN DonHang dh WITH (UPDLOCK, HOLDLOCK) ON dh.maDonHang = hd.maDonHang "
                        + "WHERE hd.maHoaDon = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlHoaDonCu)) {
                    stmt.setString(1, maHoaDon);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy hóa đơn cần cập nhật.");
                        }
                        maDonHang = rs.getString("maDonHang");
                        maKhachHangCu = rs.getString("maKhachHang");
                        trangThai = rs.getString("trangThai");
                        tongThanhToanCu = rs.getDouble("tongThanhToan");
                        tongTienHangCu = rs.getDouble("tongTienHang");
                        tienGiamCu = rs.getDouble("tienGiamGia");
                        thueCu = rs.getDouble("thueVAT");
                        maKhuyenMai = rs.getString("maKhuyenMai");
                    }
                }
                if (!"Đã thanh toán".equals(trangThai)) {
                    throw new SQLException("Chỉ có thể sửa hóa đơn đã thanh toán.");
                }
                if (coBanGhi(con, "SELECT 1 FROM PhieuTraHang WITH (UPDLOCK, HOLDLOCK) "
                                + "WHERE maHoaDon = ?", maHoaDon)
                        || coBanGhi(con,
                                "SELECT 1 FROM LichSuDiemThuong WITH (UPDLOCK, HOLDLOCK) "
                                        + "WHERE maHoaDon = ? OR maDonHang = ?",
                                maHoaDon, maDonHang)
                        || coBanGhi(con, "SELECT 1 FROM PhieuXuatKho WITH (UPDLOCK, HOLDLOCK) "
                                + "WHERE maDonHang = ?", maDonHang)) {
                    throw new SQLException("Không thể sửa hóa đơn đã phát sinh trả hàng, điểm thưởng "
                            + "hoặc phiếu xuất kho.");
                }
                if (maKhachHang != null && !maKhachHang.trim().isEmpty()
                        && !coBanGhi(con, "SELECT 1 FROM KhachHang WITH (UPDLOCK, HOLDLOCK) "
                                + "WHERE maKhachHang = ?", maKhachHang)) {
                    throw new SQLException("Không tìm thấy khách hàng được chọn.");
                }

                Map<String, ChiTietDonHang> chiTietCu = new LinkedHashMap<>();
                String sqlChiTietCu = "SELECT maSanPham, soLuong, donGia FROM ChiTietDonHang "
                        + "WITH (UPDLOCK, HOLDLOCK) WHERE maDonHang = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlChiTietCu)) {
                    stmt.setString(1, maDonHang);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            SanPham sanPham = new SanPham(rs.getString("maSanPham"));
                            chiTietCu.put(sanPham.getMaSanPham(), new ChiTietDonHang(
                                    new DonHang(maDonHang), sanPham, rs.getInt("soLuong"),
                                    rs.getDouble("donGia"), 0, 0, null));
                        }
                    }
                }
                if (chiTietCu.isEmpty()) {
                    throw new SQLException("Hóa đơn không có chi tiết sản phẩm để cập nhật.");
                }

                double tongTienHangMoi = 0;
                List<ChiTietDonHang> chiTietDaXacNhan = new ArrayList<>();
                Set<String> maSanPhamCanXuLy = new HashSet<>(chiTietCu.keySet());
                maSanPhamCanXuLy.addAll(soLuongMoi.keySet());
                Map<String, ChiTietDonHang> chiTietYeuCau = new LinkedHashMap<>();
                for (ChiTietDonHang item : chiTiet) {
                    chiTietYeuCau.put(item.getSanPham().getMaSanPham(), item);
                }
                for (String maSanPham : maSanPhamCanXuLy) {
                    ChiTietDonHang cu = chiTietCu.get(maSanPham);
                    Integer soLuong = soLuongMoi.get(maSanPham);
                    int soLuongCu = cu == null ? 0 : cu.getSoLuong();
                    int soLuongMoiCuaSP = soLuong == null ? 0 : soLuong;
                    int thayDoiTonKho = soLuongCu - soLuongMoiCuaSP;

                    double donGia;
                    int tonKho;
                    String trangThaiSP;
                    try (PreparedStatement stmt = con.prepareStatement(
                            "SELECT giaBan, soLuongTon, trangThai FROM SanPham "
                                    + "WITH (UPDLOCK, ROWLOCK) WHERE maSanPham = ?")) {
                        stmt.setString(1, maSanPham);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Không tìm thấy sản phẩm " + maSanPham + ".");
                            }
                            donGia = cu == null ? rs.getDouble("giaBan") : cu.getDonGia();
                            tonKho = rs.getInt("soLuongTon");
                            trangThaiSP = rs.getString("trangThai");
                        }
                    }
                    if (cu == null && Math.abs(donGia
                            - chiTietYeuCau.get(maSanPham).getDonGia()) > 0.001) {
                        throw new SQLException("Giá sản phẩm " + maSanPham
                                + " đã thay đổi. Vui lòng tải lại hóa đơn trước khi lưu.");
                    }
                    if (thayDoiTonKho < 0
                            && (!"Đang kinh doanh".equals(trangThaiSP)
                                    || tonKho < -thayDoiTonKho)) {
                        throw new SQLException("Sản phẩm " + maSanPham
                                + " không đủ tồn kho hoặc hiện không kinh doanh.");
                    }
                    if (thayDoiTonKho != 0) {
                        String sqlCapNhatKho = "UPDATE SanPham SET soLuongTon = soLuongTon + ?, "
                                + "trangThai = CASE WHEN soLuongTon + ? = 0 "
                                + "THEN N'Hết hàng' WHEN trangThai = N'Hết hàng' "
                                + "THEN N'Đang kinh doanh' ELSE trangThai END, ngayCapNhat = GETDATE() "
                                + "WHERE maSanPham = ?";
                        try (PreparedStatement stmt = con.prepareStatement(sqlCapNhatKho)) {
                            stmt.setInt(1, thayDoiTonKho);
                            stmt.setInt(2, thayDoiTonKho);
                            stmt.setString(3, maSanPham);
                            if (stmt.executeUpdate() != 1) {
                                throw new SQLException("Không thể cập nhật tồn kho sản phẩm "
                                        + maSanPham + ".");
                            }
                        }
                    }
                    if (soLuong != null) {
                        SanPham sanPham = cu == null ? new SanPham(maSanPham) : cu.getSanPham();
                        chiTietDaXacNhan.add(new ChiTietDonHang(new DonHang(maDonHang), sanPham,
                                soLuong, donGia, 0, donGia * soLuong, null));
                        tongTienHangMoi += donGia * soLuong;
                    }
                }

                double tienGiamMoi = tinhTienGiamHoaDon(con, maKhuyenMai, tongTienHangMoi);
                double tienChiuThueCu = tongTienHangCu - tienGiamCu;
                double tiLeThue = tienChiuThueCu > 0 ? thueCu * 100 / tienChiuThueCu : 10;
                double thueMoi = Math.round((tongTienHangMoi - tienGiamMoi) * tiLeThue / 100);
                double tongThanhToanMoi = tongTienHangMoi - tienGiamMoi + thueMoi;
                if (tienKhachDua < tongThanhToanMoi) {
                    throw new IllegalArgumentException("Tiền khách đưa chưa đủ thanh toán.");
                }

                try (PreparedStatement stmt = con.prepareStatement(
                        "DELETE FROM ChiTietDonHang WHERE maDonHang = ?")) {
                    stmt.setString(1, maDonHang);
                    stmt.executeUpdate();
                }
                String sqlThemChiTiet = "INSERT INTO ChiTietDonHang "
                        + "(maDonHang, maSanPham, soLuong, donGia, giamGia, thanhTien, ghiChu) "
                        + "VALUES (?, ?, ?, ?, 0, ?, NULL)";
                try (PreparedStatement stmt = con.prepareStatement(sqlThemChiTiet)) {
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

                String sqlUpdateOrder = "UPDATE DonHang SET maKhachHang = ?, tongTienHang = ?, "
                        + "tienGiamGia = ?, thueVAT = ?, tongThanhToan = ?, ghiChu = ?, "
                        + "ngayCapNhat = GETDATE() WHERE maDonHang = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlUpdateOrder)) {
                    stmt.setString(1, maKhachHang);
                    stmt.setDouble(2, tongTienHangMoi);
                    stmt.setDouble(3, tienGiamMoi);
                    stmt.setDouble(4, thueMoi);
                    stmt.setDouble(5, tongThanhToanMoi);
                    stmt.setString(6, ghiChu);
                    stmt.setString(7, maDonHang);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể cập nhật đơn hàng liên kết.");
                    }
                }
                String sqlUpdateInvoice = "UPDATE HoaDon SET maKhachHang = ?, tongTienHang = ?, "
                        + "tienGiamGia = ?, thueVAT = ?, tongThanhToan = ?, tienKhachDua = ?, "
                        + "tienTraLai = ?, phuongThucThanhToan = ?, ghiChu = ?, "
                        + "ngayCapNhat = GETDATE() WHERE maHoaDon = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlUpdateInvoice)) {
                    stmt.setString(1, maKhachHang);
                    stmt.setDouble(2, tongTienHangMoi);
                    stmt.setDouble(3, tienGiamMoi);
                    stmt.setDouble(4, thueMoi);
                    stmt.setDouble(5, tongThanhToanMoi);
                    stmt.setDouble(6, tienKhachDua);
                    stmt.setDouble(7, tienKhachDua - tongThanhToanMoi);
                    stmt.setString(8, phuongThucThanhToan);
                    stmt.setString(9, ghiChu);
                    stmt.setString(10, maHoaDon);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể cập nhật hóa đơn.");
                    }
                }

                if (java.util.Objects.equals(maKhachHangCu, maKhachHang)) {
                    if (maKhachHang != null) {
                        capNhatThongKeKhachHang(con, maKhachHang,
                                tongThanhToanMoi - tongThanhToanCu, 0);
                    }
                } else {
                    if (maKhachHangCu != null) {
                        capNhatThongKeKhachHang(con, maKhachHangCu, -tongThanhToanCu, -1);
                    }
                    if (maKhachHang != null) {
                        capNhatThongKeKhachHang(con, maKhachHang, tongThanhToanMoi, 1);
                    }
                }

                con.commit();
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

    private double tinhTienGiamHoaDon(Connection con, String maKhuyenMai,
                                       double tongTienHang) throws SQLException {
        if (maKhuyenMai == null) {
            return 0;
        }
        String sql = "SELECT loaiKhuyenMai, giaTriGiam, giaTriGiamToiDa, donHangToiThieu "
                + "FROM KhuyenMai WITH (UPDLOCK, HOLDLOCK) WHERE maKhuyenMai = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKhuyenMai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy khuyến mãi đang áp dụng.");
                }
                if (tongTienHang < rs.getDouble("donHangToiThieu")) {
                    return 0;
                }
                double giaTriGiam = rs.getDouble("giaTriGiam");
                double tienGiam;
                if ("Phần trăm".equalsIgnoreCase(rs.getString("loaiKhuyenMai"))
                        || "%".equals(rs.getString("loaiKhuyenMai"))) {
                    tienGiam = Math.round(tongTienHang * giaTriGiam / 100);
                    double mucGiamToiDa = rs.getDouble("giaTriGiamToiDa");
                    if (mucGiamToiDa > 0) {
                        tienGiam = Math.min(tienGiam, mucGiamToiDa);
                    }
                } else {
                    tienGiam = giaTriGiam;
                }
                return Math.min(tienGiam, tongTienHang);
            }
        }
    }

    private void capNhatThongKeKhachHang(Connection con, String maKhachHang,
                                          double thayDoiChiTieu, int thayDoiSoLanMua)
            throws SQLException {
        String sql = "UPDATE KhachHang SET "
                + "tongChiTieu = CASE WHEN tongChiTieu + ? < 0 THEN 0 ELSE tongChiTieu + ? END, "
                + "soLanMuaHang = CASE WHEN soLanMuaHang + ? < 0 THEN 0 "
                + "ELSE soLanMuaHang + ? END, "
                + "lanMuaGanNhat = (SELECT MAX(ngayLapHoaDon) FROM HoaDon "
                + "WHERE maKhachHang = ?), ngayCapNhat = GETDATE() WHERE maKhachHang = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, thayDoiChiTieu);
            stmt.setDouble(2, thayDoiChiTieu);
            stmt.setInt(3, thayDoiSoLanMua);
            stmt.setInt(4, thayDoiSoLanMua);
            stmt.setString(5, maKhachHang);
            stmt.setString(6, maKhachHang);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Không thể cập nhật thống kê khách hàng.");
            }
        }
    }

    public void xoaHoaDon(String maHoaDon) throws SQLException {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maDonHang;
                String maKhachHang;
                String maKhuyenMai;
                double tongThanhToan;
                String sqlThongTin = "SELECT hd.maDonHang, hd.maKhachHang, hd.tongThanhToan, "
                        + "dh.maKhuyenMai FROM HoaDon hd WITH (UPDLOCK, HOLDLOCK) "
                        + "JOIN DonHang dh WITH (UPDLOCK, HOLDLOCK) ON dh.maDonHang = hd.maDonHang "
                        + "WHERE hd.maHoaDon = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlThongTin)) {
                    stmt.setString(1, maHoaDon);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy hóa đơn cần xóa.");
                        }
                        maDonHang = rs.getString("maDonHang");
                        maKhachHang = rs.getString("maKhachHang");
                        tongThanhToan = rs.getDouble("tongThanhToan");
                        maKhuyenMai = rs.getString("maKhuyenMai");
                    }
                }

                if (coBanGhi(con, "SELECT 1 FROM PhieuTraHang WHERE maHoaDon = ?", maHoaDon)) {
                    throw new SQLException("Không thể xóa hóa đơn đã phát sinh phiếu trả hàng.");
                }
                if (coBanGhi(con,
                        "SELECT 1 FROM LichSuDiemThuong WHERE maHoaDon = ? OR maDonHang = ?",
                        maHoaDon, maDonHang)) {
                    throw new SQLException("Không thể xóa hóa đơn đã có lịch sử điểm thưởng.");
                }
                if (coBanGhi(con, "SELECT 1 FROM PhieuXuatKho WHERE maDonHang = ?", maDonHang)) {
                    throw new SQLException("Không thể xóa hóa đơn đã liên kết với phiếu xuất kho.");
                }

                String sqlHoanKho = "UPDATE sp SET sp.soLuongTon = sp.soLuongTon + ct.soLuong, "
                        + "sp.trangThai = CASE WHEN sp.trangThai = N'Hết hàng' "
                        + "THEN N'Đang kinh doanh' ELSE sp.trangThai END, "
                        + "sp.ngayCapNhat = GETDATE() FROM SanPham sp "
                        + "JOIN ChiTietDonHang ct ON ct.maSanPham = sp.maSanPham "
                        + "WHERE ct.maDonHang = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlHoanKho)) {
                    stmt.setString(1, maDonHang);
                    stmt.executeUpdate();
                }

                if (maKhachHang != null) {
                    String sqlCapNhatKhachHang = "UPDATE KhachHang SET "
                            + "tongChiTieu = CASE WHEN tongChiTieu >= ? THEN tongChiTieu - ? ELSE 0 END, "
                            + "soLanMuaHang = CASE WHEN soLanMuaHang > 0 THEN soLanMuaHang - 1 ELSE 0 END, "
                            + "lanMuaGanNhat = (SELECT MAX(ngayLapHoaDon) FROM HoaDon "
                            + "WHERE maKhachHang = ? AND maHoaDon <> ?), ngayCapNhat = GETDATE() "
                            + "WHERE maKhachHang = ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlCapNhatKhachHang)) {
                        stmt.setDouble(1, tongThanhToan);
                        stmt.setDouble(2, tongThanhToan);
                        stmt.setString(3, maKhachHang);
                        stmt.setString(4, maHoaDon);
                        stmt.setString(5, maKhachHang);
                        stmt.executeUpdate();
                    }
                }

                if (maKhuyenMai != null) {
                    try (PreparedStatement stmt = con.prepareStatement(
                            "UPDATE KhuyenMai SET soLuongDaSuDung = "
                                    + "CASE WHEN soLuongDaSuDung > 0 THEN soLuongDaSuDung - 1 ELSE 0 END, "
                                    + "ngayCapNhat = GETDATE() WHERE maKhuyenMai = ?")) {
                        stmt.setString(1, maKhuyenMai);
                        stmt.executeUpdate();
                    }
                }

                try (PreparedStatement stmt = con.prepareStatement(
                        "DELETE FROM ChiTietDonHang WHERE maDonHang = ?")) {
                    stmt.setString(1, maDonHang);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = con.prepareStatement(
                        "DELETE FROM HoaDon WHERE maHoaDon = ?")) {
                    stmt.setString(1, maHoaDon);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể xóa hóa đơn.");
                    }
                }
                try (PreparedStatement stmt = con.prepareStatement(
                        "DELETE FROM DonHang WHERE maDonHang = ?")) {
                    stmt.setString(1, maDonHang);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Không thể xóa đơn hàng liên kết.");
                    }
                }

                con.commit();
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

    private boolean coBanGhi(Connection con, String sql, String... parameters) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setString(i + 1, parameters[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String taoHoaDon(String maNhanVien, String maKhachHang,
                             List<ChiTietDonHang> chiTiet, String phuongThucThanhToan,
                             double tienKhachDua, String maKhuyenMai, double tienGiamGiaTrucTiep,
                             double phanTramGiamGia, double thueVAT, String ghiChu) throws SQLException {
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
        if (tienGiamGiaTrucTiep < 0 && (Double.isNaN(phanTramGiamGia) || Double.isInfinite(phanTramGiamGia)
                || phanTramGiamGia < 0 || phanTramGiamGia > 100)) {
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

                double tienGiamGia = tienGiamGiaTrucTiep >= 0
                        ? Math.min(tienGiamGiaTrucTiep, tongTienHang)
                        : Math.round(tongTienHang * phanTramGiamGia / 100);
                double tienVAT = Math.round((tongTienHang - tienGiamGia) * thueVAT / 100);
                double tongThanhToan = tongTienHang - tienGiamGia + tienVAT;
                double tienTraLai = tienKhachDua - tongThanhToan;
                if (tienTraLai < 0) {
                    throw new IllegalArgumentException("Tiền khách đưa chưa đủ thanh toán.");
                }

                String sqlDonHang = "INSERT INTO DonHang (maDonHang, maKhachHang, maNhanVien, "
                        + "ngayDatHang, ngayGiaoHang, diaChiGiaoHang, tongTienHang, tienGiamGia, "
                        + "maKhuyenMai, diemSuDung, tienDiem, phiVanChuyen, thueVAT, tongThanhToan, trangThai, ghiChu) "
                        + "VALUES (?, ?, ?, GETDATE(), GETDATE(), N'Mua trực tiếp tại cửa hàng', "
                        + "?, ?, ?, 0, 0, 0, ?, ?, N'Đã hoàn thành', ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlDonHang)) {
                    stmt.setString(1, maDonHang);
                    stmt.setString(2, maKhachHang);
                    stmt.setString(3, maNhanVien);
                    stmt.setDouble(4, tongTienHang);
                    stmt.setDouble(5, tienGiamGia);
                    stmt.setString(6, (maKhuyenMai != null && !maKhuyenMai.trim().isEmpty()) ? maKhuyenMai : null);
                    stmt.setDouble(7, tienVAT);
                    stmt.setDouble(8, tongThanhToan);
                    stmt.setString(9, ghiChu);
                    stmt.executeUpdate();
                }

                if (maKhuyenMai != null && !maKhuyenMai.trim().isEmpty()) {
                    String sqlUpdateKM = "UPDATE KhuyenMai SET soLuongDaSuDung = ISNULL(soLuongDaSuDung, 0) + 1, "
                            + "ngayCapNhat = GETDATE() WHERE maKhuyenMai = ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlUpdateKM)) {
                        stmt.setString(1, maKhuyenMai);
                        stmt.executeUpdate();
                    }
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
