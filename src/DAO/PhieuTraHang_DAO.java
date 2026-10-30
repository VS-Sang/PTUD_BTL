package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;

public class PhieuTraHang_DAO {
    public static final String TRA_HOAN_TIEN = "Trả hàng & hoàn tiền";
    public static final String DOI_HANG = "Đổi hàng";

    public static final class DongHangTra {
        private final String maSanPham;
        private final String tenSanPham;
        private final int soLuongMua;
        private final int soLuongDaTra;
        private final double giaTriHoanMoiDonVi;

        private DongHangTra(String maSanPham, String tenSanPham, int soLuongMua,
                            int soLuongDaTra, double giaTriHoanMoiDonVi) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.soLuongMua = soLuongMua;
            this.soLuongDaTra = soLuongDaTra;
            this.giaTriHoanMoiDonVi = giaTriHoanMoiDonVi;
        }

        public String getMaSanPham() { return maSanPham; }
        public String getTenSanPham() { return tenSanPham; }
        public int getSoLuongMua() { return soLuongMua; }
        public int getSoLuongDaTra() { return soLuongDaTra; }
        public int getSoLuongConLai() { return soLuongMua - soLuongDaTra; }
        public double getGiaTriHoanMoiDonVi() { return giaTriHoanMoiDonVi; }
    }

    public static final class HoaDonTraHang {
        private final String maHoaDon;
        private final String tenKhachHang;
        private final String trangThai;
        private final double tongThanhToan;
        private final List<DongHangTra> chiTiet;

        private HoaDonTraHang(String maHoaDon, String tenKhachHang, String trangThai,
                              double tongThanhToan, List<DongHangTra> chiTiet) {
            this.maHoaDon = maHoaDon;
            this.tenKhachHang = tenKhachHang;
            this.trangThai = trangThai;
            this.tongThanhToan = tongThanhToan;
            this.chiTiet = Collections.unmodifiableList(new ArrayList<>(chiTiet));
        }

        public String getMaHoaDon() { return maHoaDon; }
        public String getTenKhachHang() { return tenKhachHang; }
        public String getTrangThai() { return trangThai; }
        public double getTongThanhToan() { return tongThanhToan; }
        public List<DongHangTra> getChiTiet() { return chiTiet; }
    }

    public static final class KetQuaTraHang {
        private final int maPhieuTra;
        private final double tongGiaTriHangTra;
        private final double tongGiaTriHangDoi;
        private final double tienHoan;
        private final double chenhLechThuThem;
        private final boolean daHoanTat;

        private KetQuaTraHang(int maPhieuTra, double tongGiaTriHangTra, double tongGiaTriHangDoi,
                              double tienHoan, double chenhLechThuThem, boolean daHoanTat) {
            this.maPhieuTra = maPhieuTra;
            this.tongGiaTriHangTra = tongGiaTriHangTra;
            this.tongGiaTriHangDoi = tongGiaTriHangDoi;
            this.tienHoan = tienHoan;
            this.chenhLechThuThem = chenhLechThuThem;
            this.daHoanTat = daHoanTat;
        }

        public int getMaPhieuTra() { return maPhieuTra; }
        public double getTongGiaTriHangTra() { return tongGiaTriHangTra; }
        public double getTongGiaTriHangDoi() { return tongGiaTriHangDoi; }
        public double getTienHoan() { return tienHoan; }
        public double getChenhLechThuThem() { return chenhLechThuThem; }
        public boolean isDaHoanTat() { return daHoanTat; }
    }

    public static final class SanPhamDoi {
        private final String maSanPham;
        private final String tenSanPham;
        private final double giaBan;
        private final int soLuongTon;

        private SanPhamDoi(String maSanPham, String tenSanPham, double giaBan, int soLuongTon) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.giaBan = giaBan;
            this.soLuongTon = soLuongTon;
        }

        public String getMaSanPham() { return maSanPham; }
        public String getTenSanPham() { return tenSanPham; }
        public double getGiaBan() { return giaBan; }
        public int getSoLuongTon() { return soLuongTon; }

        @Override
        public String toString() {
            return maSanPham + " - " + tenSanPham + " (" + String.format("%,.0f", giaBan) + " đ)";
        }
    }

    public HoaDonTraHang timHoaDon(String maHoaDon) throws SQLException {
        String maCanTim = maHoaDon == null ? "" : maHoaDon.trim();
        if (maCanTim.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mã hóa đơn.");
        }

        String sqlHoaDon = "SELECT hd.maHoaDon, hd.trangThai, hd.tongThanhToan, "
                + "COALESCE(kh.hoTen, N'Khách lẻ') AS tenKhachHang "
                + "FROM HoaDon hd LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang "
                + "WHERE hd.maHoaDon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlHoaDon)) {
            stmt.setString(1, maCanTim);
            String tenKhachHang;
            String trangThai;
            double tongThanhToan;
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy hóa đơn " + maCanTim + ".");
                }
                trangThai = rs.getString("trangThai");
                if (!"Đã thanh toán".equals(trangThai)) {
                    throw new SQLException("Chỉ xử lý được hóa đơn đã thanh toán. Trạng thái hiện tại: "
                            + trangThai + ".");
                }
                tenKhachHang = rs.getString("tenKhachHang");
                tongThanhToan = rs.getDouble("tongThanhToan");
            }
            return new HoaDonTraHang(maCanTim, tenKhachHang, trangThai,
                    tongThanhToan, docChiTiet(con, maCanTim, tongThanhToan));
        }
    }

    public List<SanPhamDoi> getSanPhamDoi() throws SQLException {
        String sql = "SELECT maSanPham, tenSanPham, giaBan, soLuongTon FROM SanPham "
                + "WHERE trangThai IN (N'Đang kinh doanh', N'Hết hàng') "
                + "ORDER BY tenSanPham";
        List<SanPhamDoi> sanPham = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sanPham.add(new SanPhamDoi(
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getDouble("giaBan"),
                        rs.getInt("soLuongTon")));
            }
        }
        return sanPham;
    }

    private List<DongHangTra> docChiTiet(Connection con, String maHoaDon,
                                         double tongThanhToan) throws SQLException {
        String sql = "SELECT ctdh.maSanPham, sp.tenSanPham, ctdh.soLuong, ctdh.thanhTien, "
                + "COALESCE(daTra.soLuongDaTra, 0) AS soLuongDaTra, "
                + "SUM(ctdh.thanhTien) OVER () AS tongTienDongHang "
                + "FROM HoaDon hd "
                + "JOIN ChiTietDonHang ctdh ON ctdh.maDonHang = hd.maDonHang "
                + "JOIN SanPham sp ON sp.maSanPham = ctdh.maSanPham "
                + "LEFT JOIN (SELECT ct.maSanPham, SUM(ct.soLuongTra) AS soLuongDaTra "
                + "FROM ChiTietPhieuTraHang ct JOIN PhieuTraHang pt "
                + "ON pt.maPhieuTra = ct.maPhieuTra WHERE pt.maHoaDon = ? "
                + "GROUP BY ct.maSanPham) daTra ON daTra.maSanPham = ctdh.maSanPham "
                + "WHERE hd.maHoaDon = ? ORDER BY ctdh.maSanPham";
        List<DongHangTra> chiTiet = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int soLuongMua = rs.getInt("soLuong");
                    int soLuongDaTra = rs.getInt("soLuongDaTra");
                    double tongTienDongHang = rs.getDouble("tongTienDongHang");
                    double donGiaHoan = tongTienDongHang > 0
                            ? rs.getDouble("thanhTien") / tongTienDongHang
                            * tongThanhToan / soLuongMua : 0;
                    chiTiet.add(new DongHangTra(
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            soLuongMua,
                            soLuongDaTra,
                            donGiaHoan));
                }
            }
        }
        if (chiTiet.isEmpty()) {
            throw new SQLException("Hóa đơn không có sản phẩm để xử lý trả hàng.");
        }
        return chiTiet;
    }

    public KetQuaTraHang taoPhieuTra(String maHoaDon, String tenNhanVien,
                                     String loaiXuLy, Map<String, Integer> soLuongTra,
                                     Map<String, Integer> soLuongDoi, String ghiChu) throws SQLException {
        String maCanTra = maHoaDon == null ? "" : maHoaDon.trim();
        String nhanVien = tenNhanVien == null ? "" : tenNhanVien.trim();
        if (maCanTra.isEmpty() || nhanVien.isEmpty()) {
            throw new IllegalArgumentException("Thiếu mã hóa đơn hoặc nhân viên xử lý.");
        }
        if (!TRA_HOAN_TIEN.equals(loaiXuLy) && !DOI_HANG.equals(loaiXuLy)) {
            throw new IllegalArgumentException("Loại xử lý trả hàng không hợp lệ.");
        }
        if (soLuongTra == null || soLuongTra.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một sản phẩm và số lượng cần trả.");
        }
        for (Map.Entry<String, Integer> entry : soLuongTra.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()
                    || entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Số lượng trả phải lớn hơn 0.");
            }
        }
        if (soLuongDoi == null) {
            soLuongDoi = Collections.emptyMap();
        }
        if (DOI_HANG.equals(loaiXuLy) && soLuongDoi.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm thay thế khi đổi hàng.");
        }
        if (TRA_HOAN_TIEN.equals(loaiXuLy) && !soLuongDoi.isEmpty()) {
            throw new IllegalArgumentException("Phiếu hoàn tiền không được có sản phẩm đổi.");
        }
        for (Map.Entry<String, Integer> entry : soLuongDoi.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()
                    || entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm đổi phải lớn hơn 0.");
            }
        }

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maDonHang;
                double tongThanhToan;
                String sqlHoaDon = "SELECT maDonHang, trangThai, tongThanhToan "
                        + "FROM HoaDon WITH (UPDLOCK, HOLDLOCK) WHERE maHoaDon = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlHoaDon)) {
                    stmt.setString(1, maCanTra);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy hóa đơn " + maCanTra + ".");
                        }
                        if (!"Đã thanh toán".equals(rs.getString("trangThai"))) {
                            throw new SQLException("Hóa đơn không còn ở trạng thái có thể trả hàng.");
                        }
                        maDonHang = rs.getString("maDonHang");
                        tongThanhToan = rs.getDouble("tongThanhToan");
                    }
                }

                String maNhanVien = timMaNhanVien(con, nhanVien);
                Map<String, DongHangTra> cacDongHoaDon = docCacDongKhoa(con, maCanTra, tongThanhToan);
                double tongGiaTriHangTra = 0;
                double tienHoanDuKien = 0;
                double tongGiaTriHangDoi = 0;
                boolean daHoanTat = true;
                for (DongHangTra dong : cacDongHoaDon.values()) {
                    int soLuong = soLuongTra.containsKey(dong.getMaSanPham())
                            ? soLuongTra.get(dong.getMaSanPham()) : 0;
                    if (soLuong < 0 || soLuong > dong.getSoLuongConLai()) {
                        throw new SQLException("Số lượng trả sản phẩm " + dong.getMaSanPham()
                                + " vượt quá số lượng còn được trả (" + dong.getSoLuongConLai() + ").");
                    }
                    if (dong.getSoLuongDaTra() + soLuong < dong.getSoLuongMua()) {
                        daHoanTat = false;
                    }
                    double giaTri = soLuong * dong.getGiaTriHoanMoiDonVi();
                    tongGiaTriHangTra += giaTri;
                    if (TRA_HOAN_TIEN.equals(loaiXuLy)) {
                        tienHoanDuKien += giaTri;
                    }
                }
                for (String maSanPham : soLuongTra.keySet()) {
                    if (!cacDongHoaDon.containsKey(maSanPham)) {
                        throw new SQLException("Sản phẩm " + maSanPham + " không thuộc hóa đơn này.");
                    }
                }
                Map<String, Double> giaBanSanPhamDoi = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : soLuongDoi.entrySet()) {
                    String sqlSanPhamDoi = "SELECT giaBan, soLuongTon, trangThai FROM SanPham "
                            + "WITH (UPDLOCK, ROWLOCK) WHERE maSanPham = ?";
                    try (PreparedStatement stmt = con.prepareStatement(sqlSanPhamDoi)) {
                        stmt.setString(1, entry.getKey());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Không tìm thấy sản phẩm đổi " + entry.getKey() + ".");
                            }
                            int soLuongCoTheDung = rs.getInt("soLuongTon")
                                    + soLuongTra.getOrDefault(entry.getKey(), 0);
                            if (!"Đang kinh doanh".equals(rs.getString("trangThai"))
                                    && !"Hết hàng".equals(rs.getString("trangThai"))) {
                                throw new SQLException("Sản phẩm " + entry.getKey()
                                        + " hiện không kinh doanh.");
                            }
                            if (soLuongCoTheDung < entry.getValue()) {
                                throw new SQLException("Sản phẩm đổi " + entry.getKey()
                                        + " không đủ tồn kho.");
                            }
                            double giaBan = rs.getDouble("giaBan");
                            giaBanSanPhamDoi.put(entry.getKey(), giaBan);
                            tongGiaTriHangDoi += giaBan * entry.getValue();
                        }
                    }
                }
                double chenhLechThuThem = tongGiaTriHangDoi - tongGiaTriHangTra;

                String sqlPhieu = "INSERT INTO PhieuTraHang "
                        + "(maHoaDon, maNhanVien, loaiXuLy, tongGiaTriHangTra, "
                        + "tongGiaTriHangDoi, tienHoan, chenhLechThuThem, ghiChu) "
                        + "OUTPUT INSERTED.maPhieuTra VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                int maPhieuTra;
                try (PreparedStatement stmt = con.prepareStatement(sqlPhieu)) {
                    stmt.setString(1, maCanTra);
                    stmt.setString(2, maNhanVien);
                    stmt.setString(3, loaiXuLy);
                    stmt.setDouble(4, tongGiaTriHangTra);
                    stmt.setDouble(5, tongGiaTriHangDoi);
                    stmt.setDouble(6, tienHoanDuKien);
                    stmt.setDouble(7, chenhLechThuThem);
                    stmt.setString(8, ghiChu == null || ghiChu.trim().isEmpty()
                            ? null : ghiChu.trim());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tạo được mã phiếu trả hàng.");
                        }
                        maPhieuTra = rs.getInt(1);
                    }
                }

                String sqlChiTiet = "INSERT INTO ChiTietPhieuTraHang "
                        + "(maPhieuTra, maSanPham, soLuongTra, giaTriHoanMoiDonVi, thanhTien) "
                        + "VALUES (?, ?, ?, ?, ?)";
                String sqlCapNhatKho = "UPDATE SanPham SET soLuongTon = soLuongTon + ?, "
                        + "trangThai = CASE WHEN trangThai = N'Hết hàng' "
                        + "THEN N'Đang kinh doanh' ELSE trangThai END, ngayCapNhat = GETDATE() "
                        + "WHERE maSanPham = ?";
                try (PreparedStatement insert = con.prepareStatement(sqlChiTiet);
                     PreparedStatement updateStock = con.prepareStatement(sqlCapNhatKho)) {
                    for (Map.Entry<String, Integer> entry : soLuongTra.entrySet()) {
                        if (entry.getValue() == 0) {
                            continue;
                        }
                        DongHangTra dong = cacDongHoaDon.get(entry.getKey());
                        double donGiaHoan = dong.getGiaTriHoanMoiDonVi();
                        insert.setInt(1, maPhieuTra);
                        insert.setString(2, entry.getKey());
                        insert.setInt(3, entry.getValue());
                        insert.setDouble(4, donGiaHoan);
                        insert.setDouble(5, donGiaHoan * entry.getValue());
                        insert.addBatch();

                        updateStock.setInt(1, entry.getValue());
                        updateStock.setString(2, entry.getKey());
                        updateStock.addBatch();
                    }
                    insert.executeBatch();
                    updateStock.executeBatch();
                }

                String sqlChiTietDoi = "INSERT INTO ChiTietDoiHang "
                        + "(maPhieuTra, maSanPham, soLuongDoi, donGiaDoi, thanhTien) "
                        + "VALUES (?, ?, ?, ?, ?)";
                String sqlTruKho = "UPDATE SanPham SET soLuongTon = soLuongTon - ?, "
                        + "trangThai = CASE WHEN soLuongTon - ? = 0 "
                        + "THEN N'Hết hàng' ELSE trangThai END, ngayCapNhat = GETDATE() "
                        + "WHERE maSanPham = ? AND soLuongTon >= ?";
                try (PreparedStatement insert = con.prepareStatement(sqlChiTietDoi);
                     PreparedStatement updateStock = con.prepareStatement(sqlTruKho)) {
                    for (Map.Entry<String, Integer> entry : soLuongDoi.entrySet()) {
                        int soLuong = entry.getValue();
                        double donGia = giaBanSanPhamDoi.get(entry.getKey());
                        insert.setInt(1, maPhieuTra);
                        insert.setString(2, entry.getKey());
                        insert.setInt(3, soLuong);
                        insert.setDouble(4, donGia);
                        insert.setDouble(5, donGia * soLuong);
                        insert.addBatch();

                        updateStock.setInt(1, soLuong);
                        updateStock.setInt(2, soLuong);
                        updateStock.setString(3, entry.getKey());
                        updateStock.setInt(4, soLuong);
                        updateStock.addBatch();
                    }
                    insert.executeBatch();
                    int[] capNhat = updateStock.executeBatch();
                    for (int rows : capNhat) {
                        if (rows == 0) {
                            throw new SQLException("Tồn kho sản phẩm đổi vừa thay đổi. Vui lòng thử lại.");
                        }
                    }
                }

                if (TRA_HOAN_TIEN.equals(loaiXuLy) && daHoanTat) {
                    try (PreparedStatement stmt = con.prepareStatement(
                            "UPDATE HoaDon SET trangThai = N'Hoàn tiền', ngayCapNhat = GETDATE() "
                                    + "WHERE maHoaDon = ?")) {
                        stmt.setString(1, maCanTra);
                        stmt.executeUpdate();
                    }
                    try (PreparedStatement stmt = con.prepareStatement(
                            "UPDATE DonHang SET trangThai = N'Trả hàng', ngayCapNhat = GETDATE() "
                                    + "WHERE maDonHang = ?")) {
                        stmt.setString(1, maDonHang);
                        stmt.executeUpdate();
                    }
                }

                con.commit();
                return new KetQuaTraHang(maPhieuTra, tongGiaTriHangTra,
                        tongGiaTriHangDoi, tienHoanDuKien, chenhLechThuThem, daHoanTat);
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private String timMaNhanVien(Connection con, String tenNhanVien) throws SQLException {
        String sql = "SELECT TOP 1 maNhanVien FROM NhanVien "
                + "WHERE LTRIM(RTRIM(hoTen)) = ? ORDER BY maNhanVien";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy nhân viên \"" + tenNhanVien
                            + "\" để lập phiếu trả.");
                }
                return rs.getString("maNhanVien");
            }
        }
    }

    private Map<String, DongHangTra> docCacDongKhoa(Connection con, String maHoaDon,
                                                     double tongThanhToan) throws SQLException {
        String sql = "SELECT ctdh.maSanPham, ctdh.soLuong, ctdh.thanhTien, "
                + "COALESCE(daTra.soLuongDaTra, 0) AS soLuongDaTra, "
                + "SUM(ctdh.thanhTien) OVER () AS tongTienDongHang "
                + "FROM HoaDon hd "
                + "JOIN ChiTietDonHang ctdh ON ctdh.maDonHang = hd.maDonHang "
                + "LEFT JOIN (SELECT ct.maSanPham, SUM(ct.soLuongTra) AS soLuongDaTra "
                + "FROM ChiTietPhieuTraHang ct JOIN PhieuTraHang pt "
                + "ON pt.maPhieuTra = ct.maPhieuTra WHERE pt.maHoaDon = ? "
                + "GROUP BY ct.maSanPham) daTra ON daTra.maSanPham = ctdh.maSanPham "
                + "WHERE hd.maHoaDon = ?";
        Map<String, DongHangTra> cacDong = new LinkedHashMap<>();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int soLuongMua = rs.getInt("soLuong");
                    int soLuongDaTra = rs.getInt("soLuongDaTra");
                    double tongTienDongHang = rs.getDouble("tongTienDongHang");
                    double donGiaHoan = tongTienDongHang > 0
                            ? rs.getDouble("thanhTien") / tongTienDongHang
                            * tongThanhToan / soLuongMua : 0;
                    String maSanPham = rs.getString("maSanPham");
                    cacDong.put(maSanPham, new DongHangTra(
                            maSanPham, maSanPham, soLuongMua, soLuongDaTra, donGiaHoan));
                }
            }
        }
        if (cacDong.isEmpty()) {
            throw new SQLException("Hóa đơn không có sản phẩm để xử lý trả hàng.");
        }
        return cacDong;
    }
}
