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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
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

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import DAO.CaLamViec_DAO;
import DAO.NhanVien_DAO;
import DAO.PhanCongCaLam_DAO;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCaLam;

public class PhanCongCaPanel extends JFrame implements ActionListener {

    private final String currentUserName, currentUserRole, currentUsername;
    private JTextField txtMaPC, txtGhiChu, txtSearch;
    private UtilDateModel modelNgayLamViec;
    private JComboBox<String> cboNhanVien, cboCaLamViec, cboTrangThai, cboFilterShift, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnQuanLyCa, btnTim;
    private DefaultTableModel model;
    private JTable table;

    private final PhanCongCaLam_DAO pc_dao = new PhanCongCaLam_DAO();
    private final NhanVien_DAO nv_dao = new NhanVien_DAO();
    private final CaLamViec_DAO ca_dao = new CaLamViec_DAO();
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    public PhanCongCaPanel() {
        this(null, null, null);
    }

    public PhanCongCaPanel(String userName, String userRole) {
        this(userName, userRole, null);
    }

    public PhanCongCaPanel(String userName, String userRole, String username) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;
        this.currentUsername = username;

        setTitle("Điện Máy Đỏ — Phân Công Ca Làm Việc");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(this);

        add(new ThanhMenu(userName, userRole, username), BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout(0, 10));
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // 1. Tiêu đề + Form phân công ca làm
        JPanel pTop = new JPanel(new BorderLayout(0, 10));
        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel lblSubTitle = new JLabel("📅 Quản lý Phân Công Ca Làm Việc");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        btnQuanLyCa = taoNut("🕒 Quản lý Ca làm việc", new Color(180, 40, 40), Color.WHITE);
        pSubHeader.add(btnQuanLyCa, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("📋 Thông tin phân công ca làm"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        txtMaPC = new JTextField();
        txtMaPC.setEditable(false);
        txtMaPC.setBackground(new Color(242, 242, 242));
        cboNhanVien = new JComboBox<>();
        cboCaLamViec = new JComboBox<>();
        modelNgayLamViec = new UtilDateModel(new Date());
        modelNgayLamViec.setSelected(true);
        cboTrangThai = new JComboBox<>(new String[]{"Đã lên lịch", "Đã điểm danh", "Vắng mặt", "Nghỉ phép", "Đã hủy"});
        txtGhiChu = new JTextField();

        pGrid.add(new JLabel("Mã phân công:"));       pGrid.add(txtMaPC);
        pGrid.add(new JLabel("Nhân viên (*):"));      pGrid.add(cboNhanVien);
        pGrid.add(new JLabel("Ca làm việc (*):"));    pGrid.add(cboCaLamViec);
        pGrid.add(new JLabel("Ngày làm việc (*):"));  pGrid.add(taoDatePicker(modelNgayLamViec));
        pGrid.add(new JLabel("Trạng thái:"));         pGrid.add(cboTrangThai);
        pGrid.add(new JLabel("Ghi chú:"));            pGrid.add(txtGhiChu);
        pFormCard.add(pGrid, BorderLayout.CENTER);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pButtons.setBackground(Color.WHITE);
        btnThem = taoNut("+ Phân công", new Color(180, 40, 40), Color.WHITE);
        btnCapNhat = taoNut("💾 Cập nhật", new Color(24, 119, 242), Color.WHITE);
        btnXoa = taoNut("🗑 Xóa", new Color(205, 32, 38), Color.WHITE);
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
        txtSearch.setToolTipText("Tìm mã PC, tên nhân viên, ngày...");
        btnTim = taoNut("🔍 Tìm", Color.WHITE, Color.BLACK);
        cboFilterShift = new JComboBox<>();
        cboFilterStatus = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Đã lên lịch", "Đã điểm danh", "Vắng mặt", "Nghỉ phép", "Đã hủy"
        });
        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboFilterShift);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);
        pMain.add(pTop, BorderLayout.NORTH);

        // 3. Bảng danh sách phân công
        String[] columns = {"Mã PC", "Nhân viên", "Ca làm việc", "Ngày làm việc", "Trạng thái", "Ghi chú"};
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

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, focus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                String s = val != null ? val.toString() : "";
                setForeground("Đã điểm danh".equals(s) ? new Color(46, 125, 50)
                        : "Đã lên lịch".equals(s) ? new Color(24, 119, 242)
                        : "Nghỉ phép".equals(s) ? new Color(230, 81, 0) : new Color(205, 32, 38));
                return this;
            }
        });

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        napDuLieuComboBox();
        docDuLieuDatabaseVaoTable();
        xoaRong();

        for (JButton b : new JButton[]{btnThem, btnCapNhat, btnXoa, btnLamMoi, btnQuanLyCa, btnTim}) {
            b.addActionListener(this);
        }
        txtSearch.addActionListener(this);
        cboFilterShift.addActionListener(this);
        cboFilterStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaPC.setText(model.getValueAt(row, 0).toString());
                    chonItemTheoMa(cboNhanVien, model.getValueAt(row, 1).toString());
                    chonItemTheoMa(cboCaLamViec, model.getValueAt(row, 2).toString());
                    try {
                        modelNgayLamViec.setValue(sdfDate.parse(model.getValueAt(row, 3).toString()));
                        modelNgayLamViec.setSelected(true);
                    } catch (Exception ignored) {
                    }
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 4).toString());
                    txtGhiChu.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
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

    private JDatePickerImpl taoDatePicker(UtilDateModel dateModel) {
        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");
        return new JDatePickerImpl(new JDatePanelImpl(dateModel, p), new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) return null;
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdfDate.parse(text.trim()));
                return cal;
            }

            @Override
            public String valueToString(Object value) {
                return value != null ? sdfDate.format(((Calendar) value).getTime()) : "";
            }
        });
    }

    private void chonItemTheoMa(JComboBox<String> cbo, String fullLabel) {
        String ma = fullLabel.split(" - ")[0];
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).startsWith(ma + " - ")) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
        cbo.addItem(fullLabel);
        cbo.setSelectedItem(fullLabel);
    }

    private void napDuLieuComboBox() {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : nv_dao.getAllNhanVien()) {
            if ("Đang làm việc".equalsIgnoreCase(nv.getTrangThai())) {
                cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }
        }

        cboCaLamViec.removeAllItems();
        String currentShiftFilter = cboFilterShift.getSelectedItem() != null
                ? cboFilterShift.getSelectedItem().toString() : "Tất cả ca làm";
        cboFilterShift.removeAllItems();
        cboFilterShift.addItem("Tất cả ca làm");
        for (CaLamViec ca : ca_dao.getAllCaLamViec()) {
            String gio = (ca.getGioVao() != null && ca.getGioRa() != null)
                    ? " (" + sdfTime.format(ca.getGioVao()) + "-" + sdfTime.format(ca.getGioRa()) + ")" : "";
            if ("Hoạt động".equalsIgnoreCase(ca.getTrangThai())) {
                cboCaLamViec.addItem(ca.getMaCa() + " - " + ca.getTenCa() + gio);
            }
            cboFilterShift.addItem(ca.getMaCa() + " - " + ca.getTenCa());
        }
        cboFilterShift.setSelectedItem(currentShiftFilter);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String filterShift = cboFilterShift.getSelectedItem() != null ? cboFilterShift.getSelectedItem().toString() : "Tất cả ca làm";
        String filterStatus = cboFilterStatus.getSelectedItem() != null ? cboFilterStatus.getSelectedItem().toString() : "Tất cả trạng thái";

        for (PhanCongCaLam pc : pc_dao.getAllPhanCongCaLam()) {
            String nvText = pc.getNhanVien().getMaNhanVien() + " - " + pc.getNhanVien().getHoTen();
            String gio = (pc.getCaLamViec().getGioVao() != null && pc.getCaLamViec().getGioRa() != null)
                    ? " (" + sdfTime.format(pc.getCaLamViec().getGioVao()) + "-" + sdfTime.format(pc.getCaLamViec().getGioRa()) + ")" : "";
            String caText = pc.getCaLamViec().getMaCa() + " - " + pc.getCaLamViec().getTenCa() + gio;
            String ngayText = pc.getNgayLamViec() != null ? sdfDate.format(pc.getNgayLamViec()) : "";
            String trangThaiText = pc.getTrangThai() != null ? pc.getTrangThai() : "Đã lên lịch";

            boolean matchKey = tuKhoa.isEmpty()
                    || pc.getMaPhanCong().toLowerCase().contains(tuKhoa)
                    || nvText.toLowerCase().contains(tuKhoa)
                    || ngayText.contains(tuKhoa);
            boolean matchShift = "Tất cả ca làm".equals(filterShift) || caText.startsWith(filterShift);
            boolean matchStatus = "Tất cả trạng thái".equals(filterStatus) || filterStatus.equalsIgnoreCase(trangThaiText);

            if (matchKey && matchShift && matchStatus) {
                model.addRow(new Object[]{
                        pc.getMaPhanCong(), nvText, caText, ngayText, trangThaiText,
                        pc.getGhiChu() != null ? pc.getGhiChu() : ""
                });
            }
        }
    }

    private void xoaRong() {
        napDuLieuComboBox();
        txtMaPC.setText(pc_dao.phatSinhMaPhanCong());
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
        if (cboCaLamViec.getItemCount() > 0) cboCaLamViec.setSelectedIndex(0);
        modelNgayLamViec.setValue(new Date());
        modelNgayLamViec.setSelected(true);
        cboTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
        table.clearSelection();
    }

    private PhanCongCaLam layPhanCongTuForm(String maPC) {
        Date ngay = modelNgayLamViec.getValue();
        if (ngay == null || cboNhanVien.getSelectedItem() == null || cboCaLamViec.getSelectedItem() == null) {
            throw new IllegalArgumentException("Vui lòng chọn đầy đủ Nhân viên, Ca làm và Ngày làm việc!");
        }
        String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
        String maCa = cboCaLamViec.getSelectedItem().toString().split(" - ")[0];
        String maNguoiPC = pc_dao.getMaNhanVienTheoTenDangNhap(currentUsername);
        return new PhanCongCaLam(
                maPC, new NhanVien(maNV), new CaLamViec(maCa), ngay,
                cboTrangThai.getSelectedItem().toString(), txtGhiChu.getText().trim(), new NhanVien(maNguoiPC)
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        Component owner = UiDialogOwner.resolve(this);

        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboFilterShift) || o.equals(cboFilterStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            try {
                if (pc_dao.themPhanCongCaLam(layPhanCongTuForm(pc_dao.phatSinhMaPhanCong()))) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(owner, "Phân công ca làm việc thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Thêm thất bại (Nhân viên đã có ca này trong ngày)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, ex.getMessage());
            }
        } else if (o.equals(btnCapNhat)) {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn một dòng phân công để cập nhật!");
                return;
            }
            try {
                if (pc_dao.capNhatPhanCongCaLam(layPhanCongTuForm(txtMaPC.getText().trim()))) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(owner, "Cập nhật phân công thành công!");
                } else {
                    JOptionPane.showMessageDialog(owner, "Cập nhật thất bại (Nhân viên đã có ca này trong ngày)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, ex.getMessage());
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(owner, "Vui lòng chọn phân công cần xóa!");
                return;
            }
            String maPC = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(owner, "Bạn có chắc muốn xóa phân công " + maPC + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && pc_dao.xoaPhanCongCaLam(maPC)) {
                docDuLieuDatabaseVaoTable();
                xoaRong();
                JOptionPane.showMessageDialog(owner, "Xóa phân công thành công!");
            }
        } else if (o.equals(btnQuanLyCa)) {
            if (!KhungChinh.moTrangHienTai(ThanhMenu.MenuItem.WORK_SCHEDULE)) {
                dispose();
                new LichLamViecPanel(currentUserName, currentUserRole, currentUsername).setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhanCongCaPanel().setVisible(true));
    }
}
