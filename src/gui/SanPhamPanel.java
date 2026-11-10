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
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import DAO.PhieuNhapKho_DAO;
import DAO.SanPham_DAO;
import entity.DanhMuc;
import entity.SanPham;

public class SanPhamPanel extends JFrame implements ActionListener {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private static final Color HEADER_GRAY = new Color(238, 238, 238);
    private static final Color BORDER_GRAY = new Color(215, 215, 215);
    private static final Color BG_LIGHT = new Color(245, 246, 248);
    private static final String FONT_FAMILY = "Segoe UI";

    private DefaultTableModel model;
    private JTable table;
    private ThanhMenu menuPanel;
    private JTextField txtSearch;
    private JComboBox<DanhMuc> cboCategory;
    private JComboBox<String> cboStatus;
    private JButton btnExport;
    private JButton btnAdd;
    private Timer searchTimer;
    private final SanPham_DAO sp_dao = new SanPham_DAO();
    private final DecimalFormat df = new DecimalFormat("#,##0.## VNĐ");

    public SanPhamPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(this);

        menuPanel = new ThanhMenu();
        menuPanel.setShortcutAction(ThanhMenu.Shortcut.HOME, e -> {
            if (!KhungChinh.moTrangHienTai(ThanhMenu.MenuItem.HOME)) {
                new KhungChinh().setVisible(true);
                dispose();
            }
        });
        menuPanel.setShortcutAction(ThanhMenu.Shortcut.LOGOUT, e -> {
            DangNhapFrame.clearCurrentUsername();
            new DangNhapFrame().setVisible(true);
            dispose();
        });

        menuPanel.setMenuItemAction(ThanhMenu.MenuItem.HOME, e -> {
            if (!KhungChinh.moTrangHienTai(ThanhMenu.MenuItem.HOME)) {
                new KhungChinh().setVisible(true);
                dispose();
            }
        });
        menuPanel.setMenuItemAction(ThanhMenu.MenuItem.LOGOUT, e -> {
            DangNhapFrame.clearCurrentUsername();
            new DangNhapFrame().setVisible(true);
            dispose();
        });

        add(menuPanel, BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());
        pCenterContainer.setBackground(BG_LIGHT);

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setOpaque(false);
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("🧾 Quản lý sản phẩm / Dịch vụ");
        lblSubTitle.setForeground(BRAND_RED);
        lblSubTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pSubActions.setOpaque(false);

        btnExport = createButton("Xuất Excel", new Color(54, 103, 193), Color.WHITE);
        btnAdd = createButton("+ Thêm mới", BRAND_RED, Color.WHITE);
        pSubActions.add(btnExport);
        pSubActions.add(btnAdd);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pSearch.setOpaque(false);
        txtSearch = new JTextField(25);
        styleInput(txtSearch);
        txtSearch.setToolTipText("Nhập mã, tên, thương hiệu hoặc danh mục rồi nhấn Enter");
        
        cboCategory = new JComboBox<>();
        styleInput(cboCategory);
        
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang kinh doanh", "Ngừng kinh doanh", "Hết hàng"});
        styleInput(cboStatus);
        
