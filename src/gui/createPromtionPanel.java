package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DAO.KhuyenMai_DAO;
import entity.KhuyenMai;

public class createPromtionPanel extends JPanel {

    private MenuPanel menuPanel;
    private JTextField txtTenChuongTrinh;
    private JComboBox<String> cboLoaiKhuyenMai;
    private JTextField txtGiaTriGiam;
    private JTextField txtDieuKien;
    private JTextField txtNgayBatDau;
    private JTextField txtNgayKetThuc;
    private JTextArea txtMoTa;

    private JButton btnQuayLai;
    private JButton btnTao;
    private JButton btnHuy;

    // Placeholders
    private static final String PH_TEN = "Nhập tên KM...";
    private static final String PH_GIA_TRI = "VD: 25 (%), 500000 (VNĐ)";
    private static final String PH_DIEU_KIEN = "VD: 5000000";
    private static final String PH_NGAY_BD = "dd/mm/yyyy";
    private static final String PH_NGAY_KT = "dd/mm/yyyy";
    private static final String PH_MO_TA = "Mô tả chi tiết...";

    private Window parentWindow;

    public createPromtionPanel(Window parentWindow) {
        this.parentWindow = parentWindow;
        setLayout(new BorderLayout());
        setBackground(new Color(238, 238, 238));

        menuPanel = new MenuPanel();

        menuPanel.setShortcutAction(MenuPanel.Shortcut.HOME, e -> {
            new MainFrame().setVisible(true);
            if (parentWindow != null)
                parentWindow.dispose();
        });
        menuPanel.setShortcutAction(MenuPanel.Shortcut.LOGOUT, e -> {
            new LoginFrame().setVisible(true);
            if (parentWindow != null)
                parentWindow.dispose();
        });

        menuPanel.setMenuItemAction(MenuPanel.MenuItem.HOME, e -> {
            new MainFrame().setVisible(true);
            if (parentWindow != null)
                parentWindow.dispose();
        });
        menuPanel.setMenuItemAction(MenuPanel.MenuItem.LOGOUT, e -> {
            new LoginFrame().setVisible(true);
            if (parentWindow != null)
                parentWindow.dispose();
        });

        add(menuPanel, BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());
        pCenterContainer.setOpaque(false);

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(230, 230, 230));
        pSubHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(8, 15, 8, 15)));

        JLabel lblTitle = new JLabel("🏷  Khuyến mãi — Tạo mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(180, 40, 40));

        btnQuayLai = new JButton("← Quay lại");
        btnQuayLai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBackground(Color.WHITE);
        btnQuayLai.addActionListener(e -> {
            new PromotionPanel().setVisible(true);
            if (parentWindow != null) {
                parentWindow.dispose();
            }
        });
        pSubHeader.add(lblTitle, BorderLayout.WEST);
        pSubHeader.add(btnQuayLai, BorderLayout.EAST);

        pCenterContainer.add(pSubHeader, BorderLayout.NORTH);

        JPanel formContainer = createFormPanel();

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftWrapper.setOpaque(false);
        leftWrapper.add(formContainer);

        pCenterContainer.add(leftWrapper, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(600, 420));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new LineBorder(new Color(210, 210, 210), 1));

        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        formHeader.setBackground(new Color(230, 230, 230));
        formHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        JLabel lblFormTitle = new JLabel("🎁 Thông tin khuyến mãi");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFormTitle.setForeground(new Color(150, 30, 30));
        formHeader.add(lblFormTitle);

        cardPanel.add(formHeader, BorderLayout.NORTH);

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setBackground(Color.WHITE);
        formBody.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.weightx = 0.5;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formBody.add(createFieldLabel("Tên chương trình *"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formBody.add(createFieldLabel("Loại khuyến mãi"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        txtTenChuongTrinh = new JTextField();
        setPlaceholder(txtTenChuongTrinh, PH_TEN);
        formBody.add(txtTenChuongTrinh, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        cboLoaiKhuyenMai = new JComboBox<>(new String[] { "Phần trăm", "Số tiền" });
        cboLoaiKhuyenMai.setBackground(Color.WHITE);
        formBody.add(cboLoaiKhuyenMai, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formBody.add(createFieldLabel("Giá trị giảm *"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formBody.add(createFieldLabel("Điều kiện (đơn tối thiểu)"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        txtGiaTriGiam = new JTextField();
        setPlaceholder(txtGiaTriGiam, PH_GIA_TRI);
        formBody.add(txtGiaTriGiam, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        txtDieuKien = new JTextField();
        setPlaceholder(txtDieuKien, PH_DIEU_KIEN);
        formBody.add(txtDieuKien, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formBody.add(createFieldLabel("Ngày bắt đầu * (dd/mm/yyyy)"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formBody.add(createFieldLabel("Ngày kết thúc * (dd/mm/yyyy)"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        txtNgayBatDau = new JTextField();
        setPlaceholder(txtNgayBatDau, PH_NGAY_BD);
        formBody.add(txtNgayBatDau, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        txtNgayKetThuc = new JTextField();
        setPlaceholder(txtNgayKetThuc, PH_NGAY_KT);
        formBody.add(txtNgayKetThuc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formBody.add(createFieldLabel("Mô tả"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setPlaceholderArea(txtMoTa, PH_MO_TA);

        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        formBody.add(scrollMoTa, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonBar.setOpaque(false);

        btnTao = new JButton("💾 Tạo");
        btnTao.setBackground(new Color(180, 40, 40));
        btnTao.setForeground(Color.WHITE);
        btnTao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTao.setFocusPainted(false);
        btnTao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTao.addActionListener(e -> handleCreate());

        btnHuy = new JButton("✖ Hủy");
        btnHuy.setBackground(new Color(230, 230, 230));
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> {
            new PromotionPanel().setVisible(true);
            if (parentWindow != null) {
                parentWindow.dispose();
            }
        });

        buttonBar.add(btnTao);
        buttonBar.add(btnHuy);

        formBody.add(buttonBar, gbc);

        cardPanel.add(formBody, BorderLayout.CENTER);
        return cardPanel;
    }

    /**
     * Xử lý sự kiện tạo khuyến mãi: validate + lưu database + quay về trang quản lý.
     */
    private void handleCreate() {
        // === VALIDATION ===
        // 1. Tên chương trình: bắt buộc, 3–200 ký tự, cho phép chữ cái Unicode, số, khoảng trắng, dấu gạch ngang
        String tenCT = getFieldText(txtTenChuongTrinh, PH_TEN);
        if (tenCT.isEmpty()) {
            showError("Tên chương trình không được để trống!");
            txtTenChuongTrinh.requestFocus();
            return;
        }
        if (!tenCT.matches("^[\\p{L}\\d\\s\\-–_,.:!()]{3,200}$")) {
            showError("Tên chương trình phải từ 3–200 ký tự.\nChỉ chứa chữ cái, số, khoảng trắng và các ký tự - _ , . : ! ( )");
            txtTenChuongTrinh.requestFocus();
            return;
        }

        // 2. Giá trị giảm: bắt buộc, số dương
        String giaTriStr = getFieldText(txtGiaTriGiam, PH_GIA_TRI);
        if (giaTriStr.isEmpty()) {
            showError("Giá trị giảm không được để trống!");
            txtGiaTriGiam.requestFocus();
            return;
        }
        if (!giaTriStr.matches("^\\d+(\\.\\d{1,2})?$")) {
            showError("Giá trị giảm phải là số dương.\nVD: 25 hoặc 500000 hoặc 10.5");
            txtGiaTriGiam.requestFocus();
            return;
        }
        double giaTriGiam = Double.parseDouble(giaTriStr);
        if (giaTriGiam <= 0) {
            showError("Giá trị giảm phải lớn hơn 0!");
            txtGiaTriGiam.requestFocus();
            return;
        }

        // Kiểm tra % giảm giá <= 100
        String loai = (String) cboLoaiKhuyenMai.getSelectedItem();
        if ("Phần trăm".equals(loai) && giaTriGiam > 100) {
            showError("Giảm giá theo phần trăm không được vượt quá 100%!");
            txtGiaTriGiam.requestFocus();
            return;
        }

        // 3. Điều kiện đơn tối thiểu: không bắt buộc, nhưng nếu nhập thì phải là số >= 0
        String dieuKienStr = getFieldText(txtDieuKien, PH_DIEU_KIEN);
        double donHangToiThieu = 0;
        if (!dieuKienStr.isEmpty()) {
            if (!dieuKienStr.matches("^\\d+(\\.\\d{1,2})?$")) {
                showError("Điều kiện đơn tối thiểu phải là số.\nVD: 5000000");
                txtDieuKien.requestFocus();
                return;
            }
            donHangToiThieu = Double.parseDouble(dieuKienStr);
        }

        // 4. Ngày bắt đầu: bắt buộc, đúng định dạng dd/mm/yyyy
        String ngayBDStr = getFieldText(txtNgayBatDau, PH_NGAY_BD);
        if (ngayBDStr.isEmpty()) {
            showError("Ngày bắt đầu không được để trống!");
            txtNgayBatDau.requestFocus();
            return;
        }
        if (!ngayBDStr.matches("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            showError("Ngày bắt đầu phải đúng định dạng dd/mm/yyyy.\nVD: 25/12/2026");
            txtNgayBatDau.requestFocus();
            return;
        }

        // 5. Ngày kết thúc: bắt buộc, đúng định dạng dd/mm/yyyy
        String ngayKTStr = getFieldText(txtNgayKetThuc, PH_NGAY_KT);
        if (ngayKTStr.isEmpty()) {
            showError("Ngày kết thúc không được để trống!");
            txtNgayKetThuc.requestFocus();
            return;
        }
        if (!ngayKTStr.matches("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            showError("Ngày kết thúc phải đúng định dạng dd/mm/yyyy.\nVD: 31/12/2026");
            txtNgayKetThuc.requestFocus();
            return;
        }

        // Parse ngày tháng
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date ngayBatDau, ngayKetThuc;
        try {
            ngayBatDau = sdf.parse(ngayBDStr);
        } catch (ParseException ex) {
            showError("Ngày bắt đầu không hợp lệ! Vui lòng nhập đúng định dạng dd/mm/yyyy.");
            txtNgayBatDau.requestFocus();
            return;
        }
        try {
            ngayKetThuc = sdf.parse(ngayKTStr);
        } catch (ParseException ex) {
            showError("Ngày kết thúc không hợp lệ! Vui lòng nhập đúng định dạng dd/mm/yyyy.");
            txtNgayKetThuc.requestFocus();
            return;
        }

        // Ngày kết thúc phải sau ngày bắt đầu
        if (!ngayKetThuc.after(ngayBatDau)) {
            showError("Ngày kết thúc phải sau ngày bắt đầu!");
            txtNgayKetThuc.requestFocus();
            return;
        }

        // 6. Mô tả: không bắt buộc, tối đa 500 ký tự
        String moTa = getFieldText(txtMoTa, PH_MO_TA);
        if (moTa.length() > 500) {
            showError("Mô tả không được vượt quá 500 ký tự!\nHiện tại: " + moTa.length() + " ký tự.");
            txtMoTa.requestFocus();
            return;
        }

        // === TẠO ĐỐI TƯỢNG VÀ LƯU DATABASE ===
        KhuyenMai_DAO dao = new KhuyenMai_DAO();
        String maKM = dao.sinhMaKhuyenMai();

        // Xác định trạng thái dựa trên ngày
        Date now = new Date();
        String trangThai;
        if (now.before(ngayBatDau)) {
            trangThai = "Chưa bắt đầu";
        } else if (now.after(ngayKetThuc)) {
            trangThai = "Đã kết thúc";
        } else {
            trangThai = "Đang diễn ra";
        }

        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(maKM);
        km.setTenKhuyenMai(tenCT);
        km.setMoTa(moTa.isEmpty() ? null : moTa);
        km.setLoaiKhuyenMai(loai);
        km.setGiaTriGiam(giaTriGiam);
        km.setGiaTriGiamToiDa(0); // Có thể mở rộng thêm field
        km.setDonHangToiThieu(donHangToiThieu);
        km.setSoLuongToiDa(0);
        km.setNgayBatDau(ngayBatDau);
        km.setNgayKetThuc(ngayKetThuc);
        km.setApDungChoTatCa(true);
        km.setTrangThai(trangThai);
        km.setNguoiTao(null);

        boolean success = dao.themKhuyenMai(km);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Tạo khuyến mãi \"" + tenCT + "\" thành công!\nMã: " + maKM,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            // Quay về trang quản lý khuyến mãi
            new PromotionPanel().setVisible(true);
            if (parentWindow != null) {
                parentWindow.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tạo khuyến mãi. Vui lòng kiểm tra kết nối database và thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy text thực từ field, bỏ qua placeholder.
     */
    private String getFieldText(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (text.equals(placeholder)) return "";
        return text;
    }

    /**
     * Lấy text thực từ JTextArea, bỏ qua placeholder.
     */
    private String getFieldText(JTextArea area, String placeholder) {
        String text = area.getText().trim();
        if (text.equals(placeholder)) return "";
        return text;
    }

    /**
     * Hiện thông báo lỗi.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void setPlaceholderArea(JTextArea textArea, String placeholder) {
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);
        textArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(Color.GRAY);
                }
            }
        });
    }

    public JButton getBtnQuayLai() {
        return btnQuayLai;
    }

    public JButton getBtnTao() {
        return btnTao;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test CreatePromotionPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(new createPromtionPanel(frame));
            frame.setVisible(true);
        });
    }
}