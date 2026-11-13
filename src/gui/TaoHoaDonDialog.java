package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import DAO.HoaDon_DAO;
import DAO.KhachHang_DAO;
import DAO.KhuyenMai_DAO;
import DAO.NhanVien_DAO;
import DAO.SanPham_DAO;
import entity.ChiTietDonHang;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;
import entity.SanPham;

public class TaoHoaDonDialog extends JFrame {

    private static final Color MAU_DO = new Color(180, 40, 40);
    private static final Color MAU_NEN_TIEU_DE = new Color(238, 238, 238);
    private static final Color MAU_VIEN = new Color(210, 210, 210);
    private static final Color MAU_NEN_TRANG = new Color(245, 245, 245);
    private static final Color MAU_XANH_LA = new Color(77, 145, 72);
    private static final Color MAU_XANH_DUONG = new Color(54, 103, 193);
    private static final Color MAU_CHU_TIEN_THOI = new Color(52, 120, 61);

    private static final String TEN_FONT = "Segoe UI";
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("#,##0.##");
    private static final SimpleDateFormat DINH_DANG_NGAY = new SimpleDateFormat("MM/dd/yyyy");

    private static final int PHAN_TRAM_VAT = 10;
    private static final double TY_LE_VAT = PHAN_TRAM_VAT / 100.0;

    private static final String TIEN_MAT = "Tiền mặt";
    private static final String CHUYEN_KHOAN = "Chuyển khoản";
    private static final String THE_TIN_DUNG = "Thẻ tín dụng";
    private static final String VI_DIEN_TU = "Ví điện tử";

    private static final int COT_MA_SP = 1;
    private static final int COT_XOA = 6;
    private static final int[] DO_RONG_CAC_COT = {42, 85, 210, 105, 55, 125, 55};

