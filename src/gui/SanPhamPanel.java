package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.SanPham_DAO;
import DAO.PhieuNhapKho_DAO;
import entity.DanhMuc;
import entity.SanPham;

public class SanPhamPanel extends JFrame implements ActionListener {
    private DefaultTableModel model;
    private JTable table;
    private ThanhMenu menuPanel;
    private JTextField txtSearch;
    private JComboBox<DanhMuc> cboCategory;
    private JComboBox<String> cboStatus;
    private JButton btnExport;
    private JButton btnAdd;
    private final SanPham_DAO sp_dao = new SanPham_DAO();
    private final DecimalFormat df = new DecimalFormat("#,##0.## VNĐ");

    public SanPhamPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ThanhMenu tích hợp chuyển màn hình từ nhánh thuy
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

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("🧾 Quản lý sản phẩm / Dịch vụ");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);

        btnExport = new JButton("Xuất Excel");
        btnAdd = new JButton("Thêm mới");
        pSubActions.add(btnExport);
        pSubActions.add(btnAdd);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm tên SP, mã SP, thương hiệu...");
        cboCategory = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang kinh doanh", "Ngừng kinh doanh", "Hết hàng"});
        pSearch.add(txtSearch);
        pSearch.add(cboCategory);
        pSearch.add(cboStatus);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
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
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
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
                selectAll.setBackground(new Color(180, 40, 40));
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
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        try {
            new PhieuNhapKho_DAO().capNhatPhieuNhapDenHan();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể cập nhật các phiếu nhập đã đến ngày:\n"
                    + exception.getMessage(), "Lỗi cập nhật tồn kho", JOptionPane.ERROR_MESSAGE);
        }
        docDuLieuDatabaseVaoTable();

        txtSearch.addActionListener(this);
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

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String trangThai = (String) cboStatus.getSelectedItem();
        DanhMuc danhMucChon = (DanhMuc) cboCategory.getSelectedItem();

        ArrayList<SanPham> ds = sp_dao.getAllSanPham();
        for (SanPham sp : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || sp.getMaSanPham().toLowerCase().contains(tuKhoa)
                    || sp.getTenSanPham().toLowerCase().contains(tuKhoa)
                    || (sp.getThuongHieu() != null && sp.getThuongHieu().toLowerCase().contains(tuKhoa))
                    || (sp.getDanhMuc() != null && sp.getDanhMuc().getTenDanhMuc() != null
                            && sp.getDanhMuc().getTenDanhMuc().toLowerCase().contains(tuKhoa));
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(sp.getTrangThai());
            boolean matchCategory = danhMucChon == null || (sp.getDanhMuc() != null
                    && danhMucChon.getMaDanhMuc().equals(sp.getDanhMuc().getMaDanhMuc()));

            if (matchKey && matchStatus && matchCategory) {
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
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(txtSearch) || o.equals(cboCategory) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            exportProducts();
        } else if (o.equals(btnAdd)) {
            showAddProductDialog();
        }
    }

    private void exportProducts() {
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

        JTextField nameField = new JTextField(20);
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
        JTextArea descriptionField = new JTextArea(4, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        styleProductInput(nameField);
        styleProductInput(categoryField);
        styleProductInput(brandField);
        styleProductInput(importPriceField);
        styleProductInput(salePriceField);
        styleProductInput(stockField);
        styleProductInput(unitField);
        styleProductInput(statusField);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionField.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(10, 26, 20, 26));
        GridBagConstraints fields = new GridBagConstraints();
        fields.insets = new Insets(8, 0, 8, 12);
        fields.fill = GridBagConstraints.HORIZONTAL;
        fields.weightx = 1;
        addProductField(form, fields, 0, 0, 2, "Tên sản phẩm", nameField, 0);
        addProductField(form, fields, 1, 0, 1, "Danh mục", categoryField, 0);
        addProductField(form, fields, 1, 1, 1, "Thương hiệu", brandField, 0);
        addProductField(form, fields, 2, 0, 1, "Giá nhập", importPriceField, 0);
        addProductField(form, fields, 2, 1, 1, "Giá bán", salePriceField, 0);
        addProductField(form, fields, 3, 0, 1, "Tồn kho", stockField, 0);
        addProductField(form, fields, 3, 1, 1, "Đơn vị", unitField, 0);
        addProductField(form, fields, 4, 0, 2, "Trạng thái", statusField, 0);
        addProductField(form, fields, 5, 0, 2, "Mô tả",
                new JScrollPane(descriptionField), 1);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(12, 20, 20, 26));
        JButton addButton = new JButton("Thêm mới  +");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(190, 40, 40));
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(11, 22, 11, 22));
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cancelButton.setForeground(new Color(55, 55, 55));
        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(new EmptyBorder(11, 24, 11, 24));
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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(22, 26, 0, 20));
        JLabel title = new JLabel("Thêm mới sản phẩm");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(new Color(35, 45, 60));
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        closeButton.setForeground(new Color(105, 110, 120));
        closeButton.setBackground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(event -> dialog.dispose());
        header.add(title, BorderLayout.CENTER);
        header.add(closeButton, BorderLayout.EAST);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.add(header, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(Math.min(780, screen.width - 60), Math.min(780, screen.height - 60));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addProductField(JPanel form, GridBagConstraints constraints,
                                 int row, int column, int span, String labelText,
                                 Component input, double verticalWeight) {
        JPanel field = new JPanel(new BorderLayout(0, 8));
        field.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(45, 50, 60));
        field.add(label, BorderLayout.NORTH);
        if (input instanceof JScrollPane) {
            ((JScrollPane) input).setBorder(new LineBorder(new Color(175, 180, 187), 1));
            ((JScrollPane) input).setPreferredSize(new Dimension(100, 145));
        }
        field.add(input, BorderLayout.CENTER);
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = span;
        constraints.weighty = verticalWeight;
        constraints.fill = verticalWeight > 0
                ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8, column == 1 ? 12 : 0, 8, 12);
        form.add(field, constraints);
    }

    private void styleProductInput(JComponent input) {
        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        input.setBackground(Color.WHITE);
        input.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(175, 180, 187), 1),
                new EmptyBorder(8, 12, 8, 12)));
        input.setPreferredSize(new Dimension(100, 48));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SanPhamPanel().setVisible(true));
    }
}