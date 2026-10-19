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
import DAO.NhanVien_DAO;
import DAO.PhanCongCaLam_DAO;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCaLam;

public class AssignmentPanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JTextField txtMaPC, txtNgayLamViec, txtGhiChu, txtSearch;
    private JComboBox<String> cboNhanVien, cboCaLamViec, cboTrangThai, cboFilterShift, cboFilterStatus;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnQuanLyCa, btnExport, btnTim;
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

        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);

        pSubActions.add(btnQuanLyCa);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);
        pTop.add(pSubHeader, BorderLayout.NORTH);

        // Form nhập phân công
        JPanel pFormCard = new JPanel(new BorderLayout(0, 8));
        pFormCard.setBackground(Color.WHITE);
        pFormCard.setBorder(BorderFactory.createTitledBorder("📋 Thông tin phân công ca làm"));

        JPanel pGrid = new JPanel(new GridLayout(2, 6, 12, 10));
        pGrid.setBackground(Color.WHITE);
        pGrid.setBorder(BorderFactory.createEmptyBorder(8, 12, 5, 12));

        pGrid.add(new JLabel("Mã phân công (*):"));
        txtMaPC = new JTextField();
        pGrid.add(txtMaPC);

        pGrid.add(new JLabel("Nhân viên (*):"));
        cboNhanVien = new JComboBox<>();
        pGrid.add(cboNhanVien);

        pGrid.add(new JLabel("Ca làm việc (*):"));
        cboCaLamViec = new JComboBox<>();
        pGrid.add(cboCaLamViec);

        pGrid.add(new JLabel("Ngày làm (yyyy-MM-dd):"));
        txtNgayLamViec = new JTextField(sdfDate.format(new Date()));
        pGrid.add(txtNgayLamViec);

        pGrid.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đã lên lịch", "Đã điểm danh", "Vắng mặt", "Nghỉ phép", "Đã hủy"});
        pGrid.add(cboTrangThai);

        pGrid.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField();
        pGrid.add(txtGhiChu);

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
        cboFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đã lên lịch", "Đã điểm danh", "Vắng mặt", "Nghỉ phép", "Đã hủy"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboFilterShift);
        pSearch.add(cboFilterStatus);
        pTop.add(pSearch, BorderLayout.SOUTH);

        pMain.add(pTop, BorderLayout.NORTH);

        // 3. BẢNG DANH SÁCH PHÂN CÔNG
        String[] columns = {"Mã PC", "Nhân viên", "Ca làm việc", "Ngày làm việc", "Trạng thái", "Ghi chú"};
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
                if ("Đã điểm danh".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else if ("Đã lên lịch".equals(val)) {
                    setForeground(new Color(24, 119, 242));
                } else if ("Nghỉ phép".equals(val)) {
                    setForeground(new Color(230, 81, 0));
                } else {
                    setForeground(new Color(205, 32, 38));
                }
                return this;
            }
        });

        pMain.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Nạp ComboBox và bảng dữ liệu từ Database
        napDuLieuComboBox();
        docDuLieuDatabaseVaoTable();

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnQuanLyCa.addActionListener(this);
        btnExport.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboFilterShift.addActionListener(this);
        cboFilterStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaPC.setText(model.getValueAt(row, 0).toString());
                    chonItemTheoMa(cboNhanVien, model.getValueAt(row, 1).toString().split(" - ")[0]);
                    chonItemTheoMa(cboCaLamViec, model.getValueAt(row, 2).toString().split(" - ")[0]);
                    txtNgayLamViec.setText(model.getValueAt(row, 3).toString());
                    cboTrangThai.setSelectedItem(model.getValueAt(row, 4).toString());
                    txtGhiChu.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                }
            }
        });
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
            cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
        }

        cboCaLamViec.removeAllItems();
        cboFilterShift.removeAllItems();
        cboFilterShift.addItem("Tất cả ca làm");
        for (CaLamViec ca : ca_dao.getAllCaLamViec()) {
            String gio = (ca.getGioVao() != null && ca.getGioRa() != null)
                    ? " (" + sdfTime.format(ca.getGioVao()) + "-" + sdfTime.format(ca.getGioRa()) + ")"
                    : "";
            String label = ca.getMaCa() + " - " + ca.getTenCa() + gio;
            cboCaLamViec.addItem(label);
            cboFilterShift.addItem(ca.getMaCa() + " - " + ca.getTenCa());
        }
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String filterShift = cboFilterShift.getSelectedItem() != null ? cboFilterShift.getSelectedItem().toString() : "Tất cả ca làm";
        String filterStatus = cboFilterStatus.getSelectedItem() != null ? cboFilterStatus.getSelectedItem().toString() : "Tất cả trạng thái";

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
            boolean matchStatus = "Tất cả trạng thái".equals(filterStatus)
                    || filterStatus.equalsIgnoreCase(pc.getTrangThai());

            if (matchKey && matchShift && matchStatus) {
                model.addRow(new Object[]{
                        pc.getMaPhanCong(),
                        nvText,
                        caText,
                        ngayText,
                        pc.getTrangThai(),
                        pc.getGhiChu() != null ? pc.getGhiChu() : ""
                });
            }
        }
    }

    private void xoaRong() {
        txtMaPC.setText("");
        if (cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
        if (cboCaLamViec.getItemCount() > 0) cboCaLamViec.setSelectedIndex(0);
        txtNgayLamViec.setText(sdfDate.format(new Date()));
        cboTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
        table.clearSelection();
        txtMaPC.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLamMoi)) {
            xoaRong();
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboFilterShift) || o.equals(cboFilterStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnThem)) {
            String ma = txtMaPC.getText().trim();
            String ngayStr = txtNgayLamViec.getText().trim();
            if (ma.isEmpty() || ngayStr.isEmpty() || cboNhanVien.getSelectedItem() == null || cboCaLamViec.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã phân công và Ngày làm việc!");
                return;
            }
            try {
                Date ngay = sdfDate.parse(ngayStr);
                String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
                String maCa = cboCaLamViec.getSelectedItem().toString().split(" - ")[0];

                PhanCongCaLam pc = new PhanCongCaLam(
                        ma,
                        new NhanVien(maNV),
                        new CaLamViec(maCa),
                        ngay,
                        cboTrangThai.getSelectedItem().toString(),
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
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã PC hoặc nhân viên đã có ca này trong ngày)!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày làm việc phải có định dạng yyyy-MM-dd!");
            }
        } else if (o.equals(btnCapNhat)) {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng phân công để cập nhật!");
                return;
            }
            try {
                Date ngay = sdfDate.parse(txtNgayLamViec.getText().trim());
                String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
                String maCa = cboCaLamViec.getSelectedItem().toString().split(" - ")[0];

                PhanCongCaLam pc = new PhanCongCaLam(
                        txtMaPC.getText().trim(),
                        new NhanVien(maNV),
                        new CaLamViec(maCa),
                        ngay,
                        cboTrangThai.getSelectedItem().toString(),
                        null, null, 0,
                        txtGhiChu.getText().trim(),
                        new NhanVien("NV001"),
                        new Date(), new Date()
                );
                if (pc_dao.capNhatPhanCongCaLam(pc)) {
                    docDuLieuDatabaseVaoTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật phân công thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày làm việc phải có định dạng yyyy-MM-dd!");
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
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất bảng phân công ra Excel thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssignmentPanel().setVisible(true));
    }
}
