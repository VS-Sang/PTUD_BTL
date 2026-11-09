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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.DefaultListModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import DAO.KhuyenMai_DAO;
import DAO.NhanVien_DAO;
import DAO.SanPham_DAO;
import entity.ChiTietDonHang;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;
import entity.SanPham;

public class TaoHoaDonDialog extends JFrame implements ActionListener {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private static final Color PANEL_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(210, 210, 210);
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private final HoaDonPanel owner;
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
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, getWidth(), getHeight());
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
    private final JTextField txtTimSanPham = new JTextField();
    private final DefaultListModel<SanPham> sanPhamGoiYModel = new DefaultListModel<>();
    private final JList<SanPham> lstSanPhamGoiY = new JList<>(sanPhamGoiYModel);
    private final JPopupMenu popupSanPhamGoiY = new JPopupMenu();
    private final JComboBox<KhachHang> cboKhachHang = new JComboBox<>();
    private final JComboBox<String> cboPhuongThuc =
            new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử"});
    private final JSpinner spnSoLuong =
            new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JComboBox<KhuyenMai> cboKhuyenMai = new JComboBox<>();
    private final KhuyenMai_DAO khuyenMaiDao = new KhuyenMai_DAO();
    private final JTextField txtMaHoaDon = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JTextField txtNhanVien = new JTextField();
    private final JTextField txtTienKhachDua = new JTextField();
    private final JTextArea txtGhiChu = new JTextArea(3, 20);
    private final JLabel lblTongSoLuong = new JLabel("0");
    private final JLabel lblTongTienHang = new JLabel("0 đ");
    private final JLabel lblThueVAT = new JLabel("0 đ");
    private final JLabel lblTongThanhToan = new JLabel("0 đ");
    private final JLabel lblTienThoi = new JLabel("0 đ");
    private final JButton btnDuTien = createButton("Trả đủ tiền", new Color(77, 145, 72), Color.WHITE);

    private JPanel rowTienThoi;
    private final Map<String, Integer> soLuongDaChon = new LinkedHashMap<>();
    private final Map<String, SanPham> sanPhamTheoMa = new LinkedHashMap<>();
    private boolean invoiceSaved;
    private boolean khachHangDaChot;
    private boolean dangCapNhatTienKhachDua;
    private String maKhachHangDaChot;
    private final HoaDon_DAO hoaDonDao = new HoaDon_DAO();
    private final JButton btnThem = createButton("Thêm vào hóa đơn", BRAND_RED, Color.WHITE);
    private final JButton btnLuu = createButton("Lưu hóa đơn", new Color(77, 145, 72), Color.WHITE);
    private final JButton btnIn = createButton("In hóa đơn", new Color(54, 103, 193), Color.WHITE);
    private final JButton btnMoi = createButton("Hóa đơn mới", Color.WHITE, Color.DARK_GRAY);

    public TaoHoaDonDialog(HoaDonPanel owner, String tenNhanVien, String vaiTro) {
        this.owner = owner;
        this.tenNhanVien = tenNhanVien == null ? "" : tenNhanVien;
        this.vaiTro = vaiTro;
        setTitle("Điện Máy Đỏ — Lập Hóa Đơn Bán Hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1440, 800);
        setLocationRelativeTo(owner);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        ButtonTooltips.install(this);
        txtMaHoaDon.setEditable(false);
        txtNgayLap.setEditable(false);
        txtNhanVien.setEditable(false);

        add(new ThanhMenu(this.tenNhanVien, this.vaiTro), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);

        lstSanPhamGoiY.setVisibleRowCount(4);
        lstSanPhamGoiY.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstSanPhamGoiY.setCellRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getMaSanPham() + " — "
                    + value.getTenSanPham() + " (" + value.getSoLuongTon() + " còn)");
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            label.setBackground(selected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(selected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        lstSanPhamGoiY.setFixedCellHeight(lstSanPhamGoiY.getFontMetrics(
                lstSanPhamGoiY.getFont()).getHeight() + 6);
        JScrollPane suggestions = new JScrollPane(lstSanPhamGoiY);
        suggestions.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        popupSanPhamGoiY.setFocusable(false);
        popupSanPhamGoiY.add(suggestions);
        loadCustomersAndProducts();
        resetInvoice();
        wireActions();
        btnIn.setToolTipText("in hóa đơn");
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 8));
        workspace.setBackground(new Color(245, 245, 245));
        workspace.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        workspace.add(createPageHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(8, 0));
        content.setOpaque(false);
        content.add(createLeftWorkspace(), BorderLayout.CENTER);
        JPanel paymentPanel = createPaymentPanel();
        paymentPanel.setMinimumSize(new Dimension(300, 0));
        paymentPanel.setPreferredSize(new Dimension(320, 0));
        content.add(paymentPanel, BorderLayout.EAST);
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));

        JLabel title = new JLabel("Lập Hóa Đơn Bán Hàng");
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
        JPanel section = createSection("Thông tin hóa đơn");
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

        JLabel productLabel = createFieldLabel("Tìm mã hoặc tên");
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        fields.add(productLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        fields.add(txtTimSanPham, c);
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 4;
        fields.add(new JLabel("Số lượng"), c);
        c.gridx = 2;
        c.gridy = 5;
        fields.add(spnSoLuong, c);
        c.gridx = 3;
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
            c.gridwidth = 3;
            c.weightx = 3;
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
                if (!invoiceSaved && row >= 0 && column == 6) {
                    String maSanPham = model.getValueAt(row, 1).toString();
                    soLuongDaChon.remove(maSanPham);
                    capNhatBangVaTongTien();
                    capNhatTrangThaiKhachHang();
                    table.repaint();
                }
            }
        });
        section.add(new JScrollPane(table), BorderLayout.CENTER);
        return section;
    }

    private JPanel createPaymentPanel() {
        JPanel section = createSection("Tính tiền");
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

        cboKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboKhuyenMai.setBackground(Color.WHITE);
        addFullWidthField(fields, c, "Khuyến mãi", cboKhuyenMai);

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
        c.gridy++;
        fields.add(btnDuTien, c);








        c.gridy++;

        rowTienThoi = new JPanel(new BorderLayout(4, 4));
        rowTienThoi.setOpaque(false);
        JLabel lblTienThoiTitle = new JLabel("Tiền thối:");
        lblTienThoiTitle.setForeground(new Color(52, 120, 61));
        rowTienThoi.add(lblTienThoiTitle, BorderLayout.WEST);
        lblTienThoi.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTienThoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rowTienThoi.add(lblTienThoi, BorderLayout.EAST);
        JPanel rowTienThoiSlot = new JPanel(new BorderLayout());
        rowTienThoiSlot.setOpaque(false);
        rowTienThoiSlot.setPreferredSize(new Dimension(0, 22));
        rowTienThoiSlot.add(rowTienThoi, BorderLayout.CENTER);
        c.gridy++;
        fields.add(rowTienThoiSlot, c);
        rowTienThoi.setVisible(false);
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
        for (SanPham sanPham : new SanPham_DAO().getAllSanPham()) {
            if ("Đang kinh doanh".equals(sanPham.getTrangThai()) && sanPham.getSoLuongTon() > 0) {
                sanPhamTheoMa.put(sanPham.getMaSanPham(), sanPham);
            }
        }
        locSanPham();

        loadPromotions();
    }

    private void loadPromotions() {
        KhuyenMai previouslySelected = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem(null);
        cboKhuyenMai.setRenderer((list, value, index, selected, focus) -> {
            String text = "-- Không áp dụng --";
            if (value != null) {
                String giamText = "Phần trăm".equalsIgnoreCase(value.getLoaiKhuyenMai()) || "%".equals(value.getLoaiKhuyenMai())
                        ? (int) value.getGiaTriGiam() + "%"
                        : MONEY_FORMAT.format(value.getGiaTriGiam()) + " đ";
                text = value.getMaKhuyenMai() + " — " + value.getTenKhuyenMai() + " (" + giamText + ")";
            }
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });

        for (KhuyenMai km : khuyenMaiDao.getAllKhuyenMai()) {
            if (km.getTrangThai() == null || !"Đã kết thúc".equalsIgnoreCase(km.getTrangThai())) {
                cboKhuyenMai.addItem(km);
            }
        }

        if (previouslySelected != null) {
            for (int i = 1; i < cboKhuyenMai.getItemCount(); i++) {
                KhuyenMai item = cboKhuyenMai.getItemAt(i);
                if (item != null && item.getMaKhuyenMai().equals(previouslySelected.getMaKhuyenMai())) {
                    cboKhuyenMai.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void wireActions() {
        btnThem.addActionListener(this);
        btnLuu.addActionListener(this);
        btnIn.addActionListener(this);
        btnMoi.addActionListener(this);
        btnDuTien.addActionListener(event -> {
            capNhatTienKhachDua(String.valueOf(Math.round(tinhTongThanhToan())));
            capNhatTienThoi();
        });
        cboKhachHang.setSelectedIndex(0);
        txtTimSanPham.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                locSanPham();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                locSanPham();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                locSanPham();
            }
        });
        lstSanPhamGoiY.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                SanPham sanPham = lstSanPhamGoiY.getSelectedValue();
                btnThem.setEnabled(sanPham != null);
            }
        });
        txtTimSanPham.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent event) {
                if (!popupSanPhamGoiY.isVisible()) {
                    return;
                }
                if (event.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    int next = Math.min(lstSanPhamGoiY.getSelectedIndex() + 1,
                            sanPhamGoiYModel.size() - 1);
                    lstSanPhamGoiY.setSelectedIndex(next);
                    lstSanPhamGoiY.ensureIndexIsVisible(next);
                    event.consume();
                } else if (event.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    int next = Math.max(0, lstSanPhamGoiY.getSelectedIndex() - 1);
                    lstSanPhamGoiY.setSelectedIndex(next);
                    lstSanPhamGoiY.ensureIndexIsVisible(next);
                    event.consume();
                } else if (event.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (lstSanPhamGoiY.getSelectedValue() != null) {
                        themSanPham();
                    } else if (!sanPhamGoiYModel.isEmpty()) {
                        lstSanPhamGoiY.setSelectedIndex(0);
                    }
                    event.consume();
                } else if (event.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    popupSanPhamGoiY.setVisible(false);
                    lstSanPhamGoiY.clearSelection();
                    btnThem.setEnabled(false);
                    event.consume();
                }
            }
        });
        cboPhuongThuc.addActionListener(event -> capNhatTienThoi());
        cboKhuyenMai.addActionListener(event -> capNhatBangVaTongTien());
        cboKhuyenMai.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                loadPromotions();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                if (!dangCapNhatTienKhachDua) {
                    capNhatTienThoi();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                if (!dangCapNhatTienKhachDua) {
                    capNhatTienThoi();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                if (!dangCapNhatTienKhachDua) {
                    capNhatTienThoi();
                }
            }
        });
    }

    private void locSanPham() {
        String tuKhoa = txtTimSanPham.getText().trim().toLowerCase(java.util.Locale.ROOT);
        sanPhamGoiYModel.clear();
        lstSanPhamGoiY.clearSelection();
        if (tuKhoa.isEmpty()) {
            popupSanPhamGoiY.setVisible(false);
            btnThem.setEnabled(false);
            return;
        }
        for (SanPham sanPham : sanPhamTheoMa.values()) {
            String thongTin = (sanPham.getMaSanPham() + " " + sanPham.getTenSanPham())
                    .toLowerCase(java.util.Locale.ROOT);
            if (thongTin.contains(tuKhoa)) {
                sanPhamGoiYModel.addElement(sanPham);
            }
        }
        if (sanPhamGoiYModel.isEmpty() || !txtTimSanPham.isShowing()) {
            popupSanPhamGoiY.setVisible(false);
            btnThem.setEnabled(false);
            return;
        }
        lstSanPhamGoiY.setVisibleRowCount(Math.min(5, sanPhamGoiYModel.size()));
        popupSanPhamGoiY.setPreferredSize(new Dimension(
                txtTimSanPham.getWidth(),
                Math.min(5, sanPhamGoiYModel.size()) * lstSanPhamGoiY.getFixedCellHeight() + 4));
        popupSanPhamGoiY.show(txtTimSanPham, 0, txtTimSanPham.getHeight());
        btnThem.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == btnThem) {
            themSanPham();
        } else if (source == btnLuu) {
            luuHoaDon();
        } else if (source == btnIn) {
            xuatHoaDonPdf();
        } else if (source == btnMoi) {
            resetInvoice();
        }
    }

    private void themSanPham() {
        SanPham sanPham = lstSanPhamGoiY.getSelectedValue();
        if (sanPham == null) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Vui lòng chọn một sản phẩm trong danh sách đề xuất.",
                    "Chưa chọn sản phẩm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int soLuong = ((Number) spnSoLuong.getValue()).intValue();
        long tongSoLuong = soLuongDaChon.containsKey(sanPham.getMaSanPham())
                ? (long) soLuongDaChon.get(sanPham.getMaSanPham()) + soLuong : soLuong;
        if (tongSoLuong > sanPham.getSoLuongTon()) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Sản phẩm chỉ còn " + sanPham.getSoLuongTon() + " trong kho.",
                    "Không đủ tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }
        soLuongDaChon.put(sanPham.getMaSanPham(), (int) tongSoLuong);
        if (!khachHangDaChot) {
            KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
            maKhachHangDaChot = khachHang == null ? null : khachHang.getMaKhachHang();
            khachHangDaChot = true;
        }
        capNhatTrangThaiKhachHang();
        capNhatBangVaTongTien();
        table.repaint();
        txtTimSanPham.setText("");
        lstSanPhamGoiY.clearSelection();
        spnSoLuong.setValue(1);
    }

    private double tinhTienGiam(double tongTienHang) {
        KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        if (km == null || tongTienHang < km.getDonHangToiThieu()) {
            return 0;
        }
        double tienGiam = 0;
        if ("Phần trăm".equalsIgnoreCase(km.getLoaiKhuyenMai()) || "%".equals(km.getLoaiKhuyenMai())) {
            tienGiam = Math.round(tongTienHang * km.getGiaTriGiam() / 100.0);
            if (km.getGiaTriGiamToiDa() > 0 && tienGiam > km.getGiaTriGiamToiDa()) {
                tienGiam = km.getGiaTriGiamToiDa();
            }
        } else {
            tienGiam = km.getGiaTriGiam();
        }
        return Math.min(tienGiam, tongTienHang);
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

        double tienGiam = tinhTienGiam(tongTien);
        double thue = Math.round((tongTien - tienGiam) * 0.10);
        double tongThanhToan = tongTien - tienGiam + thue;
        lblTongSoLuong.setText(String.valueOf(tongSoLuong));
        lblTongTienHang.setText(MONEY_FORMAT.format(tongTien) + " đ");
        lblThueVAT.setText(MONEY_FORMAT.format(thue) + " đ");
        lblTongThanhToan.setText(MONEY_FORMAT.format(tongThanhToan) + " đ");
        capNhatTienThoi();
    }

    private void capNhatTienThoi() {
        String phuongThuc = String.valueOf(cboPhuongThuc.getSelectedItem()).trim();
        boolean isTienMat = "Tiền mặt".equalsIgnoreCase(phuongThuc);
        boolean isChuyenKhoan = "Chuyển khoản".equalsIgnoreCase(phuongThuc);
        if (isChuyenKhoan) {
            capNhatTienKhachDua(String.valueOf(Math.round(tinhTongThanhToan())));
        }
        if (rowTienThoi != null) {
            rowTienThoi.setVisible(isTienMat);
            rowTienThoi.getParent().revalidate();
            rowTienThoi.getParent().repaint();
        }



        if (!isTienMat) {
            lblTienThoi.setText("0 đ");
            return;
        }
        double tongTien = tinhTongThanhToan();
        BigDecimal tienKhachDua = docTienKhachDua();
        BigDecimal tienThoi = tienKhachDua.subtract(BigDecimal.valueOf(tongTien));
        if (tienThoi.signum() > 0) {
            lblTienThoi.setText(MONEY_FORMAT.format(tienThoi) + " đ");
        } else if (tienThoi.signum() < 0) {
            lblTienThoi.setText("Còn thiếu " + MONEY_FORMAT.format(tienThoi.abs()) + " đ");
        } else {
            lblTienThoi.setText("0 đ");
        }
    }

    private void capNhatTienKhachDua(String tien) {
        dangCapNhatTienKhachDua = true;
        try {
            txtTienKhachDua.setText(tien);
        } finally {
            dangCapNhatTienKhachDua = false;
        }
    }

    private double tinhTongThanhToan() {
        double tongTien = 0;
        for (Map.Entry<String, Integer> item : soLuongDaChon.entrySet()) {
            tongTien += sanPhamTheoMa.get(item.getKey()).getGiaBan() * item.getValue();
        }
        double tienGiam = tinhTienGiam(tongTien);
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
        if (invoiceSaved) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Hóa đơn này đã được lưu. Vui lòng tạo hóa đơn mới.",
                    "Hóa đơn đã lưu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (soLuongDaChon.isEmpty()) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Vui lòng thêm ít nhất một sản phẩm.",
                    "Hóa đơn chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
        String maKhachHangHienTai = khachHang == null ? null : khachHang.getMaKhachHang();
        if (khachHangDaChot && !java.util.Objects.equals(maKhachHangDaChot, maKhachHangHienTai)) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Không thể đổi khách hàng khi hóa đơn đã có sản phẩm. "
                            + "Vui lòng tạo hóa đơn mới nếu muốn chọn khách hàng khác.",
                    "Khách hàng không khớp", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal tienKhachDua;
        try {
            tienKhachDua = new BigDecimal(txtTienKhachDua.getText().trim()
                    .replace(",", "").replace(" ", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Vui lòng nhập tiền khách đưa hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }
        if (tienKhachDua.signum() < 0
                || tienKhachDua.compareTo(BigDecimal.valueOf(tinhTongThanhToan())) < 0) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Tiền khách đưa chưa đủ thanh toán.",
                    "Chưa đủ tiền thanh toán", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }

        NhanVien nhanVien = timNhanVienDangDangNhap();
        if (nhanVien == null) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Không tìm thấy nhân viên \"" + tenNhanVien
                            + "\" trong dữ liệu nhân viên. Không thể lập hóa đơn.",
                    "Không xác định được thu ngân", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ChiTietDonHang> chiTiet = new ArrayList<>();
        double tongTienHang = 0;
        for (Map.Entry<String, Integer> item : soLuongDaChon.entrySet()) {
            SanPham sanPham = sanPhamTheoMa.get(item.getKey());
            chiTiet.add(new ChiTietDonHang(null, sanPham, item.getValue(),
                    sanPham.getGiaBan(), 0, 0, null));
            tongTienHang += sanPham.getGiaBan() * item.getValue();
        }
        KhuyenMai selectedKM = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        String maKM = (selectedKM != null) ? selectedKM.getMaKhuyenMai() : null;
        double tienGiam = tinhTienGiam(tongTienHang);

        try {
            String maHoaDon = hoaDonDao.taoHoaDon(
                    nhanVien.getMaNhanVien(),
                    khachHang == null ? null : khachHang.getMaKhachHang(),
                    chiTiet,
                    (String) cboPhuongThuc.getSelectedItem(),
                    tienKhachDua.doubleValue(),
                    maKM,
                    tienGiam,
                    10,
                    txtGhiChu.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Tạo hóa đơn " + maHoaDon + " thành công.\nBạn có muốn in/xuất hóa đơn ngay không?",
                    "Hoàn tất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                xuatHoaDonPdf();
            }

            txtMaHoaDon.setText(maHoaDon);
            invoiceSaved = true;
            btnLuu.setEnabled(false);
            setInvoiceInputsEnabled(false);
            if (owner != null) {
                owner.docDuLieuDatabaseVaoTable();
            } else {
                KhungChinh.lamMoiTrangHoaDon();
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Không thể tạo hóa đơn: " + ex.getMessage(),
                    "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuatHoaDonPdf() {
        if (soLuongDaChon.isEmpty()) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Vui lòng thêm sản phẩm trước khi in hóa đơn.",
                    "Hóa đơn chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn thành PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("hoa-don-"
                + (invoiceSaved ? txtMaHoaDon.getText().trim() : "ban-nhap") + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile().getAbsoluteFile();
        if (!selectedFile.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) {
            selectedFile = new File(selectedFile.getParentFile(),
                    selectedFile.getName() + ".pdf");
        }
        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            ghiHoaDonPdf(selectedFile);
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(selectedFile);
                }
            } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Đã xuất hóa đơn PDF:\n"
                    + selectedFile.getAbsolutePath(), "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Không thể xuất hóa đơn PDF:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inHoaDonMayIn() {
        if (!invoiceSaved) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()),
                    "Hãy lưu hóa đơn trước khi in bằng máy in.",
                    "Hóa đơn chưa được lưu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            HoaDon hoaDon = hoaDonDao.getHoaDonByMa(txtMaHoaDon.getText().trim());
            BoInHoaDon.print(this, hoaDon,
                    hoaDonDao.getChiTietHoaDon(hoaDon.getMaHoaDon()));
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), "Không thể tải hóa đơn để in:\n"
                    + exception.getMessage(), "Lỗi in hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ghiHoaDonPdf(File file) throws IOException {
        String maHoaDon = invoiceSaved ? txtMaHoaDon.getText().trim() : "BAN-NHAP";
        KhachHang khachHang = (KhachHang) cboKhachHang.getSelectedItem();
        String tenKhachHang = (khachHang == null || khachHang.getHoTen() == null)
                ? "Khách lẻ" : khachHang.getHoTen();
        String sdtKhachHang = (khachHang == null || khachHang.getSoDienThoai() == null)
                ? "—" : khachHang.getSoDienThoai();
        KhuyenMai khuyenMai = (KhuyenMai) cboKhuyenMai.getSelectedItem();
        String tenKhuyenMai = khuyenMai == null ? "Không áp dụng" : khuyenMai.getTenKhuyenMai();
        String phuongThuc = String.valueOf(cboPhuongThuc.getSelectedItem());
        boolean isChuyenKhoan = "Chuyển khoản".equalsIgnoreCase(phuongThuc.trim());

        List<String[]> metadataRows = new ArrayList<>();
        metadataRows.add(new String[]{"Mã hóa đơn", maHoaDon, "Khách hàng", tenKhachHang});
        metadataRows.add(new String[]{"Ngày lập", dinhDangNgayLapPdf(), "Số điện thoại", sdtKhachHang});
        metadataRows.add(new String[]{"Nhân viên", txtNhanVien.getText().trim(), "Phương thức TT", phuongThuc});
        if (khuyenMai != null) {
            metadataRows.add(new String[]{"Khuyến mãi", tenKhuyenMai, "", ""});
        }

        List<String> headers = Arrays.asList("STT", "MÃ SP", "TÊN SẢN PHẨM", "SỐ LƯỢNG", "ĐƠN GIÁ", "GIẢM GIÁ", "THÀNH TIỀN");
        List<String[]> rows = new ArrayList<>();
        double tongTienHang = 0;
        for (int row = 0; row < model.getRowCount(); row++) {
            String maSanPham = String.valueOf(model.getValueAt(row, 1));
            String tenSanPham = String.valueOf(model.getValueAt(row, 2));
            int soLuong = ((Number) model.getValueAt(row, 4)).intValue();
            SanPham sanPham = sanPhamTheoMa.get(maSanPham);
            double donGia = sanPham != null ? sanPham.getGiaBan() : 0;
            double thanhTien = donGia * soLuong;
            tongTienHang += thanhTien;

            rows.add(new String[]{
                    String.valueOf(row + 1),
                    maSanPham,
                    tenSanPham,
                    String.valueOf(soLuong),
                    MONEY_FORMAT.format(donGia) + " đ",
                    "0 đ",
                    MONEY_FORMAT.format(thanhTien) + " đ"
            });
        }

        double tienGiam = tinhTienGiam(tongTienHang);
        double thue = Math.round((tongTienHang - tienGiam) * 0.10);
        double tongThanhToan = tongTienHang - tienGiam + thue;
        BigDecimal tienKhachDua = docTienKhachDua();
        BigDecimal tienThoi = tienKhachDua.subtract(BigDecimal.valueOf(tongThanhToan));

        List<String[]> totals = new ArrayList<>();
        totals.add(new String[]{"Tổng số lượng", lblTongSoLuong.getText()});
        totals.add(new String[]{"Tổng tiền hàng", MONEY_FORMAT.format(tongTienHang) + " đ"});
        if (tienGiam > 0) {
            totals.add(new String[]{"Tiền giảm giá", MONEY_FORMAT.format(tienGiam) + " đ"});
        }
        totals.add(new String[]{"Thuế VAT (10%)", MONEY_FORMAT.format(thue) + " đ"});
        totals.add(new String[]{"TỔNG THANH TOÁN", MONEY_FORMAT.format(tongThanhToan) + " đ", "true"});
        totals.add(new String[]{"Tiền khách đưa", MONEY_FORMAT.format(tienKhachDua) + " đ"});
        if (!isChuyenKhoan) {
            totals.add(new String[]{"Tiền thối", MONEY_FORMAT.format(tienThoi.max(BigDecimal.ZERO)) + " đ"});
        }

        double[] columnWeights = new double[]{0.22, 0.45, 1.25, 0.35, 0.55, 0.45, 0.65};
        XuatTaiLieuPdf.write(file, "HÓA ĐƠN THANH TOÁN", maHoaDon, metadataRows,
                "DANH SÁCH SẢN PHẨM", headers, rows, columnWeights, totals,
                txtGhiChu.getText().trim(), isChuyenKhoan);
    }

    private String dinhDangNgayLapPdf() {
        try {
            Date ngayLap = DATE_FORMAT.parse(txtNgayLap.getText().trim());
            return new SimpleDateFormat("dd/MM/yyyy").format(ngayLap);
        } catch (ParseException exception) {
            return txtNgayLap.getText().trim();
        }
    }

    private Component boMeThongBao() {
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(getContentPane());
        return window == null ? this : window;
    }

    private void setInvoiceInputsEnabled(boolean enabled) {
        table.setEnabled(enabled);
        btnThem.setEnabled(enabled);
        txtTimSanPham.setEnabled(enabled);
        lstSanPhamGoiY.setEnabled(enabled);
        if (!enabled) {
            popupSanPhamGoiY.setVisible(false);
        }
        spnSoLuong.setEnabled(enabled);
        capNhatTrangThaiKhachHang(enabled);
        cboPhuongThuc.setEnabled(enabled);
        cboKhuyenMai.setEnabled(enabled);
        txtTienKhachDua.setEnabled(enabled);
        txtGhiChu.setEnabled(enabled);
    }

    private void capNhatTrangThaiKhachHang() {
        capNhatTrangThaiKhachHang(!invoiceSaved);
    }

    private void capNhatTrangThaiKhachHang(boolean invoiceInputsEnabled) {
        boolean enabled = invoiceInputsEnabled && !khachHangDaChot;
        cboKhachHang.setEnabled(enabled);
        cboKhachHang.setToolTipText(khachHangDaChot
                ? "Khách hàng đã được cố định cho hóa đơn này. Tạo hóa đơn mới để chọn khách khác."
                : "Chọn khách hàng trước khi thêm sản phẩm.");
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
        invoiceSaved = false;
        khachHangDaChot = false;
        maKhachHangDaChot = null;
        btnLuu.setEnabled(true);
        setInvoiceInputsEnabled(true);
        model.setRowCount(0);
        txtMaHoaDon.setText("Tự động khi lưu");
        txtNgayLap.setText(DATE_FORMAT.format(new Date()));
        txtNhanVien.setText(tenNhanVien);
        capNhatTienKhachDua("0");
        txtGhiChu.setText("");
        cboKhachHang.setSelectedIndex(0);
        txtTimSanPham.setText("");
        lstSanPhamGoiY.clearSelection();
        locSanPham();
        cboPhuongThuc.setSelectedIndex(0);
        spnSoLuong.setValue(1);
        loadPromotions();
        cboKhuyenMai.setSelectedIndex(0);
        capNhatBangVaTongTien();
        table.repaint();
    }
}
