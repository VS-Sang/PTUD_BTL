package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;

public class ThongKe_DAO {

    // =========================================================================
    // 1. CHỨC NĂNG: DOANH THU THEO THÁNG
    // =========================================================================

    /**
     * Lấy danh sách các năm có phát sinh hóa đơn để đổ lên JComboBox chọn năm
     */
    public ArrayList<Integer> getDanhSachNam() {
        ArrayList<Integer> dsNam = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(ngayLapHoaDon) AS Nam FROM HoaDon ORDER BY Nam DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsNam.add(rs.getInt("Nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (dsNam.isEmpty()) {
            dsNam.add(java.time.Year.now().getValue());
        }
        return dsNam;
    }

    /**
     * Lấy danh sách Năm có dữ liệu hóa đơn để đổ lên JComboBox chọn năm
     */
    public ArrayList<Integer> getDanhSachNamCoData() {
        ArrayList<Integer> ds = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(ngayLapHoaDon) AS Nam FROM HoaDon ORDER BY Nam DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(rs.getInt("Nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ds.isEmpty()) {
            ds.add(java.time.Year.now().getValue());
        }
        return ds;
    }

    /**
     * Lấy thống kê chi tiết từng tháng của một năm (chỉ các tháng có dữ liệu thực tế).
     * Tính lợi nhuận ước tính = 15% doanh thu, tăng trưởng so với tháng trước.
     * Trả về: [mThang (int), soHD (int), doanhThu (double), loiNhuan (double), tangTruong (double)]
     */
    public ArrayList<Object[]> getDoanhThuTheoThang(int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT MONTH(ngayLapHoaDon) AS Thang, "
                + "COUNT(maHoaDon) AS SoHD, "
                + "ISNULL(SUM(tongThanhToan), 0) AS DoanhThu "
                + "FROM HoaDon "
                + "WHERE YEAR(ngayLapHoaDon) = ? "
                + "AND (trangThai = N'Đã thanh toán' OR trangThai LIKE N'%thanh to%') "
                + "GROUP BY MONTH(ngayLapHoaDon) "
                + "ORDER BY Thang";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("Thang");
                    int soHD = rs.getInt("SoHD");
                    double doanhThu = rs.getDouble("DoanhThu");
                    double loiNhuan = doanhThu * 0.15; // Ước tính lợi nhuận 15%
                    ds.add(new Object[]{thang, soHD, doanhThu, loiNhuan});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Tính tăng trưởng so với tháng liền trước (trong cùng danh sách)
        ArrayList<Object[]> result = new ArrayList<>();
        for (int i = 0; i < ds.size(); i++) {
            Object[] curr = ds.get(i);
            double dtCurr = (double) curr[2];
            double tangTruong = 0;
            if (i > 0) {
                double dtPrev = (double) ds.get(i - 1)[2];
                if (dtPrev > 0) {
                    tangTruong = ((dtCurr - dtPrev) / dtPrev) * 100;
                }
            }
            result.add(new Object[]{curr[0], curr[1], curr[2], curr[3], tangTruong});
        }
        return result;
    }

    /**
     * Lấy 4 chỉ số KPI tổng hợp của cả năm:
     * [0] double: Tổng doanh thu cả năm
     * [1] double: Tổng lợi nhuận ước tính (15%)
     * [2] int: Tổng số hóa đơn
     * [3] double: Doanh thu tháng cao nhất
     * [4] int: Tháng có doanh thu cao nhất
     */
    public Object[] getKPICaNam(int nam) {
        double tongDT = 0;
        int tongHD = 0;
        double maxDT = 0;
        int thangMax = 0;

        String sql = "SELECT MONTH(ngayLapHoaDon) AS Thang, "
                + "COUNT(*) AS SoHD, ISNULL(SUM(tongThanhToan), 0) AS DoanhThu "
                + "FROM HoaDon WHERE YEAR(ngayLapHoaDon) = ? "
                + "AND (trangThai = N'Đã thanh toán' OR trangThai LIKE N'%thanh to%') "
                + "GROUP BY MONTH(ngayLapHoaDon)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int t = rs.getInt("Thang");
                    int hd = rs.getInt("SoHD");
                    double dt = rs.getDouble("DoanhThu");
                    tongDT += dt;
                    tongHD += hd;
                    if (dt > maxDT) {
                        maxDT = dt;
                        thangMax = t;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double loiNhuan = tongDT * 0.15;
        return new Object[]{tongDT, loiNhuan, tongHD, maxDT, thangMax};
    }

    /**
     * Lấy doanh thu tháng cũ nhất có dữ liệu (phục vụ hiển thị ghi chú "bắt đầu từ tháng X/YYYY")
     * Trả về: [thang (int), nam (int)]
     */
    public int[] getThangNamDauTien() {
        String sql = "SELECT MONTH(MIN(ngayLapHoaDon)) AS Thang, YEAR(MIN(ngayLapHoaDon)) AS Nam FROM HoaDon";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new int[]{rs.getInt("Thang"), rs.getInt("Nam")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{java.time.LocalDate.now().getMonthValue(),
                         java.time.LocalDate.now().getYear()};
    }

    /**
     * Thống kê doanh thu từng tháng (phiên bản cũ - giữ lại để tương thích).
     */
    public ArrayList<Object[]> getDoanhThu12Thang(int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        for (Object[] row : getDoanhThuTheoThang(nam)) {
            ds.add(new Object[]{
                    "Tháng " + row[0],
                    row[1],
                    0.0,
                    row[2]
            });
        }
        return ds;
    }

    // =========================================================================
    // 2. CHỨC NĂNG: SẢN PHẨM BÁN CHẠY
    // =========================================================================

    /**
     * Lấy Top N sản phẩm bán chạy nhất (dựa trên tổng số lượng bán ra).
     * Có thể lọc theo tháng và năm (nếu thang = 0 thì lấy cả năm, nếu nam = 0 thì lấy tất cả).
     * Trả về danh sách dòng cho JTable:
     * [Mã SP, Tên sản phẩm, Danh mục, Đơn vị tính, Số lượng đã bán, Tổng doanh thu mang lại]
     */
    public ArrayList<Object[]> getTopSanPhamBanChay(int top, int thang, int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP (?) sp.maSanPham, sp.tenSanPham, dm.tenDanhMuc, sp.donViTinh, ")
           .append("SUM(ct.soLuong) AS TongSoLuongBan, ")
           .append("SUM(ct.thanhTien) AS TongDoanhThu ")
           .append("FROM ChiTietDonHang ct ")
           .append("JOIN SanPham sp ON ct.maSanPham = sp.maSanPham ")
           .append("LEFT JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc ")
           .append("JOIN HoaDon hd ON ct.maDonHang = hd.maDonHang ")
           .append("WHERE (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') ");

        if (thang > 0) {
            sql.append("AND MONTH(hd.ngayLapHoaDon) = ? ");
        }
        if (nam > 0) {
            sql.append("AND YEAR(hd.ngayLapHoaDon) = ? ");
        }

        sql.append("GROUP BY sp.maSanPham, sp.tenSanPham, dm.tenDanhMuc, sp.donViTinh ")
           .append("ORDER BY TongSoLuongBan DESC");

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, top);
            if (thang > 0) {
                stmt.setInt(paramIndex++, thang);
            }
            if (nam > 0) {
                stmt.setInt(paramIndex++, nam);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            rs.getString("tenDanhMuc") != null ? rs.getString("tenDanhMuc") : "",
                            rs.getString("donViTinh"),
                            rs.getInt("TongSoLuongBan"),
                            rs.getDouble("TongDoanhThu")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // =========================================================================
    // 3. CHỨC NĂNG: BÁO CÁO TỔNG HỢP
    // =========================================================================

    /**
     * Lấy các số liệu tổng quan (KPIs) trong một khoảng thời gian:
     * Trả về mảng 4 giá trị:
     * [0] Tổng doanh thu (double)
     * [1] Tổng số hóa đơn (int)
     * [2] Tổng số sản phẩm đã bán (int)
     * [3] Tổng số khách hàng phát sinh giao dịch (int)
     */
    public Object[] getSoLieuTongQuan(Date tuNgay, Date denNgay) {
        double tongDoanhThu = 0;
        int soHoaDon = 0;
        int tongSanPhamBan = 0;
        int soKhachHang = 0;

        String sqlHD = "SELECT COUNT(DISTINCT maHoaDon) AS SoHD, "
                + "ISNULL(SUM(tongThanhToan), 0) AS TongTien, "
                + "COUNT(DISTINCT maKhachHang) AS SoKH "
                + "FROM HoaDon "
                + "WHERE (trangThai = N'Đã thanh toán' OR trangThai LIKE N'%thanh to%') "
                + "AND CAST(ngayLapHoaDon AS DATE) BETWEEN ? AND ?";

        String sqlSP = "SELECT ISNULL(SUM(ct.soLuong), 0) AS TongSP "
                + "FROM ChiTietDonHang ct "
                + "JOIN HoaDon hd ON ct.maDonHang = hd.maDonHang "
                + "WHERE (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') "
                + "AND CAST(hd.ngayLapHoaDon AS DATE) BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getConnection()) {
            // 1. Thống kê Hóa đơn, Doanh thu, Khách hàng
            try (PreparedStatement stmt = con.prepareStatement(sqlHD)) {
                stmt.setDate(1, tuNgay);
                stmt.setDate(2, denNgay);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        soHoaDon = rs.getInt("SoHD");
                        tongDoanhThu = rs.getDouble("TongTien");
                        soKhachHang = rs.getInt("SoKH");
                    }
                }
            }

            // 2. Thống kê tổng số lượng sản phẩm đã bán
            try (PreparedStatement stmt = con.prepareStatement(sqlSP)) {
                stmt.setDate(1, tuNgay);
                stmt.setDate(2, denNgay);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        tongSanPhamBan = rs.getInt("TongSP");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Object[]{tongDoanhThu, soHoaDon, tongSanPhamBan, soKhachHang};
    }

    /**
     * Báo cáo tổng hợp doanh số theo từng Nhân viên trong một khoảng thời gian.
     * Trả về danh sách dòng cho JTable:
     * [Mã NV, Tên NV, Số hóa đơn đã lập, Tổng doanh thu bán được]
     */
    public ArrayList<Object[]> getBaoCaoDoanhThuTheoNhanVien(Date tuNgay, Date denNgay) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT nv.maNhanVien, nv.hoTen, "
                + "COUNT(hd.maHoaDon) AS SoHoaDon, "
                + "ISNULL(SUM(hd.tongThanhToan), 0) AS TongDoanhThu "
                + "FROM HoaDon hd "
                + "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien "
                + "WHERE (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') "
                + "AND CAST(hd.ngayLapHoaDon AS DATE) BETWEEN ? AND ? "
                + "GROUP BY nv.maNhanVien, nv.hoTen "
                + "ORDER BY TongDoanhThu DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, tuNgay);
            stmt.setDate(2, denNgay);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{
                            rs.getString("maNhanVien"),
                            rs.getString("hoTen"),
                            rs.getInt("SoHoaDon"),
                            rs.getDouble("TongDoanhThu")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Báo cáo cơ cấu doanh thu theo Phương thức thanh toán (Tiền mặt, Chuyển khoản, Thẻ...).
     * Trả về danh sách dòng cho JTable hoặc Biểu đồ:
     * [Phương thức thanh toán, Số giao dịch, Tổng tiền]
     */
    public ArrayList<Object[]> getBaoCaoPhuongThucThanhToan(Date tuNgay, Date denNgay) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT phuongThucThanhToan, "
                + "COUNT(maHoaDon) AS SoGiaoDich, "
                + "ISNULL(SUM(tongThanhToan), 0) AS TongTien "
                + "FROM HoaDon "
                + "WHERE (trangThai = N'Đã thanh toán' OR trangThai LIKE N'%thanh to%') "
                + "AND CAST(ngayLapHoaDon AS DATE) BETWEEN ? AND ? "
                + "GROUP BY phuongThucThanhToan "
                + "ORDER BY TongTien DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, tuNgay);
            stmt.setDate(2, denNgay);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{
                            rs.getString("phuongThucThanhToan"),
                            rs.getInt("SoGiaoDich"),
                            rs.getDouble("TongTien")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy danh sách chuỗi "Tháng MM/yyyy" có phát sinh hóa đơn để đổ lên JComboBox
     */
    public ArrayList<String> getDanhSachThangNam() {
        ArrayList<String> ds = new ArrayList<>();
        String sql = "SELECT DISTINCT MONTH(ngayLapHoaDon) AS Thang, YEAR(ngayLapHoaDon) AS Nam "
                + "FROM HoaDon ORDER BY Nam DESC, Thang DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int thang = rs.getInt("Thang");
                int nam = rs.getInt("Nam");
                ds.add(String.format("Tháng %02d/%d", thang, nam));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ds.isEmpty()) {
            ds.add("Tháng 10/2026");
        }
        return ds;
    }

    /**
     * Lấy 4 chỉ số tổng hợp cho 4 Card thẻ KPI theo tháng/năm:
     * [0] int: Số hóa đơn trong tháng
     * [1] double: Tổng doanh thu
     * [2] int: Số khách hàng mới (hoặc khách mua)
     * [3] int: Số đơn đổi/trả hoặc hủy
     */
    public Object[] getThongKeTongQuanTheoThang(int thang, int nam) {
        int soHoaDon = 0;
        double tongDoanhThu = 0;
        int khachHangMoi = 0;
        int doiTra = 0;

        String sqlHD = "SELECT COUNT(*) AS SoHD, "
                + "ISNULL(SUM(tongThanhToan), 0) AS TongTien "
                + "FROM HoaDon "
                + "WHERE MONTH(ngayLapHoaDon) = ? AND YEAR(ngayLapHoaDon) = ? "
                + "AND (trangThai = N'Đã thanh toán' OR trangThai LIKE N'%thanh to%')";

        String sqlKH = "SELECT COUNT(*) AS SoKH FROM KhachHang "
                + "WHERE MONTH(ngayTao) = ? AND YEAR(ngayTao) = ?";

        String sqlDoiTra = "SELECT COUNT(*) AS SoHuy FROM HoaDon "
                + "WHERE MONTH(ngayLapHoaDon) = ? AND YEAR(ngayLapHoaDon) = ? "
                + "AND (trangThai LIKE N'%hủy%' OR trangThai LIKE N'%trả%' OR trangThai LIKE N'%huy%')";

        try (Connection con = ConnectDB.getConnection()) {
            // 1. Số hóa đơn và tổng doanh thu
            try (PreparedStatement stmt = con.prepareStatement(sqlHD)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        soHoaDon = rs.getInt("SoHD");
                        tongDoanhThu = rs.getDouble("TongTien");
                    }
                }
            }

            // 2. Khách hàng mới
            try (PreparedStatement stmt = con.prepareStatement(sqlKH)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        khachHangMoi = rs.getInt("SoKH");
                    }
                }
            }
            // Nếu không có khách mới theo ngày tạo, đếm khách hàng phát sinh hóa đơn trong tháng
            if (khachHangMoi == 0) {
                String sqlKHThang = "SELECT COUNT(DISTINCT maKhachHang) FROM HoaDon "
                        + "WHERE MONTH(ngayLapHoaDon) = ? AND YEAR(ngayLapHoaDon) = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlKHThang)) {
                    stmt.setInt(1, thang);
                    stmt.setInt(2, nam);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            khachHangMoi = rs.getInt(1);
                        }
                    }
                }
            }

