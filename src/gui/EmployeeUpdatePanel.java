package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import DAO.NhanVien_DAO;
import DAO.PhanCongCaLam_DAO;
import entity.NhanVien;
import entity.PhanCongCaLam;

public class EmployeeUpdatePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtSDT, txtEmail, txtMucLuong;
    private JComboBox<String> cboGioiTinh, cboPhongBan, cboChucVu, cboTrangThai;
    private JTextArea txtDiaChi;
    private JButton btnLuu, btnLamMoi, btnHuy;
    private JPanel pShiftCard;

    private EmployeePanel parentPanel;
    private boolean isUpdateMode;
    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private PhanCongCaLam_DAO pc_dao = new PhanCongCaLam_DAO();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    public EmployeeUpdatePanel() {
        this(null, "NV001");
    }

    public EmployeeUpdatePanel(EmployeePanel parentPanel, String maNV) {
        this.parentPanel = parentPanel;
        this.isUpdateMode = (maNV != null);

        setTitle("Điện Máy Đỏ — Nhân viên — Thêm / Cập nhật");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. TIÊU ĐỀ PHỤ
        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(235, 235, 235));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel(isUpdateMode
                ? "👤 Nhân viên — Cập nhật (" + maNV + ")"
                : "👤+ Nhân viên — Thêm mới");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);
        pMain.add(pSubHeader, BorderLayout.NORTH);

        // 2. FORM THÔNG TIN NHÂN VIÊN
        JPanel pFormCard = new JPanel(new BorderLayout(0, 10));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("👤 Thông tin nhân viên"));

        JPanel pGrid = new JPanel(new GridLayout(5, 4, 15, 12));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        pGrid.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        pGrid.add(txtMaNV);

        pGrid.add(new JLabel("Họ và tên (*):"));
        txtHoTen = new JTextField();
        pGrid.add(txtHoTen);

        pGrid.add(new JLabel("Giới tính:"));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        pGrid.add(cboGioiTinh);

        pGrid.add(new JLabel("Ngày sinh (* yyyy-MM-dd):"));
        txtNgaySinh = new JTextField("1995-01-01");
        pGrid.add(txtNgaySinh);

        pGrid.add(new JLabel("SĐT (*):"));
        txtSDT = new JTextField();
        pGrid.add(txtSDT);

        pGrid.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pGrid.add(txtEmail);

        pGrid.add(new JLabel("Phòng ban (*):"));
        cboPhongBan = new JComboBox<>(new String[]{"Bán hàng", "Quản lý", "Kho", "Kế toán"});
        pGrid.add(cboPhongBan);

        pGrid.add(new JLabel("Chức vụ:"));
        cboChucVu = new JComboBox<>(new String[]{"Nhân viên", "Quản lý", "Thu ngân", "Nhân viên kho"});
        pGrid.add(cboChucVu);

        pGrid.add(new JLabel("Mức lương (VNĐ):"));
        txtMucLuong = new JTextField("7500000");
        pGrid.add(txtMucLuong);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        pGrid.add(cboTrangThai);

        pFormCard.add(pGrid, BorderLayout.NORTH);

        // Địa chỉ + Nút bấm
        JPanel pAddressAndButtons = new JPanel(new BorderLayout(0, 8));
        pAddressAndButtons.setBackground(Color.WHITE);
        pAddressAndButtons.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        pAddressAndButtons.add(new JLabel("Địa chỉ:"), BorderLayout.NORTH);
        txtDiaChi = new JTextArea(2, 20);
        pAddressAndButtons.add(new JScrollPane(txtDiaChi), BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);

        btnLuu = new JButton("💾 Lưu");
        btnLuu.setBackground(new Color(205, 32, 38));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);

        btnLamMoi = new JButton("↻ Làm mới");
        btnLamMoi.setBackground(new Color(235, 235, 235));
        btnLamMoi.setFocusPainted(false);

        btnHuy = new JButton("✕ Hủy");
        btnHuy.setBackground(new Color(235, 235, 235));
        btnHuy.setFocusPainted(false);

        pButtons.add(btnLuu);
        pButtons.add(btnLamMoi);
        pButtons.add(btnHuy);
        pAddressAndButtons.add(pButtons, BorderLayout.SOUTH);

        pFormCard.add(pAddressAndButtons, BorderLayout.CENTER);
        pMain.add(pFormCard, BorderLayout.CENTER);

        // 3. KHUNG CA TRỰC TUẦN NÀY (Đọc từ Database PhanCongCaLam)
        pShiftCard = new JPanel(new GridLayout(1, 7, 10, 0));
        pShiftCard.setBackground(Color.WHITE);
        pShiftCard.setBorder(BorderFactory.createTitledBorder("📅 Ca trực tuần này"));
        pShiftCard.setPreferredSize(new Dimension(0, 130));

        pMain.add(pShiftCard, BorderLayout.SOUTH);
        add(pMain, BorderLayout.CENTER);

        // Đổ dữ liệu từ Database lên Form
        if (isUpdateMode) {
            taiThongTinNhanVien(maNV);
        } else {
            txtMaNV.setText(nv_dao.phatSinhMaNhanVien());
            hienThiCaTruc(null);
        }

        // Đăng ký sự kiện
        btnLuu.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnHuy.addActionListener(this);
    }

    private void taiThongTinNhanVien(String maNV) {
        NhanVien nv = nv_dao.getNhanVienTheoMa(maNV);
        if (nv != null) {
            txtMaNV.setText(nv.getMaNhanVien());
            txtHoTen.setText(nv.getHoTen());
            cboGioiTinh.setSelectedItem(nv.getGioiTinh());
            txtNgaySinh.setText(nv.getNgaySinh() != null ? sdfDate.format(nv.getNgaySinh()) : "");
            txtSDT.setText(nv.getSoDienThoai());
            txtEmail.setText(nv.getEmail() != null ? nv.getEmail() : "");
            cboPhongBan.setSelectedItem(nv.getPhongBan());
            cboChucVu.setSelectedItem(nv.getChucVu());
            txtMucLuong.setText(String.format("%.0f", nv.getLuongCoBan()));
            cboTrangThai.setSelectedItem(nv.getTrangThai());
            txtDiaChi.setText(nv.getDiaChi() != null ? nv.getDiaChi() : "");
        }
        hienThiCaTruc(maNV);
    }

    private void hienThiCaTruc(String maNV) {
        pShiftCard.removeAll();
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};
        String[] shifts = {"", "", "", "", "", "", ""};

        if (maNV != null) {
            ArrayList<PhanCongCaLam> dsPC = pc_dao.getPhanCongTheoNhanVien(maNV);
            Calendar cal = Calendar.getInstance();
            for (PhanCongCaLam pc : dsPC) {
                if (pc.getNgayLamViec() != null && pc.getCaLamViec() != null) {
                    cal.setTime(pc.getNgayLamViec());
                    int dow = cal.get(Calendar.DAY_OF_WEEK); // CN = 1, T2 = 2..T7 = 7
                    int idx = (dow == Calendar.SUNDAY) ? 6 : (dow - 2);
                    String gio = "";
                    if (pc.getCaLamViec().getGioVao() != null && pc.getCaLamViec().getGioRa() != null) {
                        gio = ": " + sdfTime.format(pc.getCaLamViec().getGioVao())
                                + "-" + sdfTime.format(pc.getCaLamViec().getGioRa());
                    }
                    shifts[idx] = pc.getCaLamViec().getTenCa() + gio;
                }
            }
        }

        for (int i = 0; i < 7; i++) {
            pShiftCard.add(createDayBox(days[i], shifts[i]));
        }
        pShiftCard.revalidate();
        pShiftCard.repaint();
    }

    private JPanel createDayBox(String day, String shift) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblDay = new JLabel(day, SwingConstants.CENTER);
        lblDay.setForeground(new Color(180, 40, 40));
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDay.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        box.add(lblDay, BorderLayout.NORTH);

        if (shift != null && !shift.isEmpty()) {
            JLabel lblShift = new JLabel(shift, SwingConstants.CENTER);
            lblShift.setOpaque(true);
            lblShift.setBackground(new Color(253, 232, 232));
            lblShift.setForeground(new Color(180, 40, 40));
            box.add(lblShift, BorderLayout.CENTER);
        }
        return box;
    }

    private void xoaRong() {
        txtHoTen.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("1995-01-01");
        txtSDT.setText("");
        txtEmail.setText("");
        cboPhongBan.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        txtMucLuong.setText("7500000");
        cboTrangThai.setSelectedIndex(0);
        txtDiaChi.setText("");
        txtHoTen.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnHuy)) {
            dispose();
        } else if (o.equals(btnLuu)) {
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

                boolean ok = isUpdateMode ? nv_dao.capNhatNhanVien(nv) : nv_dao.themNhanVien(nv);
                if (ok) {
                    if (parentPanel != null) {
                        parentPanel.docDuLieuDatabaseVaoTable();
                    }
                    JOptionPane.showMessageDialog(this,
                            isUpdateMode ? "Cập nhật nhân viên thành công!" : "Thêm nhân viên mới thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu thất bại! Vui lòng kiểm tra SĐT/Email không bị trùng.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng Ngày sinh (yyyy-MM-dd) hoặc Mức lương không hợp lệ!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeUpdatePanel().setVisible(true));
    }
}