        pSearch.add(createInputField(createFieldLabel("Tìm kiếm:"), txtSearch));
        pSearch.add(createInputField(createFieldLabel("Danh mục:"), cboCategory));
        pSearch.add(createInputField(createFieldLabel("Trạng thái:"), cboStatus));

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.setOpaque(false);
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] column = {"", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu",
                "Giá nhập", "Giá bán", "Tồn kho", "Đơn vị", "Trạng thái"};
        model = new DefaultTableModel(column, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int columnIndex) {
                return columnIndex == 0 || columnIndex == 9;
            }

            @Override
            public void setValueAt(Object value, int row, int columnIndex) {
                if (columnIndex == 9) {
                    if (!(value instanceof String)
                            || !("Đang kinh doanh".equals(value)
                                    || "Ngừng kinh doanh".equals(value)
                                    || "Hết hàng".equals(value))) {
                        return;
                    }
                    String maSanPham = String.valueOf(getValueAt(row, 1));
                    try {
                        sp_dao.capNhatTrangThai(maSanPham, (String) value);
                    } catch (SQLException | IllegalArgumentException exception) {
                        JOptionPane.showMessageDialog(UiDialogOwner.resolve(SanPhamPanel.this),
                                "Không thể cập nhật trạng thái sản phẩm:\n" + exception.getMessage(),
                                "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                super.setValueAt(value, row, columnIndex);
                if (columnIndex == 9) {
                    SwingUtilities.invokeLater(SanPhamPanel.this::docDuLieuDatabaseVaoTable);
                }
            }
        };
        table = new JTable(model);
        table.setRowHeight(36);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(BRAND_RED);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new TableCellRenderer() {
            private final JCheckBox selectAll = new JCheckBox();

            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focus, int row, int columnIndex) {
                boolean allSelected = model.getRowCount() > 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    allSelected &= Boolean.TRUE.equals(model.getValueAt(i, 0));
                }
                selectAll.setSelected(allSelected);
                selectAll.setHorizontalAlignment(JCheckBox.CENTER);
                selectAll.setBackground(BRAND_RED);
                selectAll.setOpaque(true);
                selectAll.setToolTipText("Chọn hoặc bỏ chọn tất cả sản phẩm");
                return selectAll;
            }
        });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewColumn = table.getTableHeader().columnAtPoint(event.getPoint());
                if (viewColumn == 0) {
                    boolean selectAll = false;
                    for (int row = 0; row < model.getRowCount(); row++) {
                        if (!Boolean.TRUE.equals(model.getValueAt(row, 0))) {
                            selectAll = true;
                            break;
                        }
                    }
                    for (int row = 0; row < model.getRowCount(); row++) {
                        model.setValueAt(selectAll, row, 0);
                    }
                    table.getTableHeader().repaint();
                }
            }
        });
        JComboBox<String> statusEditor = new JComboBox<>(
                new String[]{"Đang kinh doanh", "Ngừng kinh doanh", "Hết hàng"});
        table.getColumnModel().getColumn(9).setCellEditor(
                new javax.swing.DefaultCellEditor(statusEditor));
        model.addTableModelListener(event -> table.getTableHeader().repaint());
        loadCategories();

        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);

        try {
            new PhieuNhapKho_DAO().capNhatPhieuNhapDenHan();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể cập nhật các phiếu nhập đã đến ngày:\n"
                    + exception.getMessage(), "Lỗi cập nhật tồn kho", JOptionPane.ERROR_MESSAGE);
        }
        docDuLieuDatabaseVaoTable();

        searchTimer = new Timer(250, event -> docDuLieuDatabaseVaoTable());
        searchTimer.setRepeats(false);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                scheduleProductSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                scheduleProductSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                scheduleProductSearch();
            }
        });
        txtSearch.addActionListener(event -> {
            searchTimer.stop();
            docDuLieuDatabaseVaoTable();
        });
        cboCategory.addActionListener(this);
        cboStatus.addActionListener(this);
        btnExport.addActionListener(this);
        btnAdd.addActionListener(this);
    }

    private void loadCategories() {
        cboCategory.addItem(null);
        try {
            for (DanhMuc danhMuc : sp_dao.getAllDanhMuc()) {
                cboCategory.addItem(danhMuc);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể tải danh mục sản phẩm:\n"
                    + exception.getMessage(), "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
        cboCategory.setSelectedIndex(-1);
        cboCategory.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "Tất cả danh mục" : value.getTenDanhMuc());
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
    }

    private void scheduleProductSearch() {
        searchTimer.restart();
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim();
        DanhMuc danhMucChon = (DanhMuc) cboCategory.getSelectedItem();
        if (tuKhoa.isEmpty() && cboCategory.getSelectedIndex() < 0) {
            return;
        }

        String trangThai = (String) cboStatus.getSelectedItem();
        String maDanhMuc = danhMucChon == null ? null : danhMucChon.getMaDanhMuc();
        if ("Tất cả trạng thái".equals(trangThai)) {
            trangThai = null;
        }

        try {
            ArrayList<SanPham> ds = sp_dao.timKiemSanPham(tuKhoa, maDanhMuc, trangThai);
            for (SanPham sp : ds) {
                String tenDM = sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : "";
                model.addRow(new Object[]{
                        false,
                        sp.getMaSanPham(),
                        sp.getTenSanPham(),
                        tenDM,
                        sp.getThuongHieu() != null ? sp.getThuongHieu() : "",
                        df.format(sp.getGiaNhap()),
                        df.format(sp.getGiaBan()),
                        sp.getSoLuongTon(),
                        sp.getDonViTinh(),
                        sp.getTrangThai()
                });
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this),
                    "Không thể tìm sản phẩm:\n" + exception.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(txtSearch)) {
            searchTimer.stop();
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(cboCategory) && cboCategory.getSelectedIndex() == 0) {
            cboCategory.setSelectedIndex(-1);
        } else if (o.equals(cboCategory) || o.equals(cboStatus)) {
            searchTimer.stop();
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            exportProducts();
        } else if (o.equals(btnAdd)) {
            showAddProductDialog();
        }
    }

    private void exportProducts() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this),
                    "Hãy tìm sản phẩm trước khi xuất danh sách.",
                    "Chưa có dữ liệu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất danh sách sản phẩm");
        chooser.setSelectedFile(new java.io.File("danh-sach-san-pham.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".csv");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');
            for (int column = 1; column < model.getColumnCount(); column++) {
                if (column > 1) {
                    writer.write(',');
                }
                writer.write(csv(model.getColumnName(column)));
            }
            writer.newLine();
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int column = 1; column < model.getColumnCount(); column++) {
                    if (column > 1) {
                        writer.write(',');
                    }
                    writer.write(csv(String.valueOf(model.getValueAt(row, column))));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Đã xuất danh sách sản phẩm ra tệp:\n"
                    + file.getAbsolutePath(), "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể xuất danh sách sản phẩm:\n"
                    + exception.getMessage(), "Lỗi xuất tệp", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(String value) {
        if (!value.isEmpty() && " =+-@\t\r".indexOf(value.charAt(0)) >= 0) {
            value = "'" + value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void showAddProductDialog() {
        final ArrayList<DanhMuc> danhMucList;
        try {
            danhMucList = sp_dao.getAllDanhMuc();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể tải danh mục sản phẩm:\n"
                    + exception.getMessage(), "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (danhMucList.isEmpty()) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Cần có ít nhất một danh mục đang hoạt động "
                    + "trước khi tạo sản phẩm.", "Chưa có danh mục", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Thêm mới sản phẩm", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField nameField = new JTextField();
        JComboBox<DanhMuc> categoryField = new JComboBox<>();
        for (DanhMuc danhMuc : danhMucList) {
            categoryField.addItem(danhMuc);
        }
        JTextField brandField = new JTextField();
        JTextField importPriceField = new JTextField();
        JTextField salePriceField = new JTextField();
        JTextField stockField = new JTextField("0");
        JTextField unitField = new JTextField("Cái");
        JComboBox<String> statusField = new JComboBox<>(
                new String[]{"Đang kinh doanh", "Ngừng kinh doanh", "Hết hàng"});
        JTextArea descriptionField = new JTextArea(3, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        styleInput(nameField);
        styleInput(categoryField);
        styleInput(brandField);
        styleInput(importPriceField);
        styleInput(salePriceField);
        styleInput(stockField);
        styleInput(unitField);
        styleInput(statusField);
        descriptionField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        descriptionField.setBorder(new EmptyBorder(5, 8, 5, 8));

        JPanel formCard = createTitledPanel("📦 Thông tin sản phẩm mới");
        
        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setOpaque(false);
        formBody.setBorder(new EmptyBorder(12, 16, 14, 16));

        JPanel hang1 = createTransparentRow(new BorderLayout(12, 0));
        hang1.add(createInputField(createFieldLabel("Tên sản phẩm *"), nameField), BorderLayout.CENTER);

        JPanel hang2 = createTransparentRow(new GridLayout(1, 2, 12, 0));
        hang2.add(createInputField(createFieldLabel("Danh mục"), categoryField));
        hang2.add(createInputField(createFieldLabel("Thương hiệu"), brandField));

        JPanel hang3 = createTransparentRow(new GridLayout(1, 2, 12, 0));
        hang3.add(createInputField(createFieldLabel("Giá nhập (VNĐ) *"), importPriceField));
        hang3.add(createInputField(createFieldLabel("Giá bán (VNĐ) *"), salePriceField));

        JPanel hang4 = createTransparentRow(new GridLayout(1, 2, 12, 0));
        hang4.add(createInputField(createFieldLabel("Tồn kho ban đầu"), stockField));
        hang4.add(createInputField(createFieldLabel("Đơn vị tính *"), unitField));

        JPanel hang5 = createTransparentRow(new BorderLayout(12, 0));
        hang5.add(createInputField(createFieldLabel("Trạng thái"), statusField), BorderLayout.CENTER);

        JPanel hang6 = createTransparentRow(new BorderLayout(12, 0));
        JScrollPane scrollDesc = new JScrollPane(descriptionField);
        scrollDesc.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        hang6.add(createInputField(createFieldLabel("Mô tả sản phẩm"), scrollDesc), BorderLayout.CENTER);

        formBody.add(hang1);
        formBody.add(Box.createRigidArea(new Dimension(0, 8)));
        formBody.add(hang2);
        formBody.add(Box.createRigidArea(new Dimension(0, 8)));
        formBody.add(hang3);
        formBody.add(Box.createRigidArea(new Dimension(0, 8)));
        formBody.add(hang4);
        formBody.add(Box.createRigidArea(new Dimension(0, 8)));
        formBody.add(hang5);
        formBody.add(Box.createRigidArea(new Dimension(0, 8)));
        formBody.add(hang6);

        formCard.add(formBody, BorderLayout.CENTER);

        JPanel buttons = createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        buttons.setBorder(new EmptyBorder(0, 12, 8, 12));
        
        JButton addButton = createButton("💾 Thêm mới", BRAND_RED, Color.WHITE);
        JButton cancelButton = createButton("Hủy", Color.WHITE, new Color(50, 50, 50));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(6, 14, 6, 14)));

        buttons.add(addButton);
        buttons.add(cancelButton);

        addButton.addActionListener(event -> {
            String name = nameField.getText().trim();
            String unit = unitField.getText().trim();
            if (name.isEmpty() || unit.isEmpty()) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(dialog), "Vui lòng nhập tên sản phẩm và đơn vị.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double importPrice = Double.parseDouble(importPriceField.getText().trim());
                double salePrice = Double.parseDouble(salePriceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                if (Double.isNaN(importPrice) || Double.isInfinite(importPrice)
                        || Double.isNaN(salePrice) || Double.isInfinite(salePrice)
                        || importPrice < 0 || salePrice < importPrice || stock < 0) {
                    throw new IllegalArgumentException(
                            "Giá nhập phải >= 0, giá bán phải >= giá nhập và tồn kho phải >= 0.");
                }
                SanPham sanPham = new SanPham();
                sanPham.setTenSanPham(name);
                sanPham.setDanhMuc((DanhMuc) categoryField.getSelectedItem());
                sanPham.setThuongHieu(brandField.getText().trim());
                sanPham.setMoTa(descriptionField.getText().trim());
                sanPham.setDonViTinh(unit);
                sanPham.setGiaNhap(importPrice);
                sanPham.setGiaBan(salePrice);
                sanPham.setSoLuongTon(stock);
                sanPham.setTrangThai((String) statusField.getSelectedItem());
                String maSanPham = sp_dao.themSanPham(sanPham);
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(dialog), "Đã tạo sản phẩm " + maSanPham + ".",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                docDuLieuDatabaseVaoTable();
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(dialog),
                        "Giá nhập, giá bán và tồn kho phải là số hợp lệ.",
                        "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(dialog), "Không thể tạo sản phẩm:\n"
                        + exception.getMessage(), "Lỗi tạo sản phẩm", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(event -> dialog.dispose());

        dialog.setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(dialog);
        
        JPanel pDialogContent = new JPanel(new BorderLayout());
        pDialogContent.setBackground(BG_LIGHT);
        pDialogContent.setBorder(new EmptyBorder(10, 12, 10, 12));
        pDialogContent.add(formCard, BorderLayout.CENTER);
        pDialogContent.add(buttons, BorderLayout.SOUTH);

        dialog.add(pDialogContent, BorderLayout.CENTER);
        dialog.setSize(650, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
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
        lbl.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg, 1),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleInput(JComponent input) {
        input.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        input.setBackground(Color.WHITE);
        input.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GRAY, 1),
                new EmptyBorder(5, 8, 5, 8)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SanPhamPanel().setVisible(true));
    }
}