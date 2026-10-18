package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JFrame {
        private DefaultTableModel model;
        private JTable table;
        private MenuPanel menuPanel;

        public ProductPanel() {
                setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
                setSize(1200, 650);
                setLocationRelativeTo(null);
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
                pSubHeader.setBackground(new Color(245, 245, 245));
                pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

                JLabel lblSubTitle = new JLabel("🧾 Quản lý sản phẩm / Dịch vụ");
                lblSubTitle.setForeground(new Color(150, 30, 30));
                lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
                pSubHeader.add(lblSubTitle, BorderLayout.WEST);

                JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                pSubActions.setOpaque(false);
                JButton btnExport = new JButton("Xuất Excel");
                pSubActions.add(btnExport);
                pSubHeader.add(pSubActions, BorderLayout.EAST);

                JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
                JTextField txtSearch = new JTextField("⌕ Tìm tên SP, mã SP...", 25);
                JComboBox<String> cboStatus = new JComboBox<>(
                                new String[] { "Tất cả trạng thái", "Hoàn thành", "Đang xử lý", "Chờ xác nhận" });
                JComboBox<String> cboMonth = new JComboBox<>(
                                new String[] { "Tất cả tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                                                "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9",
                                                "Tháng 10", "Tháng 11", "Tháng 12" });
                pSearch.add(txtSearch);
                pSearch.add(cboStatus);
                pSearch.add(cboMonth);

                JPanel pNorthCenter = new JPanel(new BorderLayout());
                pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
                pNorthCenter.add(pSearch, BorderLayout.CENTER);
                pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

                String[] column = { " ", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu",
                                "Giá nhập", "Giá bán", "Tồn kho", "BH(tháng)", "Trạng thái" };
                model = new DefaultTableModel(column, 0) {
                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                                return columnIndex == 0 ? Boolean.class : String.class;
                        }
                };
                table = new JTable(model);
                table.setRowHeight(36);
                table.getColumnModel().getColumn(0).setMaxWidth(40);
                table.getTableHeader().setReorderingAllowed(false);

                JScrollPane scrollPane = new JScrollPane(
                                table,
                                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                pCenterContainer.add(scrollPane, BorderLayout.CENTER);
                add(pCenterContainer, BorderLayout.CENTER);
        }

        public static void main(String[] args) {
                new ProductPanel().setVisible(true);
        }
}
