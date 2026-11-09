package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import entity.ChiTietDonHang;
import entity.HoaDon;

final class ChiTietHoaDonDialog extends JDialog {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    ChiTietHoaDonDialog(JFrame owner, HoaDon hoaDon, String maKhuyenMai,
                         List<? extends ChiTietDonHang> chiTiet) {
        super(owner, "Chi tiết hóa đơn " + hoaDon.getMaHoaDon(), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));
        ButtonTooltips.install(this);

        JPanel information = new JPanel(new GridLayout(0, 4, 8, 6));
        information.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));
        addInfo(information, "Mã hóa đơn", hoaDon.getMaHoaDon());
        addInfo(information, "Ngày lập", hoaDon.getNgayLapHoaDon() == null
                ? "" : DATE_FORMAT.format(hoaDon.getNgayLapHoaDon()));
        addInfo(information, "Khách hàng", hoaDon.getKhachHang() == null
                || hoaDon.getKhachHang().getHoTen() == null
                        ? "Khách lẻ" : hoaDon.getKhachHang().getHoTen());
        addInfo(information, "Thu ngân", hoaDon.getNhanVien() == null
                ? "" : hoaDon.getNhanVien().getHoTen());
        addInfo(information, "Phương thức", hoaDon.getPhuongThucThanhToan());
        addInfo(information, "Trạng thái", hoaDon.getTrangThai());
        addInfo(information, "Khuyến mãi",
                maKhuyenMai == null ? "Không áp dụng" : maKhuyenMai);
        addInfo(information, "Ghi chú", hoaDon.getGhiChu());
        add(information, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Giảm giá", "Thành tiền"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focus, row, column);
                if (column >= 2) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return label;
            }
        });
        for (ChiTietDonHang item : chiTiet) {
            model.addRow(new Object[]{
                    item.getSanPham().getMaSanPham(),
                    item.getSanPham().getTenSanPham(),
                    tien(item.getDonGia()),
                    item.getSoLuong(),
                    tien(item.getGiamGia()),
                    tien(item.getThanhTien())
            });
        }
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel totals = new JPanel(new GridLayout(0, 2, 8, 4));
        totals.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        addTotal(totals, "Tổng tiền hàng", hoaDon.getTongTienHang());
        addTotal(totals, "Giảm giá", hoaDon.getTienGiamGia());
        addTotal(totals, "Thuế VAT", hoaDon.getThueVAT());
        addTotal(totals, "TỔNG THANH TOÁN", hoaDon.getTongThanhToan());
        addTotal(totals, "Tiền khách đưa", hoaDon.getTienKhachDua());
        addTotal(totals, "Tiền thối", hoaDon.getTienTraLai());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton print = new JButton("In máy in");
        print.setToolTipText("in hóa đơn trực tiếp qua máy in mặc định");
        print.addActionListener(event -> BoInHoaDon.print(this, hoaDon, chiTiet));
        JButton exportPdf = new JButton("Xuất PDF");
        exportPdf.addActionListener(event -> XuatHoaDonPdf.export(
                this, hoaDon, maKhuyenMai, chiTiet));
        JButton close = new JButton("Đóng");
        close.addActionListener(event -> dispose());
        actions.add(print);
        actions.add(exportPdf);
        actions.add(close);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(totals, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addInfo(JPanel panel, String title, String value) {
        panel.add(new JLabel(title + ":"));
        panel.add(new JLabel(value == null ? "" : value));
    }

    private void addTotal(JPanel panel, String title, double value) {
        panel.add(new JLabel(title + ":"));
        panel.add(new JLabel(tien(value), SwingConstants.RIGHT));
    }

    private String tien(double value) {
        return MONEY_FORMAT.format(value) + " đ";
    }
}
