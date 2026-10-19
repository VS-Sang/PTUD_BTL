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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class MenuPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(180, 40, 40);
    private static final Color MENU_COLOR = new Color(240, 240, 240);

    public enum Menu {
        SYSTEM("⚙ Hệ thống ▾"),
        CATALOG("📁 Danh mục ▾"),
        OPERATIONS("📋 Xử lý ▾"),
        SEARCH("🔍 Tìm kiếm ▾"),
        REPORTS("📊 Thống kê ▾"),
        ACCOUNT("👤 Tài khoản ▾");

        private final String label;

        Menu(String label) {
            this.label = label;
        }
    }

    public enum Shortcut {
        HOME("🏠"),
        CALCULATOR("🖩"),
        REPORTS("📊"),
        LOGOUT("🚪");

        private final String label;

        Shortcut(String label) {
            this.label = label;
        }
    }

    private final Map<Menu, JButton> menuButtons = new EnumMap<>(Menu.class);
    private final Map<Shortcut, JButton> shortcutButtons = new EnumMap<>(Shortcut.class);

    public MenuPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public MenuPanel(String userName, String userRole) {
        super(new BorderLayout());

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

        JPanel menuItems = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        menuItems.setOpaque(false);
        for (Menu menu : Menu.values()) {
            JButton button = new JButton(menu.label);
            menuButtons.put(menu, button);
            menuItems.add(button);
        }

        JPanel shortcuts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
        shortcuts.setOpaque(false);
        for (Shortcut shortcut : Shortcut.values()) {
            JButton button = new JButton(shortcut.label);
            shortcutButtons.put(shortcut, button);
            shortcuts.add(button);
        }

        navigation.add(menuItems, BorderLayout.WEST);
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
            btnReports.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame().setVisible(true);
            });
        }

        // Shortcut Home
        JButton btnHome = shortcutButtons.get(Shortcut.HOME);
        if (btnHome != null) {
            btnHome.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame().setVisible(true);
            });
        }

        // Shortcut Reports
        JButton btnShortcutReports = shortcutButtons.get(Shortcut.REPORTS);
        if (btnShortcutReports != null) {
            btnShortcutReports.addActionListener(e -> {
                dongCuaSoHienTai();
                new MainFrame().setVisible(true);
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

    public void setMenuAction(Menu menu, ActionListener action) {
        JButton b = menuButtons.get(menu);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Điện Máy Đỏ — Hệ Thống Quản Lý");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MenuPanel());
            frame.setSize(1200, 100);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
