package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import DAO.HoaDon_DAO;
import entity.HoaDon;

public class InvoicePanel extends JFrame implements ActionListener {
    private final DefaultTableModel model;
    private final JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboStatus;
    private JButton btnCreate, btnExport, btnPrint, btnTim;
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
        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnPrint = new JButton("In báo cáo");
        btnPrint.setBackground(new Color(55, 105, 190));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);

        pSubActions.add(btnCreate);
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
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        docDuLieuDatabaseVaoTable();

        btnCreate.addActionListener(this);
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
                        tenNV,
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
            JOptionPane.showMessageDialog(this, "Xuất danh sách hóa đơn ra Excel thành công!");
        } else if (o.equals(btnPrint)) {
            JOptionPane.showMessageDialog(this, "In báo cáo doanh thu thành công!");
        } else if (o.equals(btnCreate)) {
            new CreateInvoiceDialog(this, currentUserName, currentUserRole).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InvoicePanel().setVisible(true));
    }
}
