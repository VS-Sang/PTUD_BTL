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

import DAO.NhanVien_DAO;
import entity.NhanVien;

public class EmployeePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtSDT, txtEmail, txtMucLuong, txtDiaChi, txtSearch;
    private JComboBox<String> cboGioiTinh, cboPhongBan, cboChucVu, cboTrangThai, cboDepartment, cboStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTim;
    private DefaultTableModel model;
    private JTable table;

    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    public EmployeePanel() {
        setTitle("Điện Máy Đỏ — Quản lý Nhân Viên");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. TIÊU ĐỀ PHỤ + FORM THÔNG TIN NHÂN VIÊN
        JPanel pTop = new JPanel(new BorderLayout(0, 10));

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("👥 Quản lý Nhân Viên");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập thông tin nhân viên
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("👤 Thông tin nhân viên"));

        JPanel pGrid = new JPanel(new GridLayout(4, 6, 12, 8));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(242, 242, 242));
        pGrid.add(txtMaNV);

        pGrid.add(new JLabel("Họ và tên (*):"));
        txtHoTen = new JTextField();
        pGrid.add(txtHoTen);

        pGrid.add(new JLabel("Giới tính:"));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        pGrid.add(cboGioiTinh);

        pGrid.add(new JLabel("Ngày sinh (yyyy-MM-dd):"));
        txtNgaySinh = new JTextField("1995-01-01");
        pGrid.add(txtNgaySinh);

        pGrid.add(new JLabel("SĐT (*):"));
        txtSDT = new JTextField();
        pGrid.add(txtSDT);

        pGrid.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pGrid.add(txtEmail);

        pGrid.add(new JLabel("Phòng ban:"));
        cboPhongBan = new JComboBox<>(new String[]{"Bán hàng", "Quản lý"});
        pGrid.add(cboPhongBan);

        pGrid.add(new JLabel("Chức vụ:"));
        cboChucVu = new JComboBox<>(new String[]{"Nhân viên bán hàng", "Quản lý"});
        pGrid.add(cboChucVu);

        pGrid.add(new JLabel("Mức lương (VNĐ):"));
        txtMucLuong = new JTextField("7500000");
        pGrid.add(txtMucLuong);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pGrid.add(txtDiaChi);

        pGrid.add(new JLabel(""));
        pGrid.add(new JLabel(""));

        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);

        btnThem = new JButton("+ Thêm nhân viên");
        btnThem.setBackground(new Color(180, 40, 40));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        btnCapNhat = new JButton("💾 Cập nhật");
        btnCapNhat.setBackground(new Color(24, 119, 242));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);

        btnXoa = new JButton("🗑 Xóa nhân viên");
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

        // 2. THANH TÌM KIẾM VÀ BỘ LỌC
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm tên, mã NV, SĐT...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);

        cboDepartment = new JComboBox<>(new String[]{"Tất cả chức vụ", "Nhân viên bán hàng", "Quản lý"});
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboDepartment);
        pSearch.add(cboStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH NHÂN VIÊN
        String[] columns = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Email", "Phòng ban", "Chức vụ", "Mức lương", "Địa chỉ", "Trạng thái"};
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

        // Đổi màu chữ cột Trạng thái
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if ("Đang làm việc".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else if ("Tạm nghỉ".equals(val)) {
                    setForeground(new Color(230, 81, 0));
                } else {
                    setForeground(new Color(205, 32, 38));
                }
                return this;
            }
        });

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database vào bảng
        docDuLieuDatabaseVaoTable();
        xoaRong();

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboDepartment.addActionListener(this);
        cboStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(model.getValueAt(row, 0).toString());
                    txtHoTen.setText(model.getValueAt(row, 1).toString());
                    cboGioiTinh.setSelectedItem(model.getValueAt(row, 2).toString());
                    txtNgaySinh.setText(model.getValueAt(row, 3).toString());
                    txtSDT.setText(model.getValueAt(row, 4).toString());
                    txtEmail.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                    cboPhongBan.setSelectedItem(model.getValueAt(row, 6).toString());
                    cboChucVu.setSelectedItem(model.getValueAt(row, 7).toString());
                    txtMucLuong.setText(model.getValueAt(row, 8).toString());
                    txtDiaChi.setText(model.getValueAt(row, 9) != null ? model.getValueAt(row, 9).toString() : "");
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 10).toString());
                    cboTrangThai.setEnabled(true);
                }
            }
        });
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String chucVuLoc = cboDepartment.getSelectedItem().toString();
        String trangThai = cboStatus.getSelectedItem().toString();

        ArrayList<NhanVien> ds = nv_dao.getAllNhanVien();
        for (NhanVien nv : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || nv.getMaNhanVien().toLowerCase().contains(tuKhoa)
                    || nv.getHoTen().toLowerCase().contains(tuKhoa)
                    || (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(tuKhoa));
            boolean matchRole = "Tất cả chức vụ".equals(chucVuLoc)
                    || chucVuLoc.equalsIgnoreCase(nv.getChucVu())
                    || chucVuLoc.equalsIgnoreCase(nv.getPhongBan());
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(nv.getTrangThai());

            if (matchKey && matchRole && matchStatus) {
                model.addRow(new Object[]{
                        nv.getMaNhanVien(),
                        nv.getHoTen(),
                        nv.getGioiTinh(),
                        nv.getNgaySinh() != null ? sdfDate.format(nv.getNgaySinh()) : "",
                        nv.getSoDienThoai(),
                        nv.getEmail() != null ? nv.getEmail() : "",
                        nv.getPhongBan(),
                        nv.getChucVu(),
                        String.format("%.0f", nv.getLuongCoBan()),
                        nv.getDiaChi() != null ? nv.getDiaChi() : "",
                        nv.getTrangThai()
                });
            }
        }
    }

    private void xoaRong() {
        txtMaNV.setText(nv_dao.phatSinhMaNhanVien());
        txtHoTen.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("1995-01-01");
        txtSDT.setText("");
        txtEmail.setText("");
        cboPhongBan.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        txtMucLuong.setText("7500000");
        cboTrangThai.setSelectedIndex(0);
        cboTrangThai.setEnabled(false);
        txtDiaChi.setText("");
        table.clearSelection();
        txtHoTen.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboDepartment) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            if (hoTen.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và SĐT!");
                return;
            }
            try {
                Date ngaySinh = sdfDate.parse(txtNgaySinh.getText().trim());
                double luong = Double.parseDouble(txtMucLuong.getText().trim().isEmpty() ? "0" : txtMucLuong.getText().trim());

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(nv_dao.phatSinhMaNhanVien());
                nv.setHoTen(hoTen);
                nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
                nv.setNgaySinh(ngaySinh);
                nv.setSoDienThoai(sdt);
                nv.setEmail(txtEmail.getText().trim());
                nv.setPhongBan(cboPhongBan.getSelectedItem().toString());
                nv.setChucVu(cboChucVu.getSelectedItem().toString());
                nv.setLuongCoBan(luong);
                nv.setTrangThai("Đang làm việc");
                nv.setDiaChi(txtDiaChi.getText().trim());

                if (nv_dao.themNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên mới thành công!");
                    xoaRong();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại! Vui lòng kiểm tra SĐT/Email không bị trùng.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng Ngày sinh (yyyy-MM-dd) hoặc Mức lương không hợp lệ!");
            }
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên trên bảng để cập nhật!");
                return;
            }
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            if (hoTen.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và SĐT!");
                return;
            }
            try {
                Date ngaySinh = sdfDate.parse(txtNgaySinh.getText().trim());
                double luong = Double.parseDouble(txtMucLuong.getText().trim().isEmpty() ? "0" : txtMucLuong.getText().trim());

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(txtMaNV.getText().trim());
                nv.setHoTen(hoTen);
                nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
                nv.setNgaySinh(ngaySinh);
                nv.setSoDienThoai(sdt);
                nv.setEmail(txtEmail.getText().trim());
                nv.setPhongBan(cboPhongBan.getSelectedItem().toString());
                nv.setChucVu(cboChucVu.getSelectedItem().toString());
                nv.setLuongCoBan(luong);
                nv.setTrangThai(cboTrangThai.getSelectedItem().toString());
                nv.setDiaChi(txtDiaChi.getText().trim());

                if (nv_dao.capNhatNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Vui lòng kiểm tra SĐT/Email không bị trùng.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng Ngày sinh (yyyy-MM-dd) hoặc Mức lương không hợp lệ!");
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            String maNV = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên " + maNV + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (nv_dao.xoaNhanVien(maNV)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên này (đang có dữ liệu liên quan trong hệ thống)!");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeePanel().setVisible(true));
    }
}
