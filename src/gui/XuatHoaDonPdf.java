package gui;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import entity.ChiTietDonHang;
import entity.HoaDon;

final class XuatHoaDonPdf {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0 đ");

    private XuatHoaDonPdf() {
    }

    static void export(Component parent, HoaDon hoaDon, String maKhuyenMai,
                       List<? extends ChiTietDonHang> chiTiet) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn thành PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("hoa-don-" + hoaDon.getMaHoaDon() + ".pdf"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(UiDialogOwner.resolve(parent),
                "Tệp đã tồn tại. Bạn có muốn ghi đè không?", "Xác nhận ghi đè",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            writeHoaDon(file, hoaDon, maKhuyenMai, chiTiet);
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(parent), "Đã lưu hóa đơn PDF tại:\n"
                    + file.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(parent), "Không thể lưu hóa đơn PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void writeHoaDon(File file, HoaDon hoaDon, String maKhuyenMai,
                            List<? extends ChiTietDonHang> chiTiet) throws IOException {
        String maHoaDon = hoaDon.getMaHoaDon();
        String tenKhachHang = (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getHoTen() == null)
                ? "Khách lẻ" : hoaDon.getKhachHang().getHoTen();
        String sdtKhachHang = (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getSoDienThoai() == null)
                ? "—" : hoaDon.getKhachHang().getSoDienThoai();
        String tenNhanVien = (hoaDon.getNhanVien() == null || hoaDon.getNhanVien().getHoTen() == null)
                ? "" : hoaDon.getNhanVien().getHoTen();
        String phuongThuc = hoaDon.getPhuongThucThanhToan() == null ? "Tiền mặt" : hoaDon.getPhuongThucThanhToan();
        boolean isChuyenKhoan = "Chuyển khoản".equalsIgnoreCase(phuongThuc.trim());

        List<String[]> metadataRows = new ArrayList<>();
        metadataRows.add(new String[]{"Mã hóa đơn", maHoaDon, "Khách hàng", tenKhachHang});
        metadataRows.add(new String[]{"Ngày lập", date(hoaDon.getNgayLapHoaDon()), "Số điện thoại", sdtKhachHang});
        metadataRows.add(new String[]{"Nhân viên", tenNhanVien, "Phương thức TT", phuongThuc});
        if (maKhuyenMai != null && !maKhuyenMai.trim().isEmpty()) {
            metadataRows.add(new String[]{"Khuyến mãi", maKhuyenMai, "", ""});
        }

        List<String> headers = Arrays.asList("STT", "MÃ SP", "TÊN SẢN PHẨM", "SỐ LƯỢNG", "ĐƠN GIÁ", "GIẢM GIÁ", "THÀNH TIỀN");
        List<String[]> rows = new ArrayList<>();
        int tongSoLuong = 0;
        if (chiTiet != null) {
            int stt = 1;
            for (ChiTietDonHang item : chiTiet) {
                String maSP = item.getSanPham() == null ? "" : item.getSanPham().getMaSanPham();
                String tenSP = item.getSanPham() == null ? "" : item.getSanPham().getTenSanPham();
                int sl = item.getSoLuong();
                tongSoLuong += sl;
                double donGia = item.getDonGia();
                double giamGia = item.getGiamGia();
                double thanhTien = item.getThanhTien();
                rows.add(new String[]{
                        String.valueOf(stt++),
                        maSP,
                        tenSP,
                        String.valueOf(sl),
                        MONEY_FORMAT.format(donGia),
                        MONEY_FORMAT.format(giamGia),
                        MONEY_FORMAT.format(thanhTien)
                });
            }
        }

        List<String[]> totals = new ArrayList<>();
        totals.add(new String[]{"Tổng số lượng", String.valueOf(tongSoLuong)});
        totals.add(new String[]{"Tổng tiền hàng", MONEY_FORMAT.format(hoaDon.getTongTienHang())});
        if (hoaDon.getTienGiamGia() > 0) {
            totals.add(new String[]{"Tiền giảm giá", MONEY_FORMAT.format(hoaDon.getTienGiamGia())});
        }
        totals.add(new String[]{"Thuế VAT (10%)", MONEY_FORMAT.format(hoaDon.getThueVAT())});
        totals.add(new String[]{"TỔNG THANH TOÁN", MONEY_FORMAT.format(hoaDon.getTongThanhToan()), "true"});
        totals.add(new String[]{"Tiền khách đưa", MONEY_FORMAT.format(hoaDon.getTienKhachDua())});
        if (!isChuyenKhoan) {
            totals.add(new String[]{"Tiền thối", MONEY_FORMAT.format(Math.max(0, hoaDon.getTienTraLai()))});
        }

        double[] columnWeights = new double[]{0.22, 0.45, 1.25, 0.35, 0.55, 0.45, 0.65};
        XuatTaiLieuPdf.write(file, "HÓA ĐƠN THANH TOÁN", maHoaDon, metadataRows,
                "DANH SÁCH SẢN PHẨM", headers, rows, columnWeights, totals,
                hoaDon.getGhiChu(), isChuyenKhoan);
    }

    private static String date(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }
}
