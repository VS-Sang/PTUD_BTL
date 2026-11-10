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

public class LichLamViecPanel extends JFrame implements ActionListener {

    private final String currentUserName, currentUserRole, currentUsername;
    private JTextField txtMaCa, txtTenCa, txtGioBatDau, txtGioKetThuc, txtMoTa, txtSearch;
    private JComboBox<String> cboTrangThai, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnPhanCong, btnTim;
    private DefaultTableModel model;
    private JTable table;

    private final CaLamViec_DAO ca_dao = new CaLamViec_DAO();
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    public LichLamViecPanel() {
        this(null, null, null);
    }

    public LichLamViecPanel(String userName, String userRole) {
        this(userName, userRole, null);
    }

    public LichLamViecPanel(String userName, String userRole, String username) {
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
        ButtonTooltips.ganTooltip(this);

        add(new ThanhMenu(userName, userRole, username), BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. Tiêu đề + Form ca làm việc
        JPanel pTop = new JPanel(new BorderLayout(0, 10));
        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("🕒 Quản lý Ca Làm Việc");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        btnPhanCong = taoNut("📅 Mở Phân công ca làm", new Color(180, 40, 40), Color.WHITE);
        pSubHeader.add(btnPhanCong, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("🕒 Thông tin ca làm việc"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        txtMaCa = new JTextField();
        txtMaCa.setEditable(false);
        txtMaCa.setBackground(new Color(242, 242, 242));
        txtTenCa = new JTextField();
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng sử dụng"});
        txtGioBatDau = new JTextField("08:00");
        txtGioKetThuc = new JTextField("12:00");
        txtMoTa = new JTextField();

        pGrid.add(new JLabel("Mã ca:"));                 pGrid.add(txtMaCa);
        pGrid.add(new JLabel("Tên ca (*):"));            pGrid.add(txtTenCa);
        pGrid.add(new JLabel("Trạng thái:"));            pGrid.add(cboTrangThai);
        pGrid.add(new JLabel("Giờ bắt đầu (HH:mm):"));   pGrid.add(txtGioBatDau);
        pGrid.add(new JLabel("Giờ kết thúc (HH:mm):"));  pGrid.add(txtGioKetThuc);
        pGrid.add(new JLabel("Mô tả:"));                 pGrid.add(txtMoTa);
        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);
        btnThem = taoNut("+ Thêm ca", new Color(180, 40, 40), Color.WHITE);
        btnCapNhat = taoNut("💾 Cập nhật", new Color(24, 119, 242), Color.WHITE);
        btnXoa = taoNut("🗑 Xóa ca", new Color(205, 32, 38), Color.WHITE);
        btnLamMoi = taoNut("↻ Làm mới", new Color(235, 235, 235), Color.BLACK);
        pButtons.add(btnThem);
        pButtons.add(btnCapNhat);
        pButtons.add(btnXoa);
        pButtons.add(btnLamMoi);
        pFormCard.add(pButtons, BorderLayout.SOUTH);
        pTop.add(pFormCard, BorderLayout.CENTER);

        // 2. Thanh tìm kiếm
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm mã ca, tên ca...");
        btnTim = taoNut("🔍 Tìm", Color.WHITE, Color.BLACK);
        cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Hoạt động", "Ngừng sử dụng"});
        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);
        pMain.add(pTop, BorderLayout.NORTH);

        // 3. Bảng danh sách ca làm việc
        String[] columns = {"Mã ca", "Tên ca làm việc", "Giờ bắt đầu", "Giờ kết thúc", "Mô tả", "Trạng thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, focus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground("Hoạt động".equals(String.valueOf(val)) ? new Color(46, 125, 50) : new Color(205, 32, 38));
                return this;
            }
        });

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        docDuLieuDatabaseVaoTable();
        xoaRong();

        for (JButton b : new JButton[]{btnThem, btnCapNhat, btnXoa, btnLamMoi, btnPhanCong, btnTim}) {
            b.addActionListener(this);
        }
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

    private JButton taoNut(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        return b;
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String trangThai = cboFilterStatus.getSelectedItem().toString();

        for (CaLamViec ca : ca_dao.getAllCaLamViec()) {
            boolean matchKey = tuKhoa.isEmpty()
                    || ca.getMaCa().toLowerCase().contains(tuKhoa)
                    || ca.getTenCa().toLowerCase().contains(tuKhoa);
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(ca.getTrangThai());

            if (matchKey && matchStatus) {
                model.addRow(new Object[]{
                        ca.getMaCa(), ca.getTenCa(),
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

    private CaLamViec layCaTuForm(String maCa) throws Exception {
        String ten = txtTenCa.getText().trim();
        if (ten.isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên ca làm việc!");
        Date gioVao = sdfTime.parse(txtGioBatDau.getText().trim());
        Date gioRa = sdfTime.parse(txtGioKetThuc.getText().trim());
        return new CaLamViec(maCa, ten, gioVao, gioRa, txtMoTa.getText().trim(), cboTrangThai.getSelectedItem().toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        Component owner = UiDialogOwner.resolve(this);

        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboFilterStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            try {
                if (ca_dao.themCaLamViec(layCaTuForm(ca_dao.phatSinhMaCa()))) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(owner, "Thêm ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Thêm thất bại (Mã ca đã tồn tại)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, "Vui lòng nhập đầy đủ Tên ca và Giờ định dạng HH:mm (VD: 08:00)!");
            }
        } else if (o.equals(btnCapNhat)) {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn một ca trên bảng để cập nhật!");
                return;
            }
            try {
                if (ca_dao.capNhatCaLamViec(layCaTuForm(txtMaCa.getText().trim()))) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(owner, "Cập nhật ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Cập nhật thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, "Vui lòng nhập đầy đủ Tên ca và Giờ định dạng HH:mm!");
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn ca cần xóa!");
                return;
            }
            String maCa = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(owner, "Bạn có chắc muốn xóa ca " + maCa + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (ca_dao.xoaCaLamViec(maCa)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(owner, "Xóa ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Không thể xóa ca này (đã có phân công nhân viên)!");
                }
            }
        } else if (o.equals(btnPhanCong)) {
            if (!KhungChinh.moTrangHienTai(ThanhMenu.MenuItem.ASSIGNMENT)) {
                dispose();
                new PhanCongCaPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LichLamViecPanel().setVisible(true));
    }
}
