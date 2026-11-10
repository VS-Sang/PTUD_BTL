package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DAO.NhaCungCap_DAO;
import DAO.NhanVien_DAO;
import DAO.PhieuNhapKho_DAO;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.SanPham;

public class NhapHangPanel extends JFrame implements ActionListener {

    private static final Color BRAND_RED = new Color(177, 48, 42);
    private static final Color HEADER_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(215, 215, 215);
    private static final Color BG_LIGHT = new Color(245, 246, 248);
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");

    private final String currentUserName;
    private final String currentUserRole;

    private ThanhMenu menuPanel;
    private JTextField txtMaPhieuNhap;
    private JTextField txtNgayNhap;
    private JTextField txtNguoiLap;
    private JComboBox<NhaCungCap> cboNhaCungCap;
    private JComboBox<String> cboKhoNhap;

    private JComboBox<SanPham> cboSanPham;
    private JSpinner spnSoLuong;
    private JButton btnThem;

    private DefaultTableModel tableModel;
    private JTable tableHangs;
    private JLabel lblTongGiaTri;

    private JTextArea txtGhiChu;

    private JButton btnLuuPhieu;
    private JButton btnIn;
    private JButton btnLamMoi;
    private boolean phieuDaLuu;
    private NhaCungCap nhaCungCapDaChon;
    private boolean dangCapNhatNhaCungCap;

    private final NhaCungCap_DAO nhaCungCapDao = new NhaCungCap_DAO();
    private final PhieuNhapKho_DAO phieuNhapKhoDao = new PhieuNhapKho_DAO();
    private final NhanVien_DAO nhanVienDao = new NhanVien_DAO();

    private final List<SanPham> listSanPham = new ArrayList<>();
    private final List<NhaCungCap> listNhaCungCap = new ArrayList<>();

    public NhapHangPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public NhapHangPanel(String userName, String userRole) {
        this.currentUserName = (userName != null && !userName.trim().isEmpty()) ? userName : "Nguyễn Văn An";
        this.currentUserRole = (userRole != null && !userRole.trim().isEmpty()) ? userRole : "Quản lý";

        setupWindow();
        ButtonTooltips.ganTooltip(this);

        menuPanel = new ThanhMenu(currentUserName, currentUserRole);
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(BG_LIGHT);
        pMain.setBorder(new EmptyBorder(10, 15, 10, 15));

        pMain.add(createTopTitleBar(), BorderLayout.NORTH);

        JPanel pContent = new JPanel();
        pContent.setLayout(new BoxLayout(pContent, BoxLayout.Y_AXIS));
        pContent.setOpaque(false);

        pContent.add(createInfoCard());
        pContent.add(Box.createRigidArea(new Dimension(0, 10)));
        pContent.add(createGoodsListCard());
        pContent.add(Box.createRigidArea(new Dimension(0, 10)));
        pContent.add(createNoteCard());

        JScrollPane scrollPane = new JScrollPane(pContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pMain.add(scrollPane, BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        loadComboboxData();
        cboNhaCungCap.addActionListener(event -> handleSupplierChange());
        resetForm();
    }

    private void setupWindow() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
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

        btnLuuPhieu = createButton("💾 Lưu phiếu nhập", BRAND_RED, Color.WHITE);
        btnLuuPhieu.addActionListener(this);

        btnIn = createButton("Xuất PDF", new Color(54, 103, 193), Color.WHITE);
        btnIn.setEnabled(false);
        btnIn.addActionListener(this);

        btnLamMoi = createButton("↻ Làm mới", Color.WHITE, new Color(50, 50, 50));
        btnLamMoi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btnLamMoi.addActionListener(this);

        pRight.add(btnLuuPhieu);
        pRight.add(btnIn);
        pRight.add(btnLamMoi);

        bar.add(pLeft, BorderLayout.WEST);
        bar.add(pRight, BorderLayout.EAST);
        return bar;
    }

    private JPanel createInfoCard() {
        JPanel card = createTitledPanel("🚚 Thông tin phiếu nhập");

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 16, 14, 16));

        txtMaPhieuNhap = new JTextField();
        txtMaPhieuNhap.setEditable(false);
        txtMaPhieuNhap.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMaPhieuNhap.setBackground(new Color(245, 245, 245));

        txtNgayNhap = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtNgayNhap.setEditable(false);
        txtNgayNhap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNgayNhap.setBackground(new Color(245, 245, 245));

