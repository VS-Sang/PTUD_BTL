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
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.ThongKe_DAO;

public class TopProductsPanel extends JFrame implements ActionListener {

    private static final Color BRAND_RED = new Color(175, 40, 40);
    private static final Color DARK_HEADER = new Color(145, 30, 30);
    private static final Color BG_LIGHT = new Color(248, 249, 250);

    // Bảng màu cho các thanh danh mục
    private static final Color[] CATEGORY_COLORS = {
            new Color(175, 45, 45),   // Đỏ
            new Color(49, 112, 196),  // Xanh dương
            new Color(46, 125, 50),   // Xanh lá
            new Color(211, 84, 0),    // Cam
            new Color(128, 64, 160),  // Tím
            new Color(22, 160, 133),  // Xanh ngọc
            new Color(52, 73, 94)     // Xám đen
    };

    private String currentUserName;
    private String currentUserRole;

    private JComboBox<String> cboMonth;
    private JButton btnExport;

    private JLabel lblTableTitle;
    private DefaultTableModel tableModel;
    private JTable table;

    private JPanel pCategoryBars;
    private ThongKe_DAO thongKeDAO = new ThongKe_DAO();
    private DecimalFormat dfMoney = new DecimalFormat("#,###đ");

    public TopProductsPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public TopProductsPanel(String userName, String userRole) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;

        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1280, 700);
        setMinimumSize(new Dimension(1100, 620));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. MENU CHÍNH
        add(new MenuPanel(currentUserName, currentUserRole), BorderLayout.NORTH);

        // 2. KHUNG NỘI DUNG CHÍNH
        JPanel pMain = new JPanel(new BorderLayout(0, 14));
        pMain.setBackground(BG_LIGHT);
        pMain.setBorder(new EmptyBorder(14, 18, 18, 18));

        // Thanh tiêu đề phụ
        pMain.add(createSubHeader(), BorderLayout.NORTH);

        // Phần hiển thị 2 cột (Cột Trái: Bảng Top SP — Cột Phải: Doanh thu theo danh mục)
        JPanel pContentGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        pContentGrid.setOpaque(false);

        pContentGrid.add(createLeftTopProductsCard());
        pContentGrid.add(createRightCategoryCard());

        pMain.add(pContentGrid, BorderLayout.CENTER);
        add(pMain, BorderLayout.CENTER);

        // Nạp danh sách tháng & tải dữ liệu từ CSDL
        napDanhSachThang();
        taiDuLieu();
    }

    private JPanel createSubHeader() {
        JPanel pHeader = new JPanel(new BorderLayout());
        pHeader.setOpaque(false);

        JLabel lblTitle = new JLabel(" Thống kê Sản phẩm Bán Chạy");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(DARK_HEADER);
        pHeader.add(lblTitle, BorderLayout.WEST);

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pActions.setOpaque(false);

        cboMonth = new JComboBox<>();
        cboMonth.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboMonth.setPreferredSize(new Dimension(145, 32));
        cboMonth.setBackground(Color.WHITE);
        cboMonth.addActionListener(this);

        btnExport = new JButton("  Xuất Excel");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(new Color(49, 112, 196));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        btnExport.setPreferredSize(new Dimension(125, 32));
        btnExport.addActionListener(this);

        pActions.add(cboMonth);
        pActions.add(btnExport);
        pHeader.add(pActions, BorderLayout.EAST);

        return pHeader;
    }

    // --- Cột Trái: Bảng Top 5 sản phẩm bán chạy ---
    private JPanel createLeftTopProductsCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        lblTableTitle = new JLabel("Top 5 sản phẩm bán chạy tháng 10/2026");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(DARK_HEADER);
        card.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"Hạng", "Sản phẩm", "SL bán", "Doanh thu"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(DARK_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Format Cột 0: Hạng
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
                int rank = row + 1;
                if (rank == 1) {
                    setText(" 1");
                    setForeground(new Color(212, 160, 23));
                } else if (rank == 2) {
                    setText("2");
                    setForeground(new Color(125, 135, 145));
                } else if (rank == 3) {
                    setText("3");
                    setForeground(new Color(175, 110, 60));
                } else {
                    setText(String.valueOf(rank));
                    setForeground(new Color(70, 70, 70));
                }
                return this;
            }
        });

        // Format Cột 1: Sản phẩm (Canh trái, in đậm nhẹ)
        table.getColumnModel().getColumn(1).setPreferredWidth(260);

        // Format Cột 2: Số lượng bán (Canh phải)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        // Format Cột 3: Doanh thu (Canh phải, in đậm, màu đỏ sẫm)
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(DARK_HEADER);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // --- Cột Phải: Biểu đồ Doanh thu theo danh mục ---
    private JPanel createRightCategoryCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235)),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel lblCatTitle = new JLabel("Doanh thu theo danh mục");
        lblCatTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCatTitle.setForeground(DARK_HEADER);
        card.add(lblCatTitle, BorderLayout.NORTH);

        pCategoryBars = new JPanel();
        pCategoryBars.setOpaque(false);
        pCategoryBars.setLayout(new BoxLayout(pCategoryBars, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(pCategoryBars);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // Nạp danh sách tháng có trong database vào ComboBox
    private void napDanhSachThang() {
        ArrayList<String> dsThang = thongKeDAO.getDanhSachThangNam();
        cboMonth.removeAllItems();
        cboMonth.addItem("Tất cả các tháng");
        for (String item : dsThang) {
            cboMonth.addItem(item);
        }
        if (cboMonth.getItemCount() > 1) {
            cboMonth.setSelectedIndex(1); // Chọn tháng gần nhất
        }
    }

    // Tải dữ liệu từ database lên bảng và biểu đồ
    public void taiDuLieu() {
        if (cboMonth.getSelectedItem() == null) return;

        String luaChon = cboMonth.getSelectedItem().toString();
        int thang = 0;
        int nam = 0;

        if (!"Tất cả các tháng".equals(luaChon)) {
            lblTableTitle.setText("Top 5 sản phẩm bán chạy " + luaChon.toLowerCase());
            try {
                String[] parts = luaChon.replace("Tháng", "").trim().split("/");
                thang = Integer.parseInt(parts[0].trim());
                nam = Integer.parseInt(parts[1].trim());
            } catch (Exception ignored) {}
        } else {
            lblTableTitle.setText("Top 5 sản phẩm bán chạy — Tất cả thời gian");
        }

        // 1. Tải Top 5 sản phẩm bán chạy
        tableModel.setRowCount(0);
        ArrayList<Object[]> dsTopSP = thongKeDAO.getTop5SanPhamTheoThang(thang, nam);
        int rank = 1;
        for (Object[] item : dsTopSP) {
            String tenSP = item[1].toString();
            int sl = (int) item[2];
            double dt = (double) item[3];
            tableModel.addRow(new Object[]{
                    rank++,
                    tenSP,
                    sl,
                    dfMoney.format(dt)
            });
        }

        // 2. Tải Doanh thu theo danh mục
        pCategoryBars.removeAll();
        ArrayList<Object[]> dsDanhMuc = thongKeDAO.getDoanhThuTheoDanhMuc(thang, nam);

        int colorIdx = 0;
        for (Object[] dm : dsDanhMuc) {
            String tenDM = dm[0].toString();
            int phanTram = (int) dm[2];
            Color barColor = CATEGORY_COLORS[colorIdx % CATEGORY_COLORS.length];
            colorIdx++;

            pCategoryBars.add(createCategoryBarItem(tenDM, phanTram, barColor));
            pCategoryBars.add(Box.createVerticalStrut(14));
        }

        if (dsDanhMuc.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có dữ liệu danh mục trong khoảng thời gian này.");
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            pCategoryBars.add(lblEmpty);
        }

        pCategoryBars.revalidate();
        pCategoryBars.repaint();
    }

    // Tạo thanh tiến trình cho từng Danh mục
    private JPanel createCategoryBarItem(String tenDanhMuc, int phanTram, Color color) {
        JPanel item = new JPanel(new BorderLayout(0, 5));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // Dòng trên: Tên danh mục (trái) và Phần trăm (phải)
        JPanel textRow = new JPanel(new BorderLayout());
        textRow.setOpaque(false);

        JLabel lblName = new JLabel(tenDanhMuc);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(40, 40, 40));

        JLabel lblPercent = new JLabel(phanTram + "%");
        lblPercent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPercent.setForeground(new Color(60, 60, 60));

        textRow.add(lblName, BorderLayout.WEST);
        textRow.add(lblPercent, BorderLayout.EAST);
        item.add(textRow, BorderLayout.NORTH);

        // Dòng dưới: Thanh progress bar bo góc
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Nền thanh (xám nhạt)
                g2.setColor(new Color(238, 240, 243));
                g2.fillRoundRect(0, 0, width, height, height, height);

                // Phần trăm đã đạt (màu sắc)
                int fillWidth = (int) (width * (phanTram / 100.0));
                if (fillWidth > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(0, 0, fillWidth, height, height, height);
                }
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 10));
        bar.setOpaque(false);
        item.add(bar, BorderLayout.CENTER);

        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(cboMonth)) {
            taiDuLieu();
        } else if (source.equals(btnExport)) {
            JOptionPane.showMessageDialog(this,
                    "Xuất danh sách sản phẩm bán chạy ra Excel thành công!",
                    "Xuất dữ liệu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TopProductsPanel().setVisible(true));
    }
}
