package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;

public class MainFrame extends JFrame {
    private static final Color BRAND_RED = new Color(177, 48, 42);
    private static final Color TEXT_COLOR = new Color(55, 55, 55);
    private static final Color MUTED_COLOR = new Color(125, 125, 125);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));

    private final JLabel clockLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final DefaultTableModel invoiceTableModel;

    public MainFrame() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public MainFrame(String userName, String userRole) {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1440, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        MenuPanel menuPanel = new MenuPanel(userName, userRole);
        menuPanel.setShortcutAction(MenuPanel.Shortcut.LOGOUT, event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        add(menuPanel, BorderLayout.NORTH);

        invoiceTableModel = new DefaultTableModel(
                new Object[]{"Mã HĐ", "Khách hàng", "Tổng tiền", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JPanel dashboard = new JPanel(new GridBagLayout());
        dashboard.setBackground(new Color(242, 243, 245));
        dashboard.setBorder(new EmptyBorder(20, 22, 20, 22));

        GridBagConstraints column = new GridBagConstraints();
        column.gridy = 0;
        column.weighty = 1;
        column.fill = GridBagConstraints.BOTH;
        column.insets = new Insets(0, 0, 0, 10);

        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setOpaque(false);
        addWelcomeCard(leftColumn, userName, userRole);
        addStatistics(leftColumn);
        addVerticalSpacer(leftColumn, 2);
        column.gridx = 0;
        column.weightx = 1.04;
        dashboard.add(leftColumn, column);

        JPanel rightColumn = new JPanel(new GridBagLayout());
        rightColumn.setOpaque(false);
        addClockCard(rightColumn);
        addNotificationsCard(rightColumn);
        addRecentInvoicesCard(rightColumn);
        addVerticalSpacer(rightColumn, 3);
        column.gridx = 1;
        column.weightx = 1;
        column.insets = new Insets(0, 0, 0, 0);
        dashboard.add(rightColumn, column);

        add(dashboard, BorderLayout.CENTER);
        add(createStatusBar(userName, userRole), BorderLayout.SOUTH);
        updateClock();
        new Timer(1000, event -> updateClock()).start();
    }

    public DefaultTableModel getInvoiceTableModel() {
        return invoiceTableModel;
    }

    private void addWelcomeCard(JPanel parent, String userName, String userRole) {
        JPanel welcome = new JPanel(new BorderLayout());
        welcome.setBackground(new Color(194, 61, 54));
        welcome.setBorder(new EmptyBorder(20, 22, 18, 22));
        welcome.setPreferredSize(new Dimension(0, 174));

        JPanel information = new JPanel();
        information.setOpaque(false);
        information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("HỆ THỐNG QUẢN LÝ");
        eyebrow.setForeground(new Color(255, 222, 218));
        eyebrow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        information.add(eyebrow);

        JLabel brand = new JLabel("ĐIỆN MÁY ĐỎ");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        information.add(Box.createVerticalStrut(3));
        information.add(brand);

        JLabel greeting = new JLabel("Xin chào, " + userName + "!");
        greeting.setForeground(Color.WHITE);
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 14));
        information.add(Box.createVerticalStrut(6));
        information.add(greeting);

        JLabel role = new JLabel(roleDescription(userRole));
        role.setForeground(new Color(255, 230, 227));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        information.add(Box.createVerticalStrut(2));
        information.add(role);

        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        categories.setOpaque(false);
        for (String category : new String[]{"Tivi", "Điện thoại", "Laptop", "Gia dụng", "Điều hòa"}) {
            JLabel chip = new JLabel(category);
            chip.setForeground(Color.WHITE);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(232, 155, 148)),
                    new EmptyBorder(4, 9, 4, 9)));
            categories.add(chip);
        }

        welcome.add(information, BorderLayout.CENTER);
        welcome.add(categories, BorderLayout.SOUTH);
        addLeftCard(parent, welcome, 0, 0, 0);
    }

    private void addStatistics(JPanel parent) {
        String soHD = "--", doanhThu = "--", soKH = "--", soSP = "--";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement()) {
            ResultSet rsHD = stmt.executeQuery("SELECT COUNT(*), ISNULL(SUM(tongThanhToan), 0) FROM HoaDon");
            if (rsHD.next()) {
                soHD = String.valueOf(rsHD.getInt(1));
                doanhThu = String.format("%,.0f đ", rsHD.getDouble(2));
            }
            ResultSet rsKH = stmt.executeQuery("SELECT COUNT(*) FROM KhachHang");
            if (rsKH.next()) {
                soKH = String.valueOf(rsKH.getInt(1));
            }
            ResultSet rsSP = stmt.executeQuery("SELECT COUNT(*) FROM SanPham WHERE soLuongTon > 0");
            if (rsSP.next()) {
                soSP = String.valueOf(rsSP.getInt(1));
            }
        } catch (Exception ignored) {}

        JPanel statistics = new JPanel(new GridLayout(2, 2, 10, 10));
        statistics.setOpaque(false);
        statistics.add(createStatisticCard("Hóa đơn đã lập", soHD, "Dữ liệu CSDL", new Color(184, 62, 56)));
        statistics.add(createStatisticCard("Tổng doanh thu", doanhThu, "Dữ liệu CSDL", new Color(75, 130, 76)));
        statistics.add(createStatisticCard("Khách hàng", soKH, "Thành viên & VIP", new Color(62, 117, 190)));
        statistics.add(createStatisticCard("Sản phẩm trong kho", soSP, "Đang kinh doanh", new Color(211, 125, 52)));
        statistics.setPreferredSize(new Dimension(0, 150));
        addLeftCard(parent, statistics, 1, 10, 0);
    }

    private JPanel createStatisticCard(String title, String value, String detail, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(11, 12, 11, 12)));

        JLabel icon = new JLabel("●");
        icon.setForeground(accent);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel number = new JLabel(value);
        number.setForeground(TEXT_COLOR);
        number.setFont(new Font("Segoe UI", Font.BOLD, 21));
        JLabel name = new JLabel(title);
        name.setForeground(MUTED_COLOR);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel note = new JLabel(detail);
        note.setForeground(accent);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        text.add(number);
        text.add(name);
        text.add(Box.createVerticalStrut(3));
        text.add(note);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private void addClockCard(JPanel parent) {
        JPanel clockCard = new JPanel();
        clockCard.setBackground(Color.WHITE);
        clockCard.setLayout(new BoxLayout(clockCard, BoxLayout.Y_AXIS));
        clockCard.setBorder(new EmptyBorder(12, 10, 11, 10));
        clockCard.setPreferredSize(new Dimension(0, 68));

        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clockLabel.setForeground(BRAND_RED);
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 22));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setForeground(MUTED_COLOR);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clockCard.add(clockLabel);
        clockCard.add(Box.createVerticalStrut(2));
        clockCard.add(dateLabel);
        addRightCard(parent, clockCard, 0, 0);
    }

    private void addNotificationsCard(JPanel parent) {
        JPanel notifications = createSectionCard("Thông báo mới");
        JLabel emptyState = new JLabel("Hệ thống hoạt động bình thường — Đã kết nối CSDL SQL Server.");
        emptyState.setForeground(MUTED_COLOR);
        emptyState.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emptyState.setBorder(new EmptyBorder(14, 10, 14, 10));
        notifications.add(emptyState, BorderLayout.CENTER);
        notifications.setPreferredSize(new Dimension(0, 105));
        addRightCard(parent, notifications, 1, 10);
    }

    private void addRecentInvoicesCard(JPanel parent) {
        JPanel section = createSectionCard("Hóa đơn gần đây");

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TOP 10 hd.maHoaDon, ISNULL(kh.hoTen, N'Khách vãng lai'), hd.tongThanhToan, hd.trangThai FROM HoaDon hd LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang ORDER BY hd.ngayLapHoaDon DESC")) {
            while (rs.next()) {
                invoiceTableModel.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        String.format("%,.0f đ", rs.getDouble(3)),
                        rs.getString(4)
                });
            }
        } catch (Exception ignored) {}

        JTable invoiceTable = new JTable(invoiceTableModel) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) graphics.create();
                    g2.setColor(MUTED_COLOR);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    String emptyText = "Chưa có dữ liệu hóa đơn";
                    FontMetrics metrics = g2.getFontMetrics();
                    int x = (getWidth() - metrics.stringWidth(emptyText)) / 2;
                    int y = getHeight() / 2 + metrics.getAscent() / 2;
                    g2.drawString(emptyText, x, y);
                    g2.dispose();
                }
            }
        };
        invoiceTable.setRowHeight(34);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceTable.setForeground(TEXT_COLOR);
        invoiceTable.setGridColor(new Color(232, 232, 232));
        invoiceTable.setShowVerticalLines(true);
        invoiceTable.setShowHorizontalLines(true);
        invoiceTable.setFillsViewportHeight(true);
        invoiceTable.setSelectionBackground(new Color(250, 232, 230));
        invoiceTable.setSelectionForeground(TEXT_COLOR);
        invoiceTable.getTableHeader().setReorderingAllowed(false);
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        invoiceTable.getTableHeader().setForeground(Color.WHITE);
        invoiceTable.getTableHeader().setBackground(BRAND_RED);
        invoiceTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        invoiceTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        section.add(scrollPane, BorderLayout.CENTER);
        section.setPreferredSize(new Dimension(0, 260));
        addRightCard(parent, section, 2, 10);
    }

    private JPanel createSectionCard(String title) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel(title);
        heading.setForeground(BRAND_RED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        section.add(heading, BorderLayout.NORTH);
        return section;
    }

    private void addLeftCard(JPanel parent, Component component, int row, int topInset, double weightY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.weighty = weightY;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(topInset, 0, 0, 0);
        parent.add(component, constraints);
    }

    private void addRightCard(JPanel parent, Component component, int row, int topInset) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(topInset, 0, 0, 0);
        parent.add(component, constraints);
    }

    private void addVerticalSpacer(JPanel parent, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        parent.add(Box.createGlue(), constraints);
    }

    private JPanel createStatusBar(String userName, String userRole) {
        JPanel statusBar = new JPanel(new BorderLayout(12, 0));
        statusBar.setBackground(new Color(238, 238, 238));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(205, 205, 205)),
                new EmptyBorder(5, 12, 5, 12)));

        JLabel userStatus = new JLabel("● " + userName + "   |   " + userRole + "   |   Trực tuyến");
        userStatus.setForeground(new Color(75, 120, 75));
        userStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel version = new JLabel("Điện Máy Đỏ  |  v2.0");
        version.setForeground(MUTED_COLOR);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(userStatus, BorderLayout.WEST);
        statusBar.add(version, BorderLayout.EAST);
        return statusBar;
    }

    private String roleDescription(String role) {
        return "Vai trò: " + role;
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        clockLabel.setText(now.format(TIME_FORMAT));
        dateLabel.setText(now.format(DATE_FORMAT));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