        txtNguoiLap = new JTextField(currentUserName);
        txtNguoiLap.setEditable(false);
        txtNguoiLap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNguoiLap.setBackground(new Color(245, 245, 245));

        JPanel hang1 = createTransparentRow(new GridLayout(1, 3, 16, 0));
        hang1.add(createInputField(createFieldLabel("Mã phiếu nhập"), txtMaPhieuNhap));
        hang1.add(createInputField(createFieldLabel("Ngày nhập"), txtNgayNhap));
        hang1.add(createInputField(createFieldLabel("Người lập phiếu"), txtNguoiLap));

        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboNhaCungCap.setBackground(Color.WHITE);
        cboNhaCungCap.setPreferredSize(new Dimension(cboNhaCungCap.getPreferredSize().width, 30));

        cboKhoNhap = new JComboBox<>(new String[]{
                "Kho chính Q.1", "Kho Thủ Đức", "Kho Bình Thạnh", "Kho Tân Bình"
        });
        cboKhoNhap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboKhoNhap.setBackground(Color.WHITE);
        cboKhoNhap.setPreferredSize(new Dimension(cboKhoNhap.getPreferredSize().width, 30));

        JPanel hang2 = createTransparentRow(new BorderLayout(16, 0));
        JPanel nuaTraiHang2 = createInputField(createFieldLabel("Nhà cung cấp *"), cboNhaCungCap);
        JPanel nuaPhaiHang2 = createInputField(createFieldLabel("Kho nhập"), cboKhoNhap);
        nuaPhaiHang2.setPreferredSize(new Dimension(320, 0));

        hang2.add(nuaTraiHang2, BorderLayout.CENTER);
        hang2.add(nuaPhaiHang2, BorderLayout.EAST);

        body.add(hang1);
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(hang2);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createGoodsListCard() {
        JPanel card = createTitledPanel("📋 Danh sách hàng nhập");

        JPanel pInner = new JPanel(new BorderLayout(0, 10));
        pInner.setOpaque(false);
        pInner.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel pActionRow = createTransparentRow(new BorderLayout(8, 0));

        cboSanPham = new JComboBox<>();
        cboSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboSanPham.setBackground(Color.WHITE);
        cboSanPham.setPreferredSize(new Dimension(100, 32));

        spnSoLuong = new JSpinner(new SpinnerNumberModel(10, 1, 99999, 1));
        spnSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        spnSoLuong.setPreferredSize(new Dimension(80, 32));

        btnThem = new JButton("+ Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBackground(BRAND_RED);
        btnThem.setFocusPainted(false);
        btnThem.setBorder(new EmptyBorder(7, 16, 7, 16));
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(this);

        JPanel pRightAction = createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pRightAction.add(spnSoLuong);
        pRightAction.add(btnThem);

        pActionRow.add(cboSanPham, BorderLayout.CENTER);
        pActionRow.add(pRightAction, BorderLayout.EAST);

        pInner.add(pActionRow, BorderLayout.NORTH);

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

        JTableHeader header = tableHangs.getTableHeader();
        header.setBackground(BRAND_RED);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 32));

        TableColumnModel colModel = tableHangs.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(90);
        colModel.getColumn(1).setPreferredWidth(320);
        colModel.getColumn(2).setPreferredWidth(140);
        colModel.getColumn(3).setPreferredWidth(70);
        colModel.getColumn(4).setPreferredWidth(150);
        colModel.getColumn(5).setPreferredWidth(60);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        colModel.getColumn(0).setCellRenderer(centerRenderer);
        colModel.getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        colModel.getColumn(2).setCellRenderer(rightRenderer);
        colModel.getColumn(4).setCellRenderer(rightRenderer);

        colModel.getColumn(5).setCellRenderer(new DeleteCellRenderer());

        tableHangs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableHangs.rowAtPoint(e.getPoint());
                int col = tableHangs.columnAtPoint(e.getPoint());
                if (!phieuDaLuu && row >= 0 && col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(getDialogOwnerWindow(),
                            "Bạn có chắc muốn xóa sản phẩm này khỏi danh sách nhập?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(row);
                        updateTotalValue();
                    }
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableHangs);
        scrollTable.setPreferredSize(new Dimension(800, 180));
        scrollTable.setBorder(new LineBorder(BORDER_GRAY, 1));
        pInner.add(scrollTable, BorderLayout.CENTER);

