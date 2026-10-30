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
import java.util.Map;

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

        FIND_PRODUCT,
        FIND_CUSTOMER,
        INVOICE_SEARCH,

        MONTHLY_REVENUE,
        BEST_SELLERS,
        SUMMARY_REPORT,

        LIST_ACCOUNT,
        AUTHORIZATION
    }

    public enum Shortcut {
        HOME("Trang chủ","/img/lo.png", "Trang chủ"),
        CALCULATOR("🖩", null,"Tạo hóa đơn"),
        REPORTS("📊", null,"Thống kê theo tháng"),
        LOGOUT("Đăng xuất", "/img/lo.png","Đăng xuất");

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
    private String currentUserName = "Nguyễn Văn An";
    private String currentUserRole = "Quản lý";
    private JMenuItem mnuDoanhThu;
    private JMenuItem mnuBanChay;
    private JMenuItem mnuTongHop;

    public MenuPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public MenuPanel(String userName, String userRole) {
        super(new BorderLayout());
        this.currentUserName = (userName != null && !userName.trim().isEmpty()) ? userName : "Nguyễn Văn An";
        this.currentUserRole = (userRole != null && !userRole.trim().isEmpty()) ? userRole : "Quản lý";

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
        addItem(categoryMenu, MenuItem.INVOICE, "Hoá đơn");
        addItem(categoryMenu, MenuItem.CUSTOMER, "Khách hàng");
        addItem(categoryMenu, MenuItem.EMPLOYEE, "Nhân viên");
        addItem(categoryMenu, MenuItem.WORK_SCHEDULE, "Ca làm việc");
        addItem(categoryMenu, MenuItem.PRODUCT, "Sản phẩm");
        addItem(categoryMenu, MenuItem.PROMOTION, "Khuyến mãi");

        JMenu processMenu = new JMenu("📋 Xử lý ▾");
        addItem(processMenu, MenuItem.INVOICING, "Lập hoá đơn bán hàng");
        addItem(processMenu, MenuItem.IMPORT_GOODS, "Nhập hàng vào kho");
        addItem(processMenu, MenuItem.RETURN, "Xử lý trả hàng");
        addItem(processMenu, MenuItem.ASSIGNMENT, "Phân công ca làm");

        JMenu findMenu = new JMenu("🔍 Tìm kiếm ▾");
        addItem(findMenu, MenuItem.FIND_PRODUCT, "Tìm sản phẩm");
        addItem(findMenu, MenuItem.FIND_CUSTOMER, "Tìm kiếm khách hàng");
        addItem(findMenu, MenuItem.INVOICE_SEARCH, "Tra cứu hoá đơn");

        JMenu statsMenu = new JMenu("📊 Thống kê ▾");
        addItem(statsMenu, MenuItem.MONTHLY_REVENUE, "Doanh thu theo tháng");
        addItem(statsMenu, MenuItem.BEST_SELLERS, "Sản phẩm bán chạy");
        addItem(statsMenu, MenuItem.SUMMARY_REPORT, "Báo cáo tổng hợp");

        JMenu accountMenu = new JMenu("👤 Tài khoản ▾");
        addItem(accountMenu, MenuItem.LIST_ACCOUNT, "Danh sách tài khoản");
        addItem(accountMenu, MenuItem.AUTHORIZATION, "Phân quyền hệ thống");

        menuBar.add(sysMenu);
        menuBar.add(categoryMenu);
        menuBar.add(processMenu);
        menuBar.add(findMenu);
        menuBar.add(statsMenu);
        menuBar.add(accountMenu);
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
            new MainFrame(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.LOGOUT, e -> {
            dongCuaSoHienTai();
            new LoginFrame().setVisible(true);
        });
        setMenuItemAction(MenuItem.PERSONAL_INFO, e ->
                new ProfileDialog(SwingUtilities.getWindowAncestor(this),
                        currentUserName, currentUserRole).setVisible(true));
        setMenuItemAction(MenuItem.CHANGE_PASSWORD, e ->
                new ProfileDialog(SwingUtilities.getWindowAncestor(this),
                        currentUserName, currentUserRole).setVisible(true));
        setMenuItemAction(MenuItem.LIST_ACCOUNT, e -> {
            dongCuaSoHienTai();
            new AccountPanel().setVisible(true);
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
            new EmployeePanel().setVisible(true);
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
            new WorkSchedulePanel().setVisible(true);
        });
        setMenuItemAction(MenuItem.ASSIGNMENT, e -> {
            dongCuaSoHienTai();
            new AssignmentPanel().setVisible(true);
        });

        // Tìm kiếm
        setMenuItemAction(MenuItem.FIND_PRODUCT, e -> {
            dongCuaSoHienTai();
            new ProductPanel().setVisible(true);
        });
        setMenuItemAction(MenuItem.FIND_CUSTOMER, e -> {
            dongCuaSoHienTai();
            new CustomerPanel(currentUserName, currentUserRole).setVisible(true);
        });
        setMenuItemAction(MenuItem.INVOICE_SEARCH, e -> {
            dongCuaSoHienTai();
            new InvoicePanel(currentUserName, currentUserRole).setVisible(true);
        });

        // Thống kê
        mnuDoanhThu = menuItemMap.get(MenuItem.MONTHLY_REVENUE);
        mnuBanChay = menuItemMap.get(MenuItem.BEST_SELLERS);
        mnuTongHop = menuItemMap.get(MenuItem.SUMMARY_REPORT);

        boolean isQuanLy = currentUserRole != null && (currentUserRole.equalsIgnoreCase("Quản lý") ||
                currentUserRole.equalsIgnoreCase("Admin") ||
                currentUserRole.equalsIgnoreCase("Quan Ly") ||
                currentUserRole.toLowerCase().contains("quản lý") ||
                currentUserRole.toLowerCase().contains("quan ly") ||
                currentUserRole.toLowerCase().contains("admin"));

        if (isQuanLy) {
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
        } else {
            ActionListener baoChuaPhanQuyen = e -> {
                JOptionPane.showMessageDialog(
                        this,
                        "Chức năng Thống kê chỉ dành cho tài khoản có quyền Quản lý!",
                        "Phân quyền hệ thống",
                        JOptionPane.WARNING_MESSAGE);
            };
            setMenuItemAction(MenuItem.MONTHLY_REVENUE, baoChuaPhanQuyen);
            setMenuItemAction(MenuItem.BEST_SELLERS, baoChuaPhanQuyen);
            setMenuItemAction(MenuItem.SUMMARY_REPORT, baoChuaPhanQuyen);
        }

        // Shortcut Home
        JButton btnHome = shortcutButtons.get(Shortcut.HOME);
        if (btnHome != null) {
            btnHome.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame(currentUserName, currentUserRole).setVisible(true);
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
                if (isQuanLy) {
                    dongCuaSoHienTai();
                    new MonthlyRevenuePanel(currentUserName, currentUserRole).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Chức năng Thống kê chỉ dành cho tài khoản có quyền Quản lý!",
                            "Phân quyền hệ thống",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
        }

        // Shortcut Logout
        JButton btnLogout = shortcutButtons.get(Shortcut.LOGOUT);
        if (btnLogout != null) {
            btnLogout.addActionListener(e -> {
                dongCuaSoHienTai();
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

    private void addItem(JMenu menu, MenuItem key, String label) {
        JMenuItem item = new JMenuItem(label);
        menuItemMap.put(key, item);
        menu.add(item);
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
