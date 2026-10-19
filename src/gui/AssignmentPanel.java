package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AssignmentPanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaPC, txtNgayLamViec, txtGhiChu, txtSearch;
    private JComboBox<String> cboNhanVien, cboCaLamViec, cboTrangThai, cboFilterShift, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnQuanLyCa, btnExport;
    private DefaultTableModel model;
    private JTable table;

    public AssignmentPanel() {
        setTitle("Điện Máy Đỏ — Phân Công Ca Làm Việc");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. TIÊU ĐỀ PHỤ + FORM PHÂN CÔNG CA LÀM
        JPanel pTop = new JPanel(new BorderLayout(0, 10));

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("📅 Quản lý Phân Công Ca Làm Việc");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);
        btnQuanLyCa = new JButton("🕒 Quản lý Ca làm việc");
        btnQuanLyCa.setBackground(new Color(180, 40, 40));
        btnQuanLyCa.setForeground(Color.WHITE);
        btnQuanLyCa.setFocusPainted(false);

        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);

        pSubActions.add(btnQuanLyCa);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập phân công
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("📋 Thông tin phân công ca làm"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã phân công (*):"));
        txtMaPC = new JTextField("PC001");
        pGrid.add(txtMaPC);

        pGrid.add(new JLabel("Nhân viên (*):"));
        cboNhanVien = new JComboBox<>(new String[]{
            "NV001 - Nguyễn Văn An",
            "NV002 - Trần Thị Bình",
            "NV003 - Phạm Văn Cường",
            "NV004 - Lê Thị Dung",
            "NV005 - Hoàng Minh Đức"
        });
        pGrid.add(cboNhanVien);

        pGrid.add(new JLabel("Ca làm việc (*):"));
        cboCaLamViec = new JComboBox<>(new String[]{
            "CA01 - Ca sáng (08:00-12:00)",
            "CA02 - Ca chiều (13:00-17:00)",
            "CA03 - Ca tối (18:00-22:00)",
            "CA04 - Ca hành chính (08:00-17:00)"
        });
        pGrid.add(cboCaLamViec);

        pGrid.add(new JLabel("Ngày làm việc (*):"));
        txtNgayLamViec = new JTextField("2025-05-19");
        pGrid.add(txtNgayLamViec);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đã phân công", "Đã hoàn thành", "Vắng mặt", "Nghỉ phép"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField("Trực quầy chính");
        pGrid.add(txtGhiChu);

        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);

        btnThem = new JButton("+ Phân công");
        btnThem.setBackground(new Color(180, 40, 40));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        btnCapNhat = new JButton("💾 Cập nhật");
        btnCapNhat.setBackground(new Color(24, 119, 242));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);

        btnXoa = new JButton("🗑 Xóa");
        btnXoa.setBackground(new Color(205, 32, 38));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);

        btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.setBackground(new Color(235, 235, 235));
        btnLamMoi.setFocusPainted(false);

        pButtons.add(btnThem);
        pButtons.add(btnCapNhat);
        pButtons.add(btnXoa);
        pButtons.add(btnLamMoi);
        pFormCard.add(pButtons, BorderLayout.SOUTH);

        pTop.add(pFormCard, BorderLayout.CENTER);

        // 2. THANH TÌM KIẾM VÀ LỌC
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField("🔍 Tìm mã PC, tên nhân viên, ngày...", 25);
        cboFilterShift = new JComboBox<>(new String[]{"Tất cả ca làm", "CA01 - Ca sáng", "CA02 - Ca chiều", "CA03 - Ca tối"});
        cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đã phân công", "Đã hoàn thành", "Vắng mặt"});

        pSearch.add(txtSearch);
        pSearch.add(cboFilterShift);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH PHÂN CÔNG
        String[] columns = {"Mã PC", "Nhân viên", "Ca làm việc", "Ngày làm việc", "Trạng thái", "Ghi chú"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(34);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Đổi màu cột Trạng thái
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if ("Đã hoàn thành".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else if ("Đã phân công".equals(val)) {
                    setForeground(new Color(24, 119, 242));
                } else {
                    setForeground(new Color(205, 32, 38));
                }
                return this;
            }
        });

        model.addRow(new Object[]{"PC001", "NV001 - Nguyễn Văn An", "CA01 - Ca sáng (08:00-12:00)", "2025-05-19", "Đã hoàn thành", "Trực quầy chính"});
        model.addRow(new Object[]{"PC002", "NV002 - Trần Thị Bình", "CA02 - Ca chiều (13:00-17:00)", "2025-05-19", "Đã hoàn thành", "Tư vấn khách hàng"});
        model.addRow(new Object[]{"PC003", "NV003 - Phạm Văn Cường", "CA01 - Ca sáng (08:00-12:00)", "2025-05-20", "Đã phân công", "Kiểm kho đầu ngày"});
        model.addRow(new Object[]{"PC004", "NV004 - Lê Thị Dung", "CA03 - Ca tối (18:00-22:00)", "2025-05-20", "Nghỉ phép", "Xin nghỉ có phép"});
        model.addRow(new Object[]{"PC005", "NV005 - Hoàng Minh Đức", "CA04 - Ca hành chính (08:00-17:00)", "2025-05-21", "Đã phân công", "Tổng kết sổ sách"});

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnQuanLyCa.addActionListener(this);
        btnExport.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaPC.setText(model.getValueAt(row, 0).toString());
                    cboNhanVien.setSelectedItem(model.getValueAt(row, 1).toString());
                    cboCaLamViec.setSelectedItem(model.getValueAt(row, 2).toString());
                    txtNgayLamViec.setText(model.getValueAt(row, 3).toString());
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 4).toString());
                    txtGhiChu.setText(model.getValueAt(row, 5).toString());
                }
            }
        });
    }

    private void xoaRong() {
        txtMaPC.setText("");
        cboNhanVien.setSelectedIndex(0);
        cboCaLamViec.setSelectedIndex(0);
        txtNgayLamViec.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
        table.clearSelection();
        txtMaPC.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnThem)) {
            String ma = txtMaPC.getText().trim();
            String ngay = txtNgayLamViec.getText().trim();
            if (ma.isEmpty() || ngay.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phân công và Ngày làm việc!");
                return;
            }
            model.addRow(new Object[]{
                ma,
                cboNhanVien.getSelectedItem().toString(),
                cboCaLamViec.getSelectedItem().toString(),
                ngay,
                cboTrangThai.getSelectedItem().toString(),
                txtGhiChu.getText().trim()
            });
            JOptionPane.showMessageDialog(this, "Phân công ca làm việc thành công!");
            xoaRong();
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng phân công để cập nhật!");
                return;
            }
            model.setValueAt(txtMaPC.getText().trim(), row, 0);
            model.setValueAt(cboNhanVien.getSelectedItem().toString(), row, 1);
            model.setValueAt(cboCaLamViec.getSelectedItem().toString(), row, 2);
            model.setValueAt(txtNgayLamViec.getText().trim(), row, 3);
            model.setValueAt(cboTrangThai.getSelectedItem().toString(), row, 4);
            model.setValueAt(txtGhiChu.getText().trim(), row, 5);
            JOptionPane.showMessageDialog(this, "Cập nhật phân công thành công!");
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phân công cần xóa!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phân công này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                xoaRong();
            }
        } else if (o.equals(btnQuanLyCa)) {
            new WorkSchedulePanel().setVisible(true);
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất bảng phân công ra Excel thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssignmentPanel().setVisible(true));
    }
}
