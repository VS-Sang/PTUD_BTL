package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EmployeePanel extends JFrame {

    private MenuPanel menuPanel;
    private JPanel pCenterContainer;
    private JPanel pNorthCenter;
    private JPanel pSubHeader;
    private JLabel lblSubTitle;
    private JPanel pSubActions;
    private JButton btnExport;
    private JPanel pSearch;
    private JTextField txtSearch;
    private JComboBox<String> cboDepartment;
    private JComboBox<String> cboStatus;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollPane;

    public EmployeePanel() {
        // Cấu hình khung cửa sổ chính
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Thêm MenuPanel có sẵn vào phía Bắc
        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);


        // =====================================================================
        // 3. KHỞI TẠO KHUNG TIÊU ĐỀ PHỤ VÀ NÚT XUẤT EXCEL
        // =====================================================================

        pCenterContainer = new JPanel(new BorderLayout());

        pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblSubTitle = new JLabel("👥 Quản lý Nhân Viên");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);

        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);


        // =====================================================================
        // 4. KHỞI TẠO THANH TÌM KIẾM VÀ CÁC BỘ LỌC
        // =====================================================================

        pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        txtSearch = new JTextField("🔍 Tìm tên, mã NV, SĐT...", 25);

        cboDepartment = new JComboBox<>(new String[]{
                "Tất cả phòng ban", "Quản lý", "Bán hàng", "Kho", "Kế toán"
        });

        cboStatus = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Đang làm", "Nghỉ phép"
        });

        pSearch.add(txtSearch);
        pSearch.add(cboDepartment);
        pSearch.add(cboStatus);

        pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);

        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);


        // =====================================================================
        // 5. KHỞI TẠO BẢNG DỮ LIỆU VÀ NẠP DỮ LIỆU MẪU
        // =====================================================================

        String[] columns = {
                "", "Mã NV", "Họ tên", "Giới tính", "SĐT", "Phòng ban", "Chức vụ", "Trạng thái", "Thao tác"
        };

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
        table.setRowHeight(36);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getTableHeader().setReorderingAllowed(false);

        // Đổi màu thanh tiêu đề bảng sang màu đỏ thương hiệu
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Căn lề và in đậm cột Họ tên
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return this;
            }
        });

        // Hiển thị màu sắc cho trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if ("Đang làm".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else if ("Nghỉ phép".equals(val)) {
                    setForeground(new Color(230, 81, 0));
                }
                return this;
            }
        });

        // Hiển thị 3 nút thao tác (Sửa, Xem, Xóa)
        table.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JPanel pAction = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                pAction.setOpaque(true);
                pAction.setBackground(Color.WHITE);

                JButton btnEdit = new JButton("📝");
                btnEdit.setBackground(new Color(24, 119, 242));
                btnEdit.setForeground(Color.WHITE);
                btnEdit.setPreferredSize(new Dimension(30, 24));
                btnEdit.setMargin(new Insets(0, 0, 0, 0));

                JButton btnView = new JButton("👁");
                btnView.setBackground(new Color(225, 228, 232));
                btnView.setPreferredSize(new Dimension(30, 24));
                btnView.setMargin(new Insets(0, 0, 0, 0));

                JButton btnDelete = new JButton("🗑");
                btnDelete.setBackground(new Color(205, 32, 38));
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setPreferredSize(new Dimension(30, 24));
                btnDelete.setMargin(new Insets(0, 0, 0, 0));

                pAction.add(btnEdit);
                pAction.add(btnView);
                pAction.add(btnDelete);
                return pAction;
            }
        });

        model.addRow(new Object[]{false, "NV001", "Nguyễn Văn An", "Nam", "0901234567", "Quản lý", "Trưởng phòng", "Đang làm", ""});
        model.addRow(new Object[]{false, "NV002", "Trần Thị Bình", "Nữ", "0912345678", "Bán hàng", "Nhân viên", "Đang làm", ""});
        model.addRow(new Object[]{false, "NV003", "Phạm Văn Cường", "Nam", "0923456789", "Kho", "Nhân viên", "Đang làm", ""});
        model.addRow(new Object[]{false, "NV004", "Lê Thị Dung", "Nữ", "0934567890", "Bán hàng", "Nhân viên", "Nghỉ phép", ""});
        model.addRow(new Object[]{false, "NV005", "Hoàng Minh Đức", "Nam", "0945678901", "Kế toán", "Phó phòng", "Đang làm", ""});

        scrollPane = new JScrollPane(table);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);
    }


//HÀM MAIN ĐỂ CHẠY CHƯƠNG TRÌNH


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmployeePanel().setVisible(true);
        });
    }
}
