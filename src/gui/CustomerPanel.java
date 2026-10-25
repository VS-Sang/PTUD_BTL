package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import DAO.KhachHang_DAO;
import entity.KhachHang;

public class CustomerPanel extends JFrame implements ActionListener {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JButton btnAdd, btnExport, btnTim;
    private final KhachHang_DAO kh_dao = new KhachHang_DAO();
    private final DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(contentLayout);
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private JPanel customerFormPanel;
    private JTextField txtMaKhachHang;
    private JTextField txtFormHoTen;
    private JTextField txtFormSoDienThoai;
    private JTextField txtFormEmail;
    private JTextArea txtFormDiaChi;
    private JTextField txtFormNgaySinh;
    private JComboBox<String> cboFormGioiTinh;

    public CustomerPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public CustomerPanel(String userName, String userRole) {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new MenuPanel(userName, userRole), BorderLayout.NORTH);

        // 1. KHUNG NỘI DUNG VÀ BẢNG DỮ LIỆU
        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("📖 Quản Lý Khách Hàng");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);
        btnAdd = new JButton("+ Thêm mới");
        btnAdd.setBackground(new Color(180, 40, 40));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);
        pSubActions.add(btnAdd);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(30);
        txtSearch.setToolTipText("Tìm tên, SĐT, mã KH...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);
        pSearch.add(txtSearch);
        pSearch.add(btnTim);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);

        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] columns = {"", "Mã KH", "Họ tên", "Giới tính", "SĐT", "Email", "Địa chỉ", "Hạng KH", "Điểm thưởng", "Tổng chi tiêu"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        contentCards.add(pCenterContainer, "list");
        add(contentCards, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        docDuLieuDatabaseVaoTable();

        btnAdd.addActionListener(this);
        btnExport.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();

        ArrayList<KhachHang> ds = kh_dao.getAllKhachHang();
        for (KhachHang kh : ds) {
            boolean match = tuKhoa.isEmpty()
                    || kh.getMaKhachHang().toLowerCase().contains(tuKhoa)
                    || kh.getHoTen().toLowerCase().contains(tuKhoa)
                    || (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(tuKhoa));
            if (match) {
                model.addRow(new Object[]{
                        false,
                        kh.getMaKhachHang(),
                        kh.getHoTen(),
                        kh.getGioiTinh() != null ? kh.getGioiTinh() : "",
                        kh.getSoDienThoai(),
                        kh.getEmail() != null ? kh.getEmail() : "",
                        kh.getDiaChi() != null ? kh.getDiaChi() : "",
                        kh.getLoaiKhachHang(),
                        kh.getDiemThuong(),
                        df.format(kh.getTongChiTieu())
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim) || o.equals(txtSearch)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất danh sách khách hàng ra Excel thành công!");
        } else if (o.equals(btnAdd)) {
            hienThiFormThemKhachHang();
        }
    }

    private void hienThiFormThemKhachHang() {
        txtMaKhachHang = new JTextField();
        txtMaKhachHang.setEditable(false);
        txtMaKhachHang.setBackground(new Color(242, 242, 242));
        txtFormHoTen = new JTextField();
        txtFormSoDienThoai = new JTextField();
        txtFormEmail = new JTextField();
        txtFormDiaChi = new JTextArea(3, 20);
        txtFormDiaChi.setLineWrap(true);
        txtFormDiaChi.setWrapStyleWord(true);
        txtFormNgaySinh = new JTextField();
        txtFormNgaySinh.setToolTipText("Nhập theo định dạng mm/dd/yyyy");
        cboFormGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});

        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(new Color(240, 240, 240));
        page.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);
        JLabel title = new JLabel("👤 Khách hàng — Thêm / Cập nhật");
        title.setForeground(new Color(173, 48, 42));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton btnQuayLai = new JButton("← Quay lại");
        pageHeader.add(title, BorderLayout.WEST);
        pageHeader.add(btnQuayLai, BorderLayout.EAST);
        page.add(pageHeader, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(205, 205, 205)));
        card.setPreferredSize(new Dimension(1200, 515));

        JLabel sectionTitle = new JLabel("  ●  Thông tin khách hàng");
        sectionTitle.setOpaque(true);
        sectionTitle.setBackground(new Color(225, 225, 225));
        sectionTitle.setForeground(new Color(173, 48, 42));
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        card.add(sectionTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        fields.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 5, 6, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        addFormField(fields, c, "Mã khách hàng", txtMaKhachHang, 0, 0, 1);
        addFormField(fields, c, "Họ và tên *", txtFormHoTen, 1, 0, 1);
        addFormField(fields, c, "SĐT *", txtFormSoDienThoai, 0, 1, 1);
        addFormField(fields, c, "Email", txtFormEmail, 1, 1, 1);
        JScrollPane addressScroll = new JScrollPane(txtFormDiaChi);
        addressScroll.setPreferredSize(new Dimension(400, 70));
        addFormField(fields, c, "Địa chỉ", addressScroll, 0, 2, 2);
        addFormField(fields, c, "Ngày sinh", txtFormNgaySinh, 0, 3, 1);
        addFormField(fields, c, "Giới tính", cboFormGioiTinh, 1, 3, 1);

        JPanel formContent = new JPanel(new BorderLayout());
        formContent.setBackground(Color.WHITE);
        formContent.add(fields, BorderLayout.NORTH);
        card.add(formContent, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.setBackground(Color.WHITE);
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        JButton btnLuu = taoNutForm("▣  Lưu", new Color(180, 55, 48), Color.WHITE);
        JButton btnLamMoi = taoNutForm("⟳  Làm mới", new Color(232, 232, 232), Color.DARK_GRAY);
        JButton btnHuy = taoNutForm("×  Hủy", new Color(232, 232, 232), Color.DARK_GRAY);
        actions.add(btnLuu);
        actions.add(btnLamMoi);
        actions.add(btnHuy);
        card.add(actions, BorderLayout.SOUTH);
        JPanel cardAlignment = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cardAlignment.setOpaque(false);
        cardAlignment.add(card);
        page.add(new JScrollPane(cardAlignment), BorderLayout.CENTER);

        btnQuayLai.addActionListener(e -> hienThiDanhSachKhachHang());
        btnHuy.addActionListener(e -> hienThiDanhSachKhachHang());
        btnLamMoi.addActionListener(e -> lamMoiFormKhachHang());
        btnLuu.addActionListener(e -> luuKhachHang());
        if (customerFormPanel != null) {
            contentCards.remove(customerFormPanel);
        }
        customerFormPanel = page;
        contentCards.add(page, "form");
        try {
            capNhatMaKhachHangTrenForm();
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tạo mã khách hàng: " + ex.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        contentLayout.show(contentCards, "form");
    }

    private void addFormField(JPanel panel, GridBagConstraints c, String label,
                              java.awt.Component field, int column, int row, int width) {
        c.gridx = column;
        c.gridwidth = width;
        c.weightx = 1;
        c.gridy = row * 2;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(fieldLabel, c);
        c.gridy = row * 2 + 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(new Dimension(280, 38));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setPreferredSize(new Dimension(280, 38));
        }
        panel.add(field, c);
    }

    private JButton taoNutForm(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(110, 40));
        return button;
    }

    private void capNhatMaKhachHangTrenForm() throws java.sql.SQLException {
        txtMaKhachHang.setText(kh_dao.phatSinhMaKhachHang());
    }

    private void lamMoiFormKhachHang() {
        txtFormHoTen.setText("");
        txtFormSoDienThoai.setText("");
        txtFormEmail.setText("");
        txtFormDiaChi.setText("");
        txtFormNgaySinh.setText("");
        cboFormGioiTinh.setSelectedIndex(0);
        txtFormHoTen.requestFocusInWindow();
    }

    private void hienThiDanhSachKhachHang() {
        contentLayout.show(contentCards, "list");
        txtSearch.setText("");
        docDuLieuDatabaseVaoTable();
    }

    private void luuKhachHang() {
        String hoTen = txtFormHoTen.getText().trim();
        String soDienThoai = txtFormSoDienThoai.getText().trim();
        String email = txtFormEmail.getText().trim();
        String diaChi = txtFormDiaChi.getText().trim();
        String ngaySinhText = txtFormNgaySinh.getText().trim();
        if (hoTen.isEmpty()) {
            hienThiLoiNhapLieu("Vui lòng nhập họ tên khách hàng.");
            txtFormHoTen.requestFocusInWindow();
            return;
        }
        if (hoTen.length() > 100 || email.length() > 100 || diaChi.length() > 255) {
            hienThiLoiNhapLieu("Họ tên tối đa 100 ký tự, email 100 và địa chỉ 255 ký tự.");
            return;
        }
        if (!soDienThoai.matches("\\d{9,15}")) {
            hienThiLoiNhapLieu("Số điện thoại phải gồm từ 9 đến 15 chữ số.");
            txtFormSoDienThoai.requestFocusInWindow();
            return;
        }
        if (!email.isEmpty()
                && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            hienThiLoiNhapLieu("Email không đúng định dạng.");
            txtFormEmail.requestFocusInWindow();
            return;
        }

        java.sql.Date ngaySinh = null;
        if (!ngaySinhText.isEmpty()) {
            try {
                LocalDate parsed = LocalDate.parse(ngaySinhText, dateFormatter);
                if (parsed.isAfter(LocalDate.now())) {
                    hienThiLoiNhapLieu("Ngày sinh không được lớn hơn ngày hiện tại.");
                    return;
                }
                ngaySinh = java.sql.Date.valueOf(parsed);
            } catch (DateTimeParseException ex) {
                hienThiLoiNhapLieu("Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng mm/dd/yyyy.");
                txtFormNgaySinh.requestFocusInWindow();
                return;
            }
        }

        KhachHang khachHang = new KhachHang();
        khachHang.setHoTen(hoTen);
        khachHang.setGioiTinh((String) cboFormGioiTinh.getSelectedItem());
        khachHang.setNgaySinh(ngaySinh);
        khachHang.setSoDienThoai(soDienThoai);
        khachHang.setEmail(email.isEmpty() ? null : email);
        khachHang.setDiaChi(diaChi.isEmpty() ? null : diaChi);
        try {
            if (kh_dao.themKhachHang(khachHang)) {
                JOptionPane.showMessageDialog(this,
                        "Đã thêm khách hàng " + khachHang.getMaKhachHang() + " thành công.");
                txtSearch.setText("");
                docDuLieuDatabaseVaoTable();
                contentLayout.show(contentCards, "list");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng vào cơ sở dữ liệu.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng: " + ex.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiLoiNhapLieu(String thongBao) {
        JOptionPane.showMessageDialog(this, thongBao, "Thông tin chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerPanel().setVisible(true));
    }
}