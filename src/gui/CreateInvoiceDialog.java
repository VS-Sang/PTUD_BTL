package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import DAO.NhanVien_DAO;
import DAO.SanPham_DAO;
import entity.ChiTietDonHang;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;

public class CreateInvoiceDialog extends JFrame implements ActionListener {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private static final Color PANEL_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private final InvoicePanel owner;
    private final String tenNhanVien;
    private final String vaiTro;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "SL", "Thành tiền", "Xóa"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model) {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (model.getRowCount() == 0) {
                graphics.setColor(Color.GRAY);
                String message = "Chưa có sản phẩm nào. Chọn sản phẩm và nhấn “Thêm vào hóa đơn”.";
                int x = Math.max(8, (getWidth() - graphics.getFontMetrics().stringWidth(message)) / 2);
                int y = getHeight() / 2;
                graphics.drawString(message, x, y);
            }
        }
    };
    private final JComboBox<SanPham> cboSanPham = new JComboBox<>();
    private final JComboBox<KhachHang> cboKhachHang = new JComboBox<>();
    private final JComboBox<String> cboPhuongThuc =
            new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử"});
    private final JSpinner spnSoLuong =
            new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JSpinner spnKhuyenMai =
            new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
    private final JTextField txtMaHoaDon = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JTextField txtNhanVien = new JTextField();
    private final JTextField txtSoDienThoai = new JTextField();
    private final JTextField txtTienKhachDua = new JTextField();
    private final JTextArea txtGhiChu = new JTextArea(3, 20);
    private final JLabel lblTongSoLuong = new JLabel("0");
    private final JLabel lblTongTienHang = new JLabel("0 đ");
    private final JLabel lblThueVAT = new JLabel("0 đ");
    private final JLabel lblTongThanhToan = new JLabel("0 đ");
    private final JLabel lblTienThoi = new JLabel("0 đ");
    private final Map<String, Integer> soLuongDaChon = new LinkedHashMap<>();
    private final Map<String, SanPham> sanPhamTheoMa = new LinkedHashMap<>();
    private final HoaDon_DAO hoaDonDao = new HoaDon_DAO();
    private final JButton btnThem = createButton("+ Thêm vào hóa đơn", BRAND_RED, Color.WHITE);
    private final JButton btnLuu = createButton("▣  Lưu hóa đơn", new Color(77, 145, 72), Color.WHITE);
    private final JButton btnIn = createButton("▣  In", new Color(54, 103, 193), Color.WHITE);
    private final JButton btnMoi = createButton("⟳  Hóa đơn mới", Color.WHITE, Color.DARK_GRAY);

    public CreateInvoiceDialog(InvoicePanel owner, String tenNhanVien, String vaiTro) {
        this.owner = owner;
        this.tenNhanVien = tenNhanVien == null ? "" : tenNhanVien;
        this.vaiTro = vaiTro;
        setTitle("Điện Máy Đỏ — Lập Hóa Đơn Bán Hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1440, 800);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        txtMaHoaDon.setEditable(false);
        txtNgayLap.setEditable(false);
        txtNhanVien.setEditable(false);
        txtSoDienThoai.setEditable(false);

        add(new MenuPanel(this.tenNhanVien, vaiTro), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);

        loadCustomersAndProducts();
        resetInvoice();
        wireActions();
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 8));
        workspace.setBackground(new Color(245, 245, 245));
        workspace.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        workspace.add(createPageHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 8);
        c.gridx = 0;
        c.weightx = 0.75;
        content.add(createLeftWorkspace(), c);
        c.gridx = 1;
        c.weightx = 0.25;
        c.insets = new Insets(0, 0, 0, 0);
        content.add(createPaymentPanel(), c);
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));

        JLabel title = new JLabel("▣  Lập Hóa Đơn Bán Hàng");
        title.setForeground(BRAND_RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(btnLuu);
        actions.add(btnIn);
        actions.add(btnMoi);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel createLeftWorkspace() {
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setOpaque(false);
        left.add(createInvoiceDetails(), BorderLayout.NORTH);
        left.add(createProductTable(), BorderLayout.CENTER);
        return left;
    }

    private JPanel createInvoiceDetails() {
        JPanel section = createSection("ⓘ  Thông tin hóa đơn");
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(9, 12, 10, 12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 5, 7);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        addField(fields, c, 0, 0, "Mã hóa đơn", txtMaHoaDon);
        addField(fields, c, 1, 0, "Ngày lập", txtNgayLap);
        addField(fields, c, 2, 0, "Nhân viên", txtNhanVien);
        addField(fields, c, 0, 2, "Khách hàng", cboKhachHang);
        addField(fields, c, 1, 2, "SĐT", txtSoDienThoai);

        JLabel productLabel = createFieldLabel("Thêm sản phẩm");
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        fields.add(productLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        fields.add(cboSanPham, c);
        c.gridx = 1;
        c.weightx = 0;
        fields.add(new JLabel("Số lượng"), c);
        c.gridx = 1;
        c.gridy = 5;
        fields.add(spnSoLuong, c);
        c.gridx = 2;
        c.gridwidth = 1;
        fields.add(btnThem, c);

        section.add(fields, BorderLayout.CENTER);
        return section;
    }

    private void addField(JPanel panel, GridBagConstraints c, int column, int row,
                          String label, Component input) {
        JLabel fieldLabel = createFieldLabel(label);
        c.gridx = column;
        c.gridy = row;
        c.gridwidth = 1;
        panel.add(fieldLabel, c);

        c.gridy = row + 1;
        if (column == 0 && row == 2) {
            c.gridwidth = 1;
            c.weightx = 1;
        } else if (column == 1 && row == 2) {
            c.gridwidth = 2;
            c.weightx = 2;
        } else {
            c.gridwidth = 1;
        }
        panel.add(input, c);
        c.gridwidth = 1;
        c.weightx = 1;
    }

    private JPanel createProductTable() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        table.setRowHeight(34);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(BRAND_RED);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getColumnModel().getColumn(0).setPreferredWidth(42);
        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(3).setPreferredWidth(105);
        table.getColumnModel().getColumn(4).setPreferredWidth(55);
        table.getColumnModel().getColumn(5).setPreferredWidth(125);
        table.getColumnModel().getColumn(6).setPreferredWidth(55);
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focus, row, column);
                label.setForeground(BRAND_RED);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                return label;
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int column = table.columnAtPoint(event.getPoint());
                if (row >= 0 && column == 6) {
                    String maSanPham = model.getValueAt(row, 1).toString();
                    soLuongDaChon.remove(maSanPham);
                    capNhatBangVaTongTien();
                }
            }
        });
        section.add(new JScrollPane(table), BorderLayout.CENTER);
        return section;
    }

    private JPanel createPaymentPanel() {
        JPanel section = createSection("▦  Tính tiền");
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(9, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 4, 5, 4);
        addSummaryRow(fields, c, "Tổng số lượng:", lblTongSoLuong, Color.DARK_GRAY);
        addSummaryRow(fields, c, "Tổng tiền hàng:", lblTongTienHang, Color.DARK_GRAY);

        JPanel discountRow = new JPanel(new BorderLayout(4, 4));
        discountRow.setOpaque(false);
        JLabel discountLabel = createFieldLabel("Khuyến mãi:");
        discountLabel.setForeground(BRAND_RED);
        discountRow.add(discountLabel, BorderLayout.WEST);
        JPanel discountInput = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        discountInput.setOpaque(false);
        spnKhuyenMai.setPreferredSize(new Dimension(75, 27));
        discountInput.add(spnKhuyenMai);
        discountInput.add(new JLabel("%"));
        discountRow.add(discountInput, BorderLayout.EAST);
        c.gridy++;
        fields.add(discountRow, c);

        addSummaryRow(fields, c, "Thuế VAT (10%):", lblThueVAT, Color.DARK_GRAY);
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_GRAY));
        JLabel totalLabel = new JLabel("TỔNG THANH TOÁN:");
        totalLabel.setForeground(BRAND_RED);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongThanhToan.setForeground(BRAND_RED);
        lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(lblTongThanhToan, BorderLayout.EAST);
        c.gridy++;
        c.insets = new Insets(10, 4, 10, 4);
        fields.add(totalPanel, c);
        c.insets = new Insets(5, 4, 5, 4);

        addFullWidthField(fields, c, "Phương thức thanh toán", cboPhuongThuc);
        addFullWidthField(fields, c, "Tiền khách đưa", txtTienKhachDua);
        addSummaryRow(fields, c, "Tiền thối:", lblTienThoi, new Color(52, 120, 61));
        lblTienThoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addFullWidthField(fields, c, "Ghi chú", new JScrollPane(txtGhiChu));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        section.add(fields, BorderLayout.CENTER);
        return section;
    }

    private void addSummaryRow(JPanel panel, GridBagConstraints c, String title,
                               JLabel value, Color color) {
        JPanel row = new JPanel(new BorderLayout(4, 4));
        row.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setForeground(color);
        row.add(label, BorderLayout.WEST);
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        value.setFont(new Font("Segoe UI", Font.BOLD, 12));
        row.add(value, BorderLayout.EAST);
        c.gridy++;
        panel.add(row, c);
    }

    private void addFullWidthField(JPanel panel, GridBagConstraints c,
                                   String title, Component field) {
        JLabel label = createFieldLabel(title);
        c.gridx = 0;
        c.gridy++;
        panel.add(label, c);
        c.gridy++;
        panel.add(field, c);
    }

    private JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        JLabel heading = new JLabel(title);
        heading.setOpaque(true);
        heading.setBackground(PANEL_GRAY);
        heading.setForeground(BRAND_RED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        heading.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY),
                BorderFactory.createEmptyBorder(8, 11, 8, 11)));
        section.add(heading, BorderLayout.NORTH);
        return section;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private static JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background == Color.WHITE ? BORDER_GRAY : background),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return button;
    }

    private void loadCustomersAndProducts() {
        cboKhachHang.addItem(null);
        cboKhachHang.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "-- Chọn khách hàng --" : value.toString());
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        for (KhachHang khachHang : new KhachHang_DAO().getAllKhachHang()) {
            if ("Hoạt động".equals(khachHang.getTrangThai())) {
                cboKhachHang.addItem(khachHang);
            }
        }
        cboKhachHang.addActionListener(event -> {
            KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
            txtSoDienThoai.setText(khachHang == null ? "" : khachHang.getSoDienThoai());
        });

        cboSanPham.addItem(null);
        cboSanPham.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "-- Chọn sản phẩm --"
                    : value.getMaSanPham() + " — " + value.getTenSanPham()
                            + " (" + value.getSoLuongTon() + " còn)");
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        for (SanPham sanPham : new SanPham_DAO().getAllSanPham()) {
            if ("Đang kinh doanh".equals(sanPham.getTrangThai()) && sanPham.getSoLuongTon() > 0) {
                sanPhamTheoMa.put(sanPham.getMaSanPham(), sanPham);
                cboSanPham.addItem(sanPham);
            }
        }
        if (cboSanPham.getItemCount() <= 1) {
            btnThem.setEnabled(false);
        }
    }

    private void wireActions() {
        btnThem.addActionListener(this);
        btnLuu.addActionListener(this);
        btnIn.addActionListener(this);
        btnMoi.addActionListener(this);
        cboKhachHang.setSelectedIndex(0);
        cboPhuongThuc.addActionListener(event -> capNhatTienThoi());
        spnKhuyenMai.addChangeListener(event -> capNhatBangVaTongTien());
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                capNhatTienThoi();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                capNhatTienThoi();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                capNhatTienThoi();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == btnThem) {
            themSanPham();
        } else if (source == btnLuu) {
            luuHoaDon();
        } else if (source == btnIn) {
            try {
                table.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Không thể in hóa đơn: " + ex.getMessage(),
                        "Lỗi in hóa đơn", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == btnMoi) {
            resetInvoice();
        }
    }

    private void themSanPham() {
        SanPham sanPham = (SanPham) cboSanPham.getSelectedItem();
        if (sanPham == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm.",
                    "Chưa chọn sản phẩm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int soLuong = ((Number) spnSoLuong.getValue()).intValue();
        long tongSoLuong = soLuongDaChon.containsKey(sanPham.getMaSanPham())
                ? (long) soLuongDaChon.get(sanPham.getMaSanPham()) + soLuong : soLuong;
        if (tongSoLuong > sanPham.getSoLuongTon()) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm chỉ còn " + sanPham.getSoLuongTon() + " trong kho.",
                    "Không đủ tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }
        soLuongDaChon.put(sanPham.getMaSanPham(), (int) tongSoLuong);
        capNhatBangVaTongTien();
        cboSanPham.setSelectedIndex(0);
        spnSoLuong.setValue(1);
    }

    private void capNhatBangVaTongTien() {
        model.setRowCount(0);
        int stt = 1;
        int tongSoLuong = 0;
        double tongTien = 0;
        for (Map.Entry<String, Integer> item : soLuongDaChon.entrySet()) {
            SanPham sanPham = sanPhamTheoMa.get(item.getKey());
            int soLuong = item.getValue();
            double thanhTien = sanPham.getGiaBan() * soLuong;
            tongSoLuong += soLuong;
            tongTien += thanhTien;
            model.addRow(new Object[]{
                    stt++,
                    sanPham.getMaSanPham(),
                    sanPham.getTenSanPham(),
                    MONEY_FORMAT.format(sanPham.getGiaBan()) + " đ",
                    soLuong,
                    MONEY_FORMAT.format(thanhTien) + " đ",
                    "Xóa"
            });
        }

        double phanTramGiam = ((Number) spnKhuyenMai.getValue()).doubleValue();
        double tienGiam = Math.round(tongTien * phanTramGiam / 100);
        double thue = Math.round((tongTien - tienGiam) * 0.10);
        double tongThanhToan = tongTien - tienGiam + thue;
        lblTongSoLuong.setText(String.valueOf(tongSoLuong));
        lblTongTienHang.setText(MONEY_FORMAT.format(tongTien) + " đ");
        lblThueVAT.setText(MONEY_FORMAT.format(thue) + " đ");
        lblTongThanhToan.setText(MONEY_FORMAT.format(tongThanhToan) + " đ");
        capNhatTienThoi();
    }

    private void capNhatTienThoi() {
        double tongTien = tinhTongThanhToan();
        BigDecimal tienKhachDua = docTienKhachDua();
        BigDecimal tienThoi = tienKhachDua.subtract(BigDecimal.valueOf(tongTien));
        if ("Tiền mặt".equals(cboPhuongThuc.getSelectedItem()) && tienThoi.signum() > 0) {
            lblTienThoi.setText(MONEY_FORMAT.format(tienThoi) + " đ");
        } else if (tienThoi.signum() < 0) {
            lblTienThoi.setText("Còn thiếu " + MONEY_FORMAT.format(tienThoi.abs()) + " đ");
        } else {
            lblTienThoi.setText("0 đ");
        }
    }

    private double tinhTongThanhToan() {
        double tongTien = 0;
        for (Map.Entry<String, Integer> item : soLuongDaChon.entrySet()) {
            tongTien += sanPhamTheoMa.get(item.getKey()).getGiaBan() * item.getValue();
        }
        double phanTramGiam = ((Number) spnKhuyenMai.getValue()).doubleValue();
        double tienGiam = Math.round(tongTien * phanTramGiam / 100);
        return tongTien - tienGiam + Math.round((tongTien - tienGiam) * 0.10);
    }

    private BigDecimal docTienKhachDua() {
        String text = txtTienKhachDua.getText().trim().replace(",", "").replace(" ", "");
        if (text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void luuHoaDon() {
        if (soLuongDaChon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm.",
                    "Hóa đơn chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal tienKhachDua;
        try {
            tienKhachDua = new BigDecimal(txtTienKhachDua.getText().trim()
                    .replace(",", "").replace(" ", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách đưa hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }
        if (tienKhachDua.signum() < 0
                || tienKhachDua.compareTo(BigDecimal.valueOf(tinhTongThanhToan())) < 0) {
            JOptionPane.showMessageDialog(this, "Tiền khách đưa chưa đủ thanh toán.",
                    "Chưa đủ tiền thanh toán", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }

        NhanVien nhanVien = timNhanVienDangDangNhap();
        if (nhanVien == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy nhân viên \"" + tenNhanVien
                            + "\" trong dữ liệu nhân viên. Không thể lập hóa đơn.",
                    "Không xác định được thu ngân", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ChiTietDonHang> chiTiet = new ArrayList<>();
        for (Map.Entry<String, Integer> item : soLuongDaChon.entrySet()) {
            SanPham sanPham = sanPhamTheoMa.get(item.getKey());
            chiTiet.add(new ChiTietDonHang(null, sanPham, item.getValue(),
                    sanPham.getGiaBan(), 0, 0, null));
        }
        KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
        try {
            String maHoaDon = hoaDonDao.taoHoaDon(
                    nhanVien.getMaNhanVien(),
                    khachHang == null ? null : khachHang.getMaKhachHang(),
                    chiTiet,
                    (String) cboPhuongThuc.getSelectedItem(),
                    tienKhachDua.doubleValue(),
                    ((Number) spnKhuyenMai.getValue()).doubleValue(),
                    10,
                    txtGhiChu.getText().trim());
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn " + maHoaDon + " thành công.",
                    "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
            owner.docDuLieuDatabaseVaoTable();
            dispose();
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn: " + ex.getMessage(),
                    "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private NhanVien timNhanVienDangDangNhap() {
        for (NhanVien nhanVien : new NhanVien_DAO().getAllNhanVien()) {
            if (nhanVien.getHoTen() != null
                    && nhanVien.getHoTen().trim().equals(tenNhanVien.trim())) {
                return nhanVien;
            }
        }
        return null;
    }

    private void resetInvoice() {
        soLuongDaChon.clear();
        model.setRowCount(0);
        txtMaHoaDon.setText("Tự động khi lưu");
        txtNgayLap.setText(DATE_FORMAT.format(new Date()));
        txtNhanVien.setText(tenNhanVien);
        txtSoDienThoai.setText("");
        txtTienKhachDua.setText("0");
        txtGhiChu.setText("");
        cboKhachHang.setSelectedIndex(0);
        cboSanPham.setSelectedIndex(0);
        cboPhuongThuc.setSelectedIndex(0);
        spnSoLuong.setValue(1);
        spnKhuyenMai.setValue(0.0);
        capNhatBangVaTongTien();
    }
}
