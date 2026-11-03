package gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import entity.ChiTietDonHang;
import entity.HoaDon;

final class InvoicePdfExporter {
    private static final Color RED = new Color(180, 40, 40);
    private static final Color LABEL_BACKGROUND = new Color(245, 245, 245);
    private static final Color ALTERNATE_ROW = new Color(248, 248, 248);

    private InvoicePdfExporter() {
    }

    static void export(Component parent, HoaDon hoaDon, String maKhuyenMai,
                       List<ChiTietDonHang> chiTiet) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn thành PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("HoaDon-" + hoaDon.getMaHoaDon() + ".pdf"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(parent,
                "Tệp đã tồn tại. Bạn có muốn ghi đè không?", "Xác nhận ghi đè",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            write(file, hoaDon, maKhuyenMai, chiTiet);
            JOptionPane.showMessageDialog(parent, "Đã lưu hóa đơn PDF tại:\n"
                    + file.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | DocumentException exception) {
            JOptionPane.showMessageDialog(parent, "Không thể lưu hóa đơn PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void write(File file, HoaDon hoaDon, String maKhuyenMai,
                              List<ChiTietDonHang> chiTiet)
            throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(findVietnameseFont(),
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.lowagie.text.Font titleFont = font(baseFont, 18, true, RED);
        com.lowagie.text.Font sectionFont = font(baseFont, 10, true, RED);
        com.lowagie.text.Font labelFont = font(baseFont, 9, true, Color.BLACK);
        com.lowagie.text.Font bodyFont = font(baseFont, 9, false, Color.BLACK);
        com.lowagie.text.Font headerFont = font(baseFont, 9, true, Color.WHITE);
        com.lowagie.text.Font totalFont = font(baseFont, 10, true, RED);

        try (FileOutputStream output = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4, 36, 36, 38, 42);
            PdfWriter.getInstance(document, output);
            document.open();
            try {
                Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(4);
                document.add(title);

                Paragraph code = new Paragraph("Mã hóa đơn: " + text(hoaDon.getMaHoaDon()),
                        bodyFont);
                code.setAlignment(Element.ALIGN_CENTER);
                code.setSpacingAfter(12);
                document.add(code);

                PdfPTable information = new PdfPTable(4);
                information.setWidthPercentage(100);
                information.setWidths(new float[]{1.35f, 2.15f, 1.35f, 2.15f});
                addInfo(information, "Ngày lập", date(hoaDon.getNgayLapHoaDon()),
                        "Phương thức", text(hoaDon.getPhuongThucThanhToan()),
                        labelFont, bodyFont);
                addInfo(information, "Mã khách hàng", hoaDon.getKhachHang() == null
                                ? "" : text(hoaDon.getKhachHang().getMaKhachHang()),
                        "Trạng thái", text(hoaDon.getTrangThai()), labelFont, bodyFont);
                addInfo(information, "Tên khách hàng", hoaDon.getKhachHang() == null
                                ? "Khách lẻ" : text(hoaDon.getKhachHang().getHoTen()),
                        "Thu ngân", hoaDon.getNhanVien() == null
                                ? "" : text(hoaDon.getNhanVien().getHoTen()),
                        labelFont, bodyFont);
                addInfo(information, "Khuyến mãi",
                        maKhuyenMai == null ? "Không áp dụng" : maKhuyenMai,
                        "Ghi chú", text(hoaDon.getGhiChu()), labelFont, bodyFont);
                document.add(information);
                document.add(new Paragraph(" "));

                document.add(new Paragraph("CHI TIẾT SẢN PHẨM", sectionFont));
                PdfPTable items = new PdfPTable(6);
                items.setWidthPercentage(100);
                items.setWidths(new float[]{0.75f, 3.15f, 1.35f, 0.75f, 1.25f, 1.45f});
                items.setHeaderRows(1);
                for (String column : new String[]{"STT", "Tên sản phẩm", "Đơn giá",
                        "SL", "Giảm giá", "Thành tiền"}) {
                    cell(items, column, headerFont, RED, Element.ALIGN_CENTER);
                }
                for (int row = 0; row < chiTiet.size(); row++) {
                    ChiTietDonHang item = chiTiet.get(row);
                    Color background = row % 2 == 0 ? Color.WHITE : ALTERNATE_ROW;
                    cell(items, String.valueOf(row + 1), bodyFont, background,
                            Element.ALIGN_CENTER);
                    cell(items, text(item.getSanPham().getTenSanPham()), bodyFont,
                            background, Element.ALIGN_LEFT);
                    cell(items, money(item.getDonGia()), bodyFont, background,
                            Element.ALIGN_RIGHT);
                    cell(items, String.valueOf(item.getSoLuong()), bodyFont, background,
                            Element.ALIGN_RIGHT);
                    cell(items, money(item.getGiamGia()), bodyFont, background,
                            Element.ALIGN_RIGHT);
                    cell(items, money(item.getThanhTien()), bodyFont, background,
                            Element.ALIGN_RIGHT);
                }
                if (chiTiet.isEmpty()) {
                    PdfPCell empty = new PdfPCell(new Phrase("Không có sản phẩm.", bodyFont));
                    empty.setColspan(6);
                    empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                    empty.setPadding(6);
                    items.addCell(empty);
                }
                document.add(items);
                document.add(new Paragraph(" "));

                PdfPTable totals = new PdfPTable(2);
                totals.setWidthPercentage(52);
                totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
                total(totals, "Tổng tiền hàng", money(hoaDon.getTongTienHang()), bodyFont);
                total(totals, "Giảm giá", money(hoaDon.getTienGiamGia()), bodyFont);
                total(totals, "Thuế VAT", money(hoaDon.getThueVAT()), bodyFont);
                total(totals, "TỔNG TIỀN", money(hoaDon.getTongThanhToan()), totalFont);
                total(totals, "Tiền khách đưa", money(hoaDon.getTienKhachDua()), bodyFont);
                total(totals, "Tiền thối", money(hoaDon.getTienTraLai()), bodyFont);
                document.add(totals);
            } finally {
                document.close();
            }
        }
    }

    private static void addInfo(PdfPTable table, String leftLabel, String leftValue,
                                String rightLabel, String rightValue,
                                com.lowagie.text.Font labelFont,
                                com.lowagie.text.Font bodyFont) {
        cell(table, leftLabel, labelFont, LABEL_BACKGROUND, Element.ALIGN_LEFT);
        cell(table, leftValue, bodyFont, Color.WHITE, Element.ALIGN_LEFT);
        cell(table, rightLabel, labelFont, LABEL_BACKGROUND, Element.ALIGN_LEFT);
        cell(table, rightValue, bodyFont, Color.WHITE, Element.ALIGN_LEFT);
    }

    private static void total(PdfPTable table, String label, String value,
                              com.lowagie.text.Font font) {
        cell(table, label, font, LABEL_BACKGROUND, Element.ALIGN_LEFT);
        cell(table, value, font, Color.WHITE, Element.ALIGN_RIGHT);
    }

    private static void cell(PdfPTable table, String value,
                             com.lowagie.text.Font font, Color background, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static com.lowagie.text.Font font(BaseFont baseFont, float size, boolean bold,
                                               Color color) {
        return new com.lowagie.text.Font(baseFont, size,
                bold ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL, color);
    }

    private static String date(Date value) {
        return value == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(value);
    }

    private static String money(double value) {
        return String.format(Locale.ROOT, "%,.0f đ", value);
    }

    private static String text(String value) {
        return value == null || value.trim().isEmpty() ? "" : value;
    }

    private static String findVietnameseFont() throws IOException {
        String windows = System.getenv("WINDIR");
        if (windows != null) {
            for (String name : new String[]{"arial.ttf", "segoeui.ttf", "tahoma.ttf"}) {
                File file = new File(new File(windows, "Fonts"), name);
                if (file.isFile()) {
                    return file.getAbsolutePath();
                }
            }
        }
        throw new IOException("Không tìm thấy font hỗ trợ tiếng Việt.");
    }
}
