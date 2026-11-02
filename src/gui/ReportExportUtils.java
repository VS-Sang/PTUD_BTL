package gui;

import java.awt.Component;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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

final class ReportExportUtils {
    private ReportExportUtils() {
    }

    static void exportTable(Component parent, JTable table, String title, String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter("Excel/CSV (*.csv)", "csv"));
        chooser.setSelectedFile(new File(defaultFileName));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(parent,
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

            JOptionPane.showMessageDialog(parent, "Đã xuất " + table.getRowCount()
                    + " dòng ra tệp:\n" + file.getAbsolutePath(),
                    "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(parent, "Không thể xuất báo cáo:\n"
                    + exception.getMessage(), "Lỗi xuất Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void exportTablePdf(Component parent, JTable table, String title, String defaultFileName) {
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
            int overwrite = JOptionPane.showConfirmDialog(parent,
                    "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            writeTablePdf(table, title, file);
            JOptionPane.showMessageDialog(parent, "Đã lưu báo cáo PDF tại:\n"
                    + file.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | DocumentException exception) {
            JOptionPane.showMessageDialog(parent, "Không thể lưu báo cáo PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void writeTablePdf(JTable table, String title, File file)
            throws IOException, DocumentException {
        BaseFont baseFont = BaseFont.createFont(findVietnameseFont(),
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
                baseFont, 16, com.lowagie.text.Font.BOLD, new Color(180, 40, 40));
        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(
                baseFont, 10, com.lowagie.text.Font.BOLD, Color.WHITE);
        com.lowagie.text.Font bodyFont = new com.lowagie.text.Font(
                baseFont, 10, com.lowagie.text.Font.NORMAL, Color.BLACK);

        try (FileOutputStream output = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4, 36, 36, 42, 42);
            PdfWriter.getInstance(document, output);
            document.open();
            try {
                Paragraph heading = new Paragraph(title, titleFont);
                heading.setAlignment(Element.ALIGN_CENTER);
                heading.setSpacingAfter(16);
                document.add(heading);

                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                pdfTable.setWidthPercentage(100);
                pdfTable.setHeaderRows(1);
                for (int column = 0; column < table.getColumnCount(); column++) {
                    addPdfCell(pdfTable, table.getColumnName(column), headerFont,
                            new Color(180, 40, 40), Element.ALIGN_CENTER);
                }
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int column = 0; column < table.getColumnCount(); column++) {
                        Object value = table.getValueAt(row, column);
                        addPdfCell(pdfTable, value == null ? "" : String.valueOf(value),
                                bodyFont, Color.WHITE, Element.ALIGN_LEFT);
                    }
                }
                document.add(pdfTable);
            } finally {
                document.close();
            }
        }
    }

    private static void addPdfCell(PdfPTable table, String value, com.lowagie.text.Font font,
                                   Color background, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
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
