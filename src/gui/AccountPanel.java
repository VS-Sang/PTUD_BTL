package gui;

import connectDB.ConnectDB;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPanel extends JFrame {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private static final String[] ROLES = {"Admin", "Quản lý", "Nhân viên", "Thu ngân", "Nhân viên kho"};
    private static final String[] STATUSES = {"Hoạt động", "Bị khóa", "Chưa kích hoạt"};

    private final JTextField searchField = new JTextField(22);
    private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{"Tất cả vai trò", "Admin",
            "Quản lý", "Nhân viên", "Thu ngân", "Nhân viên kho"});
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{"Tất cả trạng thái",
            "Hoạt động", "Bị khóa", "Chưa kích hoạt"});
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{
            "Mã tài khoản", "Tên đăng nhập", "Nhân viên", "Vai trò", "Trạng thái",
            "Lần đăng nhập cuối", "Số lần sai"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable accountTable = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);

    public AccountPanel() {
        setTitle("Điện Máy Đỏ - Quản lý tài khoản");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 650));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new MenuPanel(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadAccounts();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));

        JPanel heading = new JPanel(new BorderLayout());
        JLabel title = new JLabel("👤 Quản lý tài khoản");
        title.setForeground(BRAND_RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.add(title, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.add(new JLabel("Tìm kiếm:"));
        filters.add(searchField);
        filters.add(roleFilter);
        filters.add(statusFilter);
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(event -> loadAccounts());
        filters.add(refreshButton);
        heading.add(filters, BorderLayout.EAST);
        content.add(heading, BorderLayout.NORTH);

        accountTable.setRowSorter(sorter);
        accountTable.setRowHeight(34);
        accountTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        accountTable.getTableHeader().setReorderingAllowed(false);
        accountTable.getTableHeader().setBackground(BRAND_RED);
        accountTable.getTableHeader().setForeground(Color.WHITE);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        content.add(new JScrollPane(accountTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton roleButton = new JButton("Đổi vai trò");
        roleButton.addActionListener(event -> changeRole());
        JButton statusButton = new JButton("Đổi trạng thái");
        statusButton.addActionListener(event -> changeStatus());
        JButton resetButton = new JButton("Đặt lại mật khẩu");
        resetButton.addActionListener(event -> resetPassword());
        actions.add(roleButton);
        actions.add(statusButton);
        actions.add(resetButton);
        content.add(actions, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent event) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { applyFilter(); }
        });
        roleFilter.addActionListener(event -> applyFilter());
        statusFilter.addActionListener(event -> applyFilter());
        return content;
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, nv.hoTen, tk.vaiTro, tk.trangThai, "
                + "tk.lanDangNhapCuoi, tk.soLanDangNhapLoi FROM TaiKhoan tk "
                + "JOIN NhanVien nv ON nv.maNhanVien = tk.maNhanVien ORDER BY tk.maTaiKhoan";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getString("maTaiKhoan"),
                        resultSet.getString("tenDangNhap"),
                        resultSet.getString("hoTen"),
                        resultSet.getString("vaiTro"),
                        resultSet.getString("trangThai"),
                        resultSet.getTimestamp("lanDangNhapCuoi"),
                        resultSet.getInt("soLanDangNhapLoi")
                });
            }
        } catch (SQLException exception) {
            showError("Không thể tải danh sách tài khoản:\n" + exception.getMessage());
        }
        applyFilter();
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String role = (String) roleFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String searchable = (entry.getStringValue(0) + " " + entry.getStringValue(1)
                        + " " + entry.getStringValue(2)).toLowerCase();
                return (keyword.isEmpty() || searchable.contains(keyword))
                        && (role == null || role.startsWith("Tất cả") || role.equals(entry.getStringValue(3)))
                        && (status == null || status.startsWith("Tất cả") || status.equals(entry.getStringValue(4)));
            }
        });
    }

    private String selectedAccountId() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một tài khoản.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return accountTable.getValueAt(selectedRow, 0).toString();
    }

    private void changeRole() {
        String accountId = selectedAccountId();
        if (accountId == null) return;
        String role = (String) JOptionPane.showInputDialog(this, "Chọn vai trò mới:", "Đổi vai trò",
                JOptionPane.PLAIN_MESSAGE, null, ROLES, ROLES[0]);
        if (role != null && updateAccount("vaiTro", role, accountId)) {
            loadAccounts();
        }
    }

    private void changeStatus() {
        String accountId = selectedAccountId();
        if (accountId == null) return;
        String status = (String) JOptionPane.showInputDialog(this, "Chọn trạng thái mới:", "Đổi trạng thái",
                JOptionPane.PLAIN_MESSAGE, null, STATUSES, STATUSES[0]);
        if (status != null && updateAccount("trangThai", status, accountId)) {
            loadAccounts();
        }
    }

    private void resetPassword() {
        String accountId = selectedAccountId();
        if (accountId == null) return;
        String password = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới (ít nhất 6 ký tự):");
        if (password == null) return;
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        String sql = "UPDATE TaiKhoan SET matKhau = ?, ngayDoiMatKhau = GETDATE(), "
                + "ngayCapNhat = GETDATE(), soLanDangNhapLoi = 0 WHERE maTaiKhoan = ?";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, LoginFrame.toMD5(password));
            statement.setString(2, accountId);
            if (statement.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu.");
            }
        } catch (SQLException exception) {
            showError("Không thể đặt lại mật khẩu:\n" + exception.getMessage());
        }
    }

    private boolean updateAccount(String column, String value, String accountId) {
        String sql = "UPDATE TaiKhoan SET " + column + " = ?, ngayCapNhat = GETDATE() WHERE maTaiKhoan = ?";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, accountId);
            if (statement.executeUpdate() > 0) return true;
            showError("Không tìm thấy tài khoản cần cập nhật.");
        } catch (SQLException exception) {
            showError("Không thể cập nhật tài khoản:\n" + exception.getMessage());
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                                  boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground("Hoạt động".equals(value) ? new Color(35, 130, 55) : BRAND_RED);
            return this;
        }
    }
}
