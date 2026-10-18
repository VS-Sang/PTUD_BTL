package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

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
        shortcutButtons.get(shortcut).addActionListener(action);
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
