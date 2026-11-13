package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import DAO.SanPham_DAO;
import entity.ChiTietDonHang;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.SanPham;

final class SuaHoaDonDialog extends JDialog {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");

    private final HoaDonPanel owner;
    private final HoaDon hoaDon;
    private final String maKhuyenMai;
    private final HoaDon_DAO hoaDonDao = new HoaDon_DAO();
    private final JComboBox<KhachHang> cboKhachHang = new JComboBox<>();
    private final JComboBox<SanPham> cboSanPham = new JComboBox<>();
    private final JComboBox<String> cboPhuongThuc =
            new JComboBox<>(new String[]{
                    "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử", "Kết hợp"});
    private final JSpinner spnSoLuong =
            new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JTextField txtTienKhachDua = new JTextField(14);
    private final JTextArea txtGhiChu = new JTextArea(3, 24);
    private final JLabel lblTongTienHang = new JLabel("0 đ", SwingConstants.RIGHT);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final Map<String, ChiTietHoaDon> chiTietTheoMa = new LinkedHashMap<>();

    SuaHoaDonDialog(HoaDonPanel owner, Window dialogOwner, HoaDon hoaDon, String maKhuyenMai,
                      List<ChiTietHoaDon> chiTiet) {
        super(dialogOwner, "Sửa hóa đơn " + hoaDon.getMaHoaDon(),
                Dialog.ModalityType.APPLICATION_MODAL);
        this.owner = owner;
        this.hoaDon = hoaDon;
        this.maKhuyenMai = maKhuyenMai;
        for (ChiTietHoaDon item : chiTiet) {
            chiTietTheoMa.put(item.getSanPham().getMaSanPham(), item);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(dialogOwner);
        setLayout(new BorderLayout(8, 8));
        ButtonTooltips.ganTooltip(this);
        add(createInvoiceFields(), BorderLayout.NORTH);
        add(createSanPhamPanel(), BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);
        loadCustomers();
        loadProducts();
        chonKhachHang(hoaDon.getKhachHang() == null
                ? null : hoaDon.getKhachHang().getMaKhachHang());
        cboPhuongThuc.setSelectedItem(hoaDon.getPhuongThucThanhToan());
        txtTienKhachDua.setText(MONEY_FORMAT.format(hoaDon.getTienKhachDua()));
        txtGhiChu.setText(hoaDon.getGhiChu() == null ? "" : hoaDon.getGhiChu());
        capNhatBang();
    }

    private JPanel createInvoiceFields() {
        JPanel fields = new JPanel(new GridLayout(0, 4, 8, 6));
        fields.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));
        fields.add(new JLabel("Mã hóa đơn"));
        fields.add(new JLabel(hoaDon.getMaHoaDon()));
        fields.add(new JLabel("Khách hàng"));
        cboKhachHang.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "Khách lẻ" : value.toString());
            label.setOpaque(true);
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        fields.add(cboKhachHang);
        fields.add(new JLabel("Phương thức thanh toán"));
        fields.add(cboPhuongThuc);
        fields.add(new JLabel("Tiền khách đưa"));
        fields.add(txtTienKhachDua);
        fields.add(new JLabel("Ghi chú"));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        fields.add(new JScrollPane(txtGhiChu));
        fields.add(new JLabel("Khuyến mãi"));
        fields.add(new JLabel(maKhuyenMai == null ? "Không áp dụng" : maKhuyenMai));
        fields.add(new JLabel("Thu ngân"));
        fields.add(new JLabel(hoaDon.getNhanVien() == null
                ? "" : hoaDon.getNhanVien().getHoTen()));
        return fields;
    }

    private JPanel createSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Sản phẩm trong hóa đơn"));
        JPanel addProduct = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboSanPham.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "-- Chọn sản phẩm --"
                    : value.getMaSanPham() + " — " + value.getTenSanPham());
            label.setOpaque(true);
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        addProduct.add(new JLabel("Sản phẩm"));
        addProduct.add(cboSanPham);
        addProduct.add(new JLabel("Số lượng"));
        addProduct.add(spnSoLuong);
        cboSanPham.addActionListener(event -> capNhatSoLuongChon());
        JButton btnThem = new JButton("Cập nhật số lượng");
        btnThem.addActionListener(event -> themSanPham());
        addProduct.add(btnThem);
        JButton btnXoa = new JButton("Xóa sản phẩm chọn");
        btnXoa.addActionListener(event -> xoaSanPhamChon());
        addProduct.add(btnXoa);

        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] columnWidths = {85, 280, 150, 100, 170};
        for (int column = 0; column < columnWidths.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(columnWidths[column]);
        }
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
        panel.add(addProduct, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel total = new JPanel(new BorderLayout());
        total.add(new JLabel("Tổng tiền hàng tạm tính:"), BorderLayout.WEST);
        total.add(lblTongTienHang, BorderLayout.EAST);
        total.add(new JLabel("Khuyến mãi và VAT sẽ được tính lại khi lưu."),
                BorderLayout.SOUTH);
        panel.add(total, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(event -> dispose());
        JButton btnLuu = new JButton("Lưu thay đổi");
        btnLuu.addActionListener(event -> luuThayDoi());
        actions.add(btnHuy);
        actions.add(btnLuu);
        return actions;
    }

    private void loadCustomers() {
        cboKhachHang.addItem(null);
        for (KhachHang khachHang : new KhachHang_DAO().getAllKhachHang()) {
            cboKhachHang.addItem(khachHang);
        }
    }

    private void chonKhachHang(String maKhachHang) {
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            KhachHang item = cboKhachHang.getItemAt(i);
            if (maKhachHang == null ? item == null
                    : item != null && maKhachHang.equals(item.getMaKhachHang())) {
                cboKhachHang.setSelectedIndex(i);
                return;
            }
        }
        cboKhachHang.setSelectedIndex(0);
    }

    private void loadProducts() {
        cboSanPham.addItem(null);
        for (SanPham sanPham : new SanPham_DAO().getAllSanPham()) {
            if (("Đang kinh doanh".equals(sanPham.getTrangThai())
                    && sanPham.getSoLuongTon() > 0)
                    || chiTietTheoMa.containsKey(sanPham.getMaSanPham())) {
                cboSanPham.addItem(sanPham);
            }
        }
    }

    private void capNhatSoLuongChon() {
        SanPham sanPham = (SanPham) cboSanPham.getSelectedItem();
        ChiTietHoaDon item = sanPham == null
                ? null : chiTietTheoMa.get(sanPham.getMaSanPham());
        spnSoLuong.setValue(item == null ? 1 : item.getSoLuong());
    }

    private void themSanPham() {
        SanPham sanPham = (SanPham) cboSanPham.getSelectedItem();
        if (sanPham == null) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Vui lòng chọn sản phẩm.");
            return;
        }
        String maSanPham = sanPham.getMaSanPham();
        ChiTietHoaDon item = chiTietTheoMa.get(maSanPham);
        int soLuongCu = item == null ? 0 : item.getSoLuong();
        int soLuongMoi = ((Number) spnSoLuong.getValue()).intValue();
        if (soLuongMoi > (long) sanPham.getSoLuongTon() + soLuongCu) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this),
                    "Số lượng vượt quá tồn kho hiện có.", "Không đủ tồn kho",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        double donGia = item == null ? sanPham.getGiaBan() : item.getDonGia();
        chiTietTheoMa.put(maSanPham, new ChiTietHoaDon(
                hoaDon.getMaHoaDon(), item == null ? null : item.getDonHang(),
                sanPham, soLuongMoi, donGia, 0, null));
        capNhatBang();
    }

    private void xoaSanPhamChon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Vui lòng chọn sản phẩm cần xóa.");
            return;
        }
        chiTietTheoMa.remove(String.valueOf(model.getValueAt(row, 0)));
        capNhatBang();
    }

    private void capNhatBang() {
        model.setRowCount(0);
        double tongTienHang = 0;
        for (ChiTietHoaDon item : chiTietTheoMa.values()) {
            double thanhTien = item.getThanhTien();
            tongTienHang += thanhTien;
            model.addRow(new Object[]{
                    item.getSanPham().getMaSanPham(),
                    item.getSanPham().getTenSanPham(),
                    MONEY_FORMAT.format(item.getDonGia()) + " đ",
                    item.getSoLuong(),
                    MONEY_FORMAT.format(thanhTien) + " đ"
            });
        }
        lblTongTienHang.setText(MONEY_FORMAT.format(tongTienHang) + " đ");
    }

    private void luuThayDoi() {
        if (chiTietTheoMa.isEmpty()) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Hóa đơn phải có ít nhất một sản phẩm.");
            return;
        }
        double tienKhachDua;
        try {
            tienKhachDua = Double.parseDouble(txtTienKhachDua.getText().trim()
                    .replace(",", "").replace(" ", ""));
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Tiền khách đưa không hợp lệ.");
            txtTienKhachDua.requestFocusInWindow();
            return;
        }
        KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
        List<ChiTietDonHang> chiTiet = new ArrayList<>();
        for (ChiTietHoaDon item : chiTietTheoMa.values()) {
            chiTiet.add(new ChiTietDonHang(item.getDonHang(), item.getSanPham(),
                    item.getSoLuong(), item.getDonGia(), item.getGiamGia(), item.getGhiChu()));
        }
        try {
            hoaDonDao.capNhatNoiDungHoaDon(hoaDon.getMaHoaDon(),
                    khachHang == null ? null : khachHang.getMaKhachHang(),
                    chiTiet, (String) cboPhuongThuc.getSelectedItem(),
                    tienKhachDua, txtGhiChu.getText().trim());
            owner.docDuLieuDatabaseVaoTable();
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Đã cập nhật hóa đơn "
                    + hoaDon.getMaHoaDon() + ".");
            dispose();
        } catch (java.sql.SQLException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(ChuSoHuuHopThoai.resolve(this), "Không thể cập nhật hóa đơn:\n"
                    + exception.getMessage(), "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
        }
    }
}
