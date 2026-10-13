package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InvoicePanel extends JFrame {
    private final DefaultTableModel model;
    private final JTable table;

    public InvoicePanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel pTopContainer = new JPanel(new BorderLayout());

        JPanel pHeaderRed = new JPanel(new BorderLayout());
        pHeaderRed.setBackground(new Color(180, 40, 40));
        pHeaderRed.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel lblSysTitle = new JLabel("  ⚡ Điện Máy Đỏ — Hệ Thống Quản Lý");
        lblSysTitle.setForeground(Color.WHITE);
        lblSysTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pHeaderRed.add(lblSysTitle, BorderLayout.WEST);

        JPanel pUserRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pUserRight.setOpaque(false);
        JLabel lblUser = new JLabel("👤 Nguyễn Văn An");
        lblUser.setForeground(Color.WHITE);
        JButton btnRole = new JButton("Quản lý");
        btnRole.setMargin(new Insets(2, 5, 2, 5));
        pUserRight.add(lblUser);
        pUserRight.add(btnRole);
        pHeaderRed.add(pUserRight, BorderLayout.EAST);

        JPanel pMenuBar = new JPanel(new BorderLayout());
        pMenuBar.setBackground(new Color(240, 240, 240));
        pMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel pMenuLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        pMenuLeft.setOpaque(false);
        pMenuLeft.add(new JButton("⚙ Hệ thống ▾"));
        pMenuLeft.add(new JButton("📁 Danh mục ▾"));
        pMenuLeft.add(new JButton("📋 Xử lý ▾"));
        pMenuLeft.add(new JButton("🔍 Tìm kiếm ▾"));
        pMenuLeft.add(new JButton("📊 Thống kê ▾"));
        pMenuLeft.add(new JButton("👤 Tài khoản ▾"));

        JPanel pMenuRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
        pMenuRight.setOpaque(false);
        pMenuRight.add(new JButton("🏠"));
        pMenuRight.add(new JButton("🖩"));
        pMenuRight.add(new JButton("📊"));
        pMenuRight.add(new JButton("🚪"));

        pMenuBar.add(pMenuLeft, BorderLayout.WEST);
        pMenuBar.add(pMenuRight, BorderLayout.EAST);
        pTopContainer.add(pHeaderRed, BorderLayout.NORTH);
        pTopContainer.add(pMenuBar, BorderLayout.CENTER);
        add(pTopContainer, BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("🧾 Quản lý Hoá Đơn");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);

        JButton btnCreate = new JButton("+ Tạo hóa đơn");
        btnCreate.setBackground(new Color(180, 40, 40));
        btnCreate.setForeground(Color.WHITE);
        JButton btnExport = new JButton("Xuất Excel");
        JButton btnPrint = new JButton("In báo cáo");
        btnPrint.setBackground(new Color(55, 105, 190));
        btnPrint.setForeground(Color.WHITE);
        pSubActions.add(btnCreate);
        pSubActions.add(btnExport);
        pSubActions.add(btnPrint);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JTextField txtSearch = new JTextField("⌕ Tìm mã HĐ, khách hàng...", 25);
        JComboBox<String> cboStatus = new JComboBox<>(
                new String[]{"Tất cả trạng thái", "Hoàn thành", "Đang xử lý", "Chờ xác nhận"});
        JComboBox<String> cboMonth = new JComboBox<>(
                new String[]{"Tất cả tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                        "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9",
                        "Tháng 10", "Tháng 11", "Tháng 12"});
        pSearch.add(txtSearch);
        pSearch.add(cboStatus);
        pSearch.add(cboMonth);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] columns = {"", "Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên",
                "Tổng tiền", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(columns, 0) {
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
        SwingUtilities.invokeLater(() -> new InvoicePanel().setVisible(true));
    }
}
