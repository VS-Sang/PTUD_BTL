package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import DAO.NhanVien_DAO;
import entity.HoaDon;
import entity.NhanVien;

public class InvoicePanel extends JFrame implements ActionListener {
    private final DefaultTableModel model;
    private final JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboStatus;
    private JButton btnCreate, btnDelete, btnExport, btnPrint, btnTim;
    private final String currentUserName;
    private final String currentUserRole;
    private HoaDon_DAO hd_dao = new HoaDon_DAO();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public InvoicePanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public InvoicePanel(String userName, String userRole) {
        currentUserName = userName == null || userName.trim().isEmpty()
                ? "Nguyễn Văn An" : userName;
        currentUserRole = userRole == null || userRole.trim().isEmpty()
                ? "Quản lý" : userRole;
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new MenuPanel(userName, userRole), BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("Quản lý Hoá Đơn");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);

        btnCreate = new JButton("+ Tạo hóa đơn");
        btnCreate.setBackground(new Color(180, 40, 40));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnDelete = new JButton("Xóa hóa đơn");
        btnDelete.setBackground(new Color(130, 35, 35));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnPrint = new JButton("Xuất PDF");
        btnPrint.setBackground(new Color(55, 105, 190));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);

        pSubActions.add(btnCreate);
        pSubActions.add(btnDelete);
        pSubActions.add(btnExport);
        pSubActions.add(btnPrint);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm mã HĐ, khách hàng...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đã thanh toán", "Chờ thanh toán", "Đã hủy"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboStatus);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] columns = {"Mã HĐ", "Ngày lập", "Khách hàng", "Thu ngân", "Phương thức", "Tổng thanh toán", "Trạng thái"};
        model = new DefaultTableModel(columns, 0);

        table = new JTable(model);
        table.setRowHeight(36);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ArrayList<ThuNganOption> thuNganOptions = loadThuNganOptions();
        if (!thuNganOptions.isEmpty()) {
            table.getColumnModel().getColumn(3).setCellEditor(
                    new ThuNganCellEditor(thuNganOptions));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy nhân viên đang làm việc để chọn làm thu ngân.",
                    "Không thể đổi thu ngân", JOptionPane.WARNING_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        docDuLieuDatabaseVaoTable();

        btnCreate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnExport.addActionListener(this);
        btnPrint.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboStatus.addActionListener(this);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String trangThai = cboStatus.getSelectedItem().toString();

        ArrayList<HoaDon> ds = hd_dao.getAllHoaDon();
        for (HoaDon hd : ds) {
            String tenKH = hd.getKhachHang() != null && hd.getKhachHang().getHoTen() != null
                    ? hd.getKhachHang().getHoTen() : "Khách vãng lai";
            String tenNV = hd.getNhanVien() != null && hd.getNhanVien().getHoTen() != null
                    ? hd.getNhanVien().getHoTen() : "";

            boolean matchKey = tuKhoa.isEmpty()
                    || hd.getMaHoaDon().toLowerCase().contains(tuKhoa)
                    || tenKH.toLowerCase().contains(tuKhoa)
                    || tenNV.toLowerCase().contains(tuKhoa);
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(hd.getTrangThai());

            if (matchKey && matchStatus) {
                model.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        hd.getNgayLapHoaDon() != null ? sdf.format(hd.getNgayLapHoaDon()) : "",
                        tenKH,
                        new ThuNganOption(
                                hd.getNhanVien() == null ? null : hd.getNhanVien().getMaNhanVien(),
                                tenNV),
                        hd.getPhuongThucThanhToan(),
                        df.format(hd.getTongThanhToan()),
                        hd.getTrangThai()
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            ReportExportUtils.exportTable(this, table, "Xuất danh sách hóa đơn",
                    "DanhSachHoaDon.csv");
        } else if (o.equals(btnPrint)) {
            ReportExportUtils.exportTablePdf(this, table, "Danh sách hóa đơn",
                    "DanhSachHoaDon.pdf");
        } else if (o.equals(btnDelete)) {
            xoaHoaDonDangChon();
        } else if (o.equals(btnCreate)) {
            new CreateInvoiceDialog(this, currentUserName, currentUserRole).setVisible(true);
        }
    }

    private ArrayList<ThuNganOption> loadThuNganOptions() {
        ArrayList<ThuNganOption> options = new ArrayList<>();
        for (NhanVien nhanVien : new NhanVien_DAO().getAllNhanVien()) {
            if ("Đang làm việc".equals(nhanVien.getTrangThai())) {
                options.add(new ThuNganOption(nhanVien.getMaNhanVien(), nhanVien.getHoTen()));
            }
        }
        return options;
    }

    private void xoaHoaDonDangChon() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa.",
                    "Chưa chọn hóa đơn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maHoaDon = String.valueOf(model.getValueAt(selectedRow, 0));
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa hẳn hóa đơn " + maHoaDon
                        + " và đơn hàng liên kết không?\nTồn kho và số liệu khách hàng sẽ được cập nhật lại.",
                "Xác nhận xóa hóa đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            hd_dao.xoaHoaDon(maHoaDon);
            docDuLieuDatabaseVaoTable();
            JOptionPane.showMessageDialog(this, "Đã xóa hóa đơn " + maHoaDon + ".",
                    "Xóa hóa đơn", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Không thể xóa hóa đơn:\n"
                    + exception.getMessage(), "Lỗi xóa hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final class ThuNganCellEditor extends DefaultCellEditor {
        private final JComboBox<ThuNganOption> comboBox;

        ThuNganCellEditor(ArrayList<ThuNganOption> options) {
            this(new JComboBox<>(options.toArray(new ThuNganOption[0])));
        }

        private ThuNganCellEditor(JComboBox<ThuNganOption> comboBox) {
            super(comboBox);
            this.comboBox = comboBox;
        }

        @Override
        public boolean stopCellEditing() {
            int row = table.getEditingRow();
            Object selected = comboBox.getSelectedItem();
            if (row < 0 || !(selected instanceof ThuNganOption)) {
                return super.stopCellEditing();
            }

            ThuNganOption selectedOption = (ThuNganOption) selected;
            Object current = model.getValueAt(row, 3);
            if (selectedOption.equals(current)) {
                return super.stopCellEditing();
            }

            String maHoaDon = String.valueOf(model.getValueAt(row, 0));
            try {
                hd_dao.capNhatThuNganHoaDon(maHoaDon, selectedOption.maNhanVien);
                return super.stopCellEditing();
            } catch (SQLException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(InvoicePanel.this,
                        "Không thể cập nhật thu ngân:\n" + exception.getMessage(),
                        "Lỗi cập nhật hóa đơn", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    private static final class ThuNganOption {
        private final String maNhanVien;
        private final String hoTen;

        ThuNganOption(String maNhanVien, String hoTen) {
            this.maNhanVien = maNhanVien;
            this.hoTen = hoTen == null ? "" : hoTen;
        }

        @Override
        public String toString() {
            return hoTen;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ThuNganOption)) {
                return false;
            }
            ThuNganOption option = (ThuNganOption) other;
            return maNhanVien == null
                    ? option.maNhanVien == null
                    : maNhanVien.equals(option.maNhanVien);
        }

        @Override
        public int hashCode() {
            return maNhanVien == null ? 0 : maNhanVien.hashCode();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InvoicePanel().setVisible(true));
    }
}