            // 3. Đổi trả / Hủy
            try (PreparedStatement stmt = con.prepareStatement(sqlDoiTra)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        doiTra = rs.getInt("SoHuy");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Object[]{soHoaDon, tongDoanhThu, khachHangMoi, doiTra};
    }

    /**
     * Lấy danh sách hiệu suất nhân viên bán hàng theo tháng:
     * Trả về từng dòng: [Nhân viên, Số hóa đơn, Doanh thu, TB/đơn, Đánh giá]
     */
    public ArrayList<Object[]> getHieuSuatNhanVienTheoThang(int thang, int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT nv.maNhanVien, nv.hoTen, "
                + "COUNT(hd.maHoaDon) AS SoHoaDon, "
                + "ISNULL(SUM(hd.tongThanhToan), 0) AS TongDoanhThu "
                + "FROM HoaDon hd "
                + "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien "
                + "WHERE MONTH(hd.ngayLapHoaDon) = ? AND YEAR(hd.ngayLapHoaDon) = ? "
                + "AND (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') "
                + "GROUP BY nv.maNhanVien, nv.hoTen "
                + "ORDER BY TongDoanhThu DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNhanVien");
                    String hoTen = rs.getString("hoTen");
                    int soHD = rs.getInt("SoHoaDon");
                    double doanhThu = rs.getDouble("TongDoanhThu");
                    double tbDon = soHD > 0 ? (doanhThu / soHD) : 0;

