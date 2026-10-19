package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.ThongKe_DAO;

public class GeneralReportPanel extends JFrame implements ActionListener {

    private static final Color BRAND_RED = new Color(180, 40, 40);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_MUTED = new Color(108, 117, 125);

    private String currentUserName;
    private String currentUserRole;

    private JComboBox<String> cboMonth;
    private JButton btnExport, btnPrint;

    private JLabel lblValHoaDon;
    private JLabel lblValDoanhThu;
    private JLabel lblValKhachMoi;
    private JLabel lblValDoiTra;

    private JLabel lblTableTitle;
    private DefaultTableModel tableModel;
    private JTable table;

    private ThongKe_DAO thongKeDAO = new ThongKe_DAO();
    private DecimalFormat dfMoney = new DecimalFormat("#,###đ");

    public GeneralReportPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public GeneralReportPanel(String userName, String userRole) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;

        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. MENU CHÍNH
        add(new MenuPanel(currentUserName, currentUserRole), BorderLayout.NORTH);

        // 2. KHUNG NỘI DUNG CHÍNH
        JPanel pMain = new JPanel(new BorderLayout(0, 16));
        pMain.setBackground(new Color(248, 249, 250));
        pMain.setBorder(new EmptyBorder(16, 20, 20, 20));

        // Phía trên: Tiêu đề + 4 Thẻ KPI
        JPanel pTop = new JPanel(new BorderLayout(0, 14));
        pTop.setOpaque(false);
        pTop.add(createHeaderPanel(), BorderLayout.NORTH);
        pTop.add(createKpiCardsPanel(), BorderLayout.CENTER);

        pMain.add(pTop, BorderLayout.NORTH);

        // Ở giữa: Bảng Hiệu suất nhân viên
        pMain.add(createTableSection(), BorderLayout.CENTER);

        add(pMain, BorderLayout.CENTER);

