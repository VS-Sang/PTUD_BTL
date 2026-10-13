package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

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
    }

    public void setMenuAction(Menu menu, ActionListener action) {
        menuButtons.get(menu).addActionListener(action);
    }

    public void setShortcutAction(Shortcut shortcut, ActionListener action) {
        shortcutButtons.get(shortcut).addActionListener(action);
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
