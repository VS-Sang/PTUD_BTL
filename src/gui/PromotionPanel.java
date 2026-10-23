package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class PromotionPanel extends JFrame {

    private static final Color RED_PRIMARY = new Color(200, 35, 45);
    private static final Color RED_DARK = new Color(180, 30, 30);
    private static final Color BLUE_BUTTON = new Color(24, 119, 242);
    private static final Color BG_GRAY = new Color(242, 242, 242);
    private static final Color CARD_BORDER = new Color(220, 220, 220);

    private MenuPanel menuPanel;
    private JPanel cardsContainer;

    public PromotionPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();

        menuPanel.setShortcutAction(MenuPanel.Shortcut.HOME, e -> {
            new MainFrame().setVisible(true);
            dispose();
        });
        menuPanel.setShortcutAction(MenuPanel.Shortcut.LOGOUT, e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        menuPanel.setMenuItemAction(MenuPanel.MenuItem.HOME, e -> {
            new MainFrame().setVisible(true);
            dispose();
        });
        menuPanel.setMenuItemAction(MenuPanel.MenuItem.LOGOUT, e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(menuPanel, BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(230, 230, 230));
        pSubHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(8, 15, 8, 15)));

        JLabel lblSubTitle = new JLabel("🏷️ Quản lý Khuyến Mãi");
        lblSubTitle.setForeground(RED_DARK);
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnCreate = new JButton("+ Tạo khuyến mãi");
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setBackground(RED_PRIMARY);
        btnCreate.setFocusPainted(false);
        btnCreate.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.addActionListener(e -> {
            JFrame createFrame = new JFrame("Tạo khuyến mãi");
            createFrame.setSize(1200, 650);
            createFrame.setLocationRelativeTo(null);
            createFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            createFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            createFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            createPromtionPanel cpp = new createPromtionPanel(createFrame);
            createFrame.setContentPane(cpp);
            createFrame.setVisible(true);
            dispose();
        });
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);
        pSubHeader.add(btnCreate, BorderLayout.EAST);
        pCenterContainer.add(pSubHeader, BorderLayout.NORTH);

        cardsContainer = new JPanel(new GridLayout(0, 2, 15, 15));
        cardsContainer.setBackground(BG_GRAY);
        cardsContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        loadSamplePromotions();

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);
    }

    private JPanel createPromotionCard(
            String badgeText, Color badgeBg, Color badgeFg,
            String title, String description,
            String startDateStr, String endDateStr,
            String statusText, Color statusBg, Color statusFg,
            boolean isExpired) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblBadge = new JLabel(badgeText);
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(badgeFg);
        lblBadge.setBackground(badgeBg);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        lblBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(isExpired ? Color.GRAY : Color.BLACK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(isExpired ? new Color(160, 160, 160) : new Color(110, 110, 110));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTime = new JLabel("⏰ " + startDateStr + " – " + endDateStr);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setForeground(isExpired ? Color.GRAY : RED_PRIMARY);
        lblTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pFooter.setOpaque(false);
        pFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStatus = new JLabel(statusText);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusFg);
        lblStatus.setBackground(statusBg);
        lblStatus.setOpaque(true);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        pFooter.add(lblStatus);

        if (!isExpired) {
            JButton btnView = new JButton("👁 Xem");
            btnView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnView.setForeground(Color.WHITE);
            btnView.setBackground(BLUE_BUTTON);
            btnView.setFocusPainted(false);
            btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JButton btnDelete = new JButton("🗑");
            btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBackground(RED_PRIMARY);
            btnDelete.setFocusPainted(false);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

            pFooter.add(btnView);
            pFooter.add(btnDelete);
        }

        card.add(lblBadge);
        card.add(Box.createVerticalStrut(8));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTime);
        card.add(Box.createVerticalStrut(12));
        card.add(pFooter);

        return card;
    }

    private void loadSamplePromotions() {
        // Black Friday 2026
        cardsContainer.add(createPromotionCard(
                "% Giảm giá", new Color(255, 243, 224), new Color(230, 81, 0),
                "Black Friday 2026", "Giảm 25% tất cả điện thoại",
                "25/11/2026", "30/11/2026",
                "Sắp diễn ra", new Color(232, 245, 233), new Color(46, 125, 50),
                false));

        // Chào mừng 20/10
        cardsContainer.add(createPromotionCard(
                "Mã giảm giá", new Color(243, 229, 245), new Color(123, 31, 162),
                "Chào mừng 20/10", "Mã: CHAOMNV — Giảm 500.000đ cho đơn từ 5 triệu",
                "18/10/2026", "22/10/2026",
                "Đang diễn ra", new Color(227, 242, 253), new Color(21, 101, 192),
                false));

        // Mua Laptop tặng Tai nghe
        cardsContainer.add(createPromotionCard(
                "Tặng quà", new Color(232, 245, 233), new Color(46, 125, 50),
                "Mua Laptop tặng Tai nghe", "Mua laptop từ 20 triệu tặng tai nghe Sony",
                "01/10/2026", "31/10/2026",
                "Đang diễn ra", new Color(227, 242, 253), new Color(21, 101, 192),
                false));

        // Sale Quốc Khánh 2/9 (Đã kết thúc)
        cardsContainer.add(createPromotionCard(
                "% Giảm giá", new Color(245, 245, 245), Color.GRAY,
                "Sale Quốc Khánh 2/9", "Giảm 10% điện gia dụng",
                "01/09/2026", "05/09/2026",
                "Đã kết thúc", new Color(238, 238, 238), Color.GRAY,
                true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PromotionPanel().setVisible(true));
    }
}