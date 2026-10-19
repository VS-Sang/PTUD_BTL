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

public class WorkSchedulePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaCa, txtTenCa, txtGioBatDau, txtGioKetThuc, txtSearch;
    private JComboBox<String> cboTrangThai, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnPhanCong, btnExport;
    private DefaultTableModel model;
    private JTable table;

    public WorkSchedulePanel() {
        setTitle("Điện Máy Đỏ — Quản lý Ca Làm Việc");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. TIÊU ĐỀ PHỤ + FORM THÔNG TIN CA LÀM VIỆC
        JPanel pTop = new JPanel(new BorderLayout(0, 10));

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("🕒 Quản lý Ca Làm Việc");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);
        btnPhanCong = new JButton("📅 Mở Phân công ca làm");
        btnPhanCong.setBackground(new Color(180, 40, 40));
        btnPhanCong.setForeground(Color.WHITE);
        btnPhanCong.setFocusPainted(false);

        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);

        pSubActions.add(btnPhanCong);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập thông tin ca làm việc
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("🕒 Thông tin ca làm việc"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã ca (*):"));
        txtMaCa = new JTextField("CA01");
        pGrid.add(txtMaCa);

        pGrid.add(new JLabel("Tên ca (*):"));
        txtTenCa = new JTextField("Ca sáng");
        pGrid.add(txtTenCa);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng áp dụng"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Giờ bắt đầu (*):"));
        txtGioBatDau = new JTextField("08:00");
        pGrid.add(txtGioBatDau);

        pGrid.add(new JLabel("Giờ kết thúc (*):"));
        txtGioKetThuc = new JTextField("12:00");
        pGrid.add(txtGioKetThuc);

        pGrid.add(new JLabel(""));
        pGrid.add(new JLabel(""));

        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);

        btnThem = new JButton("+ Thêm ca");
        btnThem.setBackground(new Color(180, 40, 40));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        btnCapNhat = new JButton("💾 Cập nhật");
        btnCapNhat.setBackground(new Color(24, 119, 242));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);

        btnXoa = new JButton("🗑 Xóa ca");
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

        // 2. THANH TÌM KIẾM
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField("🔍 Tìm mã ca, tên ca...", 25);
        cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Hoạt động", "Ngừng áp dụng"});
        pSearch.add(txtSearch);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH CA LÀM VIỆC
        String[] columns = {"Mã ca", "Tên ca làm việc", "Giờ bắt đầu", "Giờ kết thúc", "Trạng thái"};
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
                if ("Hoạt động".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else {
                    setForeground(new Color(205, 32, 38));
                }
                return this;
            }
        });

        model.addRow(new Object[]{"CA01", "Ca sáng", "08:00", "12:00", "Hoạt động"});
        model.addRow(new Object[]{"CA02", "Ca chiều", "13:00", "17:00", "Hoạt động"});
        model.addRow(new Object[]{"CA03", "Ca tối", "18:00", "22:00", "Hoạt động"});
        model.addRow(new Object[]{"CA04", "Ca hành chính", "08:00", "17:00", "Hoạt động"});
        model.addRow(new Object[]{"CA05", "Ca tăng cường cuối tuần", "09:00", "21:00", "Ngừng áp dụng"});

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnPhanCong.addActionListener(this);
        btnExport.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaCa.setText(model.getValueAt(row, 0).toString());
                    txtTenCa.setText(model.getValueAt(row, 1).toString());
                    txtGioBatDau.setText(model.getValueAt(row, 2).toString());
                    txtGioKetThuc.setText(model.getValueAt(row, 3).toString());
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 4).toString());
                }
            }
        });
    }

    private void xoaRong() {
        txtMaCa.setText("");
        txtTenCa.setText("");
        txtGioBatDau.setText("");
        txtGioKetThuc.setText("");
        cboTrangThai.setSelectedIndex(0);
        table.clearSelection();
        txtMaCa.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnThem)) {
            String ma = txtMaCa.getText().trim();
            String ten = txtTenCa.getText().trim();
            String bd = txtGioBatDau.getText().trim();
            String kt = txtGioKetThuc.getText().trim();
            if (ma.isEmpty() || ten.isEmpty() || bd.isEmpty() || kt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin ca làm việc!");
                return;
            }
            model.addRow(new Object[]{ma, ten, bd, kt, cboTrangThai.getSelectedItem().toString()});
            JOptionPane.showMessageDialog(this, "Thêm ca làm việc thành công!");
            xoaRong();
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca trên bảng để cập nhật!");
                return;
            }
            model.setValueAt(txtMaCa.getText().trim(), row, 0);
            model.setValueAt(txtTenCa.getText().trim(), row, 1);
            model.setValueAt(txtGioBatDau.getText().trim(), row, 2);
            model.setValueAt(txtGioKetThuc.getText().trim(), row, 3);
            model.setValueAt(cboTrangThai.getSelectedItem().toString(), row, 4);
            JOptionPane.showMessageDialog(this, "Cập nhật ca làm việc thành công!");
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ca cần xóa!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ca này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                xoaRong();
            }
        } else if (o.equals(btnPhanCong)) {
            new AssignmentPanel().setVisible(true);
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất danh sách ca làm việc ra Excel thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkSchedulePanel().setVisible(true));
    }
}
