package gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import DAO.NhaCungCap_DAO;
import DAO.NhanVien_DAO;
import DAO.PhieuNhapKho_DAO;
import DAO.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.text.ParseException;
import java.util.Properties;

import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.SanPham;

public class ImportGoodsPanel extends JFrame implements ActionListener {

    private static final Color BRAND_RED = new Color(177, 48, 42);
    private static final Color HEADER_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(215, 215, 215);
    private static final Color BG_LIGHT = new Color(245, 246, 248);
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");

    private final String currentUserName;
    private final String currentUserRole;

    private MenuPanel menuPanel;
    private JTextField txtMaPhieuNhap;
    private JDatePickerImpl datePickerNgayNhap;
    private UtilDateModel modelNgayNhap;
    private JTextField txtNguoiLap;
    private JComboBox<Object> cboNhaCungCap;
    private JComboBox<String> cboKhoNhap;

    private JComboBox<Object> cboSanPham;
    private JSpinner spnSoLuong;
    private JButton btnThem;

    private DefaultTableModel tableModel;
    private JTable tableHangs;
    private JLabel lblTongGiaTri;

    private JTextArea txtGhiChu;

    private JButton btnLuuPhieu;
    private JButton btnLamMoi;

    private final NhaCungCap_DAO nhaCungCapDao = new NhaCungCap_DAO();
    private final SanPham_DAO sanPhamDao = new SanPham_DAO();
    private final PhieuNhapKho_DAO phieuNhapKhoDao = new PhieuNhapKho_DAO();
    private final NhanVien_DAO nhanVienDao = new NhanVien_DAO();

    private final List<SanPham> listSanPham = new ArrayList<>();
    private final List<NhaCungCap> listNhaCungCap = new ArrayList<>();

