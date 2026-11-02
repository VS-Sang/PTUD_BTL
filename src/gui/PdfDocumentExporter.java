package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.ImageIO;

/**
 * Xuất hóa đơn / chứng từ ra PDF theo form mẫu:
 * tên cửa hàng căn giữa, tiêu đề, thông tin 2 cột, bảng viền đen có cột STT,
 * khối tổng tiền căn phải, ghi chú, QR chuyển khoản, chân trang.
 *
 * Giữ nguyên chữ ký hàm write(...) nên các nơi đang gọi không cần sửa.
 */
final class PdfDocumentExporter {

    // ====== THÔNG TIN CỬA HÀNG (sửa lại cho đúng) ======
    private static final String STORE_NAME = "ĐIỆN MÁY ĐỎ";
    private static final String STORE_ADDRESS = "Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM";
    private static final String STORE_HOTLINE = "Hotline: 1900 0000";
    private static final String FOOTER_THANKS = "Cảm ơn quý khách đã mua hàng tại Điện Máy Đỏ";
    // ====== THANH TOÁN CHUYỂN KHOẢN ======
    private static final String BANK_NAME = "MB Bank";
    private static final String BANK_ACCOUNT = "09090909006";
    /** Đường dẫn ảnh QR: thử theo file trên đĩa, nếu không có thì tìm trong classpath. */
    private static final String QR_IMAGE_PATH = "img/qr-mbbank.png";
    private static final double QR_SIZE = 120;
    // ====================================================

    private static final double PAGE_WIDTH = 595;
    private static final double PAGE_HEIGHT = 842;
    private static final double MARGIN = 40;
    private static final double CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2;
    private static final double CONTENT_BOTTOM = 785;

    private static final double INFO_TOP = 152;
    private static final double INFO_STEP = 20;
    private static final double CONT_TABLE_TOP = 140;
    private static final double HEADER_HEIGHT = 26;
    private static final double MIN_ROW_HEIGHT = 24;
    private static final double LINE_HEIGHT = 12;
    private static final double CELL_PAD = 5;
    private static final float BODY = 9.5f;

    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;

    private static final FontRenderContext FONT_CONTEXT =
            new FontRenderContext(null, true, true);
    private static final Color TEXT = new Color(20, 20, 20);
    private static final Color MUTED = new Color(100, 100, 100);
    private static final Color BORDER = Color.BLACK;

    private PdfDocumentExporter() {}

    private static final class QrImage {
        final byte[] data;   // RGB 8-bit, đã nén Flate
        final int width;
        final int height;

        QrImage(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }
    }

