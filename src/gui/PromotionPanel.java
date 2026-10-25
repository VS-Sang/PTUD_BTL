package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import DAO.KhuyenMai_DAO;
import entity.KhuyenMai;

public class PromotionPanel extends JFrame {

    private static final Color RED_PRIMARY = new Color(200, 35, 45);
    private static final Color RED_DARK = new Color(180, 30, 30);
    private static final Color BLUE_BUTTON = new Color(24, 119, 242);
    private static final Color BG_GRAY = new Color(242, 242, 242);
    private static final Color CARD_BORDER = new Color(220, 220, 220);

    private MenuPanel menuPanel;
    private JPanel cardsContainer;
    private KhuyenMai_DAO khuyenMaiDAO;

    public PromotionPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        khuyenMaiDAO = new KhuyenMai_DAO();

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

        // Load dữ liệu từ database
        loadPromotionsFromDatabase();

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);
    }

    /**
     * Load danh sách khuyến mãi từ database và hiển thị lên giao diện.
     */
    private void loadPromotionsFromDatabase() {
        cardsContainer.removeAll();
        List<KhuyenMai> danhSach = khuyenMaiDAO.getAllKhuyenMai();

        if (danhSach.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có khuyến mãi nào. Hãy tạo mới!", JLabel.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblEmpty.setForeground(Color.GRAY);
            cardsContainer.setLayout(new BorderLayout());
            cardsContainer.add(lblEmpty, BorderLayout.CENTER);
        } else {
            cardsContainer.setLayout(new GridLayout(0, 2, 15, 15));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date now = new Date();

            for (KhuyenMai km : danhSach) {
                // Xác định badge
                String badgeText;
                Color badgeBg, badgeFg;
                String loai = km.getLoaiKhuyenMai();
                if ("Phần trăm".equalsIgnoreCase(loai)) {
                    badgeText = "% Giảm giá";
                    badgeBg = new Color(255, 243, 224);
                    badgeFg = new Color(230, 81, 0);
                } else if ("Số tiền".equalsIgnoreCase(loai)) {
                    badgeText = "Giảm tiền trực tiếp";
                    badgeBg = new Color(243, 229, 245);
                    badgeFg = new Color(123, 31, 162);
                } else if ("Mua X tặng Y".equalsIgnoreCase(loai)) {
                    badgeText = "Tặng quà";
                    badgeBg = new Color(232, 245, 233);
                    badgeFg = new Color(46, 125, 50);
                } else {
                    badgeText = loai != null ? loai : "Khuyến mãi";
                    badgeBg = new Color(227, 242, 253);
                    badgeFg = new Color(21, 101, 192);
                }

                // Xác định trạng thái hiển thị
                String trangThai = km.getTrangThai();
                String statusText;
                Color statusBg, statusFg;
                boolean isExpired;

                if ("Đã kết thúc".equals(trangThai) || "Đã hủy".equals(trangThai)) {
                    statusText = trangThai;
                    statusBg = new Color(238, 238, 238);
                    statusFg = Color.GRAY;
                    isExpired = true;
                } else if ("Đang diễn ra".equals(trangThai)) {
                    statusText = "Đang diễn ra";
                    statusBg = new Color(227, 242, 253);
                    statusFg = new Color(21, 101, 192);
                    isExpired = false;
                } else {
                    // Chưa bắt đầu
                    statusText = "Sắp diễn ra";
                    statusBg = new Color(232, 245, 233);
                    statusFg = new Color(46, 125, 50);
                    isExpired = false;
                }

                String title = km.getTenKhuyenMai();
                String description = km.getMoTa() != null ? km.getMoTa() : "";
                String startDate = km.getNgayBatDau() != null ? sdf.format(km.getNgayBatDau()) : "";
                String endDate = km.getNgayKetThuc() != null ? sdf.format(km.getNgayKetThuc()) : "";

                cardsContainer.add(createPromotionCard(
                        badgeText, badgeBg, badgeFg,
                        title, description,
                        startDate, endDate,
                        statusText, statusBg, statusFg,
                        isExpired, km));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createPromotionCard(
            String badgeText, Color badgeBg, Color badgeFg,
            String title, String description,
            String startDateStr, String endDateStr,
            String statusText, Color statusBg, Color statusFg,
            boolean isExpired, KhuyenMai km) {

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

        JLabel lblDesc = new JLabel("<html><body style='width:250px'>" + description + "</body></html>");
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

        // Nút Xem – luôn hiển thị để xem chi tiết
        JButton btnView = new JButton("👁 Xem");
        btnView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnView.setForeground(Color.WHITE);
        btnView.setBackground(BLUE_BUTTON);
        btnView.setFocusPainted(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.addActionListener(e -> showPromotionDetail(km));
        pFooter.add(btnView);

        if (!isExpired) {
            JButton btnDelete = new JButton("🗑 Xóa");
            btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBackground(RED_PRIMARY);
            btnDelete.setFocusPainted(false);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.addActionListener(e -> deletePromotion(km));
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

    /**
     * Hiển thị dialog chi tiết khuyến mãi.
     */
    private void showPromotionDetail(KhuyenMai km) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat nf = new DecimalFormat("#,###");

        JDialog dialog = new JDialog(this, "Chi tiết khuyến mãi", true);
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(RED_PRIMARY);
        JLabel lblHeader = new JLabel("🎁 Chi tiết khuyến mãi");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        addDetailRow(contentPanel, "Mã khuyến mãi:", km.getMaKhuyenMai());
        addDetailRow(contentPanel, "Tên chương trình:", km.getTenKhuyenMai());
        addDetailRow(contentPanel, "Loại khuyến mãi:", km.getLoaiKhuyenMai());

        // Giá trị giảm
        String giaTriStr;
        if ("Phần trăm".equalsIgnoreCase(km.getLoaiKhuyenMai())) {
            giaTriStr = nf.format(km.getGiaTriGiam()) + "%";
            if (km.getGiaTriGiamToiDa() > 0) {
                giaTriStr += " (tối đa " + nf.format(km.getGiaTriGiamToiDa()) + " VNĐ)";
            }
        } else {
            giaTriStr = nf.format(km.getGiaTriGiam()) + " VNĐ";
        }
        addDetailRow(contentPanel, "Giá trị giảm:", giaTriStr);

        addDetailRow(contentPanel, "Đơn hàng tối thiểu:", nf.format(km.getDonHangToiThieu()) + " VNĐ");

        if (km.getSoLuongToiDa() > 0) {
            addDetailRow(contentPanel, "Số lượng tối đa:", String.valueOf(km.getSoLuongToiDa()));
        }
        addDetailRow(contentPanel, "Đã sử dụng:", String.valueOf(km.getSoLuongDaSuDung()));

        addDetailRow(contentPanel, "Ngày bắt đầu:",
                km.getNgayBatDau() != null ? sdf.format(km.getNgayBatDau()) : "N/A");
        addDetailRow(contentPanel, "Ngày kết thúc:",
                km.getNgayKetThuc() != null ? sdf.format(km.getNgayKetThuc()) : "N/A");

        addDetailRow(contentPanel, "Áp dụng cho tất cả:", km.isApDungChoTatCa() ? "Có" : "Không");
        addDetailRow(contentPanel, "Trạng thái:", km.getTrangThai());

        addDetailRow(contentPanel, "Mô tả:", km.getMoTa() != null ? km.getMoTa() : "Không có");

        if (km.getNgayTao() != null) {
            addDetailRow(contentPanel, "Ngày tạo:", sdf.format(km.getNgayTao()));
        }
        if (km.getNgayCapNhat() != null) {
            addDetailRow(contentPanel, "Cập nhật lần cuối:", sdf.format(km.getNgayCapNhat()));
        }

        JScrollPane scrollContent = new JScrollPane(contentPanel);
        scrollContent.setBorder(null);
        scrollContent.getVerticalScrollBar().setUnitIncrement(12);
        dialog.add(scrollContent, BorderLayout.CENTER);

        // Footer - Close button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnClose.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnClose);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Thêm một dòng thông tin chi tiết vào panel.
     */
    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                new EmptyBorder(8, 0, 8, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(new Color(80, 80, 80));
        lblLabel.setPreferredSize(new Dimension(160, 25));

        JLabel lblValue = new JLabel("<html>" + value + "</html>");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValue.setForeground(new Color(40, 40, 40));

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.CENTER);

        parent.add(row);
    }

    /**
     * Xóa khuyến mãi với xác nhận.
     */
    private void deletePromotion(KhuyenMai km) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khuyến mãi:\n\"" + km.getTenKhuyenMai() + "\" (" + km.getMaKhuyenMai() + ")?",
                "Xác nhận xóa khuyến mãi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean result = khuyenMaiDAO.xoaKhuyenMai(km.getMaKhuyenMai());
            if (result) {
                JOptionPane.showMessageDialog(this,
                        "Đã xóa khuyến mãi \"" + km.getTenKhuyenMai() + "\" thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadPromotionsFromDatabase(); // Tải lại danh sách
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa khuyến mãi. Có thể khuyến mãi đang được sử dụng trong đơn hàng.",
                        "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PromotionPanel().setVisible(true));
    }
}