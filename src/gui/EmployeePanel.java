package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import DAO.NhanVien_DAO;
import entity.NhanVien;

public class EmployeePanel extends JFrame implements ActionListener {

    private MenuPanel menuPanel;
    private JButton btnAdd, btnExport, btnTim;
    private JTextField txtSearch;
    private JComboBox<String> cboDepartment, cboStatus;
    private DefaultTableModel model;
    private JTable table;
    private NhanVien_DAO nv_dao = new NhanVien_DAO();

    public EmployeePanel() {
        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        // 1. TIÊU ĐỀ PHỤ VÀ NÚT THÊM MỚI / XUẤT EXCEL
        JPanel pCenterContainer = new JPanel(new BorderLayout());

        JPanel pSubHeader = new JPanel(new BorderLayout());
        pSubHeader.setBackground(new Color(245, 245, 245));
        pSubHeader.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblSubTitle = new JLabel("👥 Quản lý Nhân Viên");
        lblSubTitle.setForeground(new Color(180, 40, 40));
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
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

        // 2. THANH TÌM KIẾM VÀ BỘ LỌC
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm tên, mã NV, SĐT...");
        btnTim = new JButton("🔍 Tìm");
        btnTim.setBackground(Color.WHITE);
        btnTim.setFocusPainted(false);

        cboDepartment = new JComboBox<>(new String[]{"Tất cả phòng ban", "Quản lý", "Bán hàng", "Kho", "Kế toán"});
        cboStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});

        pSearch.add(txtSearch);
        pSearch.add(btnTim);
        pSearch.add(cboDepartment);
        pSearch.add(cboStatus);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);

        // 3. BẢNG DỮ LIỆU
        String[] columns = {"", "Mã NV", "Họ tên", "Giới tính", "SĐT", "Phòng ban", "Chức vụ", "Trạng thái", "Thao tác"};

        model = new DefaultTableModel(columns, 0) {
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

        // In đậm cột Họ tên
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return this;
            }
        });

        // Đổi màu chữ cột Trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if ("Đang làm việc".equals(val) || "Đang làm".equals(val)) {
                    setForeground(new Color(46, 125, 50));
                } else if ("Tạm nghỉ".equals(val) || "Nghỉ phép".equals(val)) {
                    setForeground(new Color(230, 81, 0));
                } else {
                    setForeground(new Color(205, 32, 38));
                }
                return this;
            }
        });

        // Cột Thao tác (Sửa, Xem, Xóa)
        table.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel pAction = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                pAction.setBackground(Color.WHITE);

                JButton btnEdit = new JButton("📝");
                btnEdit.setBackground(new Color(24, 119, 242));
                btnEdit.setForeground(Color.WHITE);
                btnEdit.setPreferredSize(new Dimension(30, 24));
                btnEdit.setMargin(new Insets(0, 0, 0, 0));

                JButton btnView = new JButton("👁");
                btnView.setBackground(new Color(225, 228, 232));
                btnView.setPreferredSize(new Dimension(30, 24));
                btnView.setMargin(new Insets(0, 0, 0, 0));

                JButton btnDelete = new JButton("🗑");
                btnDelete.setBackground(new Color(205, 32, 38));
                btnDelete.setForeground(Color.WHITE);
                btnDelete.setPreferredSize(new Dimension(30, 24));
                btnDelete.setMargin(new Insets(0, 0, 0, 0));

                pAction.add(btnEdit);
                pAction.add(btnView);
                pAction.add(btnDelete);
                return pAction;
            }
        });

        pCenterContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);

        // Đọc dữ liệu từ Database vào bảng
        docDuLieuDatabaseVaoTable();

        // Đăng ký sự kiện
        btnAdd.addActionListener(this);
        btnExport.addActionListener(this);
        btnTim.addActionListener(this);
        txtSearch.addActionListener(this);
        cboDepartment.addActionListener(this);
        cboStatus.addActionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 8) {
                    String maNV = model.getValueAt(row, 1).toString();
                    int cellX = e.getX() - table.getCellRect(row, col, false).x;
                    int cellWidth = table.getColumnModel().getColumn(col).getWidth();
                    // Nếu click vào phần bên phải của ô Thao tác (nút Xóa 🗑)
                    if (cellX > cellWidth * 2 / 3) {
                        if (JOptionPane.showConfirmDialog(EmployeePanel.this,
                                "Bạn có chắc muốn xóa nhân viên " + maNV + "?", "Xác nhận xóa",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            if (nv_dao.xoaNhanVien(maNV)) {
                                docDuLieuDatabaseVaoTable();
                                JOptionPane.showMessageDialog(EmployeePanel.this, "Xóa nhân viên thành công!");
                            } else {
                                JOptionPane.showMessageDialog(EmployeePanel.this,
                                        "Không thể xóa nhân viên (đang có dữ liệu liên quan trong hệ thống)!");
                            }
                        }
                    } else {
                        // Click nút Sửa 📝 hoặc Xem 👁 -> Mở màn hình Cập nhật nhân viên
                        new EmployeeUpdatePanel(EmployeePanel.this, maNV).setVisible(true);
                    }
                }
            }
        });
    }

    public void docDuLieuDatabaseVaoTable() {
        model.setRowCount(0);
        String tuKhoa = txtSearch.getText().trim().toLowerCase();
        String phongBan = cboDepartment.getSelectedItem().toString();
        String trangThai = cboStatus.getSelectedItem().toString();

        ArrayList<NhanVien> ds = nv_dao.getAllNhanVien();
        for (NhanVien nv : ds) {
            boolean matchKey = tuKhoa.isEmpty()
                    || nv.getMaNhanVien().toLowerCase().contains(tuKhoa)
                    || nv.getHoTen().toLowerCase().contains(tuKhoa)
                    || (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(tuKhoa));
            boolean matchDept = "Tất cả phòng ban".equals(phongBan)
                    || phongBan.equalsIgnoreCase(nv.getPhongBan());
            boolean matchStatus = "Tất cả trạng thái".equals(trangThai)
                    || trangThai.equalsIgnoreCase(nv.getTrangThai());

            if (matchKey && matchDept && matchStatus) {
                model.addRow(new Object[]{
                        false,
                        nv.getMaNhanVien(),
                        nv.getHoTen(),
                        nv.getGioiTinh(),
                        nv.getSoDienThoai(),
                        nv.getPhongBan(),
                        nv.getChucVu(),
                        nv.getTrangThai(),
                        ""
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnAdd)) {
            new EmployeeUpdatePanel(this, null).setVisible(true);
        } else if (o.equals(btnExport)) {
            JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
        } else if (o.equals(btnTim) || o.equals(txtSearch) || o.equals(cboDepartment) || o.equals(cboStatus)) {
            docDuLieuDatabaseVaoTable();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeePanel().setVisible(true));
    }
}