    private static QrImage loadQr() {
        try {
            BufferedImage src = null;
            File file = new File(QR_IMAGE_PATH);
            if (file.isFile()) {
                src = ImageIO.read(file);
            } else {
                try (InputStream in = PdfDocumentExporter.class.getResourceAsStream(
                        "/" + QR_IMAGE_PATH)) {
                    if (in != null) {
                        src = ImageIO.read(in);
                    }
                }
            }
            if (src == null) {
                return null;
            }
            BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rgb.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            g.drawImage(src, 0, 0, null);
            g.dispose();
            ByteArrayOutputStream raw = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflater = new DeflaterOutputStream(raw)) {
                for (int y = 0; y < rgb.getHeight(); y++) {
                    for (int x = 0; x < rgb.getWidth(); x++) {
                        int px = rgb.getRGB(x, y);
                        deflater.write((px >> 16) & 0xFF);
                        deflater.write((px >> 8) & 0xFF);
                        deflater.write(px & 0xFF);
                    }
                }
            }
            return new QrImage(raw.toByteArray(), rgb.getWidth(), rgb.getHeight());
        } catch (IOException | RuntimeException ex) {
            return null;
        }
    }

    private static double qrBlockHeight(QrImage qr) {
        return 14 + 18 + (qr != null ? QR_SIZE + 8 : 0) + 32;
    }

    private static final class PageSpec {
        final int from;
        final int to;
        final boolean totals;

        PageSpec(int from, int to, boolean totals) {
            this.from = from;
            this.to = to;
            this.totals = totals;
        }
    }

    static void write(File target, String title, String documentNumber,
                      List<String[]> metadataRows, String sectionTitle,
                      List<String> columnHeaders, List<String[]> itemRows,
                      double[] columnWeights, List<String[]> totals,
                      String note) throws IOException {
        // --- Chuẩn hóa bảng: tự thêm cột STT nếu chưa có ---
        List<String> headers = new ArrayList<>(columnHeaders);
        double[] weights = normalizeWeights(columnWeights, headers.size());
        boolean hasStt = !headers.isEmpty() && "STT".equalsIgnoreCase(safe(headers.get(0)));
        if (!hasStt) {
            headers.add(0, "STT");
            double[] withStt = new double[weights.length + 1];
            withStt[0] = 0.22;
            System.arraycopy(weights, 0, withStt, 1, weights.length);
            weights = withStt;
        }
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < itemRows.size(); i++) {
            String[] src = itemRows.get(i) == null ? new String[0] : itemRows.get(i);
            if (hasStt) {
                rows.add(src);
            } else {
                String[] row = new String[src.length + 1];
                row[0] = String.valueOf(i + 1);
                System.arraycopy(src, 0, row, 1, src.length);
                rows.add(row);
            }
        }
        double[] widths = columnWidths(weights, headers.size());

        // --- Thông tin chung: đảm bảo có mã chứng từ ---
        List<String[]> info = new ArrayList<>();
        boolean hasNumber = false;
        if (metadataRows != null) {
            for (String[] row : metadataRows) {
                if (row == null) {
                    continue;
                }
                info.add(row);
                for (String cell : row) {
                    if (safe(cell).equals(safe(documentNumber))) {
                        hasNumber = true;
                    }
                }
            }
        }
        if (!hasNumber && !safe(documentNumber).isEmpty()) {
            String label = safe(title).toUpperCase(Locale.ROOT).contains("HÓA ĐƠN")
                    ? "Mã hóa đơn" : "Mã chứng từ";
            info.add(0, new String[]{label, documentNumber, "", ""});
        }

        // --- Chia trang theo chiều cao thực của từng dòng ---
        double[] heights = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            heights[i] = rowHeight(rows.get(i), widths);
        }
        boolean showPaymentQr = safe(title).toUpperCase(Locale.ROOT).contains("HÓA ĐƠN");
        QrImage qr = showPaymentQr ? loadQr() : null;
        double tailHeight = totalsHeight(totals) + noteHeight(note)
                + (showPaymentQr ? qrBlockHeight(qr) : 0);
        double firstTop = firstTableTop(info.size());
        List<PageSpec> specs = new ArrayList<>();
        int index = 0;
        boolean first = true;
        while (true) {
            double y = (first ? firstTop : CONT_TABLE_TOP) + HEADER_HEIGHT;
            int start = index;
            while (index < rows.size()
                    && (index == start || y + heights[index] <= CONTENT_BOTTOM)) {
                y += heights[index];
                index++;
            }
            if (index >= rows.size()) {
                if (y + tailHeight <= CONTENT_BOTTOM) {
                    specs.add(new PageSpec(start, index, true));
                } else {
                    specs.add(new PageSpec(start, index, false));
                    specs.add(new PageSpec(index, index, true));
                }
                break;
            }
            specs.add(new PageSpec(start, index, false));
            first = false;
        }

        List<String> pages = new ArrayList<>();
        for (int p = 0; p < specs.size(); p++) {
            PageSpec spec = specs.get(p);
            pages.add(buildPage(title, info, sectionTitle, headers, rows, heights,
                    widths, spec, p + 1, totals, note, qr, showPaymentQr));
        }
        Files.write(target.toPath(), createPdf(pages, qr));
    }

    private static String buildPage(String title, List<String[]> info, String sectionTitle,
                                    List<String> headers, List<String[]> rows,
                                    double[] heights, double[] widths, PageSpec spec,
                                    int pageNumber,
                                    List<String[]> totals, String note,
                                    QrImage qr, boolean showPaymentQr) {
        StringBuilder c = new StringBuilder();
        double center = PAGE_WIDTH / 2;

        // Đầu trang: tên cửa hàng + tiêu đề
        drawText(c, STORE_NAME, center, 52, 18, Font.BOLD, TEXT, CENTER);
        drawText(c, STORE_ADDRESS, center, 69, 9, Font.PLAIN, TEXT, CENTER);
        drawText(c, STORE_HOTLINE, center, 83, 9, Font.PLAIN, TEXT, CENTER);
        drawText(c, safe(title).toUpperCase(Locale.ROOT), center, 118, 17,
                Font.BOLD, TEXT, CENTER);

        // Thông tin chung (chỉ trang 1)
        double tableTop;
        if (pageNumber == 1) {
            double y = INFO_TOP;
            for (int i = 0; i < info.size(); i++) {
                String[] row = info.get(i);
                if (row.length >= 2) {
                    drawInfo(c, row[0], row[1], MARGIN, y, 255, i == 0);
                }
                if (row.length >= 4) {
                    drawInfo(c, row[2], row[3], 315, y, 240, false);
                }
                y += INFO_STEP;
            }
            tableTop = firstTableTop(info.size());
        } else {
            tableTop = CONT_TABLE_TOP;
        }

        double y = tableTop;
        boolean drawTable = spec.from < spec.to || pageNumber == 1;
        if (drawTable) {
            if (pageNumber == 1 && !safe(sectionTitle).isEmpty()) {
                drawText(c, safe(sectionTitle).toUpperCase(Locale.ROOT), MARGIN,
                        tableTop - 8, 10.5f, Font.BOLD, TEXT, LEFT);
            }
            drawTableHeader(c, headers, widths, y);
            y += HEADER_HEIGHT;
            for (int i = spec.from; i < spec.to; i++) {
                drawTableRow(c, rows.get(i), headers, widths, y, heights[i]);
                y += heights[i];
            }
        }

        // Khối tổng tiền (căn phải) + ghi chú: chỉ trang cuối
        if (spec.totals) {
            if (totals != null && !totals.isEmpty()) {
                y += 10;
                for (String[] total : totals) {
                    if (total == null || total.length < 2) {
                        continue;
                    }
                    boolean strong = total.length > 2 && Boolean.parseBoolean(total[2]);
                    int style = strong ? Font.BOLD : Font.PLAIN;
                    float size = strong ? 11.5f : BODY;
                    drawText(c, safe(total[0]) + ":", 330, y + 12, size, style, TEXT, LEFT);
                    drawText(c, safe(total[1]), PAGE_WIDTH - MARGIN, y + 12, size,
                            style, TEXT, RIGHT);
                    y += strong ? 24 : 18;
                }
            }
            if (note != null && !note.trim().isEmpty()) {
                y += 8;
                String text = safe(note);
                if (!text.toLowerCase(Locale.ROOT).startsWith("ghi chú")) {
                    text = "Ghi chú: " + text;
                }
                for (String line : wrapText(text, BODY, Font.PLAIN, CONTENT_WIDTH)) {
                    drawText(c, line, MARGIN, y + 10, BODY, Font.PLAIN, MUTED, LEFT);
                    y += 13;
                }
            }
            if (showPaymentQr) {
                y += 14;
                drawText(c, "QR CHUYỂN KHOẢN", center, y + 10, 10.5f, Font.BOLD, TEXT, CENTER);
                y += 18;
                if (qr != null) {
                    c.append("q ").append(number(QR_SIZE)).append(" 0 0 ")
                            .append(number(QR_SIZE)).append(' ')
                            .append(number(center - QR_SIZE / 2)).append(' ')
                            .append(number(PAGE_HEIGHT - y - QR_SIZE))
                            .append(" cm /Im1 Do Q\n");
                    y += QR_SIZE + 8;
                }
                drawText(c, "Ngân hàng: " + BANK_NAME, center, y + 10, BODY,
                        Font.PLAIN, TEXT, CENTER);
                drawText(c, "Số tài khoản: " + BANK_ACCOUNT, center, y + 26, BODY,
                        Font.PLAIN, TEXT, CENTER);
            }
        }

        // Chân trang
        drawLine(c, MARGIN, 800, PAGE_WIDTH - MARGIN, 800, new Color(190, 190, 190), 0.6);
        drawText(c, FOOTER_THANKS, center, 818, 8.5f, Font.PLAIN, MUTED, CENTER);
        return c.toString();
    }

    // ------------------------------------------------------------------ layout helpers

    private static double firstTableTop(int infoCount) {
        return infoCount == 0 ? 165 : INFO_TOP + (infoCount - 1) * INFO_STEP + 38;
    }

    private static double totalsHeight(List<String[]> totals) {
        if (totals == null || totals.isEmpty()) {
            return 0;
        }
        double h = 10;
        for (String[] t : totals) {
            if (t == null || t.length < 2) {
                continue;
            }
            h += t.length > 2 && Boolean.parseBoolean(t[2]) ? 24 : 18;
        }
        return h;
    }

    private static double noteHeight(String note) {
        if (note == null || note.trim().isEmpty()) {
            return 0;
        }
        return 8 + wrapText("Ghi chú: " + safe(note), BODY, Font.PLAIN, CONTENT_WIDTH).size() * 13;
    }

    private static double[] normalizeWeights(double[] weights, int count) {
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = weights != null && i < weights.length && weights[i] > 0
                    ? weights[i] : 1;
        }
        return result;
    }

    private static double[] columnWidths(double[] weights, int count) {
        double total = 0;
        for (int i = 0; i < count; i++) {
            total += weights[i];
        }
        double[] widths = new double[count];
        for (int i = 0; i < count; i++) {
            widths[i] = CONTENT_WIDTH * weights[i] / total;
        }
        return widths;
    }

    private static double rowHeight(String[] values, double[] widths) {
        int maxLines = 1;
        for (int col = 0; col < widths.length; col++) {
            String value = col < values.length ? values[col] : "";
            int lines = wrapText(value, BODY, Font.PLAIN, widths[col] - CELL_PAD * 2).size();
            maxLines = Math.max(maxLines, lines);
        }
        return Math.max(MIN_ROW_HEIGHT, maxLines * LINE_HEIGHT + 12);
    }

    private static int alignOf(String header) {
        String h = safe(header).toUpperCase(Locale.ROOT);
        if (h.equals("STT") || h.equals("SL") || h.contains("SỐ LƯỢNG")
                || h.contains("SỐ ĐÊM")) {
            return CENTER;
        }
        if (h.contains("TÊN") || h.contains("SẢN PHẨM") || h.startsWith("MÃ")
                || h.contains("GHI CHÚ") || h.contains("DỊCH VỤ") || h.contains("PHÒNG")) {
            return LEFT;
        }
        return RIGHT;
    }

    // ------------------------------------------------------------------ drawing

    private static void drawInfo(StringBuilder c, String label, String value,
                                 double x, double y, double width, boolean bold) {
        String l = safe(label);
        String v = safe(value);
        if (l.isEmpty() && v.isEmpty()) {
            return;
        }
        String text = l.isEmpty() ? v : l + ": " + v;
        int style = bold ? Font.BOLD : Font.PLAIN;
        drawText(c, fitText(text, BODY, style, width), x, y, BODY, style, TEXT, LEFT);
    }

    private static void drawTableHeader(StringBuilder c, List<String> headers,
                                        double[] widths, double top) {
        double x = MARGIN;
        for (int col = 0; col < headers.size(); col++) {
            drawRect(c, x, top, widths[col], HEADER_HEIGHT, Color.WHITE, BORDER, 0.8);
            drawText(c, fitText(safe(headers.get(col)), BODY, Font.BOLD, widths[col] - 6),
                    x + widths[col] / 2, top + HEADER_HEIGHT / 2 + 3.4, BODY,
                    Font.BOLD, TEXT, CENTER);
            x += widths[col];
        }
    }

    private static void drawTableRow(StringBuilder c, String[] values, List<String> headers,
                                     double[] widths, double top, double height) {
        double x = MARGIN;
        for (int col = 0; col < headers.size(); col++) {
            double width = widths[col];
            drawRect(c, x, top, width, height, Color.WHITE, BORDER, 0.8);
            String value = col < values.length ? values[col] : "";
            List<String> lines = wrapText(value, BODY, Font.PLAIN, width - CELL_PAD * 2);
            int align = alignOf(headers.get(col));
            double textX = align == LEFT ? x + CELL_PAD
                    : align == CENTER ? x + width / 2 : x + width - CELL_PAD;
            double baseline = top + (height - lines.size() * LINE_HEIGHT) / 2 + 9;
            for (String line : lines) {
                drawText(c, line, textX, baseline, BODY, Font.PLAIN, TEXT, align);
                baseline += LINE_HEIGHT;
            }
            x += width;
        }
    }

    private static void drawRect(StringBuilder content, double x, double top,
                                 double width, double height, Color fill,
                                 Color stroke, double lineWidth) {
        content.append("q ").append(color(fill)).append(" rg ")
                .append(color(stroke)).append(" RG ")
                .append(number(lineWidth)).append(" w ")
                .append(number(x)).append(' ').append(number(PAGE_HEIGHT - top - height))
                .append(' ').append(number(width)).append(' ').append(number(height))
                .append(" re B Q\n");
    }

    private static void drawLine(StringBuilder content, double x1, double y1,
                                 double x2, double y2, Color color,
                                 double lineWidth) {
        content.append("q ").append(color(color)).append(" RG ")
                .append(number(lineWidth)).append(" w ")
                .append(number(x1)).append(' ').append(number(PAGE_HEIGHT - y1))
                .append(" m ").append(number(x2)).append(' ')
                .append(number(PAGE_HEIGHT - y2)).append(" l S Q\n");
    }

    /** Vẽ chữ bằng outline glyph (không cần nhúng font). align: LEFT / CENTER / RIGHT. */
    private static void drawText(StringBuilder content, String text, double x,
                                 double baseline, float size, int style, Color color,
                                 int align) {
        if (text == null || text.isEmpty()) {
            return;
        }
        text = Normalizer.normalize(text, Normalizer.Form.NFC);
        double width = textWidth(text, size, style);
        double drawX = align == CENTER ? x - width / 2 : align == RIGHT ? x - width : x;
        Font font = new Font("SansSerif", style, 1).deriveFont(size);
        GlyphVector glyphs = font.createGlyphVector(FONT_CONTEXT, text);
        // Outline ở hệ tọa độ Java (Y hướng xuống) -> đổi sang PDF (Y hướng lên) khi ghi.
        Shape outline = glyphs.getOutline((float) drawX, (float) baseline);
        PathIterator iterator = outline.getPathIterator(null);
        double[] p = new double[6];
        double curX = 0;
        double curY = 0;
        content.append("q ").append(color(color)).append(" rg\n");
        while (!iterator.isDone()) {
            switch (iterator.currentSegment(p)) {
                case PathIterator.SEG_MOVETO:
                    curX = p[0];
                    curY = p[1];
                    content.append(number(curX)).append(' ')
                            .append(number(PAGE_HEIGHT - curY)).append(" m\n");
                    break;
                case PathIterator.SEG_LINETO:
                    curX = p[0];
                    curY = p[1];
                    content.append(number(curX)).append(' ')
                            .append(number(PAGE_HEIGHT - curY)).append(" l\n");
                    break;
                case PathIterator.SEG_QUADTO:
                    double cx = p[0];
                    double cy = p[1];
                    double ex = p[2];
                    double ey = p[3];
                    content.append(number(curX + (cx - curX) * 2 / 3)).append(' ')
                            .append(number(PAGE_HEIGHT - (curY + (cy - curY) * 2 / 3)))
                            .append(' ').append(number(ex + (cx - ex) * 2 / 3)).append(' ')
                            .append(number(PAGE_HEIGHT - (ey + (cy - ey) * 2 / 3)))
                            .append(' ').append(number(ex)).append(' ')
                            .append(number(PAGE_HEIGHT - ey)).append(" c\n");
                    curX = ex;
                    curY = ey;
                    break;
                case PathIterator.SEG_CUBICTO:
                    content.append(number(p[0])).append(' ')
                            .append(number(PAGE_HEIGHT - p[1])).append(' ')
                            .append(number(p[2])).append(' ')
                            .append(number(PAGE_HEIGHT - p[3])).append(' ')
                            .append(number(p[4])).append(' ')
                            .append(number(PAGE_HEIGHT - p[5])).append(" c\n");
                    curX = p[4];
                    curY = p[5];
                    break;
                case PathIterator.SEG_CLOSE:
                    content.append("h\n");
                    break;
                default:
                    throw new IllegalStateException("Unsupported glyph outline segment.");
            }
            iterator.next();
        }
        content.append("f Q\n");
    }

    // ------------------------------------------------------------------ text helpers

    /** Xuống dòng theo chiều rộng; từ quá dài sẽ được bẻ ký tự thay vì tràn ô. */
    private static List<String> wrapText(String text, float size, int style, double maxWidth) {
        List<String> lines = new ArrayList<>();
        String clean = safe(text);
        if (clean.isEmpty()) {
            lines.add("");
            return lines;
        }
        StringBuilder line = new StringBuilder();
        for (String word : clean.split("\\s+")) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (textWidth(candidate, size, style) <= maxWidth) {
                line.setLength(0);
                line.append(candidate);
                continue;
            }
            if (line.length() > 0) {
                lines.add(line.toString());
                line.setLength(0);
            }
            String rest = word;
            while (rest.length() > 1 && textWidth(rest, size, style) > maxWidth) {
                int cut = rest.length() - 1;
                while (cut > 1 && textWidth(rest.substring(0, cut), size, style) > maxWidth) {
                    cut--;
                }
                lines.add(rest.substring(0, cut));
                rest = rest.substring(cut);
            }
            line.append(rest);
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private static String fitText(String text, float size, int style, double maxWidth) {
        if (textWidth(text, size, style) <= maxWidth) {
            return text;
        }
        String fitted = text;
        while (!fitted.isEmpty() && textWidth(fitted + "…", size, style) > maxWidth) {
            fitted = fitted.substring(0, fitted.length() - 1);
        }
        return fitted.isEmpty() ? "" : fitted + "…";
    }

    private static double textWidth(String text, float size, int style) {
        Font font = new Font("SansSerif", style, 1).deriveFont(size);
        return font.getStringBounds(Normalizer.normalize(text, Normalizer.Form.NFC),
                FONT_CONTEXT).getWidth();
    }

    private static String color(Color value) {
        return number(value.getRed() / 255.0) + " "
                + number(value.getGreen() / 255.0) + " "
                + number(value.getBlue() / 255.0);
    }

    private static String number(double value) {
        return String.format(Locale.US, "%.3f", value);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    // ------------------------------------------------------------------ PDF writer

    private static byte[] createPdf(List<String> pages, QrImage qr) throws IOException {
        int imageObject = 3 + pages.size() * 2;
        int objectCount = 2 + pages.size() * 2 + (qr != null ? 1 : 0);
        List<byte[]> objects = new ArrayList<>();
        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++) {
            kids.append(3 + i * 2).append(" 0 R ");
        }
        objects.add(ascii("<< /Type /Pages /Kids [" + kids + "] /Count "
                + pages.size() + " >>"));

        for (int i = 0; i < pages.size(); i++) {
            int pageObject = 3 + i * 2;
            int streamObject = pageObject + 1;
            objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 "
                    + number(PAGE_WIDTH) + " " + number(PAGE_HEIGHT)
                    + "] /Resources << "
                    + (qr != null ? "/XObject << /Im1 " + imageObject + " 0 R >> " : "")
                    + ">> /Contents " + streamObject + " 0 R >>"));
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflater = new DeflaterOutputStream(compressed)) {
                deflater.write(ascii(pages.get(i)));
            }
            byte[] stream = compressed.toByteArray();
            byte[] streamHeader = ascii("<< /Length " + stream.length
                    + " /Filter /FlateDecode >>\nstream\n");
            byte[] streamFooter = ascii("\nendstream");
            byte[] object = new byte[streamHeader.length + stream.length + streamFooter.length];
            System.arraycopy(streamHeader, 0, object, 0, streamHeader.length);
            System.arraycopy(stream, 0, object, streamHeader.length, stream.length);
            System.arraycopy(streamFooter, 0, object, streamHeader.length + stream.length,
                    streamFooter.length);
            objects.add(object);
        }
        if (qr != null) {
            byte[] header = ascii("<< /Type /XObject /Subtype /Image /Width " + qr.width
                    + " /Height " + qr.height + " /ColorSpace /DeviceRGB"
                    + " /BitsPerComponent 8 /Filter /FlateDecode /Length "
                    + qr.data.length + " >>\nstream\n");
            byte[] footer = ascii("\nendstream");
            byte[] object = new byte[header.length + qr.data.length + footer.length];
            System.arraycopy(header, 0, object, 0, header.length);
            System.arraycopy(qr.data, 0, object, header.length, qr.data.length);
            System.arraycopy(footer, 0, object, header.length + qr.data.length, footer.length);
            objects.add(object);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        write(output, ascii("%PDF-1.4\n%âãÏÓ\n"));
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(output.size());
            write(output, ascii((i + 1) + " 0 obj\n"));
            write(output, objects.get(i));
            write(output, ascii("\nendobj\n"));
        }
        int xrefOffset = output.size();
        write(output, ascii("xref\n0 " + (objectCount + 1) + "\n"));
        write(output, ascii("0000000000 65535 f \n"));
        for (int offset : offsets) {
            write(output, ascii(String.format(Locale.US, "%010d 00000 n \n", offset)));
        }
        write(output, ascii("trailer\n<< /Size " + (objectCount + 1)
                + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n"));
        return output.toByteArray();
    }

    private static byte[] ascii(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static void write(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }
}