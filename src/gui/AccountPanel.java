package gui;

import connectDB.ConnectDB;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            "Tên đăng nhập", "Họ tên đầy đủ", "Vai trò", "Trạng thái",
            "Đăng nhập gần nhất", "Thao tác", "Mã tài khoản"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable accountTable = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);

    public AccountPanel() {
        this(null, null, null);
    }

    public AccountPanel(String userName, String userRole, String username) {
        setTitle("Điện Máy Đỏ - Quản lý tài khoản");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 650));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new MenuPanel(userName, userRole, username), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadAccounts();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));

        JPanel heading = new JPanel(new BorderLayout());
        JLabel title = new JLabel("👥  Quản lý Tài khoản Hệ thống");
        title.setForeground(BRAND_RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.add(title, BorderLayout.WEST);

        JButton createButton = new JButton("+ Tạo tài khoản");
        createButton.setBackground(BRAND_RED);
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.addActionListener(event -> openCreateDialog());
        heading.add(createButton, BorderLayout.EAST);
        content.add(heading, BorderLayout.NORTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("Tìm kiếm:"));
        filters.add(searchField);
        filters.add(roleFilter);
        filters.add(statusFilter);
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(event -> loadAccounts());
        filters.add(refreshButton);

        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.add(heading, BorderLayout.NORTH);
        north.add(filters, BorderLayout.CENTER);
        content.add(north, BorderLayout.NORTH);

        accountTable.setRowSorter(sorter);
        accountTable.setRowHeight(38);
        accountTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        accountTable.getTableHeader().setReorderingAllowed(false);
        accountTable.getTableHeader().setBackground(BRAND_RED);
        accountTable.getTableHeader().setForeground(Color.WHITE);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.getColumnModel().getColumn(2).setCellRenderer(new RoleRenderer());
        accountTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        accountTable.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        accountTable.getColumnModel().getColumn(6).setMinWidth(0);
        accountTable.getColumnModel().getColumn(6).setMaxWidth(0);
        accountTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() != 1) return;
                int row = accountTable.rowAtPoint(event.getPoint());
                int column = accountTable.columnAtPoint(event.getPoint());
                if (row >= 0 && column == 5) {
                    int action = (event.getX() - accountTable.getCellRect(row, column, true).x) / 42;
                    performRowAction(accountTable.convertRowIndexToModel(row), action);
                }
            }
        });
        content.add(new JScrollPane(accountTable), BorderLayout.CENTER);

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
                + "tk.lanDangNhapCuoi FROM TaiKhoan tk JOIN NhanVien nv ON nv.maNhanVien = tk.maNhanVien "
                + "ORDER BY tk.maTaiKhoan";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getString("tenDangNhap"),
                        resultSet.getString("hoTen"),
                        resultSet.getString("vaiTro"),
                        resultSet.getString("trangThai"),
                        resultSet.getTimestamp("lanDangNhapCuoi"),
                        "",
                        resultSet.getString("maTaiKhoan")
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
                String searchable = (entry.getStringValue(0) + " " + entry.getStringValue(1)).toLowerCase();
                return (keyword.isEmpty() || searchable.contains(keyword))
                        && (role == null || role.startsWith("Tất cả") || role.equals(entry.getStringValue(2)))
                        && (status == null || status.startsWith("Tất cả") || status.equals(entry.getStringValue(3)));
            }
        });
    }

    private void performRowAction(int modelRow, int action) {
        String accountId = tableModel.getValueAt(modelRow, 6).toString();
        if (action == 0) {
            openEditDialog(accountId, modelRow);
        } else if (action == 1) {
            toggleStatus(accountId, tableModel.getValueAt(modelRow, 3).toString());
        } else if (action == 2) {
            deleteAccount(accountId, tableModel.getValueAt(modelRow, 0).toString(),
                    tableModel.getValueAt(modelRow, 2).toString());
        }
    }

    private void openCreateDialog() {
        AccountDialog dialog = new AccountDialog(this, null, null);
        dialog.setVisible(true);
        if (dialog.saved) loadAccounts();
    }

    private void openEditDialog(String accountId, int modelRow) {
        AccountData data = new AccountData(accountId, tableModel.getValueAt(modelRow, 0).toString(),
                tableModel.getValueAt(modelRow, 2).toString(), tableModel.getValueAt(modelRow, 3).toString());
        AccountDialog dialog = new AccountDialog(this, data, null);
        dialog.setVisible(true);
        if (dialog.saved) loadAccounts();
    }

    private void toggleStatus(String accountId, String currentStatus) {
        String nextStatus = "Hoạt động".equals(currentStatus) ? "Bị khóa" : "Hoạt động";
        int answer = JOptionPane.showConfirmDialog(this,
                ("Hoạt động".equals(nextStatus) ? "Mở khóa" : "Khóa") + " tài khoản này?",
                "Xác nhận thao tác", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        updateAccount("trangThai", nextStatus, accountId);
    }

    private void deleteAccount(String accountId, String username, String role) {
        if ("Admin".equalsIgnoreCase(role)) {
            showError("Không thể xóa tài khoản có vai trò Admin.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + username + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM TaiKhoan WHERE maTaiKhoan = ?")) {
            statement.setString(1, accountId);
            statement.executeUpdate();
            loadAccounts();
        } catch (SQLException exception) {
            showError("Không thể xóa tài khoản. Tài khoản có thể đang được dùng trong dữ liệu lịch sử.");
        }
    }

    private boolean updateAccount(String column, String value, String accountId) {
        String sql = "UPDATE TaiKhoan SET " + column + " = ?, ngayCapNhat = GETDATE() WHERE maTaiKhoan = ?";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, accountId);
            if (statement.executeUpdate() > 0) {
                loadAccounts();
                return true;
            }
        } catch (SQLException exception) {
            showError("Không thể cập nhật tài khoản:\n" + exception.getMessage());
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static class AccountData {
        final String id;
        final String username;
        final String role;
        final String status;

        AccountData(String id, String username, String role, String status) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.status = status;
        }
    }

    private static class AccountDialog extends JDialog {
        private final AccountData data;
        private final JComboBox<EmployeeOption> employeeBox = new JComboBox<>();
        private final JTextField usernameField = new JTextField();
        private final JPasswordField passwordField = new JPasswordField();
        private final JComboBox<String> roleBox = new JComboBox<>(ROLES);
        private final JComboBox<String> statusBox = new JComboBox<>(STATUSES);
        private boolean saved;

        AccountDialog(JFrame owner, AccountData data, String ignored) {
            super(owner, data == null ? "Tạo tài khoản" : "Cập nhật tài khoản", true);
            this.data = data;
            setSize(440, 300);
            setResizable(false);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(0, 8));
            JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
            form.setBorder(BorderFactory.createEmptyBorder(14, 18, 4, 18));
            form.add(new JLabel("Nhân viên:"));
            form.add(employeeBox);
            form.add(new JLabel("Tên đăng nhập:"));
            form.add(usernameField);
            form.add(new JLabel("Mật khẩu:"));
            form.add(passwordField);
            form.add(new JLabel("Vai trò:"));
            form.add(roleBox);
            form.add(new JLabel("Trạng thái:"));
            form.add(statusBox);
            add(form, BorderLayout.CENTER);
            JButton save = new JButton("Lưu");
            save.setBackground(BRAND_RED);
            save.setForeground(Color.WHITE);
            save.addActionListener(event -> saveAccount());
            JButton cancel = new JButton("Hủy");
            cancel.addActionListener(event -> dispose());
            JPanel buttons = new JPanel();
            buttons.add(save);
            buttons.add(cancel);
            add(buttons, BorderLayout.SOUTH);
            loadEmployees();
            if (data != null) {
                usernameField.setText(data.username);
                usernameField.setEditable(false);
                passwordField.setVisible(false);
                roleBox.setSelectedItem(data.role);
                statusBox.setSelectedItem(data.status);
            }
        }

        private void loadEmployees() {
            String sql = "SELECT nv.maNhanVien, nv.hoTen FROM NhanVien nv "
                    + "WHERE NOT EXISTS (SELECT 1 FROM TaiKhoan tk WHERE tk.maNhanVien = nv.maNhanVien) "
                    + "ORDER BY nv.hoTen";
            try (Connection connection = ConnectDB.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employeeBox.addItem(new EmployeeOption(resultSet.getString(1), resultSet.getString(2)));
                }
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(this, "Không thể tải danh sách nhân viên.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void saveAccount() {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();
            if (data == null && (employeeBox.getSelectedItem() == null
                    || !username.matches("[A-Za-z0-9._-]{3,50}") || password.length < 6)) {
                JOptionPane.showMessageDialog(this,
                        "Chọn nhân viên, tên đăng nhập phải có 3-50 ký tự (chữ, số, ., _, -) "
                                + "và mật khẩu ít nhất 6 ký tự.",
                        "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                java.util.Arrays.fill(password, '\0');
                return;
            }
            try (Connection connection = ConnectDB.getConnection()) {
                if (data == null) {
                    EmployeeOption employee = (EmployeeOption) employeeBox.getSelectedItem();
                    String sql = "INSERT INTO TaiKhoan (maTaiKhoan, tenDangNhap, matKhau, vaiTro, maNhanVien, trangThai) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, nextAccountId(connection));
                        statement.setString(2, username);
                        statement.setString(3, LoginFrame.toMD5(new String(password)));
                        statement.setString(4, (String) roleBox.getSelectedItem());
                        statement.setString(5, employee.id);
                        statement.setString(6, (String) statusBox.getSelectedItem());
                        statement.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE TaiKhoan SET vaiTro = ?, trangThai = ?, ngayCapNhat = GETDATE() "
                            + "WHERE maTaiKhoan = ?";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, (String) roleBox.getSelectedItem());
                        statement.setString(2, (String) statusBox.getSelectedItem());
                        statement.setString(3, data.id);
                        statement.executeUpdate();
                    }
                }
                saved = true;
                dispose();
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(this, "Không thể lưu tài khoản:\n" + exception.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                java.util.Arrays.fill(password, '\0');
            }
        }

        private String nextAccountId(Connection connection) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maTaiKhoan, 3, 18))), 0) + 1 "
                            + "FROM TaiKhoan WHERE maTaiKhoan LIKE 'TK[0-9]%'");
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return String.format("TK%03d", resultSet.getInt(1));
                }
            }
            return "TK001";
        }
    }

    private static class EmployeeOption {
        final String id;
        final String name;

        EmployeeOption(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + id + ")";
        }
    }

    private static class RoleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setForeground("Admin".equals(value) || "Quản lý".equals(value)
                    ? BRAND_RED : new Color(30, 100, 190));
            return this;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground("Hoạt động".equals(value) ? new Color(35, 130, 55) : BRAND_RED);
            return this;
        }
    }

    private static class ActionRenderer extends JPanel implements TableCellRenderer {
        ActionRenderer() {
            super(new FlowLayout(FlowLayout.LEFT, 4, 5));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focus, int row, int column) {
            removeAll();
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            add(actionButton("✎", new Color(30, 125, 210)));
            add(actionButton("🔒", new Color(245, 125, 0)));
            add(actionButton("▣", new Color(205, 35, 42)));
            return this;
        }

        private JButton actionButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new java.awt.Dimension(34, 25));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setFocusPainted(false);
            return button;
        }
    }
}
