package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;


public class ProductPanel extends JFrame{
	private DefaultTableModel model;
	private JTable table;
	public ProductPanel() {
		setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel pTopContainer = new JPanel(new BorderLayout());

        JPanel pHeaderRed = new JPanel(new BorderLayout());
        pHeaderRed.setBackground(new Color(180, 40, 40));
        pHeaderRed.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel lblSysTitle = new JLabel("  ⚡ Điện Máy Đỏ — Hệ Thống Quản Lý");
        lblSysTitle.setForeground(Color.WHITE);
        lblSysTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pHeaderRed.add(lblSysTitle, BorderLayout.WEST);

        JPanel pUserRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pUserRight.setOpaque(false);
        JLabel lblUser = new JLabel("👤 Nguyễn Văn An");
        lblUser.setForeground(Color.WHITE);
        JButton btnRole = new JButton("Quản lý");
        btnRole.setMargin(new Insets(2, 5, 2, 5));
        pUserRight.add(lblUser);
        pUserRight.add(btnRole);
        pHeaderRed.add(pUserRight, BorderLayout.EAST);

        JPanel pMenuBar = new JPanel(new BorderLayout());
        pMenuBar.setBackground(new Color(240, 240, 240));
        pMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel pMenuLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        pMenuLeft.setOpaque(false);
        pMenuLeft.add(new JButton("⚙ Hệ thống ▾"));
        pMenuLeft.add(new JButton("📁 Danh mục ▾"));
        pMenuLeft.add(new JButton("📋 Xử lý ▾"));
        pMenuLeft.add(new JButton("🔍 Tìm kiếm ▾"));
        pMenuLeft.add(new JButton("📊 Thống kê ▾"));
        pMenuLeft.add(new JButton("👤 Tài khoản ▾"));
        JPanel pMenuRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
        pMenuRight.setOpaque(false);
        pMenuRight.add(new JButton("🏠"));
        pMenuRight.add(new JButton("🖩"));
        pMenuRight.add(new JButton("📊"));

        
        JButton btnLogout = new JButton();
        btnLogout.setToolTipText("Đăng xuất");
        ImageIcon icon  = new ImageIcon(getClass().getResource("/pic/lo.png"));
        Image image = icon.getImage().getScaledInstance(
        	    15, 15, Image.SCALE_SMOOTH
        	);
        btnLogout.setIcon(new ImageIcon(image));
        
        
        
        pMenuRight.add(btnLogout);
        pMenuBar.add(pMenuLeft, BorderLayout.WEST);
        pMenuBar.add(pMenuRight, BorderLayout.EAST);
        pTopContainer.add(pHeaderRed, BorderLayout.NORTH);
        pTopContainer.add(pMenuBar, BorderLayout.CENTER);
        add(pTopContainer, BorderLayout.NORTH);
        
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

        
        JButton btnExport = new JButton("Xuất Excel");
        pSubActions.add(btnExport);
        pSubHeader.add(pSubActions, BorderLayout.EAST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JTextField txtSearch = new JTextField("⌕ Tìm tên SP, mã SP...", 25);
        JComboBox<String> cboStatus = new JComboBox<>(
                new String[]{"Tất cả trạng thái", "Hoàn thành", "Đang xử lý", "Chờ xác nhận"});
        JComboBox<String> cboMonth = new JComboBox<>(
                new String[]{"Tất cả tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                        "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9",
                        "Tháng 10", "Tháng 11", "Tháng 12"});
        pSearch.add(txtSearch);
        pSearch.add(cboStatus);
        pSearch.add(cboMonth);

        JPanel pNorthCenter = new JPanel(new BorderLayout());
        pNorthCenter.add(pSubHeader, BorderLayout.NORTH);
        pNorthCenter.add(pSearch, BorderLayout.CENTER);
        pCenterContainer.add(pNorthCenter, BorderLayout.NORTH);
        
        String[] column = {" ", "Mã SP", "Tên sản phẩm", "Danh mục", "Thương hiệu", 
        		"Giá nhập", "Giá bán", "Tồn kho", "BH(tháng)", "Trạng thái"};
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
        
        JScrollPane scrollPane = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pCenterContainer.add(scrollPane, BorderLayout.CENTER);
        add(pCenterContainer, BorderLayout.CENTER);
	}
	public static void main(String[] args) {
		new ProductPanel().setVisible(true);
	}
}
