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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

import DAO.CaLamViec_DAO;
import entity.CaLamViec;

public class WorkSchedulePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private String currentUserName;
    private String currentUserRole;
    private String currentUsername;
    private JTextField txtMaCa, txtTenCa, txtGioBatDau, txtGioKetThuc, txtMoTa, txtSearch;
    private JComboBox<String> cboTrangThai, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnPhanCong, btnTim;
    private DefaultTableModel model;
    private JTable table;

    private CaLamViec_DAO ca_dao = new CaLamViec_DAO();
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    public WorkSchedulePanel() {
        this(null, null, null);
    }

    public WorkSchedulePanel(String userName, String userRole) {
        this(userName, userRole, null);
    }

    public WorkSchedulePanel(String userName, String userRole, String username) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;
        this.currentUsername = username;
        sdfTime.setLenient(false);

        setTitle("Điện Máy Đỏ — Quản lý Ca Làm Việc");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel(userName, userRole, username);
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

        pSubActions.add(btnPhanCong);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập thông tin ca làm việc
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("🕒 Thông tin ca làm việc"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã ca:"));
        txtMaCa = new JTextField();
        txtMaCa.setEditable(false);
        txtMaCa.setBackground(new Color(242, 242, 242));
        pGrid.add(txtMaCa);

        pGrid.add(new JLabel("Tên ca (*):"));
        txtTenCa = new JTextField();
        pGrid.add(txtTenCa);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng sử dụng"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Giờ bắt đầu (HH:mm):"));
        txtGioBatDau = new JTextField("08:00");
        pGrid.add(txtGioBatDau);

        pGrid.add(new JLabel("Giờ kết thúc (HH:mm):"));
        txtGioKetThuc = new JTextField("12:00");
        pGrid.add(txtGioKetThuc);

        pGrid.add(new JLabel("Mô tả:"));
        txtMoTa = new JTextField();
        pGrid.add(txtMoTa);

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
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm mã ca, tên ca...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);
        cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Hoạt động", "Ngừng sử dụng"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH CA LÀM VIỆC
        String[] columns = {"Mã ca", "Tên ca làm việc", "Giờ bắt đầu", "Giờ kết thúc", "Mô tả", "Trạng thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Đổi màu cột Trạng thái
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        docDuLieuDatabaseVaoTable();
        xoaRong();

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnPhanCong.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboFilterStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaCa.setText(model.getValueAt(row, 0).toString());
                    txtTenCa.setText(model.getValueAt(row, 1).toString());
                    txtGioBatDau.setText(model.getValueAt(row, 2).toString());
                    txtGioKetThuc.setText(model.getValueAt(row, 3).toString());
                    txtMoTa.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 5).toString());
                }
            }
        });
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String trangThai = cboFilterStatus.getSelectedItem().toString();

        ArrayList<CaLamViec> ds = ca_dao.getAllCaLamViec();
        for (CaLamViec ca : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || ca.getMaCa().toLowerCase().contains(tuKhoa)
                    || ca.getTenCa().toLowerCase().contains(tuKhoa);
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(ca.getTrangThai());

            if (matchKey && matchStatus) {
                model.addRow(new Object[]{
                        ca.getMaCa(),
                        ca.getTenCa(),
                        ca.getGioVao() != null ? sdfTime.format(ca.getGioVao()) : "",
                        ca.getGioRa() != null ? sdfTime.format(ca.getGioRa()) : "",
                        ca.getMoTa() != null ? ca.getMoTa() : "",
                        ca.getTrangThai()
                });
            }
        }
    }

    private void xoaRong() {
        txtMaCa.setText(ca_dao.phatSinhMaCa());
        txtTenCa.setText("");
        txtGioBatDau.setText("08:00");
        txtGioKetThuc.setText("12:00");
        txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0);
        table.clearSelection();
        txtTenCa.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboFilterStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            String ten = txtTenCa.getText().trim();
            String bd = txtGioBatDau.getText().trim();
            String kt = txtGioKetThuc.getText().trim();
            if (ten.isEmpty() || bd.isEmpty() || kt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin ca làm việc!");
                return;
            }
            try {
                Date gioVao = sdfTime.parse(bd);
                Date gioRa = sdfTime.parse(kt);

                String ma = ca_dao.phatSinhMaCa();
                CaLamViec ca = new CaLamViec(ma, ten, gioVao, gioRa, txtMoTa.getText().trim(),
                        cboTrangThai.getSelectedItem().toString(), new Date());
                if (ca_dao.themCaLamViec(ca)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Thêm ca làm việc thành công!");
                    xoaRong();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Mã ca đã tồn tại)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giờ bắt đầu / kết thúc phải có định dạng HH:mm (VD: 08:00)!");
            }
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca trên bảng để cập nhật!");
                return;
            }
            String ten = txtTenCa.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên ca làm việc!");
                return;
            }
            try {
                Date gioVao = sdfTime.parse(txtGioBatDau.getText().trim());
                Date gioRa = sdfTime.parse(txtGioKetThuc.getText().trim());
                CaLamViec ca = new CaLamViec(txtMaCa.getText().trim(), ten,
                        gioVao, gioRa, txtMoTa.getText().trim(),
                        cboTrangThai.getSelectedItem().toString(), new Date());
                if (ca_dao.capNhatCaLamViec(ca)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giờ bắt đầu / kết thúc phải có định dạng HH:mm!");
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ca cần xóa!");
                return;
            }
            String maCa = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ca " + maCa + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (ca_dao.xoaCaLamViec(maCa)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(this, "Xóa ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa ca này (đã có phân công nhân viên)!");
                }
            }
        } else if (o.equals(btnPhanCong)) {
            dispose();
            new AssignmentPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkSchedulePanel().setVisible(true));
    }
}
