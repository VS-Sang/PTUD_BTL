package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import entity.NhanVien;

public class NhanVienPanel extends JFrame implements ActionListener {

    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtCMND, txtSDT, txtEmail, txtMucLuong, txtDiaChi, txtSearch;
    private JComboBox<String> cboGioiTinh, cboPhongBan, cboChucVu, cboTrangThai, cboDepartment, cboStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTim, btnQuanLyPhongBan, btnQuanLyChucVu;
    private DefaultTableModel model;
    private JTable table;
    private boolean dangNapComboBox = false;

    private final NhanVien_DAO nv_dao = new NhanVien_DAO();
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    public NhanVienPanel() {
        this(null, null, null);
    }

    public NhanVienPanel(String userName, String userRole, String username) {
        sdfDate.setLenient(false);
        setTitle("Điện Máy Đỏ — Quản lý Nhân Viên");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(this);

        add(new ThanhMenu(userName, userRole, username), BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. Tiêu đề + Form thông tin nhân viên
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
        btnQuanLyPhongBan = taoNut("🏢 Quản lý phòng ban", new Color(180, 40, 40), Color.WHITE);
        btnQuanLyChucVu = taoNut("💼 Quản lý chức vụ", new Color(24, 119, 242), Color.WHITE);
        pSubActions.add(btnQuanLyPhongBan);
        pSubActions.add(btnQuanLyChucVu);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("👤 Thông tin nhân viên"));

        JPanel pGrid = new JPanel(new GridLayout(4, 6, 12, 8));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(242, 242, 242));
        txtHoTen = new JTextField();
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        txtNgaySinh = new JTextField("1995-01-01");
        txtCMND = new JTextField();
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        cboPhongBan = new JComboBox<>();
        cboChucVu = new JComboBox<>();
        txtMucLuong = new JTextField("7500000");
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        txtDiaChi = new JTextField();