    public ImportGoodsPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public ImportGoodsPanel(String userName, String userRole) {
        this.currentUserName = (userName != null && !userName.trim().isEmpty()) ? userName : "Nguyễn Văn An";
        this.currentUserRole = (userRole != null && !userRole.trim().isEmpty()) ? userRole : "Quản lý";

        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel(currentUserName, currentUserRole);
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(BG_LIGHT);
        pMain.setBorder(new EmptyBorder(10, 15, 10, 15));

        pMain.add(createTopTitleBar(), BorderLayout.NORTH);

        JPanel pContent = new JPanel();
        pContent.setLayout(new BoxLayout(pContent, BoxLayout.Y_AXIS));
        pContent.setOpaque(false);

        pContent.add(createCardThongTin());
        pContent.add(Box.createRigidArea(new Dimension(0, 10)));
        pContent.add(createCardDanhSachHang());
        pContent.add(Box.createRigidArea(new Dimension(0, 10)));
        pContent.add(createCardGhiChu());

        JScrollPane scrollPane = new JScrollPane(pContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pMain.add(scrollPane, BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        loadDataCombobox();
        lamMoiForm();
    }

    private JPanel createTopTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel pLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pLeft.setOpaque(false);
        JLabel lblIcon = new JLabel("🛒");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setForeground(BRAND_RED);
        JLabel lblTitle = new JLabel("Nhập Hàng Vào Kho");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(BRAND_RED);
        pLeft.add(lblIcon);
        pLeft.add(lblTitle);

        JPanel pRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pRight.setOpaque(false);

        btnLuuPhieu = new JButton("💾 Lưu phiếu nhập");
        btnLuuPhieu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLuuPhieu.setForeground(Color.WHITE);
        btnLuuPhieu.setBackground(BRAND_RED);
        btnLuuPhieu.setFocusPainted(false);
        btnLuuPhieu.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BRAND_RED, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btnLuuPhieu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuuPhieu.addActionListener(this);

        btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLamMoi.setForeground(new Color(50, 50, 50));
        btnLamMoi.setBackground(Color.WHITE);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLamMoi.addActionListener(this);

        pRight.add(btnLuuPhieu);
        pRight.add(btnLamMoi);

        bar.add(pLeft, BorderLayout.WEST);
        bar.add(pRight, BorderLayout.EAST);
        return bar;
    }

    private JPanel createCardHeader(String icon, String title) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        header.setBackground(HEADER_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblIcon.setForeground(BRAND_RED);

        JLabel lblText = new JLabel(title);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblText.setForeground(BRAND_RED);

        header.add(lblIcon);
        header.add(lblText);
        return header;
    }

    private JPanel createCardThongTin() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_GRAY, 1));

        card.add(createCardHeader("🚚", "Thông tin phiếu nhập"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 16, 14, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 8, 3, 8);

        // Row 0: Labels for Row 1
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        body.add(createLabel("Mã phiếu nhập"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        body.add(createLabel("Ngày nhập"), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.30;
        body.add(createLabel("Người lập phiếu"), gbc);

        // Row 1: Inputs
        gbc.gridy = 1;

        gbc.gridx = 0;
        txtMaPhieuNhap = new JTextField();
        txtMaPhieuNhap.setEditable(false);
        txtMaPhieuNhap.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMaPhieuNhap.setBackground(new Color(245, 245, 245));
        txtMaPhieuNhap.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(5, 8, 5, 8)));
        body.add(txtMaPhieuNhap, gbc);

        gbc.gridx = 1;
        modelNgayNhap = new UtilDateModel();
        datePickerNgayNhap = taoDatePicker(modelNgayNhap, new Date());
        body.add(datePickerNgayNhap, gbc);

        gbc.gridx = 2;
        txtNguoiLap = new JTextField(currentUserName);
        txtNguoiLap.setEditable(false);
        txtNguoiLap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNguoiLap.setBackground(new Color(245, 245, 245));
        txtNguoiLap.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(5, 8, 5, 8)));
        body.add(txtNguoiLap, gbc);

        // Row 2: Labels for Row 3
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 8, 3, 8);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        body.add(createLabel("Nhà cung cấp *"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        body.add(createLabel("Kho nhập"), gbc);

        // Row 3: Inputs
        gbc.gridy = 3;
        gbc.insets = new Insets(3, 8, 3, 8);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboNhaCungCap.setBackground(Color.WHITE);
        cboNhaCungCap.setPreferredSize(new Dimension(cboNhaCungCap.getPreferredSize().width, 30));
        body.add(cboNhaCungCap, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        cboKhoNhap = new JComboBox<>(new String[]{
                "Kho chính Q.1", "Kho Thủ Đức", "Kho Bình Thạnh", "Kho Tân Bình"
        });
        cboKhoNhap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboKhoNhap.setBackground(Color.WHITE);
        cboKhoNhap.setPreferredSize(new Dimension(cboKhoNhap.getPreferredSize().width, 30));
        body.add(cboKhoNhap, gbc);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JDatePickerImpl taoDatePicker(UtilDateModel model, Date defaultDate) {
        if (defaultDate != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(defaultDate);
            model.setDate(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
            model.setSelected(true);
        }
        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public Object stringToValue(String text) throws ParseException {
                return dateFormatter.parseObject(text);
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    java.util.Calendar cal = (java.util.Calendar) value;
                    return dateFormatter.format(cal.getTime());
                }
                return "";
            }
        });
        datePicker.setBackground(Color.WHITE);
        return datePicker;
    }

    private JPanel createCardDanhSachHang() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_GRAY, 1));

        card.add(createCardHeader("📋", "Danh sách hàng nhập"), BorderLayout.NORTH);

        JPanel pInner = new JPanel(new BorderLayout(0, 10));
        pInner.setOpaque(false);
        pInner.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Action row: Select product + Quantity + Add button
        JPanel pActionRow = new JPanel(new GridBagLayout());
        pActionRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 8);

        gbc.gridx = 0;
        gbc.weightx = 0.82;
        cboSanPham = new JComboBox<>();
        cboSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboSanPham.setBackground(Color.WHITE);
        cboSanPham.setPreferredSize(new Dimension(100, 32));
        pActionRow.add(cboSanPham, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.08;
        spnSoLuong = new JSpinner(new SpinnerNumberModel(10, 1, 99999, 1));
        spnSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        spnSoLuong.setPreferredSize(new Dimension(70, 32));
        pActionRow.add(spnSoLuong, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.10;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnThem = new JButton("+ Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBackground(BRAND_RED);
        btnThem.setFocusPainted(false);
        btnThem.setBorder(new EmptyBorder(7, 16, 7, 16));
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(this);
        pActionRow.add(btnThem, gbc);

        pInner.add(pActionRow, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Mã SP", "Tên sản phẩm", "Giá nhập", "SL", "Thành tiền", "Xóa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHangs = new JTable(tableModel);
        tableHangs.setRowHeight(36);
        tableHangs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHangs.setSelectionBackground(new Color(245, 230, 230));
        tableHangs.setSelectionForeground(Color.BLACK);
        tableHangs.setShowGrid(true);
        tableHangs.setGridColor(new Color(235, 235, 235));

        // Header style
        JTableHeader header = tableHangs.getTableHeader();
        header.setBackground(BRAND_RED);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 32));

        // Set column alignments and widths
        TableColumnModel colModel = tableHangs.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(90);  // Mã SP
        colModel.getColumn(1).setPreferredWidth(320); // Tên SP
        colModel.getColumn(2).setPreferredWidth(140); // Giá nhập
        colModel.getColumn(3).setPreferredWidth(70);  // SL
        colModel.getColumn(4).setPreferredWidth(150); // Thành tiền
        colModel.getColumn(5).setPreferredWidth(60);  // Xóa

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        colModel.getColumn(0).setCellRenderer(centerRenderer);
        colModel.getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        colModel.getColumn(2).setCellRenderer(rightRenderer);
        colModel.getColumn(4).setCellRenderer(rightRenderer);

        // Delete button renderer
        colModel.getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private final JButton btnTrash = new JButton("🗑");
            {
                btnTrash.setBackground(BRAND_RED);
                btnTrash.setForeground(Color.WHITE);
                btnTrash.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
                btnTrash.setBorder(new EmptyBorder(3, 8, 3, 8));
                btnTrash.setFocusPainted(false);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                p.add(btnTrash);
                return p;
            }
        });

        // Click delete on row
        tableHangs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableHangs.rowAtPoint(e.getPoint());
                int col = tableHangs.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(ImportGoodsPanel.this,
                            "Bạn có chắc muốn xóa sản phẩm này khỏi danh sách nhập?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(row);
                        capNhatTongGiaTri();
                    }
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableHangs);
        scrollTable.setPreferredSize(new Dimension(800, 180));
        scrollTable.setBorder(new LineBorder(BORDER_GRAY, 1));
        pInner.add(scrollTable, BorderLayout.CENTER);

        // Bottom row: Total value
        JPanel pTotalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pTotalRow.setOpaque(false);
        lblTongGiaTri = new JLabel("Tổng giá trị nhập: 0đ");
        lblTongGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongGiaTri.setForeground(BRAND_RED);
        pTotalRow.add(lblTongGiaTri);
        pInner.add(pTotalRow, BorderLayout.SOUTH);

        card.add(pInner, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardGhiChu() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_GRAY, 1));

        card.add(createCardHeader("📝", "Ghi chú"), BorderLayout.NORTH);

        JPanel pInner = new JPanel(new BorderLayout());
        pInner.setOpaque(false);
        pInner.setBorder(new EmptyBorder(10, 16, 12, 16));

        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setBorder(new LineBorder(BORDER_GRAY, 1));
        pInner.add(scrollGhiChu, BorderLayout.CENTER);

        card.add(pInner, BorderLayout.CENTER);
        return card;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private void loadDataCombobox() {
        // Load NCC
        listNhaCungCap.clear();
        cboNhaCungCap.removeAllItems();
        cboNhaCungCap.addItem("-- Chọn NCC --");
        listNhaCungCap.addAll(nhaCungCapDao.getAllNhaCungCap());
        for (NhaCungCap ncc : listNhaCungCap) {
            cboNhaCungCap.addItem(ncc.getTenNhaCungCap());
        }

        // Load SanPham
        listSanPham.clear();
        cboSanPham.removeAllItems();
        cboSanPham.addItem("-- Chọn sản phẩm nhập --");
        listSanPham.addAll(sanPhamDao.getAllSanPham());
        for (SanPham sp : listSanPham) {
            cboSanPham.addItem(sp.getMaSanPham() + " - " + sp.getTenSanPham() + " (Giá nhập: " + MONEY_FORMAT.format(sp.getGiaNhap()) + "đ)");
        }
    }

    private void lamMoiForm() {
        txtMaPhieuNhap.setText(phieuNhapKhoDao.sinhMaPhieuNhap());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        modelNgayNhap.setDate(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
        modelNgayNhap.setSelected(true);
        txtNguoiLap.setText(currentUserName);
        if (cboNhaCungCap.getItemCount() > 0) {
            cboNhaCungCap.setSelectedIndex(0);
        }
        if (cboKhoNhap.getItemCount() > 0) {
            cboKhoNhap.setSelectedIndex(0);
        }
        if (cboSanPham.getItemCount() > 0) {
            cboSanPham.setSelectedIndex(0);
        }
        spnSoLuong.setValue(10);
        tableModel.setRowCount(0);
        txtGhiChu.setText("");
        capNhatTongGiaTri();
    }

    private void themSanPhamVaoBang() {
        int spIndex = cboSanPham.getSelectedIndex();
        if (spIndex <= 0 || spIndex - 1 >= listSanPham.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần nhập!", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            cboSanPham.requestFocus();
            return;
        }

        SanPham sp = listSanPham.get(spIndex - 1);
        int soLuong = (int) spnSoLuong.getValue();
        if (soLuong <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập phải lớn hơn 0!", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double donGia = sp.getGiaNhap();

        // Check if already in table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ma = (String) tableModel.getValueAt(i, 0);
            if (ma.equals(sp.getMaSanPham())) {
                int slHienTai = (int) tableModel.getValueAt(i, 3);
                int slMoi = slHienTai + soLuong;
                double ttMoi = slMoi * donGia;
                tableModel.setValueAt(slMoi, i, 3);
                tableModel.setValueAt(MONEY_FORMAT.format(ttMoi) + "đ", i, 4);
                capNhatTongGiaTri();
                cboSanPham.setSelectedIndex(0);
                spnSoLuong.setValue(10);
                return;
            }
        }

        // Add new row
        double thanhTien = soLuong * donGia;
        tableModel.addRow(new Object[]{
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                MONEY_FORMAT.format(donGia) + "đ",
                soLuong,
                MONEY_FORMAT.format(thanhTien) + "đ",
                "Xóa"
        });

        capNhatTongGiaTri();
        cboSanPham.setSelectedIndex(0);
        spnSoLuong.setValue(10);
    }

    private void capNhatTongGiaTri() {
        double tong = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int sl = (int) tableModel.getValueAt(i, 3);
            String giaStr = tableModel.getValueAt(i, 2).toString().replace(".", "").replace(",", "").replace("đ", "").trim();
            try {
                double gia = Double.parseDouble(giaStr);
                tong += sl * gia;
            } catch (NumberFormatException ignored) {}
        }
        lblTongGiaTri.setText("Tổng giá trị nhập: " + MONEY_FORMAT.format(tong) + "đ");
    }

    private void luuPhieuNhap() {
        if (cboNhaCungCap.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà cung cấp!", "Chưa chọn NCC", JOptionPane.WARNING_MESSAGE);
            cboNhaCungCap.requestFocus();
            return;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Danh sách hàng nhập không được để trống!", "Chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            cboSanPham.requestFocus();
            return;
        }

        NhaCungCap ncc = listNhaCungCap.get(cboNhaCungCap.getSelectedIndex() - 1);

        // Find logged-in NhanVien
        NhanVien nv = null;
        for (NhanVien n : nhanVienDao.getAllNhanVien()) {
            if (n.getHoTen() != null && n.getHoTen().trim().equalsIgnoreCase(currentUserName.trim())) {
                nv = n;
                break;
            }
        }
        if (nv == null) {
            List<NhanVien> listNV = nhanVienDao.getAllNhanVien();
            if (!listNV.isEmpty()) {
                nv = listNV.get(0);
            }
        }

        String maPhieu = txtMaPhieuNhap.getText().trim();
        Date ngayNhap = (Date) modelNgayNhap.getValue();
        if (ngayNhap == null) ngayNhap = new Date();

        int tongSoLuong = 0;
        double tongTienNhap = 0;

        List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();
        PhieuNhapKho pnk = new PhieuNhapKho();
        pnk.setMaPhieuNhap(maPhieu);
        pnk.setNhaCungCap(ncc);
        pnk.setNhanVien(nv);
        pnk.setNgayNhap(ngayNhap);
        pnk.setTrangThai("Đã nhập");
        pnk.setGhiChu(txtGhiChu.getText().trim());

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String maSP = (String) tableModel.getValueAt(i, 0);
            int sl = (int) tableModel.getValueAt(i, 3);
            String giaStr = tableModel.getValueAt(i, 2).toString().replace(".", "").replace(",", "").replace("đ", "").trim();
            double gia = Double.parseDouble(giaStr);
            double thanhTien = sl * gia;

            tongSoLuong += sl;
            tongTienNhap += thanhTien;

            SanPham sp = new SanPham(maSP);
            ChiTietPhieuNhap ct = new ChiTietPhieuNhap(pnk, sp, sl, gia, thanhTien, null, null, null);
            chiTietList.add(ct);
        }

        pnk.setTongSoLuong(tongSoLuong);
        pnk.setTongTienNhap(tongTienNhap);

        try {
            boolean success = phieuNhapKhoDao.themPhieuNhap(pnk, chiTietList);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Lưu phiếu nhập kho " + maPhieu + " thành công!\n"
                                + "Tổng số lượng: " + tongSoLuong + "\n"
                                + "Tổng tiền nhập: " + MONEY_FORMAT.format(tongTienNhap) + "đ",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnThem) {
            themSanPhamVaoBang();
        } else if (source == btnLuuPhieu) {
            luuPhieuNhap();
        } else if (source == btnLamMoi) {
            lamMoiForm();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImportGoodsPanel().setVisible(true));
    }
}
