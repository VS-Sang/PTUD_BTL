package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import DAO.PhieuTraHang_DAO;
import DAO.PhieuTraHang_DAO.DongHangTra;
import DAO.PhieuTraHang_DAO.HoaDonTuyChon;
import DAO.PhieuTraHang_DAO.HoaDonTraHang;
import DAO.PhieuTraHang_DAO.KetQuaTraHang;
import DAO.PhieuTraHang_DAO.SanPhamDoi;

public class TraHangPanel extends JFrame implements ActionListener {
    private static final Color BRAND_RED = new Color(177, 48, 42);
    private static final Color HEADER_GRAY = new Color(220, 220, 220);
    private static final Color BORDER_GRAY = new Color(205, 205, 205);
    private static final Color BG_LIGHT = new Color(245, 245, 245);
    private static final Color SAVE_BLUE = new Color(55, 105, 190);
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");

    private final String currentUserName;
    private final String currentUserRole;
    private final PhieuTraHang_DAO phieuTraHangDao = new PhieuTraHang_DAO();
    private final List<SanPhamDoi> sanPhamDoi = new ArrayList<>();

    private JComboBox<HoaDonTuyChon> cboHoaDon;
    private JComboBox<String> cboLoaiXuLy;
    private JButton btnTim;
    private JButton btnLuuPhieu;
    private JButton btnThemHangDoi;
    private JButton btnXoaHangDoi;
    private JComboBox<SanPhamDoi> cboSanPhamDoi;
    private JSpinner spnSoLuongDoi;
    private JTextArea txtGhiChu;
    private JLabel lblThongTinHoaDon;
    private JLabel lblTongGiaTriTra;
    private JLabel lblTongGiaTriDoi;
    private JLabel lblChenhLech;
    private JPanel pChiTiet;
    private JPanel pHangDoi;
    private DefaultTableModel modelHangTra;
    private DefaultTableModel modelHangDoi;
    private JTable tableHangTra;
    private JTable tableHangDoi;

    private HoaDonTraHang hoaDonDangXuLy;

