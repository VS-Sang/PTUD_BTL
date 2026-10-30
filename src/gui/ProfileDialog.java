package gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class ProfileDialog extends JDialog {
    private static final Color BRAND_RED = new Color(214, 31, 47);
    private static final Color DARK_RED = new Color(193, 35, 39);
    private static final Color TEXT_GRAY = new Color(112, 112, 112);
    private static final Color LINE_GRAY = new Color(228, 228, 228);

    public ProfileDialog(Window owner, String fullName, String role) {
        this(owner, fullName, role, "admin", "admin@dienmaydo.vn", "0901234567", "Hoạt động");
    }

    public ProfileDialog(Window owner, String fullName, String role, String username) {
        this(owner, fullName, role, username, null, null, "Hoạt động");
    }

    public ProfileDialog(Window owner, String fullName, String role, String username,
                         String email, String phone, String status) {
        super(owner, "Thông tin cá nhân", Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(458, 312);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        setContentPane(createContent(fullName, role, username, email, phone, status));
    }

    private JPanel createContent(String fullName, String role, String username,
                                 String email, String phone, String status) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        root.add(createTitleBar(), BorderLayout.NORTH);
        root.add(createInformation(fullName, role, username, email, phone, status), BorderLayout.CENTER);
        root.add(createFooter(username), BorderLayout.SOUTH);
        return root;
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(DARK_RED);
        titleBar.setPreferredSize(new Dimension(0, 34));

        JLabel title = new JLabel("  👤  Thông tin cá nhân");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(title, BorderLayout.CENTER);

        JButton closeButton = new JButton("×");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(DARK_RED);
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(38, 34));
        closeButton.addActionListener(event -> dispose());
        titleBar.add(closeButton, BorderLayout.EAST);
        return titleBar;
    }

    private JPanel createInformation(String fullName, String role, String username,
                                     String email, String phone, String status) {
        JPanel information = new JPanel(new BorderLayout(18, 0));
        information.setBackground(Color.WHITE);
        information.setBorder(new EmptyBorder(16, 19, 14, 19));
        information.add(new AvatarPanel(), BorderLayout.WEST);

        JPanel details = new JPanel(new GridBagLayout());
        details.setBackground(Color.WHITE);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 8, 18);

        JLabel name = new JLabel(valueOrDefault(fullName, "Người dùng"));
        name.setForeground(DARK_RED);
        name.setFont(new Font("Segoe UI", Font.BOLD, 19));
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 2, 0);
        details.add(name, constraints);

        JLabel position = new JLabel(valueOrDefault(role, "Nhân viên"));
        position.setForeground(TEXT_GRAY);
        position.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 11, 0);
        details.add(position, constraints);

        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, 7, 18);
        details.add(new JLabel("Tên đăng nhập:"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(0, 0, 7, 0);
        details.add(valueLabel(username), constraints);

        addDetailRow(details, constraints, 3, "Email:", email);
        addDetailRow(details, constraints, 4, "Điện thoại:", phone);
        addDetailRow(details, constraints, 5, "Vai trò:", role, roleBadge(role));
        addDetailRow(details, constraints, 6, "Trạng thái:", status, statusBadge(status));
        information.add(details, BorderLayout.CENTER);
        return information;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints template, int row,
                              String label, String value) {
        addDetailRow(panel, template, row, label, value, valueLabel(value));
    }

    private void addDetailRow(JPanel panel, GridBagConstraints template, int row,
                              String label, String value, JLabel valueComponent) {
        GridBagConstraints labelConstraints = (GridBagConstraints) template.clone();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(0, 0, 7, 18);
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints valueConstraints = (GridBagConstraints) template.clone();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.insets = new Insets(0, 0, 7, 0);
        panel.add(valueComponent, valueConstraints);
    }

    private JLabel valueLabel(String value) {
        JLabel label = new JLabel(valueOrDefault(value, "Chưa cập nhật"));
        label.setForeground(new Color(35, 35, 35));
        return label;
    }

    private JLabel roleBadge(String role) {
        JLabel badge = badge(valueOrDefault(role, "Nhân viên"), new Color(255, 235, 235),
                BRAND_RED, new Color(255, 122, 122));
        return badge;
    }

    private JLabel statusBadge(String status) {
        boolean active = "Hoạt động".equalsIgnoreCase(valueOrDefault(status, ""));
        JLabel badge = badge(valueOrDefault(status, "Chưa cập nhật"),
                active ? new Color(232, 249, 234) : new Color(255, 242, 230),
                active ? new Color(35, 135, 55) : new Color(180, 95, 20),
                active ? new Color(112, 193, 120) : new Color(220, 160, 90));
        return badge;
    }

    private JLabel badge(String text, Color background, Color foreground, Color border) {
        JLabel label = new JLabel(text);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                new EmptyBorder(3, 8, 3, 8)));
        return label;
    }

    private JPanel createFooter(String username) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(new Color(247, 247, 247));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LINE_GRAY));

        JButton changePassword = new JButton("🔑 Đổi mật khẩu");
        changePassword.setForeground(Color.WHITE);
        changePassword.setBackground(DARK_RED);
        changePassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changePassword.setFocusPainted(false);
        changePassword.addActionListener(event -> new ChangePasswordDialog(this, username).setVisible(true));

        JButton close = new JButton("× Đóng");
        close.setFocusPainted(false);
        close.addActionListener(event -> dispose());
        footer.add(changePassword);
        footer.add(close);
        return footer;
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static class AvatarPanel extends JPanel {
        AvatarPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(84, 84));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setPaint(new GradientPaint(0, 0, new Color(224, 32, 48),
                    getWidth(), getHeight(), new Color(247, 33, 65)));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.fillOval(31, 20, 22, 22);
            g2.fillArc(22, 43, 40, 27, 0, 180);
            g2.dispose();
        }
    }

    private static class ChangePasswordDialog extends JDialog {
        private final String username;
        private final javax.swing.JPasswordField currentPassword = new javax.swing.JPasswordField(14);
        private final javax.swing.JPasswordField newPassword = new javax.swing.JPasswordField(14);
        private final javax.swing.JPasswordField confirmPassword = new javax.swing.JPasswordField(14);

        ChangePasswordDialog(Window owner, String username) {
            super(owner, "Đổi mật khẩu", Dialog.ModalityType.APPLICATION_MODAL);
            this.username = username;
            setSize(340, 230);
            setResizable(false);
            setLocationRelativeTo(owner);
            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(new EmptyBorder(14, 14, 4, 14));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            form.add(new JLabel("Mật khẩu hiện tại:"), c);
            c.gridx = 1;
            form.add(currentPassword, c);
            c.gridx = 0;
            c.gridy++;
            form.add(new JLabel("Mật khẩu mới:"), c);
            c.gridx = 1;
            form.add(newPassword, c);
            c.gridx = 0;
            c.gridy++;
            form.add(new JLabel("Xác nhận mật khẩu:"), c);
            c.gridx = 1;
            form.add(confirmPassword, c);
            JButton save = new JButton("Lưu");
            save.addActionListener(event -> changePassword());
            JButton cancel = new JButton("Hủy");
            cancel.addActionListener(event -> dispose());
            JPanel buttons = new JPanel();
            buttons.add(save);
            buttons.add(cancel);
            add(form, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
        }

        private void changePassword() {
            char[] current = currentPassword.getPassword();
            char[] next = newPassword.getPassword();
            char[] confirmation = confirmPassword.getPassword();
            try {
                if (current.length == 0 || next.length < 6
                        || !java.util.Arrays.equals(next, confirmation)) {
                    JOptionPane.showMessageDialog(this,
                            "Mật khẩu mới phải có ít nhất 6 ký tự và hai ô mật khẩu phải giống nhau.",
                            "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String currentText = new String(current);
                String nextText = new String(next);
                if (currentText.equals(nextText)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới phải khác mật khẩu hiện tại.",
                            "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String sql = "UPDATE TaiKhoan SET matKhau = ?, ngayDoiMatKhau = GETDATE(), "
                        + "ngayCapNhat = GETDATE() WHERE tenDangNhap = ? "
                        + "AND (matKhau = ? OR matKhau = ?)";
                try (java.sql.Connection connection = connectDB.ConnectDB.getConnection();
                     java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, LoginFrame.toMD5(nextText));
                    statement.setString(2, username);
                    statement.setString(3, currentText);
                    statement.setString(4, LoginFrame.toMD5(currentText));
                    if (statement.executeUpdate() == 0) {
                        JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác.",
                                "Đổi mật khẩu thất bại", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công.");
                dispose();
            } catch (java.sql.SQLException exception) {
                JOptionPane.showMessageDialog(this, "Không thể đổi mật khẩu:\n" + exception.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                java.util.Arrays.fill(current, '\0');
                java.util.Arrays.fill(next, '\0');
                java.util.Arrays.fill(confirmation, '\0');
            }
        }
    }
}
