package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

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

    public createPromtionPanel(Window parentWindow) {
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
        cardPanel.setPreferredSize(new Dimension(600, 380));
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
        setPlaceholder(txtTenChuongTrinh, "Nhập tên KM...");
        formBody.add(txtTenChuongTrinh, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        cboLoaiKhuyenMai = new JComboBox<>(new String[] { "% Giảm giá", "Giảm tiền trực tiếp" });
        cboLoaiKhuyenMai.setBackground(Color.WHITE);
        formBody.add(cboLoaiKhuyenMai, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formBody.add(createFieldLabel("Giá trị giảm"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formBody.add(createFieldLabel("Điều kiện (đơn tối thiểu)"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        txtGiaTriGiam = new JTextField();
        setPlaceholder(txtGiaTriGiam, "VD: 25 (%), 500000 (VNĐ)");
        formBody.add(txtGiaTriGiam, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        txtDieuKien = new JTextField();
        setPlaceholder(txtDieuKien, "VD: 5000000");
        formBody.add(txtDieuKien, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formBody.add(createFieldLabel("Ngày bắt đầu *"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formBody.add(createFieldLabel("Ngày kết thúc *"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        txtNgayBatDau = new JTextField();
        setPlaceholder(txtNgayBatDau, "dd/mm/yyyy 📅");
        formBody.add(txtNgayBatDau, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        txtNgayKetThuc = new JTextField();
        setPlaceholder(txtNgayKetThuc, "dd/mm/yyyy 📅");
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
        setPlaceholderArea(txtMoTa, "Mô tả chi tiết...");

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

        btnHuy = new JButton("✖ Hủy");
        btnHuy.setBackground(new Color(230, 230, 230));
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHuy.setFocusPainted(false);

        buttonBar.add(btnTao);
        buttonBar.add(btnHuy);

        formBody.add(buttonBar, gbc);

        cardPanel.add(formBody, BorderLayout.CENTER);
        return cardPanel;
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
            frame.setContentPane(new createPromtionPanel(frame));
            frame.setVisible(true);
        });
    }
}