    public TraHangPanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public TraHangPanel(String userName, String userRole) {
        currentUserName = userName == null || userName.trim().isEmpty()
                ? "Nguyễn Văn An" : userName;
        currentUserRole = userRole == null || userRole.trim().isEmpty()
                ? "Quản lý" : userRole;

        setTitle("Điện Máy Đỏ — Hệ Thống Quản Lý");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        ButtonTooltips.ganTooltip(this);
        add(new ThanhMenu(currentUserName, currentUserRole), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_LIGHT);
        content.setBorder(new EmptyBorder(10, 15, 10, 15));
        content.add(createTitleBar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new javax.swing.BoxLayout(body, javax.swing.BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(createInvoiceSearchCard());
        body.add(Box.createVerticalStrut(10));
        body.add(createReturnDetailsCard());
        body.add(Box.createVerticalStrut(10));
        body.add(createExchangeCard());
        body.add(Box.createVerticalStrut(10));
        body.add(createNoteCard());

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scrollPane, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
        cboLoaiXuLy.addActionListener(e -> capNhatCheDoXuLy());
        SwingUtilities.invokeLater(this::napDanhSachHoaDon);
    }

    private JPanel createTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Xử lý Trả Hàng / Đổi Hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(BRAND_RED);

        btnLuuPhieu = new JButton("Lưu phiếu trả");
        btnLuuPhieu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLuuPhieu.setForeground(Color.WHITE);
        btnLuuPhieu.setBackground(SAVE_BLUE);
        btnLuuPhieu.setFocusPainted(false);
        btnLuuPhieu.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(43, 82, 150), 1),
                new EmptyBorder(7, 14, 7, 14)));
        btnLuuPhieu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuuPhieu.addActionListener(this);

        bar.add(title, BorderLayout.WEST);
        bar.add(btnLuuPhieu, BorderLayout.EAST);
        return bar;
    }

    private JPanel createInvoiceSearchCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_GRAY, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 166));
        card.setPreferredSize(new Dimension(900, 166));

        card.add(createCardHeader("","Tìm hóa đơn gốc"), BorderLayout.NORTH);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(12, 14, 14, 14));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.52;
        body.add(createLabel("Mã hóa đơn gốc *"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.48;
        body.add(createLabel("Loại xử lý"), gbc);

        gbc.gridy = 1;
        JPanel invoiceInput = new JPanel(new BorderLayout(6, 0));
        invoiceInput.setOpaque(false);
        cboHoaDon = new JComboBox<>();
        cboHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboHoaDon.setPreferredSize(new Dimension(0, 34));
        cboHoaDon.setToolTipText("Chọn hóa đơn đã thanh toán");
        cboHoaDon.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null
                    ? "-- Chọn hóa đơn đã thanh toán --" : value.toString());
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(2, 6, 2, 6));
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        btnTim = new JButton("Tìm");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTim.setForeground(Color.WHITE);
        btnTim.setBackground(new Color(55, 105, 190));
        btnTim.setFocusPainted(false);
        btnTim.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(43, 82, 150), 1),
                new EmptyBorder(6, 10, 6, 10)));
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTim.addActionListener(this);
        invoiceInput.add(cboHoaDon, BorderLayout.CENTER);
        invoiceInput.add(btnTim, BorderLayout.EAST);
        gbc.gridx = 0;
        gbc.weightx = 0.52;
        body.add(invoiceInput, gbc);

        cboLoaiXuLy = new JComboBox<>(new String[]{
                PhieuTraHang_DAO.TRA_HOAN_TIEN,
                PhieuTraHang_DAO.DOI_HANG
        });
        cboLoaiXuLy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboLoaiXuLy.setPreferredSize(new Dimension(0, 34));
        gbc.gridx = 1;
        gbc.weightx = 0.48;
        body.add(cboLoaiXuLy, gbc);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createReturnDetailsCard() {
        pChiTiet = createCard();
        pChiTiet.setLayout(new BorderLayout(0, 8));
        pChiTiet.setBorder(new LineBorder(BORDER_GRAY, 1));
        pChiTiet.setBackground(Color.WHITE);
        pChiTiet.add(createCardHeader("", "Sản phẩm trên hóa đơn"), BorderLayout.NORTH);
        pChiTiet.setBorder(new LineBorder(BORDER_GRAY, 1));

        JPanel details = new JPanel(new BorderLayout(0, 8));
        details.setBackground(Color.WHITE);
        details.setBorder(new EmptyBorder(10, 12, 10, 12));
        lblThongTinHoaDon = new JLabel("Chưa tìm hóa đơn");
        lblThongTinHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblThongTinHoaDon.setForeground(new Color(55, 55, 55));
        details.add(lblThongTinHoaDon, BorderLayout.NORTH);

        modelHangTra = new DefaultTableModel(new Object[]{
                "Mã SP", "Tên sản phẩm", "SL mua", "Đã trả", "Còn được trả", "SL trả", "Hoàn / SP (đ)"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 && (int) getValueAt(row, 4) > 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex >= 2 ? Number.class : String.class;
            }
        };
        tableHangTra = new JTable(modelHangTra);
        tableHangTra.setRowHeight(28);
        tableHangTra.setFillsViewportHeight(true);
        tableHangTra.getTableHeader().setReorderingAllowed(false);
        tableHangTra.getColumnModel().getColumn(5).setCellRenderer(new SoLuongTraCellRenderer());
        tableHangTra.getColumnModel().getColumn(5).setCellEditor(new SoLuongTraCellEditor());
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (value instanceof Number) {
                    setText(MONEY_FORMAT.format(((Number) value).doubleValue()));
                }
                return this;
            }
        };
        tableHangTra.getColumnModel().getColumn(6).setCellRenderer(moneyRenderer);
        tableHangTra.getModel().addTableModelListener(e -> {
            capNhatTongTien();
            if (spnSoLuongDoi != null) {
                capNhatGioiHanSoLuongDoi();
            }
        });
        TableColumnModel columns = tableHangTra.getColumnModel();
        columns.getColumn(0).setPreferredWidth(85);
        columns.getColumn(1).setPreferredWidth(250);
        for (int i = 2; i < columns.getColumnCount(); i++) {
            columns.getColumn(i).setPreferredWidth(105);
        }
        details.add(new JScrollPane(tableHangTra), BorderLayout.CENTER);

        lblTongGiaTriTra = new JLabel("Giá trị hàng trả: 0 đ");
        lblTongGiaTriTra.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongGiaTriTra.setForeground(BRAND_RED);
        details.add(lblTongGiaTriTra, BorderLayout.SOUTH);
        pChiTiet.add(details, BorderLayout.CENTER);
        pChiTiet.setVisible(false);
        return pChiTiet;
    }

    private int getSoLuongToiDaTra(int modelRow) {
        return ((Number) modelHangTra.getValueAt(modelRow, 4)).intValue();
    }

    private final class SoLuongTraCellRenderer extends JSpinner implements TableCellRenderer {
        private SoLuongTraCellRenderer() {
            setToolTipText("Dùng mũi tên lên/xuống để nhập số lượng trả");
            JFormattedTextField field = ((JSpinner.DefaultEditor) getEditor()).getTextField();
            field.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            int modelRow = table.convertRowIndexToModel(row);
            int max = getSoLuongToiDaTra(modelRow);
            int initial = value instanceof Number ? ((Number) value).intValue() : 0;
            setModel(new SpinnerNumberModel(Math.max(0, Math.min(initial, max)), 0, max, 1));
            setFont(table.getFont());
            Color background = isSelected ? table.getSelectionBackground() : table.getBackground();
            Color foreground = isSelected ? table.getSelectionForeground() : table.getForeground();
            setBackground(background);
            setForeground(foreground);
            JFormattedTextField field = ((JSpinner.DefaultEditor) getEditor()).getTextField();
            field.setBackground(background);
            field.setForeground(foreground);
            setEnabled(max > 0);
            return this;
        }
    }

    private final class SoLuongTraCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner = new JSpinner();

        private SoLuongTraCellEditor() {
            JFormattedTextField field = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            field.setHorizontalAlignment(SwingConstants.CENTER);
            spinner.setToolTipText("Dùng mũi tên lên/xuống để nhập số lượng trả");
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public boolean stopCellEditing() {
            try {
                spinner.commitEdit();
            } catch (ParseException e) {
                return false;
            }
            return super.stopCellEditing();
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            int modelRow = table.convertRowIndexToModel(row);
            int max = getSoLuongToiDaTra(modelRow);
            int initial = value instanceof Number ? ((Number) value).intValue() : 0;
            spinner.setModel(new SpinnerNumberModel(Math.max(0, Math.min(initial, max)), 0, max, 1));
            return spinner;
        }

        @Override
        public boolean isCellEditable(EventObject event) {
            return !(event instanceof MouseEvent)
                    || ((MouseEvent) event).getClickCount() >= 1;
        }
    }

    private JPanel createExchangeCard() {
        pHangDoi = createCard();
        pHangDoi.setLayout(new BorderLayout(0, 8));
        pHangDoi.add(createCardHeader("🔄", "Chọn sản phẩm khách nhận"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel addProduct = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addProduct.setOpaque(false);
        cboSanPhamDoi = new JComboBox<>();
        cboSanPhamDoi.setPreferredSize(new Dimension(380, 32));
        cboSanPhamDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spnSoLuongDoi = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        spnSoLuongDoi.setPreferredSize(new Dimension(75, 32));
        cboSanPhamDoi.addActionListener(e -> capNhatGioiHanSoLuongDoi());
        btnThemHangDoi = new JButton("+ Thêm hàng đổi");
        btnThemHangDoi.addActionListener(e -> themHangDoi());
        btnXoaHangDoi = new JButton("Xóa sản phẩm chọn");
        btnXoaHangDoi.addActionListener(e -> xoaHangDoi());
        addProduct.add(new JLabel("Sản phẩm mới:"));
        addProduct.add(cboSanPhamDoi);
        addProduct.add(new JLabel("Số lượng:"));
        addProduct.add(spnSoLuongDoi);
        addProduct.add(btnThemHangDoi);
        addProduct.add(btnXoaHangDoi);
        body.add(addProduct, BorderLayout.NORTH);

        modelHangDoi = new DefaultTableModel(new Object[]{
                "Mã SP đổi", "Tên sản phẩm", "Số lượng", "Đơn giá (đ)", "Thành tiền (đ)"
        }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? String.class : Number.class;
            }
        };
        tableHangDoi = new JTable(modelHangDoi);
        tableHangDoi.setRowHeight(28);
        tableHangDoi.setFillsViewportHeight(true);
        tableHangDoi.getTableHeader().setReorderingAllowed(false);
        tableHangDoi.getModel().addTableModelListener(e -> capNhatTongTien());
        body.add(new JScrollPane(tableHangDoi), BorderLayout.CENTER);

        JPanel summary = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        summary.setOpaque(false);
        lblTongGiaTriDoi = new JLabel("Giá trị hàng đổi: 0 đ");
        lblChenhLech = new JLabel("Chênh lệch: 0 đ");
        lblTongGiaTriDoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblChenhLech.setFont(new Font("Segoe UI", Font.BOLD, 12));
        summary.add(lblTongGiaTriDoi);
        summary.add(lblChenhLech);
        body.add(summary, BorderLayout.SOUTH);
        pHangDoi.add(body, BorderLayout.CENTER);
        pHangDoi.setVisible(false);
        return pHangDoi;
    }

    private JPanel createNoteCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.add(createCardHeader("", "Ghi chú"), BorderLayout.NORTH);
        txtGhiChu = new JTextArea(3, 30);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGhiChu.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(txtGhiChu);
        scroll.setBorder(new EmptyBorder(8, 10, 8, 10));
        card.add(scroll, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        return card;
    }

    private JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_GRAY, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    private JPanel createCardHeader(String icon, String title) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        header.setBackground(HEADER_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));
        JLabel label = new JLabel(icon + "  " + title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(BRAND_RED);
        header.add(label);
        return header;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(45, 45, 45));
        return label;
    }

    private Component boMeThongBao() {
        Window window = SwingUtilities.getWindowAncestor(getContentPane());
        return window != null && window.isShowing() ? window : this;
    }

    private void hienThiThongBao(Object message, String title, int messageType) {
        JOptionPane.showMessageDialog(UiDialogOwner.resolve(boMeThongBao()), message, title, messageType);
        capNhatBoCucSauThongBao();
    }

    private int hienThiXacNhan(Object message, String title, int optionType, int messageType) {
        int result = JOptionPane.showConfirmDialog(
                UiDialogOwner.resolve(boMeThongBao()), message, title, optionType, messageType);
        capNhatBoCucSauThongBao();
        return result;
    }

    private void capNhatBoCucSauThongBao() {
        getContentPane().revalidate();
        getContentPane().repaint();

        Container parent = getContentPane().getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }

        Window window = SwingUtilities.getWindowAncestor(getContentPane());
        if (window != null) {
            window.validate();
            window.repaint();
        }
    }

    private void timHoaDon() {
        if (tableHangTra.isEditing()) {
            tableHangTra.getCellEditor().stopCellEditing();
        }
        hoaDonDangXuLy = null;
        modelHangTra.setRowCount(0);
        modelHangDoi.setRowCount(0);
        pChiTiet.setVisible(false);
        HoaDonTuyChon hoaDonDuocChon = (HoaDonTuyChon) cboHoaDon.getSelectedItem();
        if (hoaDonDuocChon == null) {
            hienThiThongBao("Vui lòng chọn hóa đơn gốc.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            cboHoaDon.requestFocusInWindow();
            return;
        }

        try {
            hoaDonDangXuLy = phieuTraHangDao.timHoaDon(hoaDonDuocChon.getMaHoaDon());
            lblThongTinHoaDon.setText("Hóa đơn: " + hoaDonDangXuLy.getMaHoaDon()
                    + "    |    Khách hàng: " + hoaDonDangXuLy.getTenKhachHang()
                    + "    |    Tổng thanh toán: "
                    + tien(hoaDonDangXuLy.getTongThanhToan()) + " đ");
            for (DongHangTra dong : hoaDonDangXuLy.getChiTiet()) {
                modelHangTra.addRow(new Object[]{
                        dong.getMaSanPham(), dong.getTenSanPham(), dong.getSoLuongMua(),
                        dong.getSoLuongDaTra(), dong.getSoLuongConLai(), 0,
                        dong.getGiaTriHoanMoiDonVi()
                });
            }
            pChiTiet.setVisible(true);
            capNhatTongTien();
            capNhatCheDoXuLy();
            revalidate();
            repaint();
        } catch (SQLException | IllegalArgumentException e) {
            hienThiThongBao(e.getMessage(),
                    "Không thể tìm hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatCheDoXuLy() {
        boolean doiHang = PhieuTraHang_DAO.DOI_HANG.equals(cboLoaiXuLy.getSelectedItem());
        pHangDoi.setVisible(doiHang && hoaDonDangXuLy != null);
        if (doiHang && hoaDonDangXuLy != null) {
            napSanPhamDoi();
        } else {
            modelHangDoi.setRowCount(0);
        }
        capNhatTongTien();
        revalidate();
        repaint();
    }

    private void napDanhSachHoaDon() {
        cboHoaDon.removeAllItems();
        try {
            for (HoaDonTuyChon hoaDon : phieuTraHangDao.getHoaDonDaThanhToan()) {
                cboHoaDon.addItem(hoaDon);
            }
            cboHoaDon.setSelectedIndex(-1);
        } catch (SQLException exception) {
            hienThiThongBao(
                    "Không thể tải danh sách hóa đơn:\n" + exception.getMessage(),
                    "Lỗi tải hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void napSanPhamDoi() {
        try {
            sanPhamDoi.clear();
            sanPhamDoi.addAll(phieuTraHangDao.getSanPhamDoi());
            cboSanPhamDoi.removeAllItems();
            for (SanPhamDoi sanPham : sanPhamDoi) {
                cboSanPhamDoi.addItem(sanPham);
            }
            if (sanPhamDoi.isEmpty()) {
                hienThiThongBao(
                        "Không có sản phẩm đang kinh doanh còn hàng để đổi.",
                        "Không có hàng đổi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            capNhatGioiHanSoLuongDoi();
        } catch (SQLException e) {
            hienThiThongBao(
                    "Không tải được danh sách sản phẩm đổi: " + e.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatGioiHanSoLuongDoi() {
        SanPhamDoi sanPham = (SanPhamDoi) cboSanPhamDoi.getSelectedItem();
        if (sanPham == null) {
            spnSoLuongDoi.setModel(new SpinnerNumberModel(1, 1, 1, 1));
            btnThemHangDoi.setEnabled(false);
            return;
        }
        int soLuongDaChon = 0;
        for (int i = 0; i < modelHangDoi.getRowCount(); i++) {
            if (sanPham.getMaSanPham().equals(modelHangDoi.getValueAt(i, 0))) {
                soLuongDaChon = (int) modelHangDoi.getValueAt(i, 2);
                break;
            }
        }
        int gioiHan = sanPham.getSoLuongTon() + soLuongDaChon;
        spnSoLuongDoi.setModel(new SpinnerNumberModel(1, 1, Math.max(1, gioiHan), 1));
        btnThemHangDoi.setEnabled(gioiHan > 0);
    }

    private void themHangDoi() {
        SanPhamDoi sanPham = (SanPhamDoi) cboSanPhamDoi.getSelectedItem();
        if (sanPham == null) {
            return;
        }
        int soLuongThem = (int) spnSoLuongDoi.getValue();
        for (int i = 0; i < modelHangDoi.getRowCount(); i++) {
            if (sanPham.getMaSanPham().equals(modelHangDoi.getValueAt(i, 0))) {
                int tongSoLuong = (int) modelHangDoi.getValueAt(i, 2) + soLuongThem;
                if (tongSoLuong > sanPham.getSoLuongTon()
                        + soLuongTraTheoMa(sanPham.getMaSanPham())) {
                    hienThiThongBao("Số lượng đổi vượt quá tồn kho.",
                            "Không đủ hàng", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                modelHangDoi.setValueAt(tongSoLuong, i, 2);
                modelHangDoi.setValueAt(sanPham.getGiaBan() * tongSoLuong, i, 4);
                capNhatTongTien();
                return;
            }
        }
        int tonCoTheDung = sanPham.getSoLuongTon() + soLuongTraTheoMa(sanPham.getMaSanPham());
        if (soLuongThem > tonCoTheDung) {
            hienThiThongBao("Số lượng đổi vượt quá tồn kho.",
                    "Không đủ hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelHangDoi.addRow(new Object[]{
                sanPham.getMaSanPham(), sanPham.getTenSanPham(), soLuongThem,
                sanPham.getGiaBan(), sanPham.getGiaBan() * soLuongThem
        });
        capNhatTongTien();
    }

    private void xoaHangDoi() {
        int row = tableHangDoi.getSelectedRow();
        if (row >= 0) {
            modelHangDoi.removeRow(tableHangDoi.convertRowIndexToModel(row));
            capNhatTongTien();
        }
    }

    private int soLuongTraTheoMa(String maSanPham) {
        for (int i = 0; i < modelHangTra.getRowCount(); i++) {
            if (maSanPham.equals(modelHangTra.getValueAt(i, 0))) {
                return laySoLuongTra(i);
            }
        }
        return 0;
    }

    private void capNhatTongTien() {
        double giaTriTra = 0;
        for (int i = 0; i < modelHangTra.getRowCount(); i++) {
            giaTriTra += laySoLuongTra(i) * ((Number) modelHangTra.getValueAt(i, 6)).doubleValue();
        }
        double giaTriDoi = 0;
        for (int i = 0; i < modelHangDoi.getRowCount(); i++) {
            giaTriDoi += ((Number) modelHangDoi.getValueAt(i, 4)).doubleValue();
        }
        if (lblTongGiaTriTra != null) {
            lblTongGiaTriTra.setText("Giá trị hàng trả: " + tien(giaTriTra) + " đ");
        }
        if (lblTongGiaTriDoi != null) {
            lblTongGiaTriDoi.setText("Giá trị hàng đổi: " + tien(giaTriDoi) + " đ");
            double chenhLech = giaTriDoi - giaTriTra;
            lblChenhLech.setText((chenhLech >= 0 ? "Khách trả thêm: " : "Hoàn/đối trừ: ")
                    + tien(Math.abs(chenhLech)) + " đ");
            lblChenhLech.setForeground(chenhLech > 0 ? BRAND_RED : new Color(40, 120, 70));
        }
    }

    private int laySoLuongTra(int row) {
        Object value = modelHangTra.getValueAt(row, 5);
        if (value instanceof Number) {
            return Math.max(0, ((Number) value).intValue());
        }
        try {
            return Math.max(0, Integer.parseInt(value.toString().trim()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void luuPhieuTra() {
        if (tableHangTra.isEditing() && !tableHangTra.getCellEditor().stopCellEditing()) {
            return;
        }
        if (hoaDonDangXuLy == null) {
            hienThiThongBao("Vui lòng tìm hóa đơn gốc trước.",
                    "Thiếu hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Integer> soLuongTra = new LinkedHashMap<>();
        for (int i = 0; i < modelHangTra.getRowCount(); i++) {
            int soLuong = laySoLuongTra(i);
            if (soLuong > 0) {
                soLuongTra.put((String) modelHangTra.getValueAt(i, 0), soLuong);
            }
        }
        if (soLuongTra.isEmpty()) {
            hienThiThongBao("Nhập số lượng trả lớn hơn 0 cho ít nhất một sản phẩm.",
                    "Chưa chọn hàng trả", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String loaiXuLy = (String) cboLoaiXuLy.getSelectedItem();
        Map<String, Integer> soLuongDoi = laySoLuongHangDoi();
        if (PhieuTraHang_DAO.DOI_HANG.equals(loaiXuLy) && soLuongDoi.isEmpty()) {
            hienThiThongBao("Vui lòng thêm sản phẩm khách nhận để đổi.",
                    "Chưa chọn hàng đổi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double giaTriTra = tinhGiaTriHangTra(soLuongTra);
        double giaTriDoi = tinhGiaTriHangDoi();
        if (hienThiXacNhan(taoNoiDungXacNhan(loaiXuLy, giaTriTra, giaTriDoi),
                "Xác nhận xử lý", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            KetQuaTraHang ketQua = phieuTraHangDao.taoPhieuTra(
                    hoaDonDangXuLy.getMaHoaDon(), currentUserName,
                    loaiXuLy, soLuongTra, soLuongDoi, txtGhiChu.getText());
            hienThiThongBao(taoThongBaoThanhCong(ketQua, loaiXuLy),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            xoaForm();
        } catch (SQLException | IllegalArgumentException e) {
            hienThiThongBao(e.getMessage(),
                    "Không thể lưu phiếu trả", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String taoNoiDungXacNhan(String loaiXuLy, double giaTriTra, double giaTriDoi) {
        StringBuilder noiDung = new StringBuilder("Lập phiếu ")
                .append(loaiXuLy).append(" cho hóa đơn ")
                .append(hoaDonDangXuLy.getMaHoaDon()).append("?\n")
                .append("Giá trị hàng trả: ").append(tien(giaTriTra)).append(" đ\n");
        if (PhieuTraHang_DAO.DOI_HANG.equals(loaiXuLy)) {
            double chenhLech = giaTriDoi - giaTriTra;
            noiDung.append("Giá trị hàng đổi: ").append(tien(giaTriDoi)).append(" đ\n")
                    .append(moTaChenhLech(chenhLech, "Khách cần trả thêm: %s đ",
                            "Cần hoàn/đối trừ: %s đ", "Hai bên không cần thanh toán chênh lệch."))
                    .append("\n(Giao dịch chênh lệch được ghi nhận trên phiếu; "
                            + "cần thu/hoàn tiền thực tế riêng.)");
        } else {
            noiDung.append("Tiền hoàn dự kiến: ").append(tien(giaTriTra))
                    .append(" đ\n(Phiếu ghi nhận số tiền; chưa thực hiện chuyển khoản/hoàn tiền thực tế.)");
        }
        return noiDung.toString();
    }

    private String taoThongBaoThanhCong(KetQuaTraHang ketQua, String loaiXuLy) {
        StringBuilder thongBao = new StringBuilder("Đã lưu phiếu trả hàng số ")
                .append(ketQua.getMaPhieuTra()).append(".\n")
                .append("Giá trị hàng nhận lại: ")
                .append(tien(ketQua.getTongGiaTriHangTra())).append(" đ.");
        if (PhieuTraHang_DAO.DOI_HANG.equals(loaiXuLy)) {
            double chenhLech = ketQua.getChenhLechThuThem();
            thongBao.append("\nGiá trị hàng giao đổi: ")
                    .append(tien(ketQua.getTongGiaTriHangDoi())).append(" đ.\n")
                    .append(moTaChenhLech(chenhLech, "Khách cần thanh toán thêm: %s đ.",
                            "Cần hoàn/đối trừ: %s đ.", "Không phát sinh chênh lệch thanh toán."))
                    .append("\nLưu ý: hệ thống ghi nhận chênh lệch; "
                            + "thu/hoàn tiền thực tế cần thực hiện riêng.");
        } else {
            thongBao.append("\nTiền hoàn ghi nhận: ").append(tien(ketQua.getTienHoan()))
                    .append(" đ (chưa thực hiện chuyển khoản/hoàn tiền thực tế).");
        }
        if (ketQua.isDaHoanTat()
                && PhieuTraHang_DAO.TRA_HOAN_TIEN.equals(loaiXuLy)) {
            thongBao.append("\nHóa đơn đã được cập nhật trạng thái Hoàn tiền.");
        }
        return thongBao.toString();
    }

    private String moTaChenhLech(double chenhLech, String moTaThuThem,
                                 String moTaHoan, String moTaCanBang) {
        if (chenhLech > 0) {
            return String.format(moTaThuThem, tien(chenhLech));
        }
        if (chenhLech < 0) {
            return String.format(moTaHoan, tien(-chenhLech));
        }
        return moTaCanBang;
    }

    private Map<String, Integer> laySoLuongHangDoi() {
        Map<String, Integer> soLuong = new LinkedHashMap<>();
        for (int i = 0; i < modelHangDoi.getRowCount(); i++) {
            soLuong.put((String) modelHangDoi.getValueAt(i, 0),
                    ((Number) modelHangDoi.getValueAt(i, 2)).intValue());
        }
        return soLuong;
    }

    private double tinhGiaTriHangTra(Map<String, Integer> soLuongTra) {
        double tong = 0;
        for (int i = 0; i < modelHangTra.getRowCount(); i++) {
            Integer soLuong = soLuongTra.get(modelHangTra.getValueAt(i, 0));
            if (soLuong != null) {
                tong += soLuong * ((Number) modelHangTra.getValueAt(i, 6)).doubleValue();
            }
        }
        return tong;
    }

    private double tinhGiaTriHangDoi() {
        double tong = 0;
        for (int i = 0; i < modelHangDoi.getRowCount(); i++) {
            tong += ((Number) modelHangDoi.getValueAt(i, 4)).doubleValue();
        }
        return tong;
    }

    private void xoaForm() {
        hoaDonDangXuLy = null;
        napDanhSachHoaDon();
        cboHoaDon.setSelectedIndex(-1);
        txtGhiChu.setText("");
        modelHangTra.setRowCount(0);
        modelHangDoi.setRowCount(0);
        lblThongTinHoaDon.setText("Chưa tìm hóa đơn");
        pChiTiet.setVisible(false);
        pHangDoi.setVisible(false);
        capNhatTongTien();
        cboHoaDon.requestFocusInWindow();
    }

    private String tien(double amount) {
        return MONEY_FORMAT.format(amount);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTim) {
            timHoaDon();
        } else if (e.getSource() == btnLuuPhieu) {
            luuPhieuTra();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TraHangPanel().setVisible(true));
    }
}
