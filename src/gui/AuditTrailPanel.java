package gui;

import connectDB.ConnectDB;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditTrailPanel extends javax.swing.JFrame {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private final String currentUsername;
    private final JTextField searchField = new JTextField(22);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{
            "Thời gian", "Tài khoản", "Hành động", "Đối tượng", "Chi tiết", "Tên máy"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AuditTrailPanel(String userName, String userRole, String username) {
        currentUsername = username;
        setTitle("Điện Máy Đỏ - Audit Trail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new MenuPanel(userName, userRole, username), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadAuditTrail();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        JPanel heading = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Nhật ký Audit Trail");
        title.setForeground(BRAND_RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.add(title, BorderLayout.WEST);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("Tìm kiếm:"));
        filters.add(searchField);
        JButton refresh = new JButton("Làm mới");
        refresh.addActionListener(event -> loadAuditTrail());
        filters.add(refresh);
        heading.add(filters, BorderLayout.EAST);
        content.add(heading, BorderLayout.NORTH);
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(BRAND_RED);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getColumnModel().getColumn(4).setPreferredWidth(420);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        return content;
    }

    private void loadAuditTrail() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText().trim();
        String sql = "SELECT TOP 500 a.thoiGian, tk.tenDangNhap, a.hanhDong, a.doiTuong, "
                + "a.chiTiet, a.tenMay FROM AuditTrail a LEFT JOIN TaiKhoan tk "
                + "ON tk.maTaiKhoan = a.maTaiKhoan "
                + "WHERE ? = N'' OR tk.tenDangNhap LIKE ? OR a.hanhDong LIKE ? "
                + "OR a.doiTuong LIKE ? OR a.chiTiet LIKE ? ORDER BY a.thoiGian DESC";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            statement.setString(1, keyword);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            statement.setString(4, pattern);
            statement.setString(5, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{resultSet.getTimestamp(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                            resultSet.getString(6)});
                }
            }
        } catch (SQLException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Không thể tải Audit Trail:\n" + exception.getMessage(), "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