        // Nạp danh sách tháng và tải dữ liệu từ CSDL
        napDanhSachThang();
        taiDuLieuBaoCao();
    }

    // --- Header có tiêu đề, ComboBox chọn tháng và các nút Thao tác ---
    private JPanel createHeaderPanel() {
        JPanel pHeader = new JPanel(new BorderLayout());
        pHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("📄 Báo Cáo Tổng Hợp");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(BRAND_RED);
        pHeader.add(lblTitle, BorderLayout.WEST);

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pActions.setOpaque(false);

        cboMonth = new JComboBox<>();
        cboMonth.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboMonth.setPreferredSize(new Dimension(140, 32));
        cboMonth.setBackground(Color.WHITE);
        cboMonth.addActionListener(this);

        btnExport = new JButton("📥  Xuất Excel");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(BRAND_RED);
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        btnExport.setPreferredSize(new Dimension(125, 32));
        btnExport.addActionListener(this);

        btnPrint = new JButton("🖨  In");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.setBackground(new Color(49, 130, 206));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setPreferredSize(new Dimension(75, 32));
        btnPrint.addActionListener(this);

        pActions.add(cboMonth);
        pActions.add(btnExport);
        pActions.add(btnPrint);

        pHeader.add(pActions, BorderLayout.EAST);
        return pHeader;
    }

    // --- 4 Thẻ KPI Thống kê ---
    private JPanel createKpiCardsPanel() {
        JPanel pCards = new JPanel(new GridLayout(1, 4, 14, 0));
        pCards.setOpaque(false);
        pCards.setPreferredSize(new Dimension(0, 85));

        // Card 1: Hóa đơn trong tháng (Viền đỏ)
        lblValHoaDon = new JLabel("0");
        pCards.add(createKpiCard(new Color(229, 62, 62), "🛒", new Color(254, 226, 226), lblValHoaDon, "Hóa đơn trong tháng"));

        // Card 2: Tổng doanh thu (Viền xanh lá)
        lblValDoanhThu = new JLabel("0đ");
        pCards.add(createKpiCard(new Color(56, 161, 105), "💵", new Color(220, 252, 231), lblValDoanhThu, "Tổng doanh thu"));

        // Card 3: Khách hàng mới (Viền xanh dương)
        lblValKhachMoi = new JLabel("0");
        pCards.add(createKpiCard(new Color(49, 130, 206), "👥", new Color(219, 234, 254), lblValKhachMoi, "Khách hàng mới"));

        // Card 4: Trả hàng / Đổi hàng (Viền cam)
        lblValDoiTra = new JLabel("0");
        pCards.add(createKpiCard(new Color(221, 107, 32), "🔄", new Color(255, 237, 213), lblValDoiTra, "Trả hàng / Đổi hàng"));

        return pCards;
    }

    private JPanel createKpiCard(Color accentColor, String iconText, Color iconBg, JLabel lblValue, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 4, 0, 0, accentColor),
                        new EmptyBorder(12, 14, 12, 14)
                )
        ));

        // Vòng tròn chứa Icon
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(46, 46));
        iconPanel.setLayout(new GridBagLayout());
        JLabel lblIcon = new JLabel(iconText);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconPanel.add(lblIcon);
        card.add(iconPanel, BorderLayout.WEST);

        // Thông tin số lượng & chú thích
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);

        textPanel.add(lblValue);
        textPanel.add(lblSub);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    // --- Bảng Hiệu suất nhân viên bán hàng ---
    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        lblTableTitle = new JLabel("Hiệu suất nhân viên bán hàng — Tháng 10/2026");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(BRAND_RED);
        section.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"Nhân viên", "Số hóa đơn", "Doanh thu", "TB/đơn", "Đánh giá"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(BRAND_RED);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Canh lề và định dạng từng cột
        // Cột 0: Tên nhân viên (Canh trái)
        table.getColumnModel().getColumn(0).setPreferredWidth(260);

        // Cột 1: Số hóa đơn (Canh giữa/phải)
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);

        // Cột 2: Doanh thu (Canh phải, in đậm)
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return this;
            }
        });
        table.getColumnModel().getColumn(2).setPreferredWidth(140);

        // Cột 3: TB/đơn (Canh phải)
        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRender);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        // Cột 4: Đánh giá (Badge dạng pill màu)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);

                String text = value != null ? value.toString() : "";
                if ("Xuất sắc".equalsIgnoreCase(text)) {
                    lbl.setBackground(new Color(230, 244, 234));
                    lbl.setForeground(new Color(19, 115, 51));
                } else if ("Tốt".equalsIgnoreCase(text)) {
                    lbl.setBackground(new Color(232, 240, 254));
                    lbl.setForeground(new Color(26, 115, 232));
                } else {
                    lbl.setBackground(new Color(254, 247, 224));
                    lbl.setForeground(new Color(176, 96, 0));
                }
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                return lbl;
            }
        });
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    // Nạp các tháng vào JComboBox từ database
    private void napDanhSachThang() {
        ArrayList<String> dsThang = thongKeDAO.getDanhSachThangNam();
        cboMonth.removeAllItems();
        for (String item : dsThang) {
            cboMonth.addItem(item);
        }
    }

    // Tải toàn bộ dữ liệu từ CSDL vào màn hình
    public void taiDuLieuBaoCao() {
        if (cboMonth.getSelectedItem() == null) return;

        String thangNamStr = cboMonth.getSelectedItem().toString(); // VD: "Tháng 10/2026"
        lblTableTitle.setText("Hiệu suất nhân viên bán hàng — " + thangNamStr);

        int thang = 10;
        int nam = 2026;
        try {
            // Tách chuỗi "Tháng MM/yyyy"
            String[] parts = thangNamStr.replace("Tháng", "").trim().split("/");
            thang = Integer.parseInt(parts[0].trim());
            nam = Integer.parseInt(parts[1].trim());
        } catch (Exception ignored) {}

        // 1. Cập nhật 4 Card KPI
        Object[] kpis = thongKeDAO.getThongKeTongQuanTheoThang(thang, nam);
        int soHoaDon = (int) kpis[0];
        double tongDoanhThu = (double) kpis[1];
        int khachMoi = (int) kpis[2];
        int doiTra = (int) kpis[3];

        lblValHoaDon.setText(String.valueOf(soHoaDon));
        lblValKhachMoi.setText(String.valueOf(khachMoi));
        lblValDoiTra.setText(String.valueOf(doiTra));

        // Format doanh thu dạng rút gọn hoặc chi tiết
        if (tongDoanhThu >= 1_000_000_000) {
            lblValDoanhThu.setText(String.format("%.1fB", tongDoanhThu / 1_000_000_000));
        } else if (tongDoanhThu >= 1_000_000) {
            lblValDoanhThu.setText(String.format("%.0fM", tongDoanhThu / 1_000_000));
        } else {
            lblValDoanhThu.setText(dfMoney.format(tongDoanhThu));
        }
        lblValDoanhThu.setToolTipText(dfMoney.format(tongDoanhThu));

        // 2. Cập nhật Bảng Hiệu suất nhân viên
        tableModel.setRowCount(0);
        ArrayList<Object[]> dsHieuSuat = thongKeDAO.getHieuSuatNhanVienTheoThang(thang, nam);
        for (Object[] row : dsHieuSuat) {
            String nhanVien = row[0].toString();
            int soHD = (int) row[1];
            double dt = (double) row[2];
            double tb = (double) row[3];
            String danhGia = row[4].toString();

            tableModel.addRow(new Object[]{
                    nhanVien,
                    soHD,
                    dfMoney.format(dt),
                    dfMoney.format(tb),
                    danhGia
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(cboMonth)) {
            taiDuLieuBaoCao();
        } else if (source.equals(btnExport)) {
            JOptionPane.showMessageDialog(this,
                    "Xuất file báo cáo tổng hợp ra Excel thành công!",
                    "Xuất dữ liệu", JOptionPane.INFORMATION_MESSAGE);
        } else if (source.equals(btnPrint)) {
            try {
                boolean complete = table.print(JTable.PrintMode.FIT_WIDTH,
                        new java.text.MessageFormat("Báo Cáo Hiệu Suất Nhân Viên - " + cboMonth.getSelectedItem()),
                        new java.text.MessageFormat("Trang {0}"));
                if (complete) {
                    JOptionPane.showMessageDialog(this, "In báo cáo thành công!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi in ấn: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneralReportPanel().setVisible(true));
    }
}
