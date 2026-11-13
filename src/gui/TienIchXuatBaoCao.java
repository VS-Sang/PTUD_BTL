package gui;

import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPageEventHelper;

final class TienIchXuatBaoCao {
    private TienIchXuatBaoCao() {
    }

    static void exportTable(Component parent, JTable table, String title, String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter("Excel/CSV (*.csv)", "csv"));
        // điền săn tên file
        chooser.setSelectedFile(new File(defaultFileName));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(ChuSoHuuHopThoai.resolve(parent),
                    "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');
            for (int column = 0; column < table.getColumnCount(); column++) {
                if (column > 0) {
                    writer.write(',');
                }
                writer.write(csv(table.getColumnName(column)));
            }
            writer.newLine();

            for (int row = 0; row < table.getRowCount(); row++) {
                for (int column = 0; column < table.getColumnCount(); column++) {
                    if (column > 0) {
                        writer.write(',');
                    }
                    Object value = table.getValueAt(row, column);
                    writer.write(csv(value == null ? "" : String.valueOf(value)));
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Đã xuất " + table.getRowCount()
                    + " dòng ra tệp:\n" + file.getAbsolutePath(),
                    "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Không thể xuất báo cáo:\n"
                    + exception.getMessage(), "Lỗi xuất Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void exportTablePdf(Component parent, JTable table, String title, String defaultFileName) {
        exportTablePdf(parent, table, title, defaultFileName, null, null, null, null);
    }

    static void exportTablePdf(Component parent, JTable table, String title, String defaultFileName,
                               String[][] summary, Component chart,
                               String detailTitle, JTable detailTable) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo thành PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File(defaultFileName));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(ChuSoHuuHopThoai.resolve(parent),
                    "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            writeTablePdf(table, title, file, summary, chart, detailTitle, detailTable);
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Đã lưu báo cáo PDF tại:\n"
                    + file.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | DocumentException exception) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Không thể lưu báo cáo PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void writeTablePdf(JTable table, String title, File file,
                                      String[][] summary, Component chart,
                                      String detailTitle, JTable detailTable)
            throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(findVietnameseFont(),
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                baseFont, 18, com.lowagie.text.Font.BOLD, new Color(180, 40, 40));
        com.lowagie.text.Font metadataFont = new com.lowagie.text.Font(
                baseFont, 9, com.lowagie.text.Font.NORMAL, new Color(90, 90, 90));
        com.lowagie.text.Font sectionFont = new com.lowagie.text.Font(
                baseFont, 12, com.lowagie.text.Font.BOLD, new Color(100, 35, 35));
        com.lowagie.text.Font summaryLabelFont = new com.lowagie.text.Font(
                baseFont, 9, com.lowagie.text.Font.NORMAL, new Color(80, 80, 80));
        com.lowagie.text.Font summaryValueFont = new com.lowagie.text.Font(
                baseFont, 12, com.lowagie.text.Font.BOLD, new Color(45, 45, 45));
        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(
                baseFont, 9, com.lowagie.text.Font.BOLD, Color.WHITE);
        com.lowagie.text.Font bodyFont = new com.lowagie.text.Font(
                baseFont, 9, com.lowagie.text.Font.NORMAL, Color.BLACK);
        boolean landscape = table.getColumnCount() >= 5;
        com.lowagie.text.Rectangle pageSize = landscape
                ? PageSize.A4.rotate() : PageSize.A4;
        String generatedAt = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        try (FileOutputStream output = new FileOutputStream(file)) {
            Document document = new Document(pageSize, 36, 36, 48, 42);
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPageEvent(new ReportPageFooter(baseFont, generatedAt));
            document.open();
            try {
                Paragraph heading = new Paragraph(title, titleFont);
                heading.setAlignment(Element.ALIGN_CENTER);
                heading.setSpacingAfter(5);
                document.add(heading);

                Paragraph period = new Paragraph("Kỳ báo cáo: " + title, metadataFont);
                period.setAlignment(Element.ALIGN_CENTER);
                period.setSpacingAfter(3);
                document.add(period);
                Paragraph generated = new Paragraph("Thời gian xuất: " + generatedAt, metadataFont);
                generated.setAlignment(Element.ALIGN_CENTER);
                generated.setSpacingAfter(14);
                document.add(generated);

                if (summary != null && summary.length > 0) {
                    document.add(new Paragraph("TỔNG QUAN", sectionFont));
                    document.add(createSummaryTable(summary, summaryLabelFont, summaryValueFont));
                    document.add(new Paragraph(" "));
                }

                if (chart != null) {
                    document.add(new Paragraph("BIỂU ĐỒ", sectionFont));
                    Image chartImage = captureComponent(chart);
                    chartImage.scaleToFit(document.getPageSize().getWidth()
                                    - document.leftMargin() - document.rightMargin(),
                            230);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(chartImage);
                    document.add(new Paragraph(" "));
                }

                document.add(new Paragraph("CHI TIẾT BÁO CÁO", sectionFont));
                PdfPTable pdfTable = createDataTable(table, headerFont, bodyFont);
                document.add(pdfTable);

                if (detailTable != null) {
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(
                            detailTitle == null ? "THỐNG KÊ BỔ SUNG" : detailTitle,
                            sectionFont));
                    document.add(createDataTable(detailTable, headerFont, bodyFont));
                }
            } finally {
                document.close();
            }
        }
    }

    private static PdfPTable createSummaryTable(String[][] summary,
                                                 com.lowagie.text.Font labelFont,
                                                 com.lowagie.text.Font valueFont)
            throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.1f, 1.4f, 1.1f, 1.4f});
        for (int i = 0; i < summary.length; i += 2) {
            addPdfCell(table, summary[i][0], labelFont, new Color(245, 245, 245),
                    Element.ALIGN_LEFT);
            addPdfCell(table, summary[i][1], valueFont, new Color(250, 250, 250),
                    Element.ALIGN_LEFT);
            if (i + 1 < summary.length) {
                addPdfCell(table, summary[i + 1][0], labelFont, new Color(245, 245, 245),
                        Element.ALIGN_LEFT);
                addPdfCell(table, summary[i + 1][1], valueFont, new Color(250, 250, 250),
                        Element.ALIGN_LEFT);
            } else {
                addPdfCell(table, "", labelFont, Color.WHITE, Element.ALIGN_LEFT);
                addPdfCell(table, "", valueFont, Color.WHITE, Element.ALIGN_LEFT);
            }
        }
        return table;
    }

    private static PdfPTable createDataTable(JTable source,
                                              com.lowagie.text.Font headerFont,
                                              com.lowagie.text.Font bodyFont)
            throws DocumentException {
        if (source.getColumnCount() == 0) {
            throw new IllegalArgumentException("Bảng báo cáo không có cột.");
        }
        PdfPTable table = new PdfPTable(source.getColumnCount());
        table.setWidthPercentage(100);
        table.setWidths(calculateColumnWidths(source));
        table.setHeaderRows(1);
        table.setSplitRows(true);
        table.setSplitLate(false);
        for (int column = 0; column < source.getColumnCount(); column++) {
            addPdfCell(table, source.getColumnName(column), headerFont,
                    new Color(150, 35, 35), Element.ALIGN_CENTER);
        }
        for (int row = 0; row < source.getRowCount(); row++) {
            Color background = row % 2 == 0 ? Color.WHITE : new Color(246, 247, 249);
            for (int column = 0; column < source.getColumnCount(); column++) {
                Object value = source.getValueAt(row, column);
                String text = value == null ? "" : String.valueOf(value);
                addPdfCell(table, text, bodyFont, background,
                        isNumericColumn(source.getColumnName(column))
                                ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
            }
        }
        if (source.getRowCount() == 0) {
            PdfPCell empty = new PdfPCell(new Phrase("Không có dữ liệu trong kỳ báo cáo.", bodyFont));
            empty.setColspan(source.getColumnCount());
            empty.setPadding(8);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setBackgroundColor(new Color(246, 247, 249));
            table.addCell(empty);
        }
        return table;
    }

    private static float[] calculateColumnWidths(JTable table) {
        float[] widths = new float[table.getColumnCount()];
        float total = 0;
        for (int column = 0; column < widths.length; column++) {
            String heading = table.getColumnName(column).toLowerCase(Locale.ROOT);
            float width;
            if (heading.contains("tên") || heading.contains("nhân viên")
                    || heading.contains("sản phẩm") || heading.contains("ghi chú")) {
                width = 2.5f;
            } else if (isNumericColumn(heading)) {
                width = 1.2f;
            } else {
                width = 1.5f;
            }
            widths[column] = width;
            total += width;
        }
        for (int column = 0; column < widths.length; column++) {
            widths[column] = widths[column] * widths.length / total;
        }
        return widths;
    }

    private static boolean isNumericColumn(String columnName) {
        String name = columnName.toLowerCase(Locale.ROOT);
        return name.contains("số") || name.contains("sl") || name.contains("doanh thu")
                || name.contains("lợi nhuận") || name.contains("tăng trưởng")
                || name.contains("tổng") || name.contains("tiền")
                || name.contains("đơn giá") || name.contains("thành tiền")
                || name.contains("hạng") || name.contains("đánh giá")
                || name.contains("tỷ lệ") || name.contains("%");
    }

    private static Image captureComponent(Component component) throws IOException, DocumentException {
        int width = component.getWidth();
        int height = component.getHeight();
        if (width <= 0 || height <= 0) {
            throw new IOException("Không thể chụp biểu đồ: thành phần chưa có kích thước.");
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        try {
            component.printAll(graphics);
        } finally {
            graphics.dispose();
        }
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            javax.imageio.ImageIO.write(bufferedImage, "png", bytes);
            return Image.getInstance(bytes.toByteArray());
        }
    }

    private static void addPdfCell(PdfPTable table, String value, com.lowagie.text.Font font,
                                   Color background, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        table.addCell(cell);
    }

    private static final class ReportPageFooter extends PdfPageEventHelper {
        private final BaseFont baseFont;
        private final String generatedAt;

        private ReportPageFooter(BaseFont baseFont, String generatedAt) {
            this.baseFont = baseFont;
            this.generatedAt = generatedAt;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            canvas.saveState();
            canvas.setColorStroke(new Color(220, 220, 220));
            canvas.moveTo(document.left(), document.bottom() - 12);
            canvas.lineTo(document.right(), document.bottom() - 12);
            canvas.stroke();
            canvas.setFontAndSize(baseFont, 8);
            canvas.setColorFill(new Color(100, 100, 100));
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase("Điện Máy Đỏ  |  Xuất lúc " + generatedAt,
                            new com.lowagie.text.Font(baseFont, 8)),
                    document.left(), document.bottom() - 26, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                    new Phrase("Trang " + writer.getPageNumber(),
                            new com.lowagie.text.Font(baseFont, 8)),
                    document.right(), document.bottom() - 26, 0);
            canvas.restoreState();
        }
    }

    private static String findVietnameseFont() throws IOException {
        List<File> candidates = new ArrayList<>();
        String windowsDirectory = System.getenv("WINDIR");
        if (windowsDirectory != null) {
            File fontsDirectory = new File(windowsDirectory, "Fonts");
            candidates.add(new File(fontsDirectory, "arial.ttf"));
            candidates.add(new File(fontsDirectory, "segoeui.ttf"));
            candidates.add(new File(fontsDirectory, "tahoma.ttf"));
        }
        candidates.add(new File("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"));
        candidates.add(new File("/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf"));
        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return candidate.getAbsolutePath();
            }
        }
        throw new IOException("Không tìm thấy font hỗ trợ tiếng Việt (Arial, Segoe UI hoặc DejaVu Sans).");
    }

    private static String csv(String value) {
        if (!value.isEmpty() && " =+-@\t\r".indexOf(value.charAt(0)) >= 0) {
            value = "'" + value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
