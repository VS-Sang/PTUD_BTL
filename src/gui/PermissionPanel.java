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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermissionPanel extends JFrame {
    private static final Color BRAND_RED = new Color(180, 40, 40);
    private final JComboBox<AccountOption> accountBox = new JComboBox<>();
    private final JComboBox<String> groupFilter = new JComboBox<>(new String[]{"Tất cả nhóm"});
    private final JTextField searchField = new JTextField(20);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{
            "Mã quyền", "Tên quyền", "Nhóm quyền", "Mô tả", "Xem", "Thêm", "Sửa", "Xóa"
    }, 0) {
        @Override
        public Class<?> getColumnClass(int column) {
            return column >= 4 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column >= 4;
        }
    };
    private final JTable permissionTable = new JTable(tableModel);

    public PermissionPanel() {
        this(null, null, null);
    }

    public PermissionPanel(String userName, String userRole, String username) {
        setTitle("Điện Máy Đỏ - Phân quyền hệ thống");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1100, 650));
        setSize(1440, 820);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new MenuPanel(userName, userRole, username), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadAccounts();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 18, 18, 18));

        JPanel title = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("🔐  Phân quyền hệ thống");
        heading.setForeground(BRAND_RED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.add(heading, BorderLayout.WEST);
        content.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.add(new JLabel("Tài khoản:"));
        accountBox.setPreferredSize(new java.awt.Dimension(280, 28));
        controls.add(accountBox);
        controls.add(new JLabel("Tìm quyền:"));
        controls.add(searchField);
        controls.add(groupFilter);
        JButton reload = new JButton("Làm mới");
        reload.addActionListener(event -> {
            loadAccounts();
            loadPermissions();
        });
        controls.add(reload);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);
        content.add(top, BorderLayout.NORTH);

        permissionTable.setRowHeight(34);
        permissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        permissionTable.getTableHeader().setReorderingAllowed(false);
        permissionTable.getTableHeader().setBackground(BRAND_RED);
        permissionTable.getTableHeader().setForeground(Color.WHITE);
        permissionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        permissionTable.getColumnModel().getColumn(4).setCellRenderer(new CheckRenderer());
        permissionTable.getColumnModel().getColumn(5).setCellRenderer(new CheckRenderer());
        permissionTable.getColumnModel().getColumn(6).setCellRenderer(new CheckRenderer());
        permissionTable.getColumnModel().getColumn(7).setCellRenderer(new CheckRenderer());
        content.add(new JScrollPane(permissionTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton selectAll = new JButton("Chọn tất cả");
        selectAll.addActionListener(event -> setAll(true));
        JButton clearAll = new JButton("Bỏ chọn tất cả");
        clearAll.addActionListener(event -> setAll(false));
        JButton save = new JButton("Lưu phân quyền");
        save.setBackground(BRAND_RED);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.addActionListener(event -> savePermissions());
        actions.add(selectAll);
        actions.add(clearAll);
        actions.add(save);
        content.add(actions, BorderLayout.SOUTH);

        accountBox.addActionListener(event -> loadPermissions());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent event) { filterPermissions(); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { filterPermissions(); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { filterPermissions(); }
        });
        groupFilter.addActionListener(event -> filterPermissions());
        return content;
    }

    private void loadAccounts() {
        accountBox.removeAllItems();
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, nv.hoTen FROM TaiKhoan tk "
                + "JOIN NhanVien nv ON nv.maNhanVien = tk.maNhanVien ORDER BY tk.tenDangNhap";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                accountBox.addItem(new AccountOption(resultSet.getString(1),
                        resultSet.getString(2) + " - " + resultSet.getString(3)));
            }
        } catch (SQLException exception) {
            showError("Không thể tải danh sách tài khoản:\n" + exception.getMessage());
        }
        if (accountBox.getItemCount() > 0) {
            accountBox.setSelectedIndex(0);
            loadPermissions();
        }
    }

    private void loadPermissions() {
        AccountOption account = (AccountOption) accountBox.getSelectedItem();
        if (account == null) return;
        tableModel.setRowCount(0);
        groupFilter.removeAllItems();
        groupFilter.addItem("Tất cả nhóm");
        String sql = "SELECT q.maQuyen, q.tenQuyen, q.nhomQuyen, q.moTa, "
                + "ISNULL(pq.coQuyenXem, 0), ISNULL(pq.coQuyenThem, 0), "
                + "ISNULL(pq.coQuyenSua, 0), ISNULL(pq.coQuyenXoa, 0) "
                + "FROM QuyenTruyCap q LEFT JOIN PhanQuyen pq ON pq.maQuyen = q.maQuyen "
                + "AND pq.maTaiKhoan = ? ORDER BY q.nhomQuyen, q.tenQuyen";
        List<String> groups = new ArrayList<>();
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String group = resultSet.getString(3);
                    if (!groups.contains(group)) groups.add(group);
                    tableModel.addRow(new Object[]{
                            resultSet.getString(1), resultSet.getString(2), group, resultSet.getString(4),
                            resultSet.getBoolean(5), resultSet.getBoolean(6),
                            resultSet.getBoolean(7), resultSet.getBoolean(8)
                    });
                }
            }
        } catch (SQLException exception) {
            showError("Không thể tải danh sách quyền:\n" + exception.getMessage());
        }
        for (String group : groups) groupFilter.addItem(group);
        filterPermissions();
    }

    private void filterPermissions() {
        String keyword = searchField.getText().trim().toLowerCase();
        String group = (String) groupFilter.getSelectedItem();
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                new javax.swing.table.TableRowSorter<>(tableModel);
        permissionTable.setRowSorter(sorter);
        sorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String text = (entry.getStringValue(0) + " " + entry.getStringValue(1)
                        + " " + entry.getStringValue(3)).toLowerCase();
                return (keyword.isEmpty() || text.contains(keyword))
                        && (group == null || group.startsWith("Tất cả") || group.equals(entry.getStringValue(2)));
            }
        });
    }

    private void setAll(boolean value) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int column = 4; column <= 7; column++) tableModel.setValueAt(value, row, column);
        }
    }

    private void savePermissions() {
        AccountOption account = (AccountOption) accountBox.getSelectedItem();
        if (account == null) {
            showError("Chưa có tài khoản để phân quyền.");
            return;
        }
        if (permissionTable.isEditing()) permissionTable.getCellEditor().stopCellEditing();
        String sql = "MERGE PhanQuyen AS target USING (SELECT ? AS maTaiKhoan, ? AS maQuyen) "
                + "AS source ON target.maTaiKhoan = source.maTaiKhoan AND target.maQuyen = source.maQuyen "
                + "WHEN MATCHED THEN UPDATE SET coQuyenXem = ?, coQuyenThem = ?, coQuyenSua = ?, "
                + "coQuyenXoa = ?, nguoiGan = ?, ngayGan = GETDATE() "
                + "WHEN NOT MATCHED THEN INSERT (maTaiKhoan, maQuyen, coQuyenXem, coQuyenThem, "
                + "coQuyenSua, coQuyenXoa, nguoiGan) VALUES (?, ?, ?, ?, ?, ?, ?);";
        Connection connection = null;
        try {
            connection = ConnectDB.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                statement.setString(1, account.id);
                statement.setString(2, tableModel.getValueAt(row, 0).toString());
                for (int column = 4; column <= 7; column++) {
                    statement.setBoolean(column - 1, (Boolean) tableModel.getValueAt(row, column));
                }
                statement.setString(7, account.id);
                statement.setString(8, account.id);
                statement.setString(9, tableModel.getValueAt(row, 0).toString());
                for (int column = 4; column <= 7; column++) {
                    statement.setBoolean(column + 6, (Boolean) tableModel.getValueAt(row, column));
                }
                statement.setString(14, account.id);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
            JOptionPane.showMessageDialog(this, "Đã lưu phân quyền cho " + account.label + ".");
            }
        } catch (SQLException exception) {
            if (connection != null) {
              try {
                connection.rollback();
              } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
              }
            }
            showError("Không thể lưu phân quyền:\n" + exception.getMessage());
        } finally {
            if (connection != null) {
              try {
                connection.setAutoCommit(true);
                connection.close();
              } catch (SQLException closeException) {
                showError("Không thể đóng kết nối cơ sở dữ liệu:\n" + closeException.getMessage());
              }
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static class AccountOption {
        private final String id;
        private final String label;

        AccountOption(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class CheckRenderer extends DefaultTableCellRenderer {
        CheckRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}
