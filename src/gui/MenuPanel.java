package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
        HOME("Trang chủ", "/img/home.png"),
        CALCULATOR("🖩", null),
        REPORTS("📊", null),
        LOGOUT("Đăng xuất", "/img/lo.png");

        public final String label;
        public final String iconPath;

        Shortcut(String label, String iconPath) {
            this.label = label;
            this.iconPath = iconPath;
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
        addItem(categoryMenu, MenuItem.PRODUCT, "Sản phẩm");
        addItem(categoryMenu, MenuItem.PROMOTION, "Khuyến mãi");

        JMenu processMenu = new JMenu("📋 Xử lý ▾");
        addItem(processMenu, MenuItem.INVOICING, "Lập hoá đơn bán hàng");
        addItem(processMenu, MenuItem.IMPORT_GOODS, "Nhập hàng vào kho");
        addItem(processMenu, MenuItem.RETURN, "Xử lí trả hàng");

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
        // Menu Danh mục
        JButton btnCatalog = menuButtons.get(Menu.CATALOG);
        if (btnCatalog != null) {
            JPopupMenu popupCatalog = new JPopupMenu();
            JMenuItem mnuNhanVien = new JMenuItem("👥 Quản lý Nhân viên");
            JMenuItem mnuSanPham = new JMenuItem("📦 Quản lý Sản phẩm");
            JMenuItem mnuKhachHang = new JMenuItem("👤 Quản lý Khách hàng");

            mnuNhanVien.addActionListener(e -> {
                dongCuaSoHienTai();
                new EmployeePanel().setVisible(true);
            });
            mnuSanPham.addActionListener(e -> {
                dongCuaSoHienTai();
                new ProductPanel().setVisible(true);
            });
            mnuKhachHang.addActionListener(e -> {
                dongCuaSoHienTai();
                new CustomerPanel().setVisible(true);
            });

            popupCatalog.add(mnuNhanVien);
            popupCatalog.add(mnuSanPham);
            popupCatalog.add(mnuKhachHang);
            btnCatalog.addActionListener(e -> popupCatalog.show(btnCatalog, 0, btnCatalog.getHeight()));
        }

        // Menu Xử lý
        JButton btnOps = menuButtons.get(Menu.OPERATIONS);
        if (btnOps != null) {
            JPopupMenu popupOps = new JPopupMenu();
            JMenuItem mnuCaLam = new JMenuItem("🕒 Quản lý Ca làm việc");
            JMenuItem mnuPhanCong = new JMenuItem("📅 Phân công ca làm");
            JMenuItem mnuHoaDon = new JMenuItem("🧾 Quản lý Hóa đơn");

            mnuCaLam.addActionListener(e -> {
                dongCuaSoHienTai();
                new WorkSchedulePanel().setVisible(true);
            });
            mnuPhanCong.addActionListener(e -> {
                dongCuaSoHienTai();
                new AssignmentPanel().setVisible(true);
            });
            mnuHoaDon.addActionListener(e -> {
                dongCuaSoHienTai();
                new InvoicePanel().setVisible(true);
            });

            popupOps.add(mnuCaLam);
            popupOps.add(mnuPhanCong);
            popupOps.add(mnuHoaDon);
            btnOps.addActionListener(e -> popupOps.show(btnOps, 0, btnOps.getHeight()));
        }

        // Menu Tìm kiếm
        JButton btnSearch = menuButtons.get(Menu.SEARCH);
        if (btnSearch != null) {
            JPopupMenu popupSearch = new JPopupMenu();
            JMenuItem mnuTimSP = new JMenuItem("📦 Tìm Sản phẩm");
            JMenuItem mnuTimNV = new JMenuItem("👥 Tìm Nhân viên");
            JMenuItem mnuTimKH = new JMenuItem("👤 Tìm Khách hàng");
            JMenuItem mnuTimHD = new JMenuItem("🧾 Tìm Hóa đơn");

            mnuTimSP.addActionListener(e -> {
                dongCuaSoHienTai();
                new ProductPanel().setVisible(true);
            });
            mnuTimNV.addActionListener(e -> {
                dongCuaSoHienTai();
                new EmployeePanel().setVisible(true);
            });
            mnuTimKH.addActionListener(e -> {
                dongCuaSoHienTai();
                new CustomerPanel().setVisible(true);
            });
            mnuTimHD.addActionListener(e -> {
                dongCuaSoHienTai();
                new InvoicePanel().setVisible(true);
            });

            popupSearch.add(mnuTimSP);
            popupSearch.add(mnuTimNV);
            popupSearch.add(mnuTimKH);
            popupSearch.add(mnuTimHD);
            btnSearch.addActionListener(e -> popupSearch.show(btnSearch, 0, btnSearch.getHeight()));
        }

        // Menu Thống kê
        JButton btnReports = menuButtons.get(Menu.REPORTS);
        if (btnReports != null) {
            boolean isQuanLy = currentUserRole != null && (
                    currentUserRole.equalsIgnoreCase("Quản lý") ||
                    currentUserRole.equalsIgnoreCase("Admin") ||
                    currentUserRole.equalsIgnoreCase("Quan Ly") ||
                    currentUserRole.toLowerCase().contains("quản lý") ||
                    currentUserRole.toLowerCase().contains("quan ly") ||
                    currentUserRole.toLowerCase().contains("admin")
            );

            if (isQuanLy) {
                JPopupMenu popupReports = new JPopupMenu();
                mnuDoanhThu = new JMenuItem("📈 Doanh thu theo tháng");
                mnuBanChay = new JMenuItem("📊 Sản phẩm bán chạy");
                mnuTongHop = new JMenuItem("📄 Báo cáo tổng hợp");

                mnuDoanhThu.addActionListener(e -> {
                    dongCuaSoHienTai();
                    new MonthlyRevenuePanel(currentUserName, currentUserRole).setVisible(true);
                });

                mnuBanChay.addActionListener(e -> {
                    dongCuaSoHienTai();
                    new TopProductsPanel(currentUserName, currentUserRole).setVisible(true);
                });

                mnuTongHop.addActionListener(e -> {
                    dongCuaSoHienTai();
                    new GeneralReportPanel(currentUserName, currentUserRole).setVisible(true);
                });

                popupReports.add(mnuDoanhThu);
                popupReports.add(mnuBanChay);
                popupReports.add(mnuTongHop);
                btnReports.addActionListener(e -> popupReports.show(btnReports, 0, btnReports.getHeight()));
            } else {
                btnReports.addActionListener(e -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Chức năng Thống kê chỉ dành cho tài khoản có quyền Quản lý!",
                            "Phân quyền hệ thống",
                            JOptionPane.WARNING_MESSAGE);
                });
            }
        }

        // Shortcut Home
        JButton btnHome = shortcutButtons.get(Shortcut.HOME);
        if (btnHome != null) {
            btnHome.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame(currentUserName, currentUserRole).setVisible(true);
            });
        }

        // Shortcut Reports
        JButton btnShortcutReports = shortcutButtons.get(Shortcut.REPORTS);
        if (btnShortcutReports != null) {
            btnShortcutReports.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame(currentUserName, currentUserRole).setVisible(true);
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
        btn.setToolTipText(sc.label);
        if (sc.iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(sc.iconPath));
                Image img = icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                btn.setText(sc.label);
            }
        } else {
            btn.setText(sc.label);
        }
        return btn;
    }

    public void setMenuItemAction(MenuItem item, ActionListener action) {
        menuItemMap.get(item).addActionListener(action);
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