    private final DefaultTableModel duLieuBang = new DefaultTableModel(
            new Object[]{"STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "SL", "Thành tiền", "Xóa"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable bangSanPham = new ProductTable(duLieuBang);

    private final JTextField txtMaHoaDon = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JTextField txtNhanVien = new JTextField();
    private final JComboBox<KhachHang> cboKhachHang = new JComboBox<>();

    private final JTextField txtTimSanPham = new JTextField();
    private final DefaultListModel<SanPham> sanPhamGoiYModel = new DefaultListModel<>();
    private final JList<SanPham> lstSanPhamGoiY = new JList<>(sanPhamGoiYModel);
    private final JPopupMenu popupSanPhamGoiY = new JPopupMenu();
    private final JSpinner spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JLabel lblTongSoLuong = new JLabel("0");
    private final JLabel lblTongTienHang = new JLabel("0 đ");
    private final JComboBox<KhuyenMai> cboKhuyenMai = new JComboBox<>();
    private final JLabel lblThueVAT = new JLabel("0 đ");
    private final JLabel lblTongThanhToan = new JLabel("0 đ");
    private final JComboBox<String> cboPhuongThuc =
            new JComboBox<>(new String[]{TIEN_MAT, CHUYEN_KHOAN, THE_TIN_DUNG, VI_DIEN_TU});
    private final JTextField txtTienKhachDua = new JTextField();
    private final JLabel lblTienThoi = new JLabel("0 đ");
    private final JTextArea txtGhiChu = new JTextArea(3, 20);
    private JPanel dongTienThoi;

    private final JButton btnThem = createButton("Thêm vào hóa đơn", MAU_DO, Color.WHITE);
    private final JButton btnLuu = createButton("Lưu hóa đơn", MAU_XANH_LA, Color.WHITE);
    private final JButton btnIn = createButton("In hóa đơn", MAU_XANH_DUONG, Color.WHITE);
    private final JButton btnMoi = createButton("Hóa đơn mới", Color.WHITE, Color.DARK_GRAY);
    private final JButton btnDuTien = createButton("Trả đủ tiền", MAU_XANH_LA, Color.WHITE);

    private final HoaDonPanel manHinhCha;
    private final String tenNhanVien;
    private final String vaiTro;

    private final HoaDon_DAO hoaDonDao = new HoaDon_DAO();
    private final KhuyenMai_DAO khuyenMaiDao = new KhuyenMai_DAO();

    private final Map<String, SanPham> sanPhamTheoMa = new LinkedHashMap<>();
    private final Map<String, Integer> gioHang = new LinkedHashMap<>();

    private boolean daLuuHoaDon;
    private boolean daChotKhachHang;
    private String maKhachHangDaChot;
    private boolean dangTuDongDienTien;

    public TaoHoaDonDialog(HoaDonPanel owner, String tenNhanVien, String vaiTro) {
        this.manHinhCha = owner;
        this.tenNhanVien = tenNhanVien == null ? "" : tenNhanVien;
        this.vaiTro = vaiTro;

        setupWindow();
        ButtonTooltips.ganTooltip(this);

        txtMaHoaDon.setEditable(false);
        txtNgayLap.setEditable(false);
        txtNhanVien.setEditable(false);

        add(new ThanhMenu(this.tenNhanVien, this.vaiTro), BorderLayout.NORTH);
        add(createWorkArea(), BorderLayout.CENTER);
        setupSuggestionList();

        loadCustomersAndProducts();
        createNewInvoice();
        registerEvents();
        btnIn.setToolTipText("in hóa đơn");
    }

    private void setupWindow() {
        setTitle("Điện Máy Đỏ — Lập Hóa Đơn Bán Hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1440, 800);
        setLocationRelativeTo(manHinhCha);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
    }

    private JPanel createWorkArea() {
        JPanel vung = new JPanel(new BorderLayout(0, 8));
        vung.setBackground(MAU_NEN_TRANG);
        vung.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        vung.add(createTitleBar(), BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(8, 0));
        noiDung.setOpaque(false);
        noiDung.add(createLeftSection(), BorderLayout.CENTER);

        JPanel phanTinhTien = createPaymentArea();
        phanTinhTien.setMinimumSize(new Dimension(300, 0));
        phanTinhTien.setPreferredSize(new Dimension(320, 0));
        noiDung.add(phanTinhTien, BorderLayout.EAST);

        vung.add(noiDung, BorderLayout.CENTER);
        return vung;
    }

    private JPanel createTitleBar() {
        JPanel thanh = new JPanel(new BorderLayout());
        thanh.setOpaque(false);
        thanh.setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));

        JLabel tieuDe = new JLabel("Lập Hóa Đơn Bán Hàng");
        tieuDe.setForeground(MAU_DO);
        tieuDe.setFont(new Font(TEN_FONT, Font.BOLD, 16));
        thanh.add(tieuDe, BorderLayout.WEST);

        JPanel cacNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cacNut.setOpaque(false);
        cacNut.add(btnLuu);
        cacNut.add(btnIn);
        cacNut.add(btnMoi);
        thanh.add(cacNut, BorderLayout.EAST);
        return thanh;
    }

    private JPanel createLeftSection() {
        JPanel trai = new JPanel(new BorderLayout(0, 10));
        trai.setOpaque(false);
        trai.add(createInvoiceInfoSection(), BorderLayout.NORTH);
        trai.add(createProductTableSection(), BorderLayout.CENTER);
        return trai;
    }

    private JPanel createInvoiceInfoSection() {
        JPanel khung = createTitledPanel("Thông tin hóa đơn");

        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(BorderFactory.createEmptyBorder(9, 12, 10, 12));

        noiDung.add(createGeneralInfoRow());
        noiDung.add(createCustomerRow());
        noiDung.add(createProductSearchRow());

        khung.add(noiDung, BorderLayout.CENTER);
        return khung;
    }

    private JPanel createGeneralInfoRow() {
        JPanel hang = createTransparentRow(new GridLayout(1, 4));
        hang.add(createInputField(createFieldLabel("Mã hóa đơn"), txtMaHoaDon));
        hang.add(createInputField(createFieldLabel("Ngày lập"), txtNgayLap));
        hang.add(createInputField(createFieldLabel("Nhân viên"), txtNhanVien));
        hang.add(createTransparentPanel(new BorderLayout()));
        return hang;
    }

    private JPanel createCustomerRow() {
        JPanel hang = createTransparentRow(new BorderLayout());
        hang.add(createInputField(createFieldLabel("Khách hàng"), cboKhachHang), BorderLayout.CENTER);
        return hang;
    }

    private JPanel createProductSearchRow() {
        JPanel nuaPhai = createTransparentPanel(new GridLayout(1, 2));
        nuaPhai.add(createInputField(new JLabel("Số lượng"), spnSoLuong));
        nuaPhai.add(createInputField(new JLabel(" "), btnThem));

        JPanel hang = createTransparentRow(new GridLayout(1, 2));
        hang.add(createInputField(createFieldLabel("Tìm mã hoặc tên"), txtTimSanPham));
        hang.add(nuaPhai);
        return hang;
    }

    private JPanel createInputField(Component nhan, Component oNhapLieu) {
        JPanel o = createTransparentPanel(new BorderLayout(0, 8));
        o.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 7));
        o.add(nhan, BorderLayout.NORTH);

