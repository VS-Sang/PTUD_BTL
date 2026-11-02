package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.EnumMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import DAO.AuditTrail_DAO;
import DAO.VaiTroQuyen_DAO;

public class MenuPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(180, 40, 40);
    private static final Color MENU_COLOR = new Color(240, 240, 240);

    public enum MenuItem {
        HOME,
        PERSONAL_INFO,
        CHANGE_PASSWORD,
        LOGOUT,
        INVOICE,
        CUSTOMER,
        EMPLOYEE,
        PRODUCT,
        PROMOTION,

        INVOICING,
        IMPORT_GOODS,
        RETURN,
        WORK_SCHEDULE,
        ASSIGNMENT,

        MONTHLY_REVENUE,
        BEST_SELLERS,
        SUMMARY_REPORT,

        LIST_ACCOUNT,
        AUDIT_TRAIL
    }

    public enum Shortcut {
        HOME("", "/img/home.png", "Trang chủ"),
        CALCULATOR("🛒", null, "Lập hoá đơn bán hàng"),
        REPORTS("📊", null, "Doanh thu theo tháng"),
        LOGOUT("🚪", null, "Đăng xuất");

        public final String label;
        public final String iconPath;
        public final String tooltip;

        Shortcut(String label, String iconPath,String tooltip) {
            this.label = label;
            this.iconPath = iconPath;
            this.tooltip = tooltip;
        }
    }

    private final Map<MenuItem, JMenuItem> menuItemMap = new EnumMap<>(MenuItem.class);
    private final Map<Shortcut, JButton> shortcutButtons = new EnumMap<>(Shortcut.class);
    private final Set<String> rolePermissions;
    private String currentUserName = "Nguyễn Văn An";
    private String currentUserRole = "Quản lý";
    private String currentUsername = "admin";
    private JMenuItem mnuDoanhThu;
    private JMenuItem mnuBanChay;
    private JMenuItem mnuTongHop;

    public MenuPanel() {
        this(LoginFrame.getCurrentFullName(), LoginFrame.getCurrentRole(),
                LoginFrame.getCurrentUsername());
    }

    public MenuPanel(String userName, String userRole) {
        this(userName, userRole, LoginFrame.getCurrentUsername());
    }

    public MenuPanel(String userName, String userRole, String username) {
        super(new BorderLayout());
        this.currentUserName = (userName != null && !userName.trim().isEmpty()) ? userName : "Chưa đăng nhập";
        this.currentUserRole = (userRole != null && !userRole.trim().isEmpty()) ? userRole.trim() : "";
        this.currentUsername = username == null ? "" : username.trim();
        this.rolePermissions = loadRolePermissions(this.currentUserRole);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 35));

        JLabel title = new JLabel("  ⚡ Điện Máy Đỏ — Hệ Thống Quản Lý");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.add(title, BorderLayout.WEST);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        userInfo.setOpaque(false);
        JLabel userLabel = new JLabel("👤 " + userName);
        userLabel.setForeground(Color.WHITE);
        JButton roleButton = new JButton(userRole);
        roleButton.setMargin(new Insets(2, 5, 2, 5));
        roleButton.setFocusable(false);
        roleButton.setForeground(HEADER_COLOR);
        roleButton.setBackground(Color.WHITE);
        roleButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 210)),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)));
        userInfo.add(userLabel);
        userInfo.add(roleButton);
        header.add(userInfo, BorderLayout.EAST);

        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(MENU_COLOR);
        navigation.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel menuLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        menuLeft.setOpaque(false);

        JMenuBar menuBar = new JMenuBar();

        JMenu sysMenu = new JMenu("⚙ Hệ thống ▾");
        addItem(sysMenu, MenuItem.HOME, "Trang chủ");
        addItem(sysMenu, MenuItem.PERSONAL_INFO, "Thông tin cá nhân");
        addItem(sysMenu, MenuItem.CHANGE_PASSWORD, "Đổi mật khẩu");
        addItem(sysMenu, MenuItem.LOGOUT, "Đăng xuất");

        JMenu categoryMenu = new JMenu("📁 Danh mục ▾");
        addAuthorizedItem(categoryMenu, MenuItem.INVOICE, "Hoá đơn", "Q007");
        addAuthorizedItem(categoryMenu, MenuItem.CUSTOMER, "Khách hàng", "Q005");
        addAuthorizedItem(categoryMenu, MenuItem.EMPLOYEE, "Nhân viên", "Q008");
        addAuthorizedItem(categoryMenu, MenuItem.WORK_SCHEDULE, "Ca làm việc", "Q009");
        addAuthorizedItem(categoryMenu, MenuItem.PRODUCT, "Sản phẩm", "Q001");
        addAuthorizedItem(categoryMenu, MenuItem.PROMOTION, "Khuyến mãi", "Q003");

        JMenu processMenu = new JMenu("📋 Xử lý ▾");
        addAuthorizedItem(processMenu, MenuItem.INVOICING, "Lập hoá đơn bán hàng", "Q006");
        addAuthorizedItem(processMenu, MenuItem.IMPORT_GOODS, "Nhập hàng vào kho", "Q004");
        addAuthorizedItem(processMenu, MenuItem.RETURN, "Xử lý trả hàng", "Q004");
        addAuthorizedItem(processMenu, MenuItem.ASSIGNMENT, "Phân công ca làm", "Q009");

        JMenu statsMenu = new JMenu("📊 Thống kê ▾");
        addAuthorizedItem(statsMenu, MenuItem.MONTHLY_REVENUE, "Doanh thu theo tháng", "Q011");
        addAuthorizedItem(statsMenu, MenuItem.BEST_SELLERS, "Sản phẩm bán chạy", "Q011");
        addAuthorizedItem(statsMenu, MenuItem.SUMMARY_REPORT, "Báo cáo tổng hợp", "Q011");

        JMenu accountMenu = new JMenu("👤 Tài khoản ▾");
        addAuthorizedItem(accountMenu, MenuItem.LIST_ACCOUNT, "Danh sách tài khoản", "Q010");
        addAuthorizedItem(accountMenu, MenuItem.AUDIT_TRAIL, "Nhật ký Audit Trail", "Q013");

        menuBar.add(sysMenu);
        addMenuIfNotEmpty(menuBar, categoryMenu);
        addMenuIfNotEmpty(menuBar, processMenu);
        addMenuIfNotEmpty(menuBar, statsMenu);
        addMenuIfNotEmpty(menuBar, accountMenu);
        menuLeft.add(menuBar);

        JPanel shortcuts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
        shortcuts.setOpaque(false);
        for (Shortcut sc : Shortcut.values()) {
            JButton btn = buildShortcutButton(sc);
            shortcutButtons.put(sc, btn);
            shortcuts.add(btn);
        }

        navigation.add(menuLeft, BorderLayout.WEST);
        navigation.add(shortcuts, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(navigation, BorderLayout.CENTER);

        // Thiết lập menu điều hướng mặc định
        caiDatDieuHuongMacDinh();
    }

    private void caiDatDieuHuongMacDinh() {
        // Hệ thống
        setMenuItemAction(MenuItem.HOME, e -> {
            dongCuaSoHienTai();
            new MainFrame(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });
        setMenuItemAction(MenuItem.LOGOUT, e -> {
            ghiNhatKyDangXuat();
            dongCuaSoHienTai();
            LoginFrame.clearCurrentUsername();
            new LoginFrame().setVisible(true);
        });
        setMenuItemAction(MenuItem.PERSONAL_INFO, e ->
                new ProfileDialog(SwingUtilities.getWindowAncestor(this),
                        currentUserName, currentUserRole, currentUsername).setVisible(true));
        setMenuItemAction(MenuItem.CHANGE_PASSWORD, e ->
                new ProfileDialog(SwingUtilities.getWindowAncestor(this),
                        currentUserName, currentUserRole, currentUsername).setVisible(true));
        setMenuItemAction(MenuItem.LIST_ACCOUNT, e -> {
            dongCuaSoHienTai();
            new AccountPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });
        setMenuItemAction(MenuItem.AUDIT_TRAIL, e -> {
            dongCuaSoHienTai();
            new AuditTrailPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });

        // Danh mục
        setMenuItemAction(MenuItem.INVOICE, e -> {
            dongCuaSoHienTai();
            new InvoicePanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.CUSTOMER, e -> {
            dongCuaSoHienTai();
            new CustomerPanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.EMPLOYEE, e -> {
            dongCuaSoHienTai();
            new EmployeePanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });
        setMenuItemAction(MenuItem.PRODUCT, e -> {
            dongCuaSoHienTai();
            new ProductPanel().setVisible(true);
        });
        setMenuItemAction(MenuItem.PROMOTION, e -> {
            dongCuaSoHienTai();
            new PromotionPanel().setVisible(true);
        });

        // Xử lý
        setMenuItemAction(MenuItem.INVOICING, e -> {
            dongCuaSoHienTai();
            new CreateInvoiceDialog(null, currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.IMPORT_GOODS, e -> {
            dongCuaSoHienTai();
            new ImportGoodsPanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.RETURN, e -> {
            dongCuaSoHienTai();
            new ReturnGoodsPanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.WORK_SCHEDULE, e -> {
            dongCuaSoHienTai();
            new WorkSchedulePanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });
        setMenuItemAction(MenuItem.ASSIGNMENT, e -> {
            dongCuaSoHienTai();
            new AssignmentPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        });

        // Thống kê
        mnuDoanhThu = menuItemMap.get(MenuItem.MONTHLY_REVENUE);
        mnuBanChay = menuItemMap.get(MenuItem.BEST_SELLERS);
        mnuTongHop = menuItemMap.get(MenuItem.SUMMARY_REPORT);

        setMenuItemAction(MenuItem.MONTHLY_REVENUE, e -> {
            dongCuaSoHienTai();
            new MonthlyRevenuePanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.BEST_SELLERS, e -> {
            dongCuaSoHienTai();
            new TopProductsPanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.SUMMARY_REPORT, e -> {
            dongCuaSoHienTai();
            new GeneralReportPanel(currentUserName, currentUserRole).setVisible(true);
        });

        // Shortcut Home
        JButton btnHome = shortcutButtons.get(Shortcut.HOME);
        if (btnHome != null) {
            btnHome.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame(currentUserName, currentUserRole, currentUsername).setVisible(true);
            });
        }

        JButton btnShortcutInvoice = shortcutButtons.get(Shortcut.CALCULATOR);
        if (btnShortcutInvoice != null) {
            btnShortcutInvoice.addActionListener(e -> {
                dongCuaSoHienTai();
                new CreateInvoiceDialog(null, currentUserName, currentUserRole).setVisible(true);
            });
        }

        // Shortcut Reports
        JButton btnShortcutReports = shortcutButtons.get(Shortcut.REPORTS);
        if (btnShortcutReports != null) {
            btnShortcutReports.addActionListener(e -> {
                if (coQuyen("Q011")) {
                    dongCuaSoHienTai();
                    new MonthlyRevenuePanel(currentUserName, currentUserRole).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Tài khoản hiện tại không có quyền xem báo cáo thống kê.",
                            "Phân quyền hệ thống", JOptionPane.WARNING_MESSAGE);
                }
            });
        }

        // Shortcut Logout
        JButton btnLogout = shortcutButtons.get(Shortcut.LOGOUT);
        if (btnLogout != null) {
            btnLogout.addActionListener(e -> {
                ghiNhatKyDangXuat();
                dongCuaSoHienTai();
                LoginFrame.clearCurrentUsername();
                new LoginFrame().setVisible(true);
            });
        }
    }

    private void dongCuaSoHienTai() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private boolean coQuyen(String maQuyen) {
        return rolePermissions.contains(maQuyen);
    }

    private Set<String> loadRolePermissions(String role) {
        try {
            return VaiTroQuyen_DAO.layMaQuyenTheoVaiTro(role);
        } catch (java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải quyền theo vai trò:\n" + exception.getMessage(),
                    "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
            return Collections.emptySet();
        }
    }

    private void ghiNhatKyDangXuat() {
        try {
            AuditTrail_DAO.ghiDangXuat(currentUsername);
        } catch (java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "Không thể ghi Audit Trail khi đăng xuất:\n" + exception.getMessage(),
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addItem(JMenu menu, MenuItem key, String label) {
        JMenuItem item = new JMenuItem(label);
        menuItemMap.put(key, item);
        menu.add(item);
    }

    private void addAuthorizedItem(JMenu menu, MenuItem key, String label, String permission) {
        if (coQuyen(permission)) {
            addItem(menu, key, label);
        }
    }

    private void addMenuIfNotEmpty(JMenuBar menuBar, JMenu menu) {
        if (menu.getItemCount() > 0) {
            menuBar.add(menu);
        }
    }

    private JButton buildShortcutButton(Shortcut sc) {
        JButton btn = new JButton();
        btn.setToolTipText(sc.tooltip);
        if (sc.iconPath != null) {
            try {
                URL iconUrl = getClass().getResource(sc.iconPath);
                if (iconUrl != null) {
                    ImageIcon icon = new ImageIcon(iconUrl);
                    Image img = icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
                    btn.setIcon(new ImageIcon(img));
                } else {
                    btn.setText(sc.label);
                }
            } catch (Exception ex) {
                btn.setText(sc.label);
            }
        } else {
            btn.setText(sc.label);
        }
        return btn;
    }

    public void setMenuItemAction(MenuItem item, ActionListener action) {
        JMenuItem b = menuItemMap.get(item);
        if (b != null) {
            for (ActionListener al : b.getActionListeners()) {
                b.removeActionListener(al);
            }
            b.addActionListener(action);
        }
    }

    public void setShortcutAction(Shortcut shortcut, ActionListener action) {
        JButton b = shortcutButtons.get(shortcut);
        if (b != null) {
            for (ActionListener al : b.getActionListeners()) {
                b.removeActionListener(al);
            }
            b.addActionListener(action);
        }
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public JMenuItem getMnuDoanhThu() {
        return mnuDoanhThu;
    }

    public JMenuItem getMnuBanChay() {
        return mnuBanChay;
    }

    public JMenuItem getMnuTongHop() {
        return mnuTongHop;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Điện Máy Đỏ — MenuPanel Preview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 100);
            frame.setLocationRelativeTo(null);
            MenuPanel mp = new MenuPanel();
            mp.setShortcutAction(Shortcut.LOGOUT, e -> System.exit(0));
            frame.setContentPane(mp);
            frame.setVisible(true);
        });
    }
}
