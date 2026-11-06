package gui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JOptionPane;

import entity.ChiTietDonHang;
import entity.HoaDon;

final class BoInHoaDon {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private BoInHoaDon() {
    }

    static void print(Component parent, HoaDon hoaDon, List<? extends ChiTietDonHang> chiTiet) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Hóa đơn " + hoaDon.getMaHoaDon());
        job.setPrintable(new InvoicePrintable(hoaDon, chiTiet));
        if (!job.printDialog()) {
            return;
        }
        try {
            job.print();
        } catch (PrinterException exception) {
            JOptionPane.showMessageDialog(parent, "Không thể in hóa đơn:\n"
                    + exception.getMessage(), "Lỗi in hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class InvoicePrintable implements Printable {
        private final HoaDon hoaDon;
        private final List<? extends ChiTietDonHang> chiTiet;

        InvoicePrintable(HoaDon hoaDon, List<? extends ChiTietDonHang> chiTiet) {
            this.hoaDon = hoaDon;
            this.chiTiet = chiTiet;
        }

        @Override
        public int print(Graphics graphics, PageFormat format, int pageIndex) {
            int rowsPerPage = 34;
            int pages = Math.max(1, (chiTiet.size() + rowsPerPage - 1) / rowsPerPage);
            if (pageIndex >= pages) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(format.getImageableX(), format.getImageableY());
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            int y = 20;
            g.drawString("HÓA ĐƠN THANH TOÁN", 210, y);
            y += 18;
            g.drawString("Mã hóa đơn: " + hoaDon.getMaHoaDon(), 20, y);
            y += 14;
            g.drawString("Ngày lập: " + (hoaDon.getNgayLapHoaDon() == null
                    ? "" : DATE.format(hoaDon.getNgayLapHoaDon())), 20, y);
            y += 14;
            g.drawString("Khách hàng: " + (hoaDon.getKhachHang() == null
                    ? "Khách lẻ" : hoaDon.getKhachHang().getHoTen()), 20, y);
            y += 20;
            g.setStroke(new BasicStroke(1));
            g.drawLine(20, y, 560, y);
            y += 14;
            g.drawString("STT", 20, y);
            g.drawString("Sản phẩm", 55, y);
            g.drawString("SL", 350, y);
            g.drawString("Đơn giá", 390, y);
            g.drawString("Thành tiền", 480, y);
            y += 14;

            int start = pageIndex * rowsPerPage;
            int end = Math.min(start + rowsPerPage, chiTiet.size());
            for (int i = start; i < end; i++) {
                ChiTietDonHang item = chiTiet.get(i);
                g.drawString(String.valueOf(i + 1), 20, y);
                g.drawString(shortText(item.getSanPham().getTenSanPham(), 38), 55, y);
                g.drawString(String.valueOf(item.getSoLuong()), 350, y);
                g.drawString(MONEY.format(item.getDonGia()), 390, y);
                g.drawString(MONEY.format(item.getThanhTien()), 480, y);
                y += 14;
            }
            if (pageIndex == pages - 1) {
                y += 8;
                g.drawLine(350, y, 560, y);
                y += 16;
                g.drawString("Tổng tiền hàng: " + MONEY.format(hoaDon.getTongTienHang()), 350, y);
                y += 14;
                g.drawString("Giảm giá: " + MONEY.format(hoaDon.getTienGiamGia()), 350, y);
                y += 14;
                g.drawString("TỔNG THANH TOÁN: " + MONEY.format(hoaDon.getTongThanhToan()), 350, y);
            }
            g.dispose();
            return PAGE_EXISTS;
        }

        private String shortText(String value, int maxLength) {
            if (value == null) {
                return "";
            }
            return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
        }
    }
}