        JPanel giuChieuCao = createTransparentPanel(new BorderLayout());
        giuChieuCao.add(oNhapLieu, BorderLayout.NORTH);
        o.add(giuChieuCao, BorderLayout.CENTER);
        return o;
    }

    private JPanel createProductTableSection() {
        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(MAU_VIEN));

        bangSanPham.setRowHeight(34);
        bangSanPham.setFillsViewportHeight(true);
        bangSanPham.setShowVerticalLines(false);
        bangSanPham.setGridColor(new Color(230, 230, 230));

        bangSanPham.getTableHeader().setReorderingAllowed(false);
        bangSanPham.getTableHeader().setBackground(MAU_DO);
        bangSanPham.getTableHeader().setForeground(Color.WHITE);
        bangSanPham.getTableHeader().setFont(new Font(TEN_FONT, Font.BOLD, 12));

        for (int cot = 0; cot < DO_RONG_CAC_COT.length; cot++) {
            bangSanPham.getColumnModel().getColumn(cot).setPreferredWidth(DO_RONG_CAC_COT[cot]);
        }
        bangSanPham.getColumnModel().getColumn(COT_XOA).setCellRenderer(new DeleteCellRenderer());

        bangSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int dong = bangSanPham.rowAtPoint(event.getPoint());
                int cot = bangSanPham.columnAtPoint(event.getPoint());
                if (!daLuuHoaDon && dong >= 0 && cot == COT_XOA) {
                    removeProductFromCart(dong);
                }
            }
        });

        khung.add(new JScrollPane(bangSanPham), BorderLayout.CENTER);
        return khung;
    }

    private JPanel createPaymentArea() {
        JPanel khung = createTitledPanel("Tính tiền");

        cboKhuyenMai.setFont(new Font(TEN_FONT, Font.PLAIN, 12));
        cboKhuyenMai.setBackground(Color.WHITE);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));

        JPanel cacDong = new JPanel();
        cacDong.setLayout(new BoxLayout(cacDong, BoxLayout.Y_AXIS));
        cacDong.setBackground(Color.WHITE);
        cacDong.setBorder(BorderFactory.createEmptyBorder(9, 12, 12, 12));

        cacDong.add(Box.createVerticalGlue());
        cacDong.add(createPaymentRow(createSummaryRow("Tổng số lượng:", lblTongSoLuong), 5));
        cacDong.add(createPaymentRow(createSummaryRow("Tổng tiền hàng:", lblTongTienHang), 5));
        cacDong.add(createPaymentRow(createFieldLabel("Khuyến mãi"), 5));
        cacDong.add(createPaymentRow(cboKhuyenMai, 5));
        cacDong.add(createPaymentRow(createSummaryRow("Thuế VAT (" + PHAN_TRAM_VAT + "%):", lblThueVAT), 5));
        cacDong.add(createPaymentRow(createTotalPaymentRow(), 10));
        cacDong.add(createPaymentRow(createFieldLabel("Phương thức thanh toán"), 5));
        cacDong.add(createPaymentRow(cboPhuongThuc, 5));
        cacDong.add(createPaymentRow(createFieldLabel("Tiền khách đưa"), 5));
        cacDong.add(createPaymentRow(txtTienKhachDua, 5));
        cacDong.add(createPaymentRow(btnDuTien, 5));
        cacDong.add(createPaymentRow(createChangeRow(), 5));
        cacDong.add(createPaymentRow(createFieldLabel("Ghi chú"), 5));
        cacDong.add(createPaymentRow(new JScrollPane(txtGhiChu), 5));
        cacDong.add(Box.createVerticalGlue());

        khung.add(cacDong, BorderLayout.CENTER);
        return khung;
    }

    private JPanel createPaymentRow(Component noiDung, int leTrenDuoi) {
        JPanel dong = createTransparentRow(new BorderLayout());
        dong.setBorder(BorderFactory.createEmptyBorder(leTrenDuoi, 4, leTrenDuoi, 4));
        dong.add(noiDung, BorderLayout.CENTER);
        return dong;
    }

    private JPanel createSummaryRow(String tenDong, JLabel giaTri) {
        JPanel dong = createTransparentPanel(new BorderLayout(4, 4));

        JLabel nhan = new JLabel(tenDong);
        nhan.setForeground(Color.DARK_GRAY);
        dong.add(nhan, BorderLayout.WEST);

        giaTri.setHorizontalAlignment(SwingConstants.RIGHT);
        giaTri.setFont(new Font(TEN_FONT, Font.BOLD, 12));
        dong.add(giaTri, BorderLayout.EAST);
        return dong;
    }

    private JPanel createTotalPaymentRow() {
        JPanel dong = createTransparentPanel(new BorderLayout());
        dong.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, MAU_VIEN));

        JLabel nhan = new JLabel("TỔNG THANH TOÁN:");
        nhan.setForeground(MAU_DO);
        nhan.setFont(new Font(TEN_FONT, Font.BOLD, 16));
        lblTongThanhToan.setForeground(MAU_DO);
        lblTongThanhToan.setFont(new Font(TEN_FONT, Font.BOLD, 16));
        dong.add(nhan, BorderLayout.WEST);
        dong.add(lblTongThanhToan, BorderLayout.EAST);
        return dong;
    }

    private JPanel createChangeRow() {
        dongTienThoi = createTransparentPanel(new BorderLayout(4, 4));

        JLabel nhan = new JLabel("Tiền thối:");
        nhan.setForeground(MAU_CHU_TIEN_THOI);
        dongTienThoi.add(nhan, BorderLayout.WEST);

        lblTienThoi.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTienThoi.setFont(new Font(TEN_FONT, Font.BOLD, 13));
        dongTienThoi.add(lblTienThoi, BorderLayout.EAST);

        JPanel choGiuCho = createTransparentPanel(new BorderLayout());
        choGiuCho.setPreferredSize(new Dimension(0, 22));
        choGiuCho.add(dongTienThoi, BorderLayout.CENTER);

        dongTienThoi.setVisible(false);
        return choGiuCho;
    }

    private JPanel createTitledPanel(String tieuDe) {
        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(MAU_VIEN));

        JLabel nhan = new JLabel(tieuDe);
        nhan.setOpaque(true);
        nhan.setBackground(MAU_NEN_TIEU_DE);
        nhan.setForeground(MAU_DO);
        nhan.setFont(new Font(TEN_FONT, Font.BOLD, 12));
        nhan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                BorderFactory.createEmptyBorder(8, 11, 8, 11)));
        khung.add(nhan, BorderLayout.NORTH);
        return khung;
    }

    private static JPanel createTransparentPanel(LayoutManager boCuc) {
        JPanel panel = new JPanel(boCuc);
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel createTransparentRow(LayoutManager boCuc) {
        JPanel hang = new JPanel(boCuc) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        hang.setOpaque(false);
        return hang;
    }

    private JLabel createFieldLabel(String chu) {
        JLabel nhan = new JLabel(chu);
        nhan.setFont(new Font(TEN_FONT, Font.PLAIN, 12));
        return nhan;
    }

    private static JButton createButton(String chu, Color mauNen, Color mauChu) {
        JButton nut = new JButton(chu);
        nut.setBackground(mauNen);
        nut.setForeground(mauChu);
        nut.setFocusPainted(false);
        nut.setFont(new Font(TEN_FONT, Font.BOLD, 12));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauNen == Color.WHITE ? MAU_VIEN : mauNen),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return nut;
    }

    private void setupSuggestionList() {
        lstSanPhamGoiY.setVisibleRowCount(4);
        lstSanPhamGoiY.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSanPhamGoiY.setCellRenderer((danhSach, sp, viTri, dangChon, coFocus) -> createListItemLabel(
                danhSach,
                sp == null ? "" : sp.getMaSanPham() + " — " + sp.getTenSanPham()
                        + " (" + sp.getSoLuongTon() + " còn)",
                dangChon, 2));
        lstSanPhamGoiY.setFixedCellHeight(
                lstSanPhamGoiY.getFontMetrics(lstSanPhamGoiY.getFont()).getHeight() + 6);

        JScrollPane khungCuon = new JScrollPane(lstSanPhamGoiY);
        khungCuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        popupSanPhamGoiY.setFocusable(false);
        popupSanPhamGoiY.add(khungCuon);
    }

    private JLabel createListItemLabel(JList<?> danhSach, String chu, boolean dangChon, int leTrenDuoi) {
        JLabel nhan = new JLabel(chu);
        nhan.setOpaque(true);
        nhan.setBorder(BorderFactory.createEmptyBorder(leTrenDuoi, 5, leTrenDuoi, 5));
        nhan.setBackground(dangChon ? danhSach.getSelectionBackground() : danhSach.getBackground());
        nhan.setForeground(dangChon ? danhSach.getSelectionForeground() : danhSach.getForeground());
        return nhan;
    }

    private class ProductTable extends JTable {
        ProductTable(DefaultTableModel duLieu) {
            super(duLieu);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
            if (duLieuBang.getRowCount() == 0) {
                g.setColor(Color.GRAY);
                String loiNhac = "Chưa có sản phẩm nào. Chọn sản phẩm và nhấn “Thêm vào hóa đơn”.";
                int x = Math.max(8, (getWidth() - g.getFontMetrics().stringWidth(loiNhac)) / 2);
                int y = getHeight() / 2;
                g.drawString(loiNhac, x, y);
            }
        }
    }

    private static class DeleteCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable bang, Object giaTri,
                                                       boolean dangChon, boolean coFocus, int dong, int cot) {
            JLabel nhan = (JLabel) super.getTableCellRendererComponent(bang, giaTri, dangChon, coFocus, dong, cot);
            nhan.setForeground(MAU_DO);
            nhan.setHorizontalAlignment(SwingConstants.CENTER);
            nhan.setFont(nhan.getFont().deriveFont(Font.BOLD));
            return nhan;
        }
    }

    private void loadCustomersAndProducts() {
        cboKhachHang.addItem(null);
        cboKhachHang.setRenderer((danhSach, khach, viTri, dangChon, coFocus) -> createListItemLabel(
                danhSach,
                khach == null ? "-- Chọn khách hàng --" : khach.toString(),
                dangChon, 2));

        for (KhachHang khach : new KhachHang_DAO().getAllKhachHang()) {
            if ("Hoạt động".equals(khach.getTrangThai())) {
                cboKhachHang.addItem(khach);
            }
        }

        for (SanPham sp : new SanPham_DAO().getAllSanPham()) {
            if ("Đang kinh doanh".equals(sp.getTrangThai()) && sp.getSoLuongTon() > 0) {
                sanPhamTheoMa.put(sp.getMaSanPham(), sp);
            }
        }
        filterProducts();
        loadPromotions();
    }

    private void loadPromotions() {
        KhuyenMai daChonTruocDo = (KhuyenMai) cboKhuyenMai.getSelectedItem();

        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem(null);
        cboKhuyenMai.setRenderer(createPromotionRenderer());

        for (KhuyenMai km : khuyenMaiDao.getAllKhuyenMai()) {
            boolean daKetThuc = km.getTrangThai() != null && "Đã kết thúc".equalsIgnoreCase(km.getTrangThai());
            if (!daKetThuc) {
                cboKhuyenMai.addItem(km);
            }
        }

        if (daChonTruocDo != null) {
            for (int i = 1; i < cboKhuyenMai.getItemCount(); i++) {
                KhuyenMai km = cboKhuyenMai.getItemAt(i);
                if (km != null && km.getMaKhuyenMai().equals(daChonTruocDo.getMaKhuyenMai())) {
                    cboKhuyenMai.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private ListCellRenderer<KhuyenMai> createPromotionRenderer() {
        return (danhSach, km, viTri, dangChon, coFocus) -> {
            String chu = "-- Không áp dụng --";
            if (km != null) {
                String mucGiam = isPercentagePromotion(km)
                        ? (int) km.getGiaTriGiam() + "%"
                        : DINH_DANG_TIEN.format(km.getGiaTriGiam()) + " đ";
                chu = km.getMaKhuyenMai() + " — " + km.getTenKhuyenMai() + " (" + mucGiam + ")";
            }
            JLabel nhan = createListItemLabel(danhSach, chu, dangChon, 3);
            nhan.setFont(new Font(TEN_FONT, Font.PLAIN, 12));
            return nhan;
        };
    }

    private void registerEvents() {
        btnThem.addActionListener(e -> addProduct());
        btnLuu.addActionListener(e -> saveInvoice());
        btnIn.addActionListener(e -> printInvoiceToPrinter());
        btnMoi.addActionListener(e -> createNewInvoice());
        btnDuTien.addActionListener(e -> {
            setCustomerPaidAmount(String.valueOf(Math.round(calculateTotal().tongThanhToan)));
            updateChangeAmount();
        });

        cboKhachHang.setSelectedIndex(0);

        onTextChange(txtTimSanPham, () -> filterProducts());
        txtTimSanPham.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                handleSearchKeyEvents(event);
            }
        });
        lstSanPhamGoiY.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                btnThem.setEnabled(lstSanPhamGoiY.getSelectedValue() != null);
            }
        });

        cboPhuongThuc.addActionListener(e -> updateChangeAmount());
        cboKhuyenMai.addActionListener(e -> updateTableAndTotals());
        cboKhuyenMai.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                loadPromotions();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) { }
        });
        onTextChange(txtTienKhachDua, () -> {
            if (!dangTuDongDienTien) {
                updateChangeAmount();
            }
        });
    }

    private void onTextChange(JTextComponent o, Runnable viecCanLam) {
        o.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                viecCanLam.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                viecCanLam.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                viecCanLam.run();
            }
        });
    }

    private void handleSearchKeyEvents(KeyEvent event) {
        if (!popupSanPhamGoiY.isVisible()) {
            return;
        }
        int phim = event.getKeyCode();
        int dongDangChon = lstSanPhamGoiY.getSelectedIndex();

        if (phim == KeyEvent.VK_DOWN) {
            selectSuggestedRow(Math.min(dongDangChon + 1, sanPhamGoiYModel.size() - 1));
            event.consume();
        } else if (phim == KeyEvent.VK_UP) {
            selectSuggestedRow(Math.max(0, dongDangChon - 1));
            event.consume();
        } else if (phim == KeyEvent.VK_ENTER) {
            if (lstSanPhamGoiY.getSelectedValue() != null) {
                addProduct();
            } else if (!sanPhamGoiYModel.isEmpty()) {
                lstSanPhamGoiY.setSelectedIndex(0);
            }
            event.consume();
        } else if (phim == KeyEvent.VK_ESCAPE) {
            popupSanPhamGoiY.setVisible(false);
            lstSanPhamGoiY.clearSelection();
            btnThem.setEnabled(false);
            event.consume();
        }
    }

    private void selectSuggestedRow(int viTri) {
        lstSanPhamGoiY.setSelectedIndex(viTri);
        lstSanPhamGoiY.ensureIndexIsVisible(viTri);
    }

    private void filterProducts() {
        String tuKhoa = txtTimSanPham.getText().trim().toLowerCase(Locale.ROOT);
        sanPhamGoiYModel.clear();
        lstSanPhamGoiY.clearSelection();

        if (tuKhoa.isEmpty()) {
            hideSuggestions();
            return;
        }

        for (SanPham sp : sanPhamTheoMa.values()) {
            String thongTin = (sp.getMaSanPham() + " " + sp.getTenSanPham()).toLowerCase(Locale.ROOT);
            if (thongTin.contains(tuKhoa)) {
                sanPhamGoiYModel.addElement(sp);
            }
        }

        if (sanPhamGoiYModel.isEmpty() || !txtTimSanPham.isShowing()) {
            hideSuggestions();
            return;
        }

        int soDongHien = Math.min(5, sanPhamGoiYModel.size());
        lstSanPhamGoiY.setVisibleRowCount(soDongHien);
        popupSanPhamGoiY.setPreferredSize(new Dimension(
                txtTimSanPham.getWidth(),
                soDongHien * lstSanPhamGoiY.getFixedCellHeight() + 4));
        popupSanPhamGoiY.show(txtTimSanPham, 0, txtTimSanPham.getHeight());
        btnThem.setEnabled(false);
    }

    private void hideSuggestions() {
        popupSanPhamGoiY.setVisible(false);
        btnThem.setEnabled(false);
    }

    private void addProduct() {
        SanPham sp = lstSanPhamGoiY.getSelectedValue();
        if (sp == null) {
            showMessage("Vui lòng chọn một sản phẩm trong danh sách đề xuất.",
                    "Chưa chọn sản phẩm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int soLuongThem = ((Number) spnSoLuong.getValue()).intValue();
        long soLuongSauKhiThem = soLuongThem;
        if (gioHang.containsKey(sp.getMaSanPham())) {
            soLuongSauKhiThem += gioHang.get(sp.getMaSanPham());
        }

        if (soLuongSauKhiThem > sp.getSoLuongTon()) {
            showMessage("Sản phẩm chỉ còn " + sp.getSoLuongTon() + " trong kho.",
                    "Không đủ tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }
        gioHang.put(sp.getMaSanPham(), (int) soLuongSauKhiThem);

        if (!daChotKhachHang) {
            maKhachHangDaChot = getCustomerId(getSelectedCustomer());
            daChotKhachHang = true;
        }
        updateCustomerSelector();
        updateTableAndTotals();
        bangSanPham.repaint();

        txtTimSanPham.setText("");
        lstSanPhamGoiY.clearSelection();
        spnSoLuong.setValue(1);
    }

    private void removeProductFromCart(int dong) {
        String maSanPham = duLieuBang.getValueAt(dong, COT_MA_SP).toString();
        gioHang.remove(maSanPham);
        updateTableAndTotals();
        updateCustomerSelector();
        bangSanPham.repaint();
    }

    private static final class CalculationResult {
        final int tongSoLuong;
        final double tongTienHang;
        final double tienGiam;
        final double thue;
        final double tongThanhToan;

        CalculationResult(int tongSoLuong, double tongTienHang, double tienGiam, double thue) {
            this.tongSoLuong = tongSoLuong;
            this.tongTienHang = tongTienHang;
            this.tienGiam = tienGiam;
            this.thue = thue;
            this.tongThanhToan = tongTienHang - tienGiam + thue;
        }
    }

    private CalculationResult calculateTotal() {
        int tongSoLuong = 0;
        double tongTienHang = 0;
        for (Map.Entry<String, Integer> mon : gioHang.entrySet()) {
            SanPham sp = sanPhamTheoMa.get(mon.getKey());
            tongSoLuong += mon.getValue();
            tongTienHang += sp.getGiaBan() * mon.getValue();
        }
        double tienGiam = calculateDiscountAmount(tongTienHang);
        double thue = Math.round((tongTienHang - tienGiam) * TY_LE_VAT);
        return new CalculationResult(tongSoLuong, tongTienHang, tienGiam, thue);
    }

    private double calculateDiscountAmount(double tongTienHang) {
        KhuyenMai km = getSelectedPromotion();
        if (km == null || tongTienHang < km.getDonHangToiThieu()) {
            return 0;
        }

        double tienGiam;
        if (isPercentagePromotion(km)) {
            tienGiam = Math.round(tongTienHang * km.getGiaTriGiam() / 100.0);
            if (km.getGiaTriGiamToiDa() > 0 && tienGiam > km.getGiaTriGiamToiDa()) {
                tienGiam = km.getGiaTriGiamToiDa();
            }
        } else {
            tienGiam = km.getGiaTriGiam();
        }
        return Math.min(tienGiam, tongTienHang);
    }

    private boolean isPercentagePromotion(KhuyenMai km) {
        return "Phần trăm".equalsIgnoreCase(km.getLoaiKhuyenMai()) || "%".equals(km.getLoaiKhuyenMai());
    }

    private void updateTableAndTotals() {
        duLieuBang.setRowCount(0);
        int stt = 1;
        for (Map.Entry<String, Integer> mon : gioHang.entrySet()) {
            SanPham sp = sanPhamTheoMa.get(mon.getKey());
            int soLuong = mon.getValue();
            duLieuBang.addRow(new Object[]{
                    stt++,
                    sp.getMaSanPham(),
                    sp.getTenSanPham(),
                    DINH_DANG_TIEN.format(sp.getGiaBan()) + " đ",
                    soLuong,
                    DINH_DANG_TIEN.format(sp.getGiaBan() * soLuong) + " đ",
                    "Xóa"
            });
        }

        CalculationResult tien = calculateTotal();
        lblTongSoLuong.setText(String.valueOf(tien.tongSoLuong));
        lblTongTienHang.setText(DINH_DANG_TIEN.format(tien.tongTienHang) + " đ");
        lblThueVAT.setText(DINH_DANG_TIEN.format(tien.thue) + " đ");
        lblTongThanhToan.setText(DINH_DANG_TIEN.format(tien.tongThanhToan) + " đ");
        updateChangeAmount();
    }

    private void updateChangeAmount() {
        String phuongThuc = String.valueOf(cboPhuongThuc.getSelectedItem()).trim();
        boolean laTienMat = TIEN_MAT.equalsIgnoreCase(phuongThuc);
        boolean laChuyenKhoan = CHUYEN_KHOAN.equalsIgnoreCase(phuongThuc);

        if (laChuyenKhoan) {
            setCustomerPaidAmount(String.valueOf(Math.round(calculateTotal().tongThanhToan)));
        }

        if (dongTienThoi != null) {
            dongTienThoi.setVisible(laTienMat);
            dongTienThoi.getParent().revalidate();
            dongTienThoi.getParent().repaint();
        }

        if (!laTienMat) {
            lblTienThoi.setText("0 đ");
            return;
        }

        BigDecimal tienThoi = parseCustomerPaidAmount().subtract(BigDecimal.valueOf(calculateTotal().tongThanhToan));
        if (tienThoi.signum() > 0) {
            lblTienThoi.setText(DINH_DANG_TIEN.format(tienThoi) + " đ");
        } else if (tienThoi.signum() < 0) {
            lblTienThoi.setText("Còn thiếu " + DINH_DANG_TIEN.format(tienThoi.abs()) + " đ");
        } else {
            lblTienThoi.setText("0 đ");
        }
    }

    private void setCustomerPaidAmount(String soTien) {
        dangTuDongDienTien = true;
        try {
            txtTienKhachDua.setText(soTien);
        } finally {
            dangTuDongDienTien = false;
        }
    }

    private BigDecimal parseCustomerPaidAmount() {
        BigDecimal soTien = parseStrictCustomerPaidAmount();
        return soTien == null ? BigDecimal.ZERO : soTien;
    }

    private BigDecimal parseStrictCustomerPaidAmount() {
        String chu = txtTienKhachDua.getText().trim().replace(",", "").replace(" ", "");
        try {
            return new BigDecimal(chu);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void saveInvoice() {
        if (daLuuHoaDon) {
            showMessage("Hóa đơn này đã được lưu. Vui lòng tạo hóa đơn mới.",
                    "Hóa đơn đã lưu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (gioHang.isEmpty()) {
            showMessage("Vui lòng thêm ít nhất một sản phẩm.",
                    "Hóa đơn chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang khachHang = getSelectedCustomer();
        if (daChotKhachHang && !Objects.equals(maKhachHangDaChot, getCustomerId(khachHang))) {
            showMessage("Không thể đổi khách hàng khi hóa đơn đã có sản phẩm. "
                            + "Vui lòng tạo hóa đơn mới nếu muốn chọn khách hàng khác.",
                    "Khách hàng không khớp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal tienKhachDua = parseStrictCustomerPaidAmount();
        if (tienKhachDua == null) {
            showMessage("Vui lòng nhập tiền khách đưa hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }
        boolean khachDuaChuaDu = tienKhachDua.signum() < 0
                || tienKhachDua.compareTo(BigDecimal.valueOf(calculateTotal().tongThanhToan)) < 0;
        if (khachDuaChuaDu) {
            showMessage("Tiền khách đưa chưa đủ thanh toán.",
                    "Chưa đủ tiền thanh toán", JOptionPane.WARNING_MESSAGE);
            txtTienKhachDua.requestFocusInWindow();
            return;
        }

        NhanVien nhanVien = findLoggedInEmployee();
        if (nhanVien == null) {
            showMessage("Không tìm thấy nhân viên \"" + tenNhanVien
                            + "\" trong dữ liệu nhân viên. Không thể lập hóa đơn.",
                    "Không xác định được thu ngân", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveInvoiceToDatabase(nhanVien, khachHang, tienKhachDua);
    }

    private void saveInvoiceToDatabase(NhanVien nhanVien, KhachHang khachHang, BigDecimal tienKhachDua) {
        KhuyenMai km = getSelectedPromotion();
        String maKhuyenMai = km != null ? km.getMaKhuyenMai() : null;
        double tienGiam = calculateTotal().tienGiam;

        try {
            String maHoaDon = hoaDonDao.taoHoaDon(
                    nhanVien.getMaNhanVien(),
                    getCustomerId(khachHang),
                    createOrderDetailList(),
                    (String) cboPhuongThuc.getSelectedItem(),
                    tienKhachDua.doubleValue(),
                    maKhuyenMai,
                    tienGiam,
                    PHAN_TRAM_VAT,
                    txtGhiChu.getText().trim());

            txtMaHoaDon.setText(maHoaDon);
            daLuuHoaDon = true;
            btnLuu.setEnabled(false);
            setEditableFields(false);

            if (manHinhCha != null) {
                manHinhCha.docDuLieuDatabaseVaoTable();
            } else {
                KhungChinh.lamMoiTrangHoaDon();
            }
        } catch (SQLException | IllegalArgumentException ex) {
            showMessage("Không thể tạo hóa đơn: " + ex.getMessage(),
                    "Lỗi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<ChiTietDonHang> createOrderDetailList() {
        List<ChiTietDonHang> chiTiet = new ArrayList<>();
        for (Map.Entry<String, Integer> mon : gioHang.entrySet()) {
            SanPham sp = sanPhamTheoMa.get(mon.getKey());
            chiTiet.add(new ChiTietDonHang(null, sp, mon.getValue(), sp.getGiaBan(), 0, null));
        }
        return chiTiet;
    }

    private NhanVien findLoggedInEmployee() {
        for (NhanVien nv : new NhanVien_DAO().getAllNhanVien()) {
            if (nv.getHoTen() != null && nv.getHoTen().trim().equals(tenNhanVien.trim())) {
                return nv;
            }
        }
        return null;
    }

    private void exportInvoicePdf() {
        if (gioHang.isEmpty()) {
            showMessage("Vui lòng thêm sản phẩm trước khi in hóa đơn.",
                    "Hóa đơn chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maHoaDon = daLuuHoaDon ? txtMaHoaDon.getText().trim() : "";
        if (maHoaDon.isEmpty() || "Tự động khi lưu".equalsIgnoreCase(maHoaDon)) {
            showMessage("Vui lòng lưu hóa đơn trước khi xuất PDF để sử dụng mã hóa đơn chính thức.",
                    "Hóa đơn chưa được lưu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File fileDaChon = promptPdfSaveLocation(maHoaDon);
        if (fileDaChon == null) {
            return;
        }

        try {
            writeInvoicePdf(fileDaChon, maHoaDon);
            openFileWithDefaultApp(fileDaChon);
            showMessage("Đã xuất hóa đơn PDF:\n" + fileDaChon.getAbsolutePath(),
                    "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showMessage("Không thể xuất hóa đơn PDF:\n" + ex.getMessage(),
                    "Lỗi xuất PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File promptPdfSaveLocation(String maHoaDon) {
        JFileChooser hop = new JFileChooser();
        hop.setDialogTitle("Lưu hóa đơn thành PDF");
        hop.setFileFilter(new FileNameExtensionFilter("Tệp PDF (*.pdf)", "pdf"));
        hop.setSelectedFile(new File("HD-" + maHoaDon + ".pdf"));
        if (hop.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = hop.getSelectedFile().getAbsoluteFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists()) {
            int traLoi = JOptionPane.showConfirmDialog(getDialogOwnerWindow(),
                    "Tệp đã tồn tại. Bạn có muốn ghi đè không?",
                    "Xác nhận ghi đè", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (traLoi != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        return file;
    }

    private void openFileWithDefaultApp(File file) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception boQua) {
        }
    }

    private void writeInvoicePdf(File file, String maHoaDon) throws IOException {
        try {
            HoaDon hoaDon = hoaDonDao.getHoaDonByMa(maHoaDon);
            BoInHoaDon.writePdf(file, hoaDon, hoaDonDao.getChiTietHoaDon(maHoaDon));
        } catch (SQLException exception) {
            throw new IOException("Không thể tải dữ liệu hóa đơn " + maHoaDon + ".", exception);
        }
    }

    private void printInvoiceToPrinter() {
        if (!daLuuHoaDon) {
            showMessage("Hãy lưu hóa đơn trước khi in bằng máy in.",
                    "Hóa đơn chưa được lưu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            HoaDon hoaDon = hoaDonDao.getHoaDonByMa(txtMaHoaDon.getText().trim());
            BoInHoaDon.print(this, hoaDon, hoaDonDao.getChiTietHoaDon(hoaDon.getMaHoaDon()));
        } catch (SQLException ex) {
            showMessage("Không thể tải hóa đơn để in:\n" + ex.getMessage(),
                    "Lỗi in hóa đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewInvoice() {
        gioHang.clear();
        daLuuHoaDon = false;
        daChotKhachHang = false;
        maKhachHangDaChot = null;
        btnLuu.setEnabled(true);
        setEditableFields(true);
        duLieuBang.setRowCount(0);

        txtMaHoaDon.setText("Tự động khi lưu");
        txtNgayLap.setText(DINH_DANG_NGAY.format(new Date()));
        txtNhanVien.setText(tenNhanVien);
        setCustomerPaidAmount("0");
        txtGhiChu.setText("");
        cboKhachHang.setSelectedIndex(0);

        txtTimSanPham.setText("");
        lstSanPhamGoiY.clearSelection();
        filterProducts();
        cboPhuongThuc.setSelectedIndex(0);
        spnSoLuong.setValue(1);

        loadPromotions();
        cboKhuyenMai.setSelectedIndex(0);
        updateTableAndTotals();
        bangSanPham.repaint();
    }

    private void setEditableFields(boolean choPhep) {
        bangSanPham.setEnabled(choPhep);
        btnThem.setEnabled(choPhep);
        txtTimSanPham.setEnabled(choPhep);
        lstSanPhamGoiY.setEnabled(choPhep);
        if (!choPhep) {
            popupSanPhamGoiY.setVisible(false);
        }
        spnSoLuong.setEnabled(choPhep);
        updateCustomerSelector(choPhep);
        cboPhuongThuc.setEnabled(choPhep);
        cboKhuyenMai.setEnabled(choPhep);
        txtTienKhachDua.setEnabled(choPhep);
        txtGhiChu.setEnabled(choPhep);
    }

    private void updateCustomerSelector() {
        updateCustomerSelector(!daLuuHoaDon);
    }

    private void updateCustomerSelector(boolean hoaDonConSuaDuoc) {
        cboKhachHang.setEnabled(hoaDonConSuaDuoc && !daChotKhachHang);
        cboKhachHang.setToolTipText(daChotKhachHang
                ? "Khách hàng đã được cố định cho hóa đơn này. Tạo hóa đơn mới để chọn khách khác."
                : "Chọn khách hàng trước khi thêm sản phẩm.");
    }

    private KhachHang getSelectedCustomer() {
        return (KhachHang) cboKhachHang.getSelectedItem();
    }

    private KhuyenMai getSelectedPromotion() {
        return (KhuyenMai) cboKhuyenMai.getSelectedItem();
    }

    private String getCustomerId(KhachHang khachHang) {
        return khachHang == null ? null : khachHang.getMaKhachHang();
    }

    private void showMessage(String noiDung, String tieuDe, int loai) {
        JOptionPane.showMessageDialog(getDialogOwnerWindow(), noiDung, tieuDe, loai);
    }

    private Component getDialogOwnerWindow() {
        Window cuaSo = SwingUtilities.getWindowAncestor(getContentPane());
        return ChuSoHuuHopThoai.resolve(cuaSo == null ? this : cuaSo);
    }
}