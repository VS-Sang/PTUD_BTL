package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class EmployeeUpdatePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtSDT, txtEmail, txtMucLuong;
    private JComboBox<String> cboGioiTinh, cboPhongBan, cboChucVu, cboTrangThai;
    private JTextArea txtDiaChi;
    private JButton btnLuu, btnLamMoi, btnHuy;

    public EmployeeUpdatePanel() {
        setTitle("Điện Máy Đỏ — Nhân viên — Thêm / Cập nhật");
        setSize(1200, 720);
        setLocationRelativeTo(null);
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

        JLabel lblSubTitle = new JLabel("👤+ Nhân viên — Thêm / Cập nhật");
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
        txtMaNV = new JTextField("NV001");
        txtMaNV.setEditable(false);
        pGrid.add(txtMaNV);

        pGrid.add(new JLabel("Họ và tên (*):"));
        txtHoTen = new JTextField("Nguyễn Văn An");
        pGrid.add(txtHoTen);

        pGrid.add(new JLabel("Giới tính:"));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        pGrid.add(cboGioiTinh);

        pGrid.add(new JLabel("Ngày sinh (*):"));
        txtNgaySinh = new JTextField("1990-05-15");
        pGrid.add(txtNgaySinh);

        pGrid.add(new JLabel("SĐT (*):"));
        txtSDT = new JTextField("0901234567");
        pGrid.add(txtSDT);

        pGrid.add(new JLabel("Email:"));
        txtEmail = new JTextField("an@dienmaydo.vn");
        pGrid.add(txtEmail);

        pGrid.add(new JLabel("Phòng ban (*):"));
        cboPhongBan = new JComboBox<>(new String[]{"Bán hàng", "Quản lý", "Kho", "Kế toán"});
        pGrid.add(cboPhongBan);

        pGrid.add(new JLabel("Chức vụ:"));
        cboChucVu = new JComboBox<>(new String[]{"Nhân viên", "Trưởng phòng", "Phó phòng", "Quản lý", "Thu ngân"});
        pGrid.add(cboChucVu);

        pGrid.add(new JLabel("Mức lương (VNĐ):"));
        txtMucLuong = new JTextField("8000000");
        pGrid.add(txtMucLuong);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm", "Nghỉ phép", "Đã nghỉ việc"});
        pGrid.add(cboTrangThai);

        pFormCard.add(pGrid, BorderLayout.NORTH);

        // Địa chỉ + Nút bấm
        JPanel pAddressAndButtons = new JPanel(new BorderLayout(0, 8));
        pAddressAndButtons.setBackground(Color.WHITE);
        pAddressAndButtons.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        pAddressAndButtons.add(new JLabel("Địa chỉ:"), BorderLayout.NORTH);
        txtDiaChi = new JTextArea(2, 20);
        txtDiaChi.setText("Nhập địa chỉ...");
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

        // 3. KHUNG CA TRỰC TUẦN NÀY
        JPanel pShiftCard = new JPanel(new GridLayout(1, 7, 10, 0));
        pShiftCard.setBackground(Color.WHITE);
        pShiftCard.setBorder(BorderFactory.createTitledBorder("📅 Ca trực tuần này"));
        pShiftCard.setPreferredSize(new Dimension(0, 130));

        pShiftCard.add(createDayBox("Thứ 2", "Sáng: 8h-12h"));
        pShiftCard.add(createDayBox("Thứ 3", "Chiều: 13h-17h"));
        pShiftCard.add(createDayBox("Thứ 4", "Sáng: 8h-12h"));
        pShiftCard.add(createDayBox("Thứ 5", ""));
        pShiftCard.add(createDayBox("Thứ 6", "Chiều: 13h-17h"));
        pShiftCard.add(createDayBox("Thứ 7", "Tối: 18h-22h"));
        pShiftCard.add(createDayBox("CN", ""));

        pMain.add(pShiftCard, BorderLayout.SOUTH);
        add(pMain, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnLuu.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnHuy.addActionListener(this);
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

        if (!shift.isEmpty()) {
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
        txtNgaySinh.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        cboPhongBan.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        txtMucLuong.setText("");
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
            if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và SĐT!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Lưu thông tin nhân viên thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeUpdatePanel().setVisible(true));
    }
}
