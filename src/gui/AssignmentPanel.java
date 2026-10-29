package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.swing.SwingUtilities;
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

public class AssignmentPanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaPC, txtGhiChu, txtSearch;
    private JDatePickerImpl datePickerNgayLamViec;
    private UtilDateModel modelNgayLamViec;
    private JComboBox<String> cboNhanVien, cboCaLamViec, cboFilterShift;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnQuanLyCa, btnTim;
    private DefaultTableModel model;
    private JTable table;

    private PhanCongCaLam_DAO pc_dao = new PhanCongCaLam_DAO();
    private NhanVien_DAO nv_dao = new NhanVien_DAO();
    private CaLamViec_DAO ca_dao = new CaLamViec_DAO();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    public AssignmentPanel() {
        setTitle("Điện Máy Đỏ — Phân Công Ca Làm Việc");
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        pSubActions.add(btnQuanLyCa);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập phân công
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("📋 Thông tin phân công ca làm"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã phân công:"));
        txtMaPC = new JTextField();
        txtMaPC.setEditable(false);
        txtMaPC.setBackground(new Color(242, 242, 242));
        pGrid.add(txtMaPC);

        pGrid.add(new JLabel("Nhân viên (*):"));
        cboNhanVien = new JComboBox<>();
        pGrid.add(cboNhanVien);

        pGrid.add(new JLabel("Ca làm việc (*):"));
        cboCaLamViec = new JComboBox<>();
        pGrid.add(cboCaLamViec);

        pGrid.add(new JLabel("Ngày làm việc (*):"));
        modelNgayLamViec = new UtilDateModel();
        datePickerNgayLamViec = taoDatePicker(modelNgayLamViec, new Date());
        pGrid.add(datePickerNgayLamViec);

        pGrid.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField();
        pGrid.add(txtGhiChu);

        pGrid.add(new JLabel(""));
        pGrid.add(new JLabel(""));

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
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm mã PC, tên nhân viên, ngày...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);

        cboFilterShift = new JComboBox<>();

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboFilterShift);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH PHÂN CÔNG
        String[] columns = {"Mã PC", "Nhân viên", "Ca làm việc", "Ngày làm việc", "Ghi chú"};
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

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Nạp ComboBox và bảng dữ liệu từ Database
        napDuLieuComboBox();
        docDuLieuDatabaseVaoTable();
        xoaRong();

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnQuanLyCa.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboFilterShift.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaPC.setText(model.getValueAt(row, 0).toString());
                    chonItemTheoMa(cboNhanVien, model.getValueAt(row, 1).toString().split(" - ")[0]);
                    chonItemTheoMa(cboCaLamViec, model.getValueAt(row, 2).toString().split(" - ")[0]);
                    try {
                        Date ngay = sdfDate.parse(model.getValueAt(row, 3).toString());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(ngay);
                        modelNgayLamViec.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                        modelNgayLamViec.setSelected(true);
                    } catch (Exception ignored) {
                    }
                    txtGhiChu.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                }
            }
        });
    }

    private JDatePickerImpl taoDatePicker(UtilDateModel model, Date defaultDate) {
        if (defaultDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(defaultDate);
            model.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            model.setSelected(true);
        }
        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdfDate.parse(text.trim()));
                return cal;
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    Calendar cal = (Calendar) value;
                    return sdfDate.format(cal.getTime());
                }
                return "";
            }
        });
        datePicker.setBackground(Color.WHITE);
        return datePicker;
    }

    private void chonItemTheoMa(JComboBox<String> cbo, String ma) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).startsWith(ma + " - ")) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void napDuLieuComboBox() {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : nv_dao.getAllNhanVien()) {
            if ("Đang làm việc".equalsIgnoreCase(nv.getTrangThai())) {
                cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }
        }

        cboCaLamViec.removeAllItems();
        cboFilterShift.removeAllItems();
        cboFilterShift.addItem("Tất cả ca làm");
        for (CaLamViec ca : ca_dao.getAllCaLamViec()) {
            String gio = (ca.getGioVao() != null && ca.getGioRa() != null)
                    ? " (" + sdfTime.format(ca.getGioVao()) + "-" + sdfTime.format(ca.getGioRa()) + ")"
                    : "";
            String label = ca.getMaCa() + " - " + ca.getTenCa() + gio;
            if ("Hoạt động".equalsIgnoreCase(ca.getTrangThai())) {
                cboCaLamViec.addItem(label);
            }
            cboFilterShift.addItem(ca.getMaCa() + " - " + ca.getTenCa());
        }
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String filterShift = cboFilterShift.getSelectedItem() != null ? cboFilterShift.getSelectedItem().toString() : "Tất cả ca làm";

        ArrayList<PhanCongCaLam> ds = pc_dao.getAllPhanCongCaLam();
        for (PhanCongCaLam pc : ds) {
            String nvText = pc.getNhanVien().getMaNhanVien() + " - " + pc.getNhanVien().getHoTen();
            String gio = (pc.getCaLamViec().getGioVao() != null && pc.getCaLamViec().getGioRa() != null)
                    ? " (" + sdfTime.format(pc.getCaLamViec().getGioVao()) + "-" + sdfTime.format(pc.getCaLamViec().getGioRa()) + ")"
                    : "";
            String caText = pc.getCaLamViec().getMaCa() + " - " + pc.getCaLamViec().getTenCa() + gio;
            String ngayText = pc.getNgayLamViec() != null ? sdfDate.format(pc.getNgayLamViec()) : "";

            boolean matchKey = tuKhoa.isEmpty()
                    || pc.getMaPhanCong().toLowerCase().contains(tuKhoa)
                    || nvText.toLowerCase().contains(tuKhoa)
                    || ngayText.contains(tuKhoa);
            boolean matchShift = "Tất cả ca làm".equals(filterShift)
                    || caText.startsWith(filterShift);

            if (matchKey && matchShift) {
                model.addRow(new Object[]{
                        pc.getMaPhanCong(),
                        nvText,
                        caText,
                        ngayText,
                        pc.getGhiChu() != null ? pc.getGhiChu() : ""
                });
            }
        }
    }

    private void xoaRong() {
        txtMaPC.setText(pc_dao.phatSinhMaPhanCong());
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
        if (cboCaLamViec.getItemCount() > 0) cboCaLamViec.setSelectedIndex(0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        modelNgayLamViec.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        modelNgayLamViec.setSelected(true);
        txtGhiChu.setText("");
        table.clearSelection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboFilterShift)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            Date ngay = (Date) modelNgayLamViec.getValue();
            if (ngay == null || cboNhanVien.getSelectedItem() == null || cboCaLamViec.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Ngày làm việc!");
                return;
            }
            try {
                String ma = pc_dao.phatSinhMaPhanCong();
                String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
                String maCa = cboCaLamViec.getSelectedItem().toString().split(" - ")[0];

                PhanCongCaLam pc = new PhanCongCaLam(
                        ma,
                        new NhanVien(maNV),
                        new CaLamViec(maCa),
                        ngay,
                        "Đã lên lịch",
                        null, null, 0,
                        txtGhiChu.getText().trim(),
                        new NhanVien("NV001"),
                        new Date(), new Date()
                );
                if (pc_dao.themPhanCongCaLam(pc)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Phân công ca làm việc thành công!");
                    xoaRong();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Nhân viên đã có ca này trong ngày)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi phân công ca làm việc: " + ex.getMessage());
            }
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng phân công để cập nhật!");
                return;
            }
            Date ngay = (Date) modelNgayLamViec.getValue();
            if (ngay == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày làm việc!");
                return;
            }
            try {
                String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
                String maCa = cboCaLamViec.getSelectedItem().toString().split(" - ")[0];

                PhanCongCaLam pc = new PhanCongCaLam(
                        txtMaPC.getText().trim(),
                        new NhanVien(maNV),
                        new CaLamViec(maCa),
                        ngay,
                        "Đã lên lịch",
                        null, null, 0,
                        txtGhiChu.getText().trim(),
                        new NhanVien("NV001"),
                        new Date(), new Date()
                );
                if (pc_dao.capNhatPhanCongCaLam(pc)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật phân công thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại (Nhân viên đã có ca này trong ngày)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phân công: " + ex.getMessage());
            }
        } else if (o.equals(btnXoa)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phân công cần xóa!");
                return;
            }
            String maPC = model.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phân công " + maPC + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (pc_dao.xoaPhanCongCaLam(maPC)) {
                    docDuLieuDatabaseVaoTable();
                    xoaRong();
                    JOptionPane.showMessageDialog(this, "Xóa phân công thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa phân công thất bại!");
                }
            }
        } else if (o.equals(btnQuanLyCa)) {
            new WorkSchedulePanel().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssignmentPanel().setVisible(true));
    }
}
