package gui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import entity.ChiTietHoaDon;
import entity.HoaDon;

final class BoInHoaDon {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final String STORE_NAME = "ĐIỆN MÁY ĐỎ";
    private static final String STORE_ADDRESS = "Địa chỉ: 123, Nguyễn Văn Nghi, Gò Vấp, TP HCM";
    private static final String BANK_NAME = "MB Bank";
    private static final String BANK_ACCOUNT = "09090909006";
    private static final double A5_WIDTH = PageSize.A5.getWidth();
    private static final double A5_HEIGHT = PageSize.A5.getHeight();
    private static final double PAGE_MARGIN = 18;
    private static final double ROW_HEIGHT = 14;
    private static final double TABLE_HEADER_HEIGHT = 18;
    private static final double[] COLUMN_WEIGHTS = {0.06, 0.12, 0.32, 0.06, 0.15, 0.12, 0.17};
    private static final String[] TABLE_HEADERS = {
        "STT", "MÃ SP", "TÊN SẢN PHẨM", "SL", "ĐƠN GIÁ", "GIẢM GIÁ", "THÀNH TIỀN"
    };

    private BoInHoaDon() {
    }

    static void print(Component parent, HoaDon hoaDon, List<ChiTietHoaDon> chiTiet) {
        BufferedImage qr;
        try {
            qr = loadQrIfNeeded(hoaDon);
        } catch (IOException exception) {
            showError(parent, "Không thể tải mã QR chuyển khoản:\n" + exception.getMessage());
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Hóa đơn " + hoaDon.getMaHoaDon());
        job.setPrintable(new InvoicePrintable(hoaDon, chiTiet, qr), a5PageFormat());
        if (!job.printDialog()) {
            return;
        }
        try {
            job.print();
        } catch (PrinterException exception) {
            showError(parent, "Không thể in hóa đơn:\n" + exception.getMessage());
        }
    }

    static void writePdf(File file, HoaDon hoaDon, List<ChiTietHoaDon> chiTiet)
            throws IOException {
        BufferedImage qr = loadQrIfNeeded(hoaDon);
        PageFormat pageFormat = a5PageFormat();
        InvoicePrintable printable = new InvoicePrintable(hoaDon, chiTiet, qr);

        Document document = new Document(PageSize.A5, 0, 0, 0, 0);
        try (FileOutputStream output = new FileOutputStream(file)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            document.open();
            PdfContentByte content = writer.getDirectContent();
            int pageCount = printable.getPageCount(pageFormat);
            for (int page = 0; page < pageCount; page++) {
                if (page > 0) {
                    document.newPage();
                }
                Graphics2D graphics = content.createGraphicsShapes(
                        (float) A5_WIDTH, (float) A5_HEIGHT);
                try {
                    printable.print(graphics, pageFormat, page);
                } finally {
                    graphics.dispose();
                }
                printable.addQrImage(content, pageFormat, page);
            }
            document.close();
        } catch (com.lowagie.text.DocumentException exception) {
            throw new IOException("Không thể tạo hóa đơn PDF.", exception);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static BufferedImage loadQrIfNeeded(HoaDon hoaDon) throws IOException {
        String method = hoaDon.getPhuongThucThanhToan();
        return method != null && "Chuyển khoản".equalsIgnoreCase(method.trim())
                ? XuatTaiLieuPdf.loadPaymentQrImage() : null;
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), message,
                "Lỗi hóa đơn", JOptionPane.ERROR_MESSAGE);
    }

    private static PageFormat a5PageFormat() {
        Paper paper = new Paper();
        paper.setSize(A5_WIDTH, A5_HEIGHT);
        paper.setImageableArea(PAGE_MARGIN, PAGE_MARGIN,
                A5_WIDTH - PAGE_MARGIN * 2, A5_HEIGHT - PAGE_MARGIN * 2);
        PageFormat format = new PageFormat();
        format.setOrientation(PageFormat.PORTRAIT);
        format.setPaper(paper);
        return format;
    }

    private static final class PageSlice {
        final int from;
        final int to;

        PageSlice(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    private static final class InvoicePrintable implements Printable {
        private final HoaDon hoaDon;
        private final List<ChiTietHoaDon> chiTiet;
        private final BufferedImage qr;

        InvoicePrintable(HoaDon hoaDon, List<ChiTietHoaDon> chiTiet, BufferedImage qr) {
            this.hoaDon = hoaDon;
            this.chiTiet = chiTiet == null ? new ArrayList<>() : chiTiet;
            this.qr = qr;
        }

        int getPageCount(PageFormat format) {
            return createPageSlices(format).size();
        }

        void addQrImage(PdfContentByte content, PageFormat format, int pageIndex) throws IOException {
            List<PageSlice> pages = createPageSlices(format);
            if (qr == null || pageIndex != pages.size() - 1) {
                return;
            }

            PageSlice slice = pages.get(pageIndex);
            double tableTop = pageIndex == 0 ? firstTableTop() : continuationTableTop();
            double tableBottom = tableTop + TABLE_HEADER_HEIGHT
                    + (slice.to - slice.from) * ROW_HEIGHT;
            float qrSize = 58;
            float x = (float) (PAGE_MARGIN + 1);
            float y = (float) (A5_HEIGHT - PAGE_MARGIN - tableBottom - 24 - qrSize);
            try {
                com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(qr, null);
                pdfImage.scaleAbsolute(qrSize, qrSize);
                pdfImage.setAbsolutePosition(x, y);
                content.addImage(pdfImage);
            } catch (com.lowagie.text.BadElementException exception) {
                throw new IOException("Không thể thêm mã QR vào hóa đơn PDF.", exception);
            }
        }

        @Override
        public int print(java.awt.Graphics graphics, PageFormat format, int pageIndex) {
            List<PageSlice> pages = createPageSlices(format);
            if (pageIndex < 0 || pageIndex >= pages.size()) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.translate(format.getImageableX(), format.getImageableY());
                g.setClip(0, 0, (int) format.getImageableWidth(),
                        (int) format.getImageableHeight());
                drawPage(g, format, pages.get(pageIndex), pageIndex);
            } finally {
                g.dispose();
            }
            return PAGE_EXISTS;
        }

        private List<PageSlice> createPageSlices(PageFormat format) {
            double pageHeight = format.getImageableHeight();
            double bottomPadding = 8;
            int index = 0;
            int pageNumber = 0;
            List<PageSlice> pages = new ArrayList<>();
            do {
                boolean firstPage = pageNumber == 0;
                double tableTop = firstPage ? firstTableTop() : continuationTableTop();
                int capacity = Math.max(1, (int) Math.floor((pageHeight - bottomPadding
                        - tableTop - TABLE_HEADER_HEIGHT) / ROW_HEIGHT));
                int remaining = chiTiet.size() - index;
                int finalCapacity = Math.max(1, (int) Math.floor((pageHeight - bottomPadding
                        - tableTop - TABLE_HEADER_HEIGHT - tailHeight()) / ROW_HEIGHT));

                if (remaining <= finalCapacity) {
                    pages.add(new PageSlice(index, chiTiet.size()));
                    break;
                }

                int rowsOnPage = Math.min(capacity, remaining - 1);
                pages.add(new PageSlice(index, index + rowsOnPage));
                index += rowsOnPage;
                pageNumber++;
            } while (true);
            return pages;
        }

        private double firstTableTop() {
            return 86;
        }

        private double continuationTableTop() {
            return 28;
        }

        private double tailHeight() {
            double totalsHeight = hoaDon.getTienTraLai() > 0 ? 72 : 60;
            double qrHeight = qr == null ? 0 : 70;
            double noteHeight = wrapNote().size() * 10;
            return 10 + Math.max(totalsHeight, qrHeight) + noteHeight + 20;
        }

        private void drawPage(Graphics2D g, PageFormat format, PageSlice slice, int pageIndex) {
            double width = format.getImageableWidth();
            double[] edges = columnEdges(width);
            int y = pageIndex == 0 ? drawFirstPageHeader(g, width) : drawContinuationHeader(g, width);
            y = drawTableHeader(g, edges, y);

            double totalBeforePage = 0;
            double totalMerchandise = 0;
            for (ChiTietHoaDon item : chiTiet) {
                totalMerchandise += item.getDonGia() * item.getSoLuong();
            }
            for (int i = 0; i < slice.from; i++) {
                totalBeforePage += lineGross(chiTiet.get(i));
            }
            double discountBeforePage = totalMerchandise <= 0 ? 0
                    : Math.round(hoaDon.getTienGiamGia() * totalBeforePage / totalMerchandise * 100) / 100.0;
            for (int i = slice.from; i < slice.to; i++) {
                ChiTietHoaDon item = chiTiet.get(i);
                double discount;
                if (i == chiTiet.size() - 1) {
                    discount = hoaDon.getTienGiamGia() - discountBeforePage;
                } else if (totalMerchandise > 0) {
                    totalBeforePage += lineGross(item);
                    double discountThroughRow = Math.round(
                            hoaDon.getTienGiamGia() * totalBeforePage / totalMerchandise * 100) / 100.0;
                    discount = discountThroughRow - discountBeforePage;
                    discountBeforePage = discountThroughRow;
                } else {
                    discount = 0;
                }
                y = drawItemRow(g, edges, y, i + 1, item, discount);
            }
            if (slice.to == chiTiet.size()) {
                drawInvoiceFooter(g, width, y);
            }
        }

        private int drawFirstPageHeader(Graphics2D g, double width) {
            setFont(g, Font.BOLD, 12);
            drawCentered(g, STORE_NAME, width / 2, 12);
            setFont(g, Font.PLAIN, 7);
            drawCentered(g, STORE_ADDRESS, width / 2, 23);
            setFont(g, Font.BOLD, 10);
            drawCentered(g, "HÓA ĐƠN THANH TOÁN", width / 2, 38);
            setFont(g, Font.PLAIN, 7);
            drawLeft(g, "Mã hóa đơn: " + safe(hoaDon.getMaHoaDon()), 0, 51);
            drawRight(g, "Ngày lập: " + formatDate(hoaDon.getNgayLapHoaDon()), (int) width, 51);
            drawLeft(g, "Khách hàng: " + customerName(), 0, 63);
            if (!customerPhone().isEmpty()) {
                drawRight(g, "SĐT: " + customerPhone(), (int) width, 63);
            }
            drawLeft(g, "Nhân viên: " + employeeName(), 0, 75);
            drawRight(g, "Thanh toán: " + safe(hoaDon.getPhuongThucThanhToan()),
                    (int) width, 75);
            return (int) firstTableTop();
        }

        private int drawContinuationHeader(Graphics2D g, double width) {
            setFont(g, Font.BOLD, 9);
            drawCentered(g, "HÓA ĐƠN " + safe(hoaDon.getMaHoaDon()) + " (TIẾP THEO)",
                    width / 2, 14);
            return (int) continuationTableTop();
        }

        private int drawTableHeader(Graphics2D g, double[] edges, int y) {
            setFont(g, Font.BOLD, 6.5f);
            g.setStroke(new BasicStroke(0.7f));
            g.drawLine((int) edges[0], y, (int) edges[7], y);
            g.drawLine((int) edges[0], y + (int) TABLE_HEADER_HEIGHT,
                    (int) edges[7], y + (int) TABLE_HEADER_HEIGHT);
            for (int col = 0; col <= 7; col++) {
                g.drawLine((int) edges[col], y,
                        (int) edges[col], y + (int) TABLE_HEADER_HEIGHT);
            }
            for (int col = 0; col < TABLE_HEADERS.length; col++) {
                drawCentered(g, TABLE_HEADERS[col], midpoint(edges, col, col + 1), y + 12);
            }
            return y + (int) TABLE_HEADER_HEIGHT;
        }

        private int drawItemRow(Graphics2D g, double[] edges, int y, int index,
                                ChiTietHoaDon item, double discount) {
            int baseline = y + 10;
            setFont(g, Font.PLAIN, 7);
            String code = item.getSanPham() == null ? "" : safe(item.getSanPham().getMaSanPham());
            String name = item.getSanPham() == null ? "" : safe(item.getSanPham().getTenSanPham());
            drawCentered(g, String.valueOf(index), midpoint(edges, 0, 1), baseline);
            drawCentered(g, fitText(g, code, edges[2] - edges[1] - 4),
                    midpoint(edges, 1, 2), baseline);
            drawLeft(g, fitText(g, name, edges[3] - edges[2] - 5),
                    (int) edges[2] + 2, baseline);
            drawCentered(g, String.valueOf(item.getSoLuong()), midpoint(edges, 3, 4), baseline);
            drawRight(g, money(item.getDonGia()), (int) edges[5] - 2, baseline);
            drawRight(g, money(discount), (int) edges[6] - 2, baseline);
            drawRight(g, money(Math.max(0, item.getThanhTien() - discount)),
                    (int) edges[7] - 2, baseline);

            g.setStroke(new BasicStroke(0.35f));
            g.drawLine((int) edges[0], y + (int) ROW_HEIGHT, (int) edges[7], y + (int) ROW_HEIGHT);
            for (int col = 0; col <= 7; col++) {
                g.drawLine((int) edges[col], y, (int) edges[col], y + (int) ROW_HEIGHT);
            }
            return y + (int) ROW_HEIGHT;
        }

        private void drawInvoiceFooter(Graphics2D g, double width, int tableBottom) {
            int y = tableBottom + 9;
            g.setStroke(new BasicStroke(0.7f));
            g.drawLine((int) (width * 0.50), y, (int) width, y);
            y += 12;

            int totalsX = (int) (width * 0.52);
            int totalRows = hoaDon.getTienTraLai() > 0 ? 6 : 5;
            setFont(g, Font.PLAIN, 7);
            drawTotal(g, totalsX, width, y, "Tổng tiền hàng", hoaDon.getTongTienHang(), false);
            y += 11;
            drawTotal(g, totalsX, width, y, "Giảm giá", hoaDon.getTienGiamGia(), false);
            y += 11;
            drawTotal(g, totalsX, width, y, "Thuế VAT", hoaDon.getThueVAT(), false);
            y += 11;
            drawTotal(g, totalsX, width, y, "TỔNG THANH TOÁN", hoaDon.getTongThanhToan(), true);
            y += 11;
            drawTotal(g, totalsX, width, y, "Tiền khách đưa", hoaDon.getTienKhachDua(), false);
            if (totalRows == 6) {
                y += 11;
                drawTotal(g, totalsX, width, y, "Tiền thối", hoaDon.getTienTraLai(), false);
            }

            int bottom = y + 10;
            if (qr != null) {
                int qrSize = 58;
                int qrY = tableBottom + 20;
                setFont(g, Font.BOLD, 6.5f);
                drawLeft(g, "QR CHUYỂN KHOẢN", 1, qrY);
                g.drawImage(qr, 1, qrY + 4, qrSize, qrSize, null);
                setFont(g, Font.PLAIN, 6);
                drawLeft(g, BANK_NAME, qrSize + 5, qrY + 19);
                drawLeft(g, "STK: " + BANK_ACCOUNT, qrSize + 5, qrY + 30);
                bottom = Math.max(bottom, qrY + qrSize + 4);
            }

            List<String> noteLines = wrapNote();
            if (!noteLines.isEmpty()) {
                setFont(g, Font.PLAIN, 6.5f);
                int noteY = bottom + 10;
                for (String line : noteLines) {
                    drawLeft(g, line, 0, noteY);
                    noteY += 9;
                }
                bottom = noteY;
            }
            setFont(g, Font.PLAIN, 7);
            drawCentered(g, "Cảm ơn quý khách đã mua hàng tại Điện Máy Đỏ",
                    width / 2, bottom + 10);
        }

        private double[] columnEdges(double width) {
            double[] edges = new double[COLUMN_WEIGHTS.length + 1];
            for (int i = 0; i < COLUMN_WEIGHTS.length; i++) {
                edges[i + 1] = edges[i] + width * COLUMN_WEIGHTS[i];
            }
            return edges;
        }

        private double lineGross(ChiTietHoaDon item) {
            return item.getDonGia() * item.getSoLuong();
        }

        private double midpoint(double[] edges, int first, int afterLast) {
            return (edges[first] + edges[afterLast]) / 2;
        }

        private void drawTotal(Graphics2D g, int x, double width, int y,
                               String label, double amount, boolean bold) {
            setFont(g, bold ? Font.BOLD : Font.PLAIN, 7);
            drawLeft(g, label + ":", x, y);
            drawRight(g, money(amount), (int) width, y);
        }

        private void drawCentered(Graphics2D g, String text, double center, int baseline) {
            drawLeft(g, text, (int) (center - g.getFontMetrics().stringWidth(text) / 2.0), baseline);
        }

        private void drawLeft(Graphics2D g, String text, int x, int baseline) {
            g.drawString(text, x, baseline);
        }

        private void drawRight(Graphics2D g, String text, int right, int baseline) {
            drawLeft(g, text, right - g.getFontMetrics().stringWidth(text), baseline);
        }

        private String fitText(Graphics2D g, String value, double maxWidth) {
            FontMetrics metrics = g.getFontMetrics();
            String text = value;
            while (!text.isEmpty() && metrics.stringWidth(text) > maxWidth) {
                text = text.substring(0, text.length() - 1);
            }
            return text.length() < value.length() && text.length() > 3
                    ? text.substring(0, text.length() - 3) + "..." : text;
        }

        private String customerName() {
            return hoaDon.getKhachHang() == null ? "Khách lẻ"
                    : safe(hoaDon.getKhachHang().getHoTen());
        }

        private String customerPhone() {
            return hoaDon.getKhachHang() == null ? "" : safe(hoaDon.getKhachHang().getSoDienThoai());
        }

        private String employeeName() {
            return hoaDon.getNhanVien() == null ? "" : safe(hoaDon.getNhanVien().getHoTen());
        }

        private List<String> wrapNote() {
            String note = hoaDon.getGhiChu();
            if (note == null || note.trim().isEmpty()) {
                return new ArrayList<>();
            }
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder("Ghi chú: ");
            for (String word : note.trim().split("\\s+")) {
                String candidate = line.length() == 0 ? word : line + " " + word;
                if (line.length() > 0 && candidate.length() > 68) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            if (line.length() > 0) {
                lines.add(line.toString());
            }
            return lines;
        }

        private String formatDate(Date date) {
            return date == null ? "" : DATE.format(date);
        }

        private String money(double amount) {
            return MONEY.format(amount) + " đ";
        }

        private String safe(String value) {
            return value == null ? "" : value;
        }

        private void setFont(Graphics2D g, int style, float size) {
            g.setFont(new Font("SansSerif", style, Math.round(size)).deriveFont(size));
        }
    }
}
