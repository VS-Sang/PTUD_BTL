package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JFrame {
    private DefaultTableModel model;
    private JTable table;

    public CustomerPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new MenuPanel(), BorderLayout.NORTH);

        // 1. KHUNG NỘI DUNG VÀ BẢNG DỮ LIỆU
        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("📖 Quản Lý Khách Hàng");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);
        JButton btnAdd = new JButton("+ Thêm mới");
        btnAdd.setBackground(new Color(180, 40, 40));
        btnAdd.setForeground(Color.WHITE);
        JButton btnExport = new JButton("Xuất Excel");
        pSubActions.add(btnAdd);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JTextField txtSearch = new JTextField("🔍 Tìm tên, SĐT, email...", 30);
        pSearch.add(txtSearch);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);

        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] columns = {"", "Mã KH", "Họ tên", "SĐT", "Email", "Địa chỉ", "Tổng mua", "Hạng", "Thao tác"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class; // Biến cột đầu tiên thành ô Checkbox
                }
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scrollPane = new JScrollPane(table);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerPanel().setVisible(true);
        });
    }
}