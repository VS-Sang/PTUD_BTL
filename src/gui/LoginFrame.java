package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private static final Color BRAND_RED = new Color(180, 55, 48);
    private static final Color DARK_RED = new Color(150, 42, 36);
    private static final Color TEXT_COLOR = new Color(75, 75, 75);

    private final JTextField usernameField = new PlaceholderTextField("Nhập tên đăng nhập...");
    private final JPasswordField passwordField = new PlaceholderPasswordField("Nhập mật khẩu...");

    public LoginFrame() {
        setTitle("Đăng Nhập - Điện Máy Đỏ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 700));
        setSize(760, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(BRAND_RED);
        background.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(background);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        background.add(card, BorderLayout.CENTER);

        card.add(createTitleBar(), BorderLayout.NORTH);
        card.add(createLoginContent(), BorderLayout.CENTER);
        card.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BRAND_RED);
        titleBar.setPreferredSize(new Dimension(0, 48));

        JLabel title = new JLabel("  Đăng Nhập - Điện Máy Đỏ");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleBar.add(title, BorderLayout.WEST);

        JPanel windowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowButtons.setOpaque(false);

        JButton minimizeButton = createWindowButton(false);
        minimizeButton.addActionListener(event -> setState(JFrame.ICONIFIED));
        JButton closeButton = createWindowButton(true);
        closeButton.addActionListener(event -> dispose());
        windowButtons.add(minimizeButton);
        windowButtons.add(closeButton);
        titleBar.add(windowButtons, BorderLayout.EAST);

        MouseAdapter dragWindow = new MouseAdapter() {
            private Point dragOffset;

            @Override
            public void mousePressed(MouseEvent event) {
                dragOffset = event.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                Point screenLocation = event.getLocationOnScreen();
                Point offset = SwingUtilities.convertPoint(event.getComponent(), dragOffset, LoginFrame.this);
                setLocation(screenLocation.x - offset.x, screenLocation.y - offset.y);
            }
        };
        titleBar.addMouseListener(dragWindow);
        titleBar.addMouseMotionListener(dragWindow);
        title.addMouseListener(dragWindow);
        title.addMouseMotionListener(dragWindow);
        return titleBar;
    }

    private JButton createWindowButton(boolean close) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.7f));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                if (close) {
                    g2.drawLine(centerX - 5, centerY - 5, centerX + 5, centerY + 5);
                    g2.drawLine(centerX + 5, centerY - 5, centerX - 5, centerY + 5);
                } else {
                    g2.drawLine(centerX - 6, centerY + 4, centerX + 6, centerY + 4);
                }
                g2.dispose();
            }
        };
        button.getAccessibleContext().setAccessibleName(close ? "Đóng" : "Thu nhỏ");
        button.setForeground(Color.WHITE);
        button.setBackground(BRAND_RED);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(46, 46));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(close ? new Color(155, 40, 36) : new Color(195, 70, 62));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(BRAND_RED);
            }
        });
        return button;
    }

    private JPanel createLoginContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 0, 22, 0));
        form.setPreferredSize(new Dimension(560, 580));

        GridBagConstraints row = new GridBagConstraints();
        row.gridx = 0;
        row.weightx = 1;
        row.fill = GridBagConstraints.HORIZONTAL;

        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(204, 48, 61));
                g2.fillOval(4, 4, 104, 104);
                g2.setColor(Color.WHITE);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                Polygon bolt = new Polygon(
                        new int[] { centerX + 9, centerX - 10, centerX + 1, centerX - 8, centerX + 13, centerX + 3 },
                        new int[] { centerY - 27, centerY + 1, centerY + 1, centerY + 27, centerY - 3, centerY - 3 },
                        6);
                g2.fillPolygon(bolt);
                g2.dispose();
            }
        };
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(112, 112));
        row.fill = GridBagConstraints.NONE;
        row.weightx = 0;
        row.gridy = 0;
        row.insets = new Insets(0, 0, 10, 0);
        row.anchor = GridBagConstraints.CENTER;
        form.add(logo, row);

        row.fill = GridBagConstraints.HORIZONTAL;
        row.weightx = 1;
        row.anchor = GridBagConstraints.WEST;
        JLabel brand = new JLabel("ĐIỆN MÁY ĐỎ");
        brand.setHorizontalAlignment(SwingConstants.CENTER);
        brand.setForeground(DARK_RED);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 30));
        row.gridy++;
        row.insets = new Insets(0, 0, 0, 0);
        form.add(brand, row);

        JLabel subtitle = new JLabel("Hệ thống quản lý cửa hàng điện máy — v2.0");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(135, 135, 135));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        row.gridy++;
        row.insets = new Insets(0, 0, 26, 0);
        form.add(subtitle, row);

        JLabel usernameLabel = createFieldLabel("Tên đăng nhập");
        row.gridy++;
        row.insets = new Insets(0, 0, 6, 0);
        row.anchor = GridBagConstraints.WEST;
        form.add(usernameLabel, row);

        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(0, 48));
        usernameField.setBorder(createInputBorder(new Color(190, 190, 190)));
        usernameField.setToolTipText("Nhập tên đăng nhập");
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                usernameField.setBorder(createInputBorder(BRAND_RED));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                usernameField.setBorder(createInputBorder(new Color(190, 190, 190)));
            }
        });
        row.gridy++;
        row.insets = new Insets(0, 0, 18, 0);
        row.fill = GridBagConstraints.HORIZONTAL;
        form.add(usernameField, row);

        JLabel passwordLabel = createFieldLabel("Mật khẩu");
        row.gridy++;
        row.insets = new Insets(0, 0, 6, 0);
        row.anchor = GridBagConstraints.WEST;
        form.add(passwordLabel, row);

        JPanel passwordInput = new JPanel(new BorderLayout());
        passwordInput.setBackground(Color.WHITE);
        passwordInput.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));
        passwordInput.setPreferredSize(new Dimension(0, 48));

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(new EmptyBorder(8, 12, 8, 4));
        passwordField.setToolTipText("Nhập mật khẩu");
        passwordField.addActionListener(event -> login());
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                passwordInput.setBorder(BorderFactory.createLineBorder(BRAND_RED));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                passwordInput.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));
            }
        });
        passwordInput.add(passwordField, BorderLayout.CENTER);

        JButton showPasswordButton = new JButton("Hiện");
        showPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordButton.setToolTipText("Hiện hoặc ẩn mật khẩu");
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setBackground(Color.WHITE);
        showPasswordButton.setForeground(BRAND_RED);
        showPasswordButton.addActionListener(event -> {
            passwordField.setEchoChar(passwordField.getEchoChar() == '\u0000' ? '•' : '\u0000');
            showPasswordButton.setText(passwordField.getEchoChar() == '\u0000' ? "Ẩn" : "Hiện");
            passwordField.requestFocusInWindow();
        });
        passwordInput.add(showPasswordButton, BorderLayout.EAST);
        row.gridy++;
        row.insets = new Insets(0, 0, 20, 0);
        row.fill = GridBagConstraints.HORIZONTAL;
        form.add(passwordInput, row);

        JLabel hint = new JLabel(
                "<html>ⓘ Quản lý: <b>admin / admin</b>"
                        + " &nbsp;|&nbsp; Nhân viên: <b>nv / nv</b></html>");
        hint.setForeground(DARK_RED);
        hint.setBackground(new Color(255, 248, 248));
        hint.setOpaque(true);
        hint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 205, 205)),
                new EmptyBorder(10, 12, 10, 12)));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.gridy++;
        row.insets = new Insets(0, 0, 22, 0);
        form.add(hint, row);

        JPanel actions = new JPanel(new BorderLayout(12, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(0, 52));

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(BRAND_RED);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(event -> login());
        actions.add(loginButton, BorderLayout.CENTER);

        JButton exitButton = new JButton("×  Thoát");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(event -> dispose());
        actions.add(exitButton, BorderLayout.EAST);
        row.gridy++;
        row.insets = new Insets(0, 0, 0, 0);
        form.add(actions, row);

        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.gridx = 0;
        formConstraints.gridy = 0;
        formConstraints.weightx = 1;
        formConstraints.weighty = 1;
        formConstraints.anchor = GridBagConstraints.CENTER;
        content.add(form, formConstraints);
        return content;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private Border createInputBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                new EmptyBorder(8, 12, 8, 12));
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)));
        JLabel copyright = new JLabel("© 2026 Điện Máy Đỏ - Phần mềm quản lý v2.0");
        copyright.setForeground(new Color(145, 145, 145));
        footer.add(copyright);
        return footer;
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        String passwordText = new String(password);
        Arrays.fill(password, '\0');

        String role = null;
        String fullName = null;

        String md5Pass = toMD5(passwordText);

        // 1. Thử xác thực từ Database TaiKhoan
        try (java.sql.Connection con = connectDB.ConnectDB.getConnection();
                java.sql.PreparedStatement stmt = con.prepareStatement(
                        "SELECT tk.vaiTro, nv.hoTen FROM TaiKhoan tk "
                                + "JOIN NhanVien nv ON tk.maNhanVien = nv.maNhanVien "
                                + "WHERE tk.tenDangNhap = ? AND (tk.matKhau = ? OR tk.matKhau = ?) AND tk.trangThai = N'Hoạt động'")) {
            stmt.setString(1, username);
            stmt.setString(2, passwordText);
            stmt.setString(3, md5Pass);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString(1);
                fullName = rs.getString(2);
            }
        } catch (Exception ignored) {
        }

        // 2. Mặc định hỗ trợ admin/admin hoặc nv/nv
        if (fullName == null) {
            if ("admin".equalsIgnoreCase(username) && ("admin".equals(passwordText) || "123456".equals(passwordText))) {
                role = "Quản lý";
                fullName = "Nguyễn Văn An";
            } else if ("nv".equalsIgnoreCase(username)
                    && ("nv".equals(passwordText) || "123456".equals(passwordText))) {
                role = "Nhân viên";
                fullName = "Lê Thị Dung";
            }
        }

        if (role == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác.",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        dispose();
        new MainFrame(fullName, role).setVisible(true);
    }

    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        private PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(new Color(155, 155, 155));
                g2.setFont(getFont());
                Insets insets = getInsets();
                g2.drawString(placeholder, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }

    private static class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;

        private PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(new Color(155, 155, 155));
                g2.setFont(getFont());
                Insets insets = getInsets();
                g2.drawString(placeholder, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }

    static String toMD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