        pGrid.add(new JLabel("Mã nhân viên:"));           pGrid.add(txtMaNV);
        pGrid.add(new JLabel("Họ và tên (*):"));          pGrid.add(txtHoTen);
        pGrid.add(new JLabel("Giới tính:"));              pGrid.add(cboGioiTinh);
        pGrid.add(new JLabel("Ngày sinh (yyyy-MM-dd):")); pGrid.add(txtNgaySinh);
        pGrid.add(new JLabel("CCCD/CMND (*):"));          pGrid.add(txtCMND);
        pGrid.add(new JLabel("SĐT (*):"));                pGrid.add(txtSDT);
        pGrid.add(new JLabel("Email:"));                  pGrid.add(txtEmail);
        pGrid.add(new JLabel("Phòng ban:"));              pGrid.add(cboPhongBan);
        pGrid.add(new JLabel("Chức vụ:"));                pGrid.add(cboChucVu);
        pGrid.add(new JLabel("Mức lương (VNĐ):"));        pGrid.add(txtMucLuong);
        pGrid.add(new JLabel("Trạng thái:"));             pGrid.add(cboTrangThai);
        pGrid.add(new JLabel("Địa chỉ:"));                pGrid.add(txtDiaChi);
        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);
        btnThem = taoNut("+ Thêm nhân viên", new Color(180, 40, 40), Color.WHITE);
        btnCapNhat = taoNut("💾 Cập nhật", new Color(24, 119, 242), Color.WHITE);
        btnXoa = taoNut("🗑 Xóa nhân viên", new Color(205, 32, 38), Color.WHITE);
        btnLamMoi = taoNut("↻ Làm mới", new Color(235, 235, 235), Color.BLACK);
        pButtons.add(btnThem);
        pButtons.add(btnCapNhat);
        pButtons.add(btnXoa);
        pButtons.add(btnLamMoi);
        pFormCard.add(pButtons, BorderLayout.SOUTH);
        pTop.add(pFormCard, BorderLayout.CENTER);

        // 2. Thanh tìm kiếm và lọc
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm tên, mã NV, CCCD/CMND, SĐT...");
        btnTim = taoNut("🔍 Tìm", Color.WHITE, Color.BLACK);
        cboDepartment = new JComboBox<>();
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboDepartment);
        pSearch.add(cboStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);
        pMain.add(pTop, BorderLayout.NORTH);

        // 3. Bảng danh sách nhân viên
        String[] columns = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "CCCD/CMND", "SĐT", "Email", "Phòng ban", "Chức vụ", "Mức lương", "Địa chỉ", "Trạng thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, focus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                String s = val != null ? val.toString() : "";
                setForeground("Đang làm việc".equals(s) ? new Color(46, 125, 50)
                        : "Tạm nghỉ".equals(s) ? new Color(230, 81, 0) : new Color(205, 32, 38));
                return this;
            }
        });

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        napDanhSachPhongBanVaChucVu();
        docDuLieuDatabaseVaoTable();
        xoaRong();

        for (JButton b : new JButton[]{btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTim, btnQuanLyPhongBan, btnQuanLyChucVu}) {
            b.addActionListener(this);
        }
        txtSearch.addActionListener(this);
        cboDepartment.addActionListener(this);
        cboStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(String.valueOf(model.getValueAt(row, 0)));
                    txtHoTen.setText(String.valueOf(model.getValueAt(row, 1)));
                    cboGioiTinh.setSelectedItem(model.getValueAt(row, 2));
                    txtNgaySinh.setText(String.valueOf(model.getValueAt(row, 3)));
                    txtCMND.setText(String.valueOf(model.getValueAt(row, 4)));
                    txtSDT.setText(String.valueOf(model.getValueAt(row, 5)));
                    txtEmail.setText(String.valueOf(model.getValueAt(row, 6)));
                    cboPhongBan.setSelectedItem(model.getValueAt(row, 7));
                    cboChucVu.setSelectedItem(model.getValueAt(row, 8));
                    txtMucLuong.setText(String.valueOf(model.getValueAt(row, 9)));
                    txtDiaChi.setText(String.valueOf(model.getValueAt(row, 10)));
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 11));
                    cboTrangThai.setEnabled(true);
                }
            }
        });
    }

    private JButton taoNut(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        return b;
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim();
        String chucVuLoc = cboDepartment.getSelectedItem() != null ? cboDepartment.getSelectedItem().toString() : "Tất cả chức vụ";
        String trangThai = cboStatus.getSelectedItem() != null ? cboStatus.getSelectedItem().toString() : "Tất cả trạng thái";

        ArrayList<NhanVien> ds;
        if (tuKhoa.isEmpty()) {
            ds = nv_dao.getAllNhanVien();
        } else {
            NhanVien nvTheoMa = nv_dao.getNhanVienTheoMa(tuKhoa);
            if (nvTheoMa != null) {
                ds = new ArrayList<>();
                ds.add(nvTheoMa);
            } else {
                ds = nv_dao.timKiemNhanVien(tuKhoa);
            }
        }

        for (NhanVien nv : ds) {
            boolean matchRole = "Tất cả chức vụ".equals(chucVuLoc)
                    || chucVuLoc.equalsIgnoreCase(nv.getChucVu())
                    || chucVuLoc.equalsIgnoreCase(nv.getPhongBan());
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(nv.getTrangThai());

            if (matchRole && matchStatus) {
                model.addRow(new Object[]{
                        nv.getMaNhanVien(), nv.getHoTen(), nv.getGioiTinh(),
                        nv.getNgaySinh() != null ? sdfDate.format(nv.getNgaySinh()) : "",
                        nv.getSoCMND() != null ? nv.getSoCMND() : "",
                        nv.getSoDienThoai(),
                        nv.getEmail() != null ? nv.getEmail() : "",
                        nv.getPhongBan(), nv.getChucVu(),
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
        if (cboPhongBan.getItemCount() > 0) cboPhongBan.setSelectedIndex(0);
        if (cboChucVu.getItemCount() > 0) cboChucVu.setSelectedIndex(0);
        txtMucLuong.setText("7500000");
        cboTrangThai.setSelectedIndex(0);
        cboTrangThai.setEnabled(false);
        txtDiaChi.setText("");
        table.clearSelection();
        txtHoTen.requestFocus();
    }

    private void napDanhSachPhongBanVaChucVu() {
        dangNapComboBox = true;
        cboPhongBan.removeAllItems();
        cboChucVu.removeAllItems();
        cboDepartment.removeAllItems();
        cboDepartment.addItem("Tất cả chức vụ");

        for (String pb : nv_dao.layDanhSachDanhMuc(true)) cboPhongBan.addItem(pb);
        for (String cv : nv_dao.layDanhSachDanhMuc(false)) {
            cboChucVu.addItem(cv);
            cboDepartment.addItem(cv);
        }
        dangNapComboBox = false;
    }

    private void moCuaSoQuanLyDanhMuc(boolean laPhongBan) {
        String tenLoai = laPhongBan ? "phòng ban" : "chức vụ";
        Component owner = UiDialogOwner.resolve(this);
        Window win = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(win, "Quản lý " + tenLoai, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel pTop = new JPanel(new BorderLayout(6, 6));
        pTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JTextField txtTen = new JTextField();
        pTop.add(new JLabel("Tên " + tenLoai + ":"), BorderLayout.WEST);
        pTop.add(txtTen, BorderLayout.CENTER);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton bThem = taoNut("+ Thêm", new Color(180, 40, 40), Color.WHITE);
        JButton bSua = taoNut("💾 Sửa", new Color(24, 119, 242), Color.WHITE);
        JButton bXoa = taoNut("🗑 Xóa", new Color(205, 32, 38), Color.WHITE);
        JButton bDong = taoNut("Đóng", new Color(235, 235, 235), Color.BLACK);
        pBtns.add(bThem); pBtns.add(bSua); pBtns.add(bXoa); pBtns.add(bDong);
        pTop.add(pBtns, BorderLayout.SOUTH);
        dialog.add(pTop, BorderLayout.NORTH);

        DefaultTableModel m = new DefaultTableModel(new String[]{"STT", "Tên " + tenLoai}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setRowHeight(28);
        t.getColumnModel().getColumn(0).setMaxWidth(60);
        dialog.add(new JScrollPane(t), BorderLayout.CENTER);

        Runnable napBang = () -> {
            m.setRowCount(0);
            int stt = 1;
            for (String item : nv_dao.layDanhSachDanhMuc(laPhongBan)) {
                m.addRow(new Object[]{stt++, item});
            }
        };
        napBang.run();

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = t.getSelectedRow();
                if (r >= 0) txtTen.setText(m.getValueAt(r, 1).toString());
            }
        });

        bThem.addActionListener(ev -> {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) return;
            if (nv_dao.themDanhMuc(laPhongBan, ten)) {
                napBang.run();
                napDanhSachPhongBanVaChucVu();
                txtTen.setText("");
            } else {
                JOptionPane.showMessageDialog(dialog, "Tên " + tenLoai + " đã tồn tại hoặc không hợp lệ!");
            }
        });

        bSua.addActionListener(ev -> {
            int r = t.getSelectedRow();
            String tenMoi = txtTen.getText().trim();
            if (r < 0 || tenMoi.isEmpty()) return;
            if (nv_dao.suaDanhMuc(laPhongBan, m.getValueAt(r, 1).toString(), tenMoi)) {
                napBang.run();
                napDanhSachPhongBanVaChucVu();
                docDuLieuDatabaseVaoTable();
                txtTen.setText("");
            } else {
                JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại!");
            }
        });

        bXoa.addActionListener(ev -> {
            int r = t.getSelectedRow();
            if (r < 0) return;
            String ten = m.getValueAt(r, 1).toString();
            if (JOptionPane.showConfirmDialog(dialog, "Xóa " + tenLoai + " \"" + ten + "\"?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && nv_dao.xoaDanhMuc(laPhongBan, ten)) {
                napBang.run();
                napDanhSachPhongBanVaChucVu();
                txtTen.setText("");
            }
        });

        bDong.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }

    private NhanVien layNhanVienTuForm(String maNV, String trangThai) throws Exception {
        String hoTen = txtHoTen.getText().trim();
        String cmnd = txtCMND.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (hoTen.length() < 2) throw new IllegalArgumentException("Họ tên phải từ 2 ký tự trở lên.");
        if (!cmnd.matches("\\d{9}|\\d{12}")) throw new IllegalArgumentException("CCCD/CMND phải gồm 9 hoặc 12 chữ số.");
        if (!sdt.matches("0\\d{9,10}")) throw new IllegalArgumentException("SĐT phải gồm 10-11 chữ số và bắt đầu bằng 0.");
        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }

        Date ngaySinh;
        try {
            ngaySinh = sdfDate.parse(txtNgaySinh.getText().trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Ngày sinh phải đúng định dạng yyyy-MM-dd.");
        }
        if (!ngaySinh.before(new Date())) throw new IllegalArgumentException("Ngày sinh phải trước ngày hiện tại.");

        double luong;
        try {
            luong = Double.parseDouble(txtMucLuong.getText().trim());
            if (luong < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mức lương không hợp lệ.");
        }

        return new NhanVien(
                maNV, hoTen, cboGioiTinh.getSelectedItem().toString(), ngaySinh, cmnd, sdt, email,
                txtDiaChi.getText().trim(), cboChucVu.getSelectedItem().toString(),
                cboPhongBan.getSelectedItem().toString(), luong, trangThai
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (dangNapComboBox) return;
        Object o = e.getSource();
        Component owner = UiDialogOwner.resolve(this);

        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnQuanLyPhongBan)) {
            moCuaSoQuanLyDanhMuc(true);
        } else if (o.equals(btnQuanLyChucVu)) {
            moCuaSoQuanLyDanhMuc(false);
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboDepartment) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            try {
                NhanVien nv = layNhanVienTuForm(nv_dao.phatSinhMaNhanVien(), "Đang làm việc");
                if (nv_dao.themNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(owner, "Thêm nhân viên mới thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Thêm thất bại (trùng CCCD/CMND, SĐT hoặc Email)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        } else if (o.equals(btnCapNhat)) {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn một nhân viên trên bảng để cập nhật!");
                return;
            }
            try {
                NhanVien nv = layNhanVienTuForm(txtMaNV.getText().trim(), cboTrangThai.getSelectedItem().toString());
                if (nv_dao.capNhatNhanVien(nv)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(owner, "Cập nhật nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Cập nhật thất bại (trùng CCCD/CMND, SĐT hoặc Email)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            String maNV = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(owner, "Bạn có chắc muốn xóa nhân viên " + maNV + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (nv_dao.xoaNhanVien(maNV)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(owner, "Xóa nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Không thể xóa nhân viên này (đang có dữ liệu liên quan)!");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienPanel().setVisible(true));
    }
}
