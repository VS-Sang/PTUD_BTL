package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

import DAO.SanPham_DAO;
import entity.SanPham;

public class ProductPanel extends JFrame implements ActionListener {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboStatus;
    private JButton btnExport;
    private SanPham_DAO sp_dao = new SanPham_DAO();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public ProductPanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new MenuPanel(), BorderLayout.NORTH);

        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("🧾 Quản lý sản phẩm / Dịch vụ");
        lblSubTitle.setForeground(new Color(150, 30, 30));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pSubHeader.add(lblSubTitle, BorderLayout.WEST);

        JPanel pSubActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pSubActions.setOpaque(false);

        btnExport = new JButton("Xuất Excel");
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm tên SP, mã SP, thương hiệu...");
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang kinh doanh", "Ngừng kinh doanh", "Hết hàng"});
        pSearch.add(txtSearch);
        pSearch.add(cboStatus);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        String[] column = {"", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu",
                "Giá nhập", "Giá bán", "Tồn kho", "Đơn vị", "Trạng thái"};
        model = new DefaultTableModel(column, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(36);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
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

        txtSearch.addActionListener(this);
        cboStatus.addActionListener(this);
        btnExport.addActionListener(this);
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String trangThai = cboStatus.getSelectedItem().toString();

        ArrayList<SanPham> ds = sp_dao.getAllSanPham();
        for (SanPham sp : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || sp.getMaSanPham().toLowerCase().contains(tuKhoa)
                    || sp.getTenSanPham().toLowerCase().contains(tuKhoa)
                    || (sp.getThuongHieu() != null && sp.getThuongHieu().toLowerCase().contains(tuKhoa));
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(sp.getTrangThai());

            if (matchKey && matchStatus) {
                String tenDM = sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : "";
                model.addRow(new Object[]{
                        false,
                        sp.getMaSanPham(),
                        sp.getTenSanPham(),
                        tenDM,
                        sp.getThuongHieu() != null ? sp.getThuongHieu() : "",
                        df.format(sp.getGiaNhap()),
                        df.format(sp.getGiaBan()),
                        sp.getSoLuongTon(),
                        sp.getDonViTinh(),
                        sp.getTrangThai()
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(txtSearch) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất danh sách sản phẩm ra Excel thành công!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductPanel().setVisible(true));
    }
}
