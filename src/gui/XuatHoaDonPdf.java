package gui;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import entity.ChiTietHoaDon;
import entity.HoaDon;

final class XuatHoaDonPdf {
    private XuatHoaDonPdf() {
    }

    static void export(Component parent, HoaDon hoaDon, String maKhuyenMai,
                       List<ChiTietHoaDon> chiTiet) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn thành PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("HD-" + hoaDon.getMaHoaDon() + ".pdf"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(ChuSoHuuHopThoai.resolve(parent),
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
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Đã lưu hóa đơn PDF tại:\n"
                    + file.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(parent), "Không thể lưu hóa đơn PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void writeHoaDon(File file, HoaDon hoaDon, String maKhuyenMai,
                            List<ChiTietHoaDon> chiTiet) throws IOException {
        BoInHoaDon.writePdf(file, hoaDon, chiTiet);
    }
}
