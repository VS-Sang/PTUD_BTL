package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.NhanVien_DAO;
import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVienPanel extends JFrame implements ActionListener {

    private ThanhMenu menuPanel;
    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtCMND, txtSDT, txtEmail, txtMucLuong, txtDiaChi, txtSearch;
    private JComboBox<String> cboGioiTinh, cboPhongBan, cboChucVu, cboTrangThai, cboDepartment, cboStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTim;
    private JButton btnQuanLyPhongBan, btnQuanLyChucVu, btnPhongBanNhanh, btnChucVuNhanh;
    private DefaultTableModel model;
    private JTable table;
    private boolean dangNapComboBox = false;

    private static final Set<String> dsPhongBanDuPhong = new LinkedHashSet<>(
            Arrays.asList("Bán hàng", "Kế toán", "Kho", "Quản lý"));
    private static final Set<String> dsChucVuDuPhong = new LinkedHashSet<>(
            Arrays.asList("Nhân viên", "Nhân viên kho", "Quản lý", "Thu ngân"));

    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    public NhanVienPanel() {
        this(null, null, null);
    }

    public NhanVienPanel(String userName, String userRole, String username) {
        setTitle("Điện Máy Đỏ — Quản lý Nhân Viên");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(this);

        menuPanel = new ThanhMenu(userName, userRole, username);
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

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pSubActions.setOpaque(false);

        btnQuanLyPhongBan = new JButton("🏢 Quản lý phòng ban");
        btnQuanLyPhongBan.setBackground(new Color(180, 40, 40));
        btnQuanLyPhongBan.setForeground(Color.WHITE);
        btnQuanLyPhongBan.setFocusPainted(false);

        btnQuanLyChucVu = new JButton("💼 Quản lý chức vụ");
        btnQuanLyChucVu.setBackground(new Color(24, 119, 242));
        btnQuanLyChucVu.setForeground(Color.WHITE);
        btnQuanLyChucVu.setFocusPainted(false);

        pSubActions.add(btnQuanLyPhongBan);
        pSubActions.add(btnQuanLyChucVu);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
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

        pGrid.add(new JLabel("CCCD/CMND (*):"));
        txtCMND = new JTextField();
        pGrid.add(txtCMND);

        pGrid.add(new JLabel("SĐT (*):"));
        txtSDT = new JTextField();
        pGrid.add(txtSDT);

        pGrid.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pGrid.add(txtEmail);

        pGrid.add(new JLabel("Phòng ban:"));
        cboPhongBan = new JComboBox<>();
        btnPhongBanNhanh = new JButton("⚙");
        btnPhongBanNhanh.setToolTipText("Quản lý phòng ban (Thêm / Sửa / Xóa)");
        btnPhongBanNhanh.setBackground(new Color(242, 242, 242));
        btnPhongBanNhanh.setFocusPainted(false);
        btnPhongBanNhanh.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JPanel pPhongBanWrap = new JPanel(new BorderLayout(4, 0));
        pPhongBanWrap.setBackground(Color.WHITE);
        pPhongBanWrap.add(cboPhongBan, BorderLayout.CENTER);
        pPhongBanWrap.add(btnPhongBanNhanh, BorderLayout.EAST);
        pGrid.add(pPhongBanWrap);

        pGrid.add(new JLabel("Chức vụ:"));
        cboChucVu = new JComboBox<>();
        btnChucVuNhanh = new JButton("⚙");
        btnChucVuNhanh.setToolTipText("Quản lý chức vụ (Thêm / Sửa / Xóa)");
        btnChucVuNhanh.setBackground(new Color(242, 242, 242));
        btnChucVuNhanh.setFocusPainted(false);
        btnChucVuNhanh.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JPanel pChucVuWrap = new JPanel(new BorderLayout(4, 0));
        pChucVuWrap.setBackground(Color.WHITE);
        pChucVuWrap.add(cboChucVu, BorderLayout.CENTER);
        pChucVuWrap.add(btnChucVuNhanh, BorderLayout.EAST);
        pGrid.add(pChucVuWrap);

        pGrid.add(new JLabel("Mức lương (VNĐ):"));
        txtMucLuong = new JTextField("7500000");
        pGrid.add(txtMucLuong);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pGrid.add(txtDiaChi);

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
        txtSearch.setToolTipText("Tìm tên, mã NV, CCCD/CMND, SĐT...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);

        cboDepartment = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboDepartment);
        pSearch.add(cboStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH NHÂN VIÊN
        String[] columns = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "CCCD/CMND", "SĐT", "Email", "Phòng ban", "Chức vụ", "Mức lương", "Địa chỉ", "Trạng thái"};
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
        table.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
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

        napDanhSachPhongBanVaChucVu();
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
        btnQuanLyPhongBan.addActionListener(this);
        btnQuanLyChucVu.addActionListener(this);
        btnPhongBanNhanh.addActionListener(this);
        btnChucVuNhanh.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(model.getValueAt(row, 0).toString());
                    txtHoTen.setText(model.getValueAt(row, 1).toString());
                    cboGioiTinh.setSelectedItem(model.getValueAt(row, 2).toString());
                    txtNgaySinh.setText(model.getValueAt(row, 3).toString());
                    txtCMND.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                    txtSDT.setText(model.getValueAt(row, 5).toString());
                    txtEmail.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");
                    chonHoacThemItem(cboPhongBan, model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "");
                    chonHoacThemItem(cboChucVu, model.getValueAt(row, 8) != null ? model.getValueAt(row, 8).toString() : "");
                    txtMucLuong.setText(model.getValueAt(row, 9).toString());
                    txtDiaChi.setText(model.getValueAt(row, 10) != null ? model.getValueAt(row, 10).toString() : "");
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 11).toString());
                    cboTrangThai.setEnabled(true);
                }
            }
        });
    }

    private void chonHoacThemItem(JComboBox<String> comboBox, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (value.equalsIgnoreCase(comboBox.getItemAt(i))) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.addItem(value);
        comboBox.setSelectedItem(value);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String chucVuLoc = cboDepartment.getSelectedItem() != null
                ? cboDepartment.getSelectedItem().toString()
                : "Tất cả chức vụ";
        String trangThai = cboStatus.getSelectedItem() != null
                ? cboStatus.getSelectedItem().toString()
                : "Tất cả trạng thái";

        ArrayList<NhanVien> ds = nv_dao.getAllNhanVien();
        for (NhanVien nv : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || nv.getMaNhanVien().toLowerCase().contains(tuKhoa)
                    || nv.getHoTen().toLowerCase().contains(tuKhoa)
                    || (nv.getSoCMND() != null && nv.getSoCMND().contains(tuKhoa))
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
                        nv.getSoCMND() != null ? nv.getSoCMND() : "",
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
        txtCMND.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        if (cboPhongBan.getItemCount() > 0) {
            cboPhongBan.setSelectedIndex(0);
        }
        if (cboChucVu.getItemCount() > 0) {
            cboChucVu.setSelectedIndex(0);
        }
        txtMucLuong.setText("7500000");
        cboTrangThai.setSelectedIndex(0);
        cboTrangThai.setEnabled(false);
        txtDiaChi.setText("");
        table.clearSelection();
        txtHoTen.requestFocus();
    }

    private void ensureBangPhongBanVaChucVu(Connection connection) throws SQLException {
        String sql = "IF OBJECT_ID('PhongBan', 'U') IS NULL "
                + "BEGIN "
                + "    CREATE TABLE PhongBan (tenPhongBan NVARCHAR(50) NOT NULL PRIMARY KEY); "
                + "    INSERT INTO PhongBan (tenPhongBan) "
                + "    SELECT DISTINCT LTRIM(RTRIM(phongBan)) FROM NhanVien "
                + "    WHERE phongBan IS NOT NULL AND LTRIM(RTRIM(phongBan)) <> ''; "
                + "    IF NOT EXISTS (SELECT 1 FROM PhongBan) "
                + "        INSERT INTO PhongBan (tenPhongBan) VALUES (N'Bán hàng'), (N'Kế toán'), (N'Kho'), (N'Quản lý'); "
                + "END; "
                + "IF OBJECT_ID('ChucVu', 'U') IS NULL "
                + "BEGIN "
                + "    CREATE TABLE ChucVu (tenChucVu NVARCHAR(50) NOT NULL PRIMARY KEY); "
                + "    INSERT INTO ChucVu (tenChucVu) "
                + "    SELECT DISTINCT LTRIM(RTRIM(chucVu)) FROM NhanVien "
                + "    WHERE chucVu IS NOT NULL AND LTRIM(RTRIM(chucVu)) <> ''; "
                + "    IF NOT EXISTS (SELECT 1 FROM ChucVu) "
                + "        INSERT INTO ChucVu (tenChucVu) VALUES (N'Nhân viên'), (N'Nhân viên kho'), (N'Quản lý'), (N'Thu ngân'); "
                + "END;";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private List<String> layDanhSachDanhMuc(boolean laPhongBan) {
        List<String> ds = new ArrayList<>();
        String sql = laPhongBan
                ? "SELECT tenPhongBan FROM PhongBan ORDER BY tenPhongBan"
                : "SELECT tenChucVu FROM ChucVu ORDER BY tenChucVu";
        try (Connection connection = ConnectDB.getConnection()) {
            ensureBangPhongBanVaChucVu(connection);
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(rs.getString(1));
                }
            }
            Set<String> duPhong = laPhongBan ? dsPhongBanDuPhong : dsChucVuDuPhong;
            duPhong.clear();
            duPhong.addAll(ds);
        } catch (SQLException exception) {
            ds.addAll(laPhongBan ? dsPhongBanDuPhong : dsChucVuDuPhong);
        }
        return ds;
    }

    private int demSoNhanVienTheoDanhMuc(boolean laPhongBan, String ten) {
        String sql = laPhongBan
                ? "SELECT COUNT(*) FROM NhanVien WHERE phongBan = ?"
                : "SELECT COUNT(*) FROM NhanVien WHERE chucVu = ?";
        try (Connection connection = ConnectDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ten);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    private boolean themDanhMuc(boolean laPhongBan, String tenMoi) {
        String ten = tenMoi.trim();
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String cot = laPhongBan ? "tenPhongBan" : "tenChucVu";
        Set<String> duPhong = laPhongBan ? dsPhongBanDuPhong : dsChucVuDuPhong;
        try (Connection connection = ConnectDB.getConnection()) {
            ensureBangPhongBanVaChucVu(connection);
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT COUNT(*) FROM " + bang + " WHERE LOWER(" + cot + ") = LOWER(?)")) {
                check.setString(1, ten);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO " + bang + " (" + cot + ") VALUES (?)")) {
                insert.setString(1, ten);
                boolean ok = insert.executeUpdate() > 0;
                if (ok) {
                    duPhong.add(ten);
                }
                return ok;
            }
        } catch (SQLException exception) {
            for (String item : duPhong) {
                if (item.equalsIgnoreCase(ten)) {
                    return false;
                }
            }
            return duPhong.add(ten);
        }
    }

    private boolean suaDanhMuc(boolean laPhongBan, String tenCu, String tenMoi) {
        String cu = tenCu.trim();
        String moi = tenMoi.trim();
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String cot = laPhongBan ? "tenPhongBan" : "tenChucVu";
        String cotNV = laPhongBan ? "phongBan" : "chucVu";
        Set<String> duPhong = laPhongBan ? dsPhongBanDuPhong : dsChucVuDuPhong;
        try (Connection connection = ConnectDB.getConnection()) {
            ensureBangPhongBanVaChucVu(connection);
            connection.setAutoCommit(false);
            try {
                if (!cu.equalsIgnoreCase(moi)) {
                    try (PreparedStatement check = connection.prepareStatement(
                            "SELECT COUNT(*) FROM " + bang + " WHERE LOWER(" + cot + ") = LOWER(?)")) {
                        check.setString(1, moi);
                        try (ResultSet rs = check.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                connection.rollback();
                                return false;
                            }
                        }
                    }
                }
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE " + bang + " SET " + cot + " = ? WHERE " + cot + " = ?")) {
                    update.setString(1, moi);
                    update.setString(2, cu);
                    if (update.executeUpdate() <= 0) {
                        connection.rollback();
                        return false;
                    }
                }
                try (PreparedStatement updateNV = connection.prepareStatement(
                        "UPDATE NhanVien SET " + cotNV + " = ? WHERE " + cotNV + " = ?")) {
                    updateNV.setString(1, moi);
                    updateNV.setString(2, cu);
                    updateNV.executeUpdate();
                }
                connection.commit();
                duPhong.remove(cu);
                duPhong.add(moi);
                return true;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            if (!cu.equalsIgnoreCase(moi)) {
                for (String item : duPhong) {
                    if (item.equalsIgnoreCase(moi)) {
                        return false;
                    }
                }
            }
            if (duPhong.remove(cu)) {
                duPhong.add(moi);
                return true;
            }
            return false;
        }
    }

    private boolean xoaDanhMuc(boolean laPhongBan, String tenXoa) {
        String ten = tenXoa.trim();
        String bang = laPhongBan ? "PhongBan" : "ChucVu";
        String cot = laPhongBan ? "tenPhongBan" : "tenChucVu";
        Set<String> duPhong = laPhongBan ? dsPhongBanDuPhong : dsChucVuDuPhong;
        try (Connection connection = ConnectDB.getConnection()) {
            ensureBangPhongBanVaChucVu(connection);
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM " + bang + " WHERE " + cot + " = ?")) {
                delete.setString(1, ten);
                boolean ok = delete.executeUpdate() > 0;
                if (ok) {
                    duPhong.remove(ten);
                }
                return ok;
            }
        } catch (SQLException exception) {
            return duPhong.remove(ten);
        }
    }

    private void napDanhSachPhongBanVaChucVu() {
        String chonPhongBan = cboPhongBan.getSelectedItem() != null
                ? cboPhongBan.getSelectedItem().toString() : null;
        String chonChucVu = cboChucVu.getSelectedItem() != null
                ? cboChucVu.getSelectedItem().toString() : null;
        String chonLoc = cboDepartment.getSelectedItem() != null
                ? cboDepartment.getSelectedItem().toString() : "Tất cả chức vụ";

        dangNapComboBox = true;
        try {
            cboPhongBan.removeAllItems();
            cboChucVu.removeAllItems();
            cboDepartment.removeAllItems();
            cboDepartment.addItem("Tất cả chức vụ");

            List<String> dsPhongBan = layDanhSachDanhMuc(true);
            List<String> dsChucVu = layDanhSachDanhMuc(false);

            for (String pb : dsPhongBan) {
                cboPhongBan.addItem(pb);
            }
            for (String cv : dsChucVu) {
                cboChucVu.addItem(cv);
                cboDepartment.addItem(cv);
            }

            if (chonPhongBan != null) {
                cboPhongBan.setSelectedItem(chonPhongBan);
            }
            if (chonChucVu != null) {
                cboChucVu.setSelectedItem(chonChucVu);
            }
            cboDepartment.setSelectedItem(chonLoc);
            if (cboDepartment.getSelectedIndex() < 0 && cboDepartment.getItemCount() > 0) {
                cboDepartment.setSelectedIndex(0);
            }
        } finally {
            dangNapComboBox = false;
        }
    }

    public void moCuaSoQuanLyPhongBan() {
        moCuaSoQuanLyDanhMuc(true);
    }

    public void moCuaSoQuanLyChucVu() {
        moCuaSoQuanLyDanhMuc(false);
    }

    private void moCuaSoQuanLyDanhMuc(boolean laPhongBan) {
        String tenLoai = laPhongBan ? "phòng ban" : "chức vụ";
        String tenLoaiHoa = laPhongBan ? "Phòng Ban" : "Chức Vụ";
        String bieuTuong = laPhongBan ? "🏢" : "💼";

        Component ownerComp = UiDialogOwner.resolve(this);
        Window ownerWin = ownerComp instanceof Window
                ? (Window) ownerComp
                : SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(ownerWin, "Quản lý " + tenLoaiHoa, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(560, 460);
        dialog.setLocationRelativeTo(ownerComp);
        dialog.setLayout(new BorderLayout(0, 10));
        ButtonTooltips.ganTooltip(dialog);

        JPanel pRoot = new JPanel(new BorderLayout(0, 10));
        pRoot.setBackground(new Color(245, 245, 245));
        pRoot.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        // Tiêu đề cửa sổ
        JPanel pHeader = new JPanel(new BorderLayout());
        pHeader.setBackground(Color.WHITE);
        pHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(180, 40, 40)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel lblTitle = new JLabel(bieuTuong + " Quản lý " + tenLoaiHoa);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(180, 40, 40));
        JLabel lblHint = new JLabel("Thêm, sửa, xóa danh sách " + tenLoai + " hiển thị trong ComboBox");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHint.setForeground(new Color(110, 110, 110));
        pHeader.add(lblTitle, BorderLayout.NORTH);
        pHeader.add(lblHint, BorderLayout.SOUTH);
        pRoot.add(pHeader, BorderLayout.NORTH);

        // Form nhập + nút thao tác
        JPanel pCenter = new JPanel(new BorderLayout(0, 10));
        pCenter.setOpaque(false);

        JPanel pForm = new JPanel(new BorderLayout(0, 8));
        pForm.setBackground(Color.WHITE);
        pForm.setBorder(BorderFactory.createTitledBorder("Thông tin " + tenLoai));

        JPanel pInputRow = new JPanel(new BorderLayout(10, 0));
        pInputRow.setBackground(Color.WHITE);
        pInputRow.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));
        JLabel lblTen = new JLabel("Tên " + tenLoai + " (*):");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField txtTen = new JTextField();
        txtTen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pInputRow.add(lblTen, BorderLayout.WEST);
        pInputRow.add(txtTen, BorderLayout.CENTER);
        pForm.add(pInputRow, BorderLayout.CENTER);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pBtns.setBackground(Color.WHITE);

        JButton btnThemMuc = new JButton("+ Thêm");
        btnThemMuc.setBackground(new Color(180, 40, 40));
        btnThemMuc.setForeground(Color.WHITE);
        btnThemMuc.setFocusPainted(false);

        JButton btnSuaMuc = new JButton("💾 Sửa");
        btnSuaMuc.setBackground(new Color(24, 119, 242));
        btnSuaMuc.setForeground(Color.WHITE);
        btnSuaMuc.setFocusPainted(false);

        JButton btnXoaMuc = new JButton("🗑 Xóa");
        btnXoaMuc.setBackground(new Color(205, 32, 38));
        btnXoaMuc.setForeground(Color.WHITE);
        btnXoaMuc.setFocusPainted(false);

        JButton btnLamMoiMuc = new JButton("↻ Làm mới");
        btnLamMoiMuc.setBackground(new Color(235, 235, 235));
        btnLamMoiMuc.setFocusPainted(false);

        JButton btnDong = new JButton("Đóng");
        btnDong.setBackground(new Color(235, 235, 235));
        btnDong.setFocusPainted(false);

        pBtns.add(btnThemMuc);
        pBtns.add(btnSuaMuc);
        pBtns.add(btnXoaMuc);
        pBtns.add(btnLamMoiMuc);
        pBtns.add(btnDong);
        pForm.add(pBtns, BorderLayout.SOUTH);

        pCenter.add(pForm, BorderLayout.NORTH);

        // Bảng danh sách
        String[] cols = {"STT", "Tên " + tenLoai, "Số nhân viên"};
        DefaultTableModel dialogModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable dialogTable = new JTable(dialogModel);
        dialogTable.setRowHeight(32);
        dialogTable.getTableHeader().setReorderingAllowed(false);
        dialogTable.getTableHeader().setBackground(new Color(180, 40, 40));
        dialogTable.getTableHeader().setForeground(Color.WHITE);
        dialogTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialogTable.getColumnModel().getColumn(0).setMaxWidth(60);
        dialogTable.getColumnModel().getColumn(2).setMaxWidth(120);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        dialogTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(dialogTable);
        scroll.setPreferredSize(new Dimension(500, 220));
        pCenter.add(scroll, BorderLayout.CENTER);

        pRoot.add(pCenter, BorderLayout.CENTER);
        dialog.setContentPane(pRoot);

        Runnable napBangDialog = () -> {
            dialogModel.setRowCount(0);
            List<String> ds = layDanhSachDanhMuc(laPhongBan);
            int stt = 1;
            for (String item : ds) {
                int soNV = demSoNhanVienTheoDanhMuc(laPhongBan, item);
                dialogModel.addRow(new Object[]{stt++, item, soNV});
            }
        };

        napBangDialog.run();

        dialogTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = dialogTable.getSelectedRow();
                if (row >= 0) {
                    txtTen.setText(dialogModel.getValueAt(row, 1).toString());
                }
            }
        });

        ActionListener themAction = ev -> {
            String tenMoi = txtTen.getText().trim();
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên " + tenLoai + "!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtTen.requestFocus();
                return;
            }
            if (tenMoi.length() > 50) {
                JOptionPane.showMessageDialog(dialog, "Tên " + tenLoai + " không được vượt quá 50 ký tự!",
                        "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (themDanhMuc(laPhongBan, tenMoi)) {
                napBangDialog.run();
                napDanhSachPhongBanVaChucVu();
                if (laPhongBan) {
                    cboPhongBan.setSelectedItem(tenMoi);
                } else {
                    cboChucVu.setSelectedItem(tenMoi);
                }
                txtTen.setText("");
                dialogTable.clearSelection();
                JOptionPane.showMessageDialog(dialog, "Đã thêm " + tenLoai + " \"" + tenMoi + "\" thành công!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Tên " + tenLoai + " \"" + tenMoi + "\" đã tồn tại!",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        };

        btnThemMuc.addActionListener(themAction);
        txtTen.addActionListener(themAction);

        btnSuaMuc.addActionListener(ev -> {
            int row = dialogTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một " + tenLoai + " trong bảng để sửa!",
                        "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String tenCu = dialogModel.getValueAt(row, 1).toString();
            String tenMoi = txtTen.getText().trim();
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên " + tenLoai + " mới!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtTen.requestFocus();
                return;
            }
            if (tenMoi.length() > 50) {
                JOptionPane.showMessageDialog(dialog, "Tên " + tenLoai + " không được vượt quá 50 ký tự!",
                        "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (tenCu.equals(tenMoi)) {
                JOptionPane.showMessageDialog(dialog, "Tên " + tenLoai + " chưa thay đổi!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (suaDanhMuc(laPhongBan, tenCu, tenMoi)) {
                napBangDialog.run();
                napDanhSachPhongBanVaChucVu();
                if (laPhongBan) {
                    cboPhongBan.setSelectedItem(tenMoi);
                } else {
                    cboChucVu.setSelectedItem(tenMoi);
                }
                docDuLieuDatabaseVaoTable();
                txtTen.setText("");
                dialogTable.clearSelection();
                JOptionPane.showMessageDialog(dialog,
                        "Đã cập nhật " + tenLoai + " từ \"" + tenCu + "\" thành \"" + tenMoi + "\"!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Không thể cập nhật (tên mới đã tồn tại)!",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoaMuc.addActionListener(ev -> {
            int row = dialogTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn " + tenLoai + " cần xóa trong bảng!",
                        "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String tenXoa = dialogModel.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc muốn xóa " + tenLoai + " \"" + tenXoa + "\" khỏi danh sách ComboBox?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (xoaDanhMuc(laPhongBan, tenXoa)) {
                    napBangDialog.run();
                    napDanhSachPhongBanVaChucVu();
                    txtTen.setText("");
                    dialogTable.clearSelection();
                    JOptionPane.showMessageDialog(dialog, "Đã xóa " + tenLoai + " \"" + tenXoa + "\" thành công!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Xóa " + tenLoai + " thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLamMoiMuc.addActionListener(ev -> {
            txtTen.setText("");
            dialogTable.clearSelection();
            napBangDialog.run();
            txtTen.requestFocus();
        });

        btnDong.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }

    private Date parseNgaySinh() throws ParseException {
        String text = txtNgaySinh.getText().trim();
        sdfDate.setLenient(false);
        Date ngaySinh = sdfDate.parse(text);
        if (!ngaySinh.before(new Date())) {
            throw new ParseException("Ngày sinh phải trước ngày hiện tại", 0);
        }
        return ngaySinh;
    }

    private double parseLuong() {
        double luong = Double.parseDouble(txtMucLuong.getText().trim());
        if (!Double.isFinite(luong) || luong < 0) {
            throw new NumberFormatException("Mức lương không hợp lệ");
        }
        return luong;
    }

    private void validateEmployeeFields(String hoTen, String cmnd, String sdt) {
        if (hoTen.length() < 2 || hoTen.length() > 100) {
            throw new IllegalArgumentException("Họ tên phải từ 2 đến 100 ký tự.");
        }
        if (!cmnd.matches("\\d{9}|\\d{12}")) {
            throw new IllegalArgumentException("Số CCCD/CMND phải gồm 9 hoặc 12 chữ số.");
        }
        if (!sdt.matches("0\\d{9,10}")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10-11 chữ số và bắt đầu bằng 0.");
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        if (cboPhongBan.getSelectedItem() == null || cboPhongBan.getSelectedItem().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn hoặc thêm phòng ban.");
        }
        if (cboChucVu.getSelectedItem() == null || cboChucVu.getSelectedItem().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn hoặc thêm chức vụ.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (dangNapComboBox) {
            return;
        }
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnQuanLyPhongBan) || o.equals(btnPhongBanNhanh)) {
            moCuaSoQuanLyPhongBan();
        } else if (o.equals(btnQuanLyChucVu) || o.equals(btnChucVuNhanh)) {
            moCuaSoQuanLyChucVu();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboDepartment) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String sdt = txtSDT.getText().trim();
            try {
                validateEmployeeFields(hoTen, cmnd, sdt);
                Date ngaySinh = parseNgaySinh();
                double luong = parseLuong();

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(nv_dao.phatSinhMaNhanVien());
                nv.setHoTen(hoTen);
                nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
                nv.setNgaySinh(ngaySinh);
                nv.setSoCMND(cmnd);
                nv.setSoDienThoai(sdt);
                nv.setEmail(txtEmail.getText().trim());
                nv.setPhongBan(cboPhongBan.getSelectedItem().toString());
                nv.setChucVu(cboChucVu.getSelectedItem().toString());
                nv.setLuongCoBan(luong);
                nv.setTrangThai("Đang làm việc");
                nv.setDiaChi(txtDiaChi.getText().trim());

                if (nv_dao.themNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Thêm nhân viên mới thành công!");
                    xoaRong();
                } else {
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Thêm thất bại! Vui lòng kiểm tra CCCD/CMND, SĐT hoặc Email không bị trùng.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), ex.getMessage(),
                        "Dữ liệu nhân viên không hợp lệ", JOptionPane.WARNING_MESSAGE);
            }
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Vui lòng chọn một nhân viên trên bảng để cập nhật!");
                return;
            }
            String hoTen = txtHoTen.getText().trim();
            String cmnd = txtCMND.getText().trim();
            String sdt = txtSDT.getText().trim();
            try {
                validateEmployeeFields(hoTen, cmnd, sdt);
                Date ngaySinh = parseNgaySinh();
                double luong = parseLuong();

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(txtMaNV.getText().trim());
                nv.setHoTen(hoTen);
                nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
                nv.setNgaySinh(ngaySinh);
                nv.setSoCMND(cmnd);
                nv.setSoDienThoai(sdt);
                nv.setEmail(txtEmail.getText().trim());
                nv.setPhongBan(cboPhongBan.getSelectedItem().toString());
                nv.setChucVu(cboChucVu.getSelectedItem().toString());
                nv.setLuongCoBan(luong);
                nv.setTrangThai(cboTrangThai.getSelectedItem().toString());
                nv.setDiaChi(txtDiaChi.getText().trim());

                if (nv_dao.capNhatNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Cập nhật thông tin nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Cập nhật thất bại! Vui lòng kiểm tra CCCD/CMND, SĐT hoặc Email không bị trùng.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), ex.getMessage(),
                        "Dữ liệu nhân viên không hợp lệ", JOptionPane.WARNING_MESSAGE);
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            String maNV = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(UiDialogOwner.resolve(this), "Bạn có chắc muốn xóa nhân viên " + maNV + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (nv_dao.xoaNhanVien(maNV)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Xóa nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(UiDialogOwner.resolve(this), "Không thể xóa nhân viên này (đang có dữ liệu liên quan trong hệ thống)!");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienPanel().setVisible(true));
    }
}