        JPanel pTotalRow = createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        lblTongGiaTri = new JLabel("Tổng giá trị nhập: 0đ");
        lblTongGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongGiaTri.setForeground(BRAND_RED);
        pTotalRow.add(lblTongGiaTri);
        pInner.add(pTotalRow, BorderLayout.SOUTH);

        card.add(pInner, BorderLayout.CENTER);
        return card;
    }

    private static class DeleteCellRenderer extends DefaultTableCellRenderer {
        private final JButton btnTrash = new JButton("🗑");
        {
            ButtonTooltips.ganTooltipNut(btnTrash);
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
    }

    private JPanel createNoteCard() {
        JPanel card = createTitledPanel("📝 Ghi chú");

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

    private JPanel createInputField(Component label, Component inputField) {
        JPanel container = createTransparentPanel(new BorderLayout(0, 4));
        container.add(label, BorderLayout.NORTH);
        container.add(inputField, BorderLayout.CENTER);
        return container;
    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        JLabel label = new JLabel(title);
        label.setOpaque(true);
        label.setBackground(HEADER_GRAY);
        label.setForeground(BRAND_RED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY),
                BorderFactory.createEmptyBorder(8, 11, 8, 11)));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private static JPanel createTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel createTransparentRow(LayoutManager layout) {
        JPanel row = new JPanel(layout) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        row.setOpaque(false);
        return row;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadComboboxData() {
        try {
            phieuNhapKhoDao.capNhatPhieuNhapDenHan();
        } catch (SQLException exception) {
            showMessage("Không thể cập nhật các phiếu nhập đã đến ngày:\n" + exception.getMessage(),
                    "Lỗi cập nhật tồn kho", JOptionPane.ERROR_MESSAGE);
        }

        listNhaCungCap.clear();
        cboNhaCungCap.removeAllItems();
        cboNhaCungCap.addItem(null);
        listNhaCungCap.addAll(nhaCungCapDao.getAllNhaCungCap());
        for (NhaCungCap nhaCungCap : listNhaCungCap) {
            cboNhaCungCap.addItem(nhaCungCap);
        }
        cboSanPham.removeAllItems();
        cboSanPham.addItem(null);
        cboNhaCungCap.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "-- Chọn nhà cung cấp --" : value.getTenNhaCungCap());
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(3, 5, 3, 5));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        cboSanPham.setRenderer((list, value, index, selected, focus) -> {
            String text = value == null
                    ? (cboNhaCungCap.getSelectedItem() == null
                    ? "-- Chọn nhà cung cấp trước --"
                    : listSanPham.isEmpty()
                    ? "-- Chưa có sản phẩm trong danh mục --"
                    : "-- Chọn sản phẩm --")
                    : value.getMaSanPham() + " - " + value.getTenSanPham()
                    + " (Giá nhập: " + MONEY_FORMAT.format(value.getGiaNhap()) + "đ)";
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(3, 5, 3, 5));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        cboSanPham.setEnabled(false);
    }

    private void handleSupplierChange() {
        if (dangCapNhatNhaCungCap) {
            return;
        }
        NhaCungCap nhaCungCapMoi = (NhaCungCap) cboNhaCungCap.getSelectedItem();
        boolean cungNhaCungCap = nhaCungCapDaChon == null
                ? nhaCungCapMoi == null
                : nhaCungCapMoi != null && nhaCungCapDaChon.getMaNhaCungCap()
                .equals(nhaCungCapMoi.getMaNhaCungCap());
        if (cungNhaCungCap) {
            return;
        }

        if (tableModel.getRowCount() > 0) {
            int choice = JOptionPane.showConfirmDialog(getDialogOwnerWindow(),
                    "Đổi nhà cung cấp sẽ xóa các sản phẩm đã thêm vào phiếu. Bạn có muốn tiếp tục?",
                    "Đổi nhà cung cấp", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                dangCapNhatNhaCungCap = true;
                cboNhaCungCap.setSelectedItem(nhaCungCapDaChon);
                dangCapNhatNhaCungCap = false;
                return;
            }
            tableModel.setRowCount(0);
            updateTotalValue();
        }

        nhaCungCapDaChon = nhaCungCapMoi;
        listSanPham.clear();
        cboSanPham.removeAllItems();
        cboSanPham.addItem(null);
        cboSanPham.setEnabled(nhaCungCapMoi != null);
        btnThem.setEnabled(false);
        if (nhaCungCapMoi == null) {
            return;
        }

        try {
            listSanPham.addAll(phieuNhapKhoDao.getSanPhamTheoNhaCungCap(nhaCungCapMoi.getMaNhaCungCap()));
            for (SanPham sanPham : listSanPham) {
                cboSanPham.addItem(sanPham);
            }
            btnThem.setEnabled(!listSanPham.isEmpty());
        } catch (SQLException exception) {
            showMessage("Không thể tải sản phẩm của nhà cung cấp:\n" + exception.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        phieuDaLuu = false;
        btnLuuPhieu.setEnabled(true);
        btnIn.setEnabled(false);
        setFormInputsEnabled(true);
        nhaCungCapDaChon = null;
        dangCapNhatNhaCungCap = true;
        txtMaPhieuNhap.setText(phieuNhapKhoDao.sinhMaPhieuNhap());
        txtNgayNhap.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtNguoiLap.setText(currentUserName);
        if (cboNhaCungCap.getItemCount() > 0) {
            cboNhaCungCap.setSelectedIndex(0);
        }
        dangCapNhatNhaCungCap = false;
        listSanPham.clear();
        cboSanPham.removeAllItems();
        cboSanPham.addItem(null);
        cboSanPham.setEnabled(false);
        btnThem.setEnabled(false);
        if (cboKhoNhap.getItemCount() > 0) {
            cboKhoNhap.setSelectedIndex(0);
        }
        if (cboSanPham.getItemCount() > 0) {
            cboSanPham.setSelectedIndex(0);
        }
        spnSoLuong.setValue(10);
        tableModel.setRowCount(0);
        txtGhiChu.setText("");
        updateTotalValue();
    }

    private void addProductToTable() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        if (sp == null) {
            showMessage("Vui lòng chọn sản phẩm cần nhập!", "Chưa chọn sản phẩm", JOptionPane.WARNING_MESSAGE);
            cboSanPham.requestFocus();
            return;
        }

        int soLuong = (int) spnSoLuong.getValue();
        if (soLuong <= 0) {
            showMessage("Số lượng nhập phải lớn hơn 0!", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double donGia = sp.getGiaNhap();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ma = (String) tableModel.getValueAt(i, 0);
            if (ma.equals(sp.getMaSanPham())) {
                int slHienTai = (int) tableModel.getValueAt(i, 3);
                int slMoi = slHienTai + soLuong;
                double ttMoi = slMoi * donGia;
                tableModel.setValueAt(slMoi, i, 3);
                tableModel.setValueAt(MONEY_FORMAT.format(ttMoi) + "đ", i, 4);
                updateTotalValue();
                cboSanPham.setSelectedItem(null);
                spnSoLuong.setValue(10);
                return;
            }
        }

        double thanhTien = soLuong * donGia;
        tableModel.addRow(new Object[]{
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                MONEY_FORMAT.format(donGia) + "đ",
                soLuong,
                MONEY_FORMAT.format(thanhTien) + "đ",
                "Xóa"
        });

        updateTotalValue();
        cboSanPham.setSelectedIndex(0);
        spnSoLuong.setValue(10);
    }

    private void updateTotalValue() {
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

    private void savePurchaseOrder() {
        if (phieuDaLuu) {
            return;
        }
        if (cboNhaCungCap.getSelectedIndex() <= 0) {
            showMessage("Vui lòng chọn Nhà cung cấp!", "Chưa chọn NCC", JOptionPane.WARNING_MESSAGE);
            cboNhaCungCap.requestFocus();
            return;
        }

        if (tableModel.getRowCount() == 0) {
            showMessage("Danh sách hàng nhập không được để trống!", "Chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            cboSanPham.requestFocus();
            return;
        }

        NhaCungCap ncc = (NhaCungCap) cboNhaCungCap.getSelectedItem();

        NhanVien nv = findLoggedInEmployee();
        if (nv == null) {
            List<NhanVien> listNV = nhanVienDao.getAllNhanVien();
            if (!listNV.isEmpty()) {
                nv = listNV.get(0);
            }
        }

        String maPhieu = txtMaPhieuNhap.getText().trim();
        Date ngayNhap = new Date();

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
                showMessage("Lưu phiếu nhập kho " + maPhieu + " thành công!\n"
                                + "Tổng số lượng: " + tongSoLuong + "\n"
                                + "Tổng tiền nhập: " + MONEY_FORMAT.format(tongTienNhap) + "đ"
                                + (ngayNhap.after(new Date())
                                ? "\nPhiếu đang chờ đến ngày nhập; tồn kho chưa được cộng."
                                : ""),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                phieuDaLuu = true;
                btnLuuPhieu.setEnabled(false);
                btnIn.setEnabled(true);
                setFormInputsEnabled(false);
            } else {
                showMessage("Lưu phiếu nhập thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IllegalArgumentException e) {
            showMessage("Lỗi cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private NhanVien findLoggedInEmployee() {
        for (NhanVien n : nhanVienDao.getAllNhanVien()) {
            if (n.getHoTen() != null && n.getHoTen().trim().equalsIgnoreCase(currentUserName.trim())) {
                return n;
            }
        }
        return null;
    }

    private void setFormInputsEnabled(boolean enabled) {
        cboNhaCungCap.setEnabled(enabled);
        cboKhoNhap.setEnabled(enabled);
        cboSanPham.setEnabled(enabled);
        spnSoLuong.setEnabled(enabled);
        btnThem.setEnabled(enabled);
        tableHangs.setEnabled(enabled);
        txtGhiChu.setEnabled(enabled);
    }

    private void exportPurchaseOrderPdf() {
        if (!phieuDaLuu) {
            showMessage("Vui lòng lưu phiếu nhập trước khi in.",
                    "Phiếu nhập chưa được lưu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất phiếu nhập PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File("phieu-nhap-"
                + txtMaPhieuNhap.getText().trim() + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists()
                && JOptionPane.showConfirmDialog(getDialogOwnerWindow(),
                "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            List<String[]> rows = new ArrayList<>();
            int tongSoLuong = 0;
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                int soLuong = (int) tableModel.getValueAt(row, 3);
                tongSoLuong += soLuong;
                rows.add(new String[]{
                        String.valueOf(tableModel.getValueAt(row, 0)),
                        String.valueOf(tableModel.getValueAt(row, 1)),
                        String.valueOf(soLuong),
                        String.valueOf(tableModel.getValueAt(row, 2)),
                        String.valueOf(tableModel.getValueAt(row, 4))
                });
            }
            String ngayNhap = txtNgayNhap.getText().trim();
            NhaCungCap nhaCungCap = (NhaCungCap) cboNhaCungCap.getSelectedItem();
            List<String[]> metadata = Arrays.asList(
                    new String[]{"Ngày nhập", ngayNhap,
                            "Nhà cung cấp", nhaCungCap == null
                            ? "" : nhaCungCap.getTenNhaCungCap()},
                    new String[]{"Người lập", txtNguoiLap.getText().trim(),
                            "Kho nhập", String.valueOf(cboKhoNhap.getSelectedItem())});
            List<String[]> totals = Arrays.asList(
                    new String[]{"Tổng số lượng", String.valueOf(tongSoLuong)},
                    new String[]{"Tổng giá trị nhập", lblTongGiaTri.getText()
                            .replace("Tổng giá trị nhập: ", ""), "true"});
            XuatTaiLieuPdf.write(file, "PHIẾU NHẬP KHO",
                    txtMaPhieuNhap.getText().trim(), metadata, "CHI TIẾT HÀNG NHẬP",
                    Arrays.asList("MÃ SP", "TÊN SẢN PHẨM", "SL NHẬP",
                            "ĐƠN GIÁ", "THÀNH TIỀN"),
                    rows, new double[]{0.12, 0.36, 0.12, 0.18, 0.22},
                    totals, "Ghi chú: " + txtGhiChu.getText().trim());
            showMessage("Đã xuất phiếu nhập PDF:\n"
                            + file.getAbsolutePath(), "Xuất phiếu nhập",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            showMessage("Không thể xuất phiếu nhập:\n"
                    + exception.getMessage(), "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(getDialogOwnerWindow(), message, title, messageType);
    }

    private Component getDialogOwnerWindow() {
        return UiDialogOwner.resolve(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnThem) {
            addProductToTable();
        } else if (source == btnLuuPhieu) {
            savePurchaseOrder();
        } else if (source == btnIn) {
            exportPurchaseOrderPdf();
        } else if (source == btnLamMoi) {
            resetForm();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhapHangPanel().setVisible(true));
    }
}