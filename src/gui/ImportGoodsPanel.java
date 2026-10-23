package gui;

import javax.swing.*;
import java.awt.*;

public class ImportGoodsPanel extends JFrame {
    private MenuPanel menuPanel;

    public ImportGoodsPanel() {
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

    }

    private void setLayout(BorderLayout borderLayout) {
        throw new UnsupportedOperationException("Unimplemented method 'setLayout'");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImportGoodsPanel().setVisible(true));
    }
}