                    // Đánh giá dựa trên doanh thu
                    String danhGia;
                    if (doanhThu >= 25000000 || soHD >= 2) {
                        danhGia = "Xuất sắc";
                    } else if (doanhThu >= 10000000 || soHD >= 1) {
                        danhGia = "Tốt";
                    } else {
                        danhGia = "Trung bình";
                    }

                    ds.add(new Object[]{
                            hoTen + " (" + maNV + ")",
                            soHD,
                            doanhThu,
                            tbDon,
                            danhGia
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy Top 5 sản phẩm bán chạy nhất theo tháng và năm
     * Trả về từng dòng: [Hạng (int), Tên sản phẩm (String), Số lượng bán (int), Doanh thu (double)]
     */
    public ArrayList<Object[]> getTop5SanPhamTheoThang(int thang, int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT TOP 5 sp.tenSanPham, SUM(ct.soLuong) AS TongSL, SUM(ct.thanhTien) AS DoanhThu "
                + "FROM ChiTietDonHang ct "
                + "JOIN SanPham sp ON ct.maSanPham = sp.maSanPham "
                + "JOIN HoaDon hd ON ct.maDonHang = hd.maDonHang "
                + "WHERE (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') "
                + "AND (? <= 0 OR MONTH(hd.ngayLapHoaDon) = ?) "
                + "AND (? <= 0 OR YEAR(hd.ngayLapHoaDon) = ?) "
                + "GROUP BY sp.maSanPham, sp.tenSanPham "
                + "ORDER BY TongSL DESC, DoanhThu DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            stmt.setInt(4, nam);

            try (ResultSet rs = stmt.executeQuery()) {
                int hang = 1;
                while (rs.next()) {
                    ds.add(new Object[]{
                            hang++,
                            rs.getString("tenSanPham"),
                            rs.getInt("TongSL"),
                            rs.getDouble("DoanhThu")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Thống kê doanh thu theo danh mục sản phẩm (kèm tỷ lệ phần trăm):
     * Trả về danh sách: [Tên danh mục (String), Doanh thu (double), Tỷ lệ % (int)]
     */
    public ArrayList<Object[]> getDoanhThuTheoDanhMuc(int thang, int nam) {
        ArrayList<Object[]> ds = new ArrayList<>();
        String sql = "SELECT dm.tenDanhMuc, ISNULL(SUM(ct.thanhTien), 0) AS DoanhThu "
                + "FROM ChiTietDonHang ct "
                + "JOIN SanPham sp ON ct.maSanPham = sp.maSanPham "
                + "JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc "
                + "JOIN HoaDon hd ON ct.maDonHang = hd.maDonHang "
                + "WHERE (hd.trangThai = N'Đã thanh toán' OR hd.trangThai LIKE N'%thanh to%') "
                + "AND (? <= 0 OR MONTH(hd.ngayLapHoaDon) = ?) "
                + "AND (? <= 0 OR YEAR(hd.ngayLapHoaDon) = ?) "
                + "GROUP BY dm.maDanhMuc, dm.tenDanhMuc "
                + "ORDER BY DoanhThu DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            stmt.setInt(4, nam);

            try (ResultSet rs = stmt.executeQuery()) {
                double tongTienTatCa = 0;
                ArrayList<Object[]> rawList = new ArrayList<>();
                while (rs.next()) {
                    String tenDM = rs.getString("tenDanhMuc");
                    double dt = rs.getDouble("DoanhThu");
                    tongTienTatCa += dt;
                    rawList.add(new Object[]{tenDM, dt});
                }

                for (Object[] item : rawList) {
                    String tenDM = item[0].toString();
                    double dt = (double) item[1];
                    int phanTram = tongTienTatCa > 0 ? (int) Math.round((dt / tongTienTatCa) * 100) : 0;
                    ds.add(new Object[]{tenDM, dt, phanTram});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
