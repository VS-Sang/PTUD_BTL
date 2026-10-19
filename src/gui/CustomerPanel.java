package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import DAO.KhachHang_DAO;
import entity.KhachHang;

public class CustomerPanel extends JFrame implements ActionListener {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JButton btnAdd, btnExport, btnTim;
    private KhachHang_DAO kh_dao = new KhachHang_DAO();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public CustomerPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new MenuPanel(), BorderLayout.NORTH);

        // 1. KHUNG NỘI DUNG VÀ BẢNG DỮ LIỆU
        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("📖 Quản Lý Khách Hàng");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);
        btnAdd = new JButton("+ Thêm mới");
        btnAdd.setBackground(new Color(180, 40, 40));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnExport = new JButton("Xuất Excel");
        btnExport.setBackground(Color.WHITE);
        btnExport.setFocusPainted(false);
        pSubActions.add(btnAdd);
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(30);
        txtSearch.setToolTipText("Tìm tên, SĐT, mã KH...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);
        pSearch.add(txtSearch);
        pSearch.add(btnTim);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);

        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] columns = {"", "Mã KH", "Họ tên", "Giới tính", "SĐT", "Email", "Địa chỉ", "Hạng KH", "Điểm thưởng", "Tổng chi tiêu"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(180, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);

        add(pCenterContainer, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database
        docDuLieuDatabaseVaoTable();

        btnAdd.addActionListener(this);
        btnExport.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();

        ArrayList<KhachHang> ds = kh_dao.getAllKhachHang();
        for (KhachHang kh : ds) {
            boolean match = tuKhoa.isEmpty()
                    || kh.getMaKhachHang().toLowerCase().contains(tuKhoa)
                    || kh.getHoTen().toLowerCase().contains(tuKhoa)
                    || (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(tuKhoa));
            if (match) {
                model.addRow(new Object[]{
                        false,
                        kh.getMaKhachHang(),
                        kh.getHoTen(),
                        kh.getGioiTinh() != null ? kh.getGioiTinh() : "",
                        kh.getSoDienThoai(),
                        kh.getEmail() != null ? kh.getEmail() : "",
                        kh.getDiaChi() != null ? kh.getDiaChi() : "",
                        kh.getLoaiKhachHang(),
                        kh.getDiemThuong(),
                        df.format(kh.getTongChiTieu())
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim) || o.equals(txtSearch)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất danh sách khách hàng ra Excel thành công!");
        } else if (o.equals(btnAdd)) {
            JOptionPane.showMessageDialog(this, "Chức năng thêm mới khách hàng đang được phát triển.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerPanel().setVisible(true));
    }
}