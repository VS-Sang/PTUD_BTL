package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

public class MonthlyRevenuePanel extends JFrame implements ActionListener {
    private static final Color BRAND_RED = new Color(177, 48, 42);
    private static final Color TEXT_COLOR = new Color(55, 55, 55);
    private static final Color MUTED_COLOR = new Color(125, 125, 125);
    private static final Color BACKGROUND = new Color(242, 243, 245);

    private final String userName;
    private final String userRole;
    private final ThongKe_DAO thongKeDAO = new ThongKe_DAO();
    private final NumberFormat moneyFormat = NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN"));
    private final JComboBox<Integer> yearCombo = new JComboBox<>();
    private final JLabel[] kpiValues = new JLabel[4];
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Tháng", "Số hóa đơn", "Doanh thu", "Lợi nhuận ước tính", "Tăng trưởng"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final RevenueChart chart = new RevenueChart();

    public MonthlyRevenuePanel() {
        this("Nguyễn Văn An", "Quản lý");
    }

    public MonthlyRevenuePanel(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;

        setTitle("Điện Máy Đỏ — Doanh thu theo tháng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setSize(1440, 820);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new MenuPanel(userName, userRole), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        loadYears();
        loadReport();
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("▰  Thống kê Doanh thu theo Tháng");
        title.setForeground(BRAND_RED);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        header.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setPreferredSize(new Dimension(100, 32));
        yearCombo.addActionListener(this);

        JButton exportButton = new JButton("Xuất CSV");
        exportButton.addActionListener(event -> exportCsv());
        JButton printButton = new JButton("In báo cáo");
        printButton.addActionListener(event -> printReport());
        controls.add(yearCombo);
        controls.add(exportButton);
        controls.add(printButton);
        header.add(controls, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(createKpiCards(), BorderLayout.NORTH);
        body.add(createChartCard(), BorderLayout.CENTER);
        body.add(createTableCard(), BorderLayout.SOUTH);
        content.add(body, BorderLayout.CENTER);
        return content;
    }

    private JPanel createKpiCards() {
        JPanel cards = new JPanel(new java.awt.GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 82));
        cards.add(createKpiCard("Tổng doanh thu năm", new Color(184, 62, 56), "Doanh thu đã thanh toán"));
        cards.add(createKpiCard("Lợi nhuận ước tính", new Color(75, 130, 76), "Tạm tính 15% doanh thu"));
        cards.add(createKpiCard("Hóa đơn đã thanh toán", new Color(62, 117, 190), "Trong năm được chọn"));
        cards.add(createKpiCard("Doanh thu cao nhất", new Color(211, 105, 52), "Tháng có doanh thu cao nhất"));
        return cards;
    }

    private JPanel createKpiCard(String title, Color accent, String detail) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(9, 12, 8, 12)));

        JLabel heading = new JLabel(title);
        heading.setForeground(MUTED_COLOR);
        heading.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel value = new JLabel("--");
        value.setForeground(TEXT_COLOR);
        value.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel note = new JLabel(detail);
        note.setForeground(accent);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JPanel labels = new JPanel(new BorderLayout(0, 2));
        labels.setOpaque(false);
        labels.add(heading, BorderLayout.NORTH);
        labels.add(value, BorderLayout.CENTER);
        labels.add(note, BorderLayout.SOUTH);
        card.add(labels, BorderLayout.CENTER);
        kpiValues[indexForKpi++] = value;
        return card;
    }

    private int indexForKpi;

    private JPanel createChartCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 10, 12));
        JLabel heading = new JLabel("▰  Biểu đồ doanh thu theo tháng");
        heading.setForeground(BRAND_RED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(heading, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 230));
        return card;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 9));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel heading = new JLabel("▦  Bảng thống kê chi tiết");
        heading.setForeground(BRAND_RED);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(heading, BorderLayout.NORTH);

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(232, 232, 232));
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBackground(BRAND_RED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer rightAligned = new DefaultTableCellRenderer();
        rightAligned.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int column = 1; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(rightAligned);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        card.add(scrollPane, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 265));
        return card;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(238, 238, 238));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(205, 205, 205)),
                new EmptyBorder(5, 12, 5, 12)));
        JLabel user = new JLabel("● " + userName + "   |   " + userRole + "   |   Trực tuyến");
        user.setForeground(new Color(75, 120, 75));
        user.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel version = new JLabel("Điện Máy Đỏ  |  v2.0");
        version.setForeground(MUTED_COLOR);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(user, BorderLayout.WEST);
        statusBar.add(version, BorderLayout.EAST);
        return statusBar;
    }

    private void loadYears() {
        for (Integer year : thongKeDAO.getDanhSachNam()) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(java.time.Year.now().getValue());
    }

    private void loadReport() {
        Integer selectedYear = (Integer) yearCombo.getSelectedItem();
        if (selectedYear == null) {
            return;
        }

        int year = selectedYear;
        Object[] kpis = thongKeDAO.getKPICaNam(year);
        double revenue = ((Number) kpis[0]).doubleValue();
        double profit = ((Number) kpis[1]).doubleValue();
        int invoiceCount = ((Number) kpis[2]).intValue();
        double maxRevenue = ((Number) kpis[3]).doubleValue();
        int peakMonth = ((Number) kpis[4]).intValue();

        kpiValues[0].setText(formatCompactMoney(revenue));
        kpiValues[0].setToolTipText(formatMoney(revenue));
        kpiValues[1].setText(formatCompactMoney(profit));
        kpiValues[1].setToolTipText(formatMoney(profit));
        kpiValues[2].setText(String.format("%,d", invoiceCount));
        kpiValues[3].setText(formatCompactMoney(maxRevenue));
        kpiValues[3].setToolTipText(peakMonth == 0
                ? "Chưa có dữ liệu" : "Tháng " + peakMonth + " · " + formatMoney(maxRevenue));

        tableModel.setRowCount(0);
        double[] monthlyRevenue = new double[12];
        ArrayList<Object[]> rows = thongKeDAO.getDoanhThuTheoThang(year);
        for (Object[] row : rows) {
            int month = ((Number) row[0]).intValue();
            int invoices = ((Number) row[1]).intValue();
            double monthRevenue = ((Number) row[2]).doubleValue();
            double monthProfit = ((Number) row[3]).doubleValue();
            double growth = ((Number) row[4]).doubleValue();
            if (month >= 1 && month <= monthlyRevenue.length) {
                monthlyRevenue[month - 1] = monthRevenue;
            }
            tableModel.addRow(new Object[]{
                    String.format("T%d/%d", month, year),
                    invoices,
                    formatMoney(monthRevenue),
                    formatMoney(monthProfit),
                    String.format(Locale.ROOT, "%+.0f%%", growth)
            });
        }
        chart.setMonthlyRevenue(monthlyRevenue);
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value) + " đ";
    }

    private String formatCompactMoney(double value) {
        if (value >= 1_000_000_000) {
            return String.format(Locale.ROOT, "%.1fB", value / 1_000_000_000);
        }
        if (value >= 1_000_000) {
            return String.format(Locale.ROOT, "%.0fM", value / 1_000_000);
        }
        return formatMoney(value);
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo doanh thu");
        chooser.setSelectedFile(new File("doanh-thu-" + yearCombo.getSelectedItem() + ".csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            writer.write("Thang,So hoa don,Doanh thu,Loi nhuan uoc tinh,Tang truong");
            writer.newLine();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int column = 0; column < tableModel.getColumnCount(); column++) {
                    if (column > 0) {
                        writer.write(',');
                    }
                    writer.write(csvValue(tableModel.getValueAt(row, column)));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Đã xuất báo cáo tại:\n" + file.getAbsolutePath(),
                    "Xuất báo cáo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Không thể xuất báo cáo:\n" + exception.getMessage(),
                    "Lỗi xuất báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private void printReport() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("Báo cáo doanh thu theo tháng - " + yearCombo.getSelectedItem()),
                    new java.text.MessageFormat("Trang {0}"));
        } catch (java.awt.print.PrinterException exception) {
            JOptionPane.showMessageDialog(this, "Không thể in báo cáo:\n" + exception.getMessage(),
                    "Lỗi in báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == yearCombo) {
            loadReport();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MonthlyRevenuePanel().setVisible(true));
    }

    private static final class RevenueChart extends JPanel {
        private double[] monthlyRevenue = new double[12];

        private RevenueChart() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(0, 175));
        }

        private void setMonthlyRevenue(double[] values) {
            monthlyRevenue = values.clone();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int left = 34;
            int right = 16;
            int top = 26;
            int bottom = 34;
            int plotWidth = getWidth() - left - right;
            int plotHeight = getHeight() - top - bottom;
            double max = 0;
            for (double value : monthlyRevenue) {
                max = Math.max(max, value);
            }
            if (max <= 0) {
                g.setColor(MUTED_COLOR);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                String message = "Chưa có dữ liệu doanh thu trong năm này";
                int x = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2;
                g.drawString(message, Math.max(left, x), getHeight() / 2);
                g.dispose();
                return;
            }

            int slotWidth = Math.max(1, plotWidth / 12);
            int barWidth = Math.max(8, Math.min(34, slotWidth / 2));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            for (int month = 0; month < monthlyRevenue.length; month++) {
                int xCenter = left + month * plotWidth / 12 + slotWidth / 2;
                int barHeight = (int) Math.round(monthlyRevenue[month] / max * (plotHeight - 12));
                int x = xCenter - barWidth / 2;
                int y = top + plotHeight - barHeight;
                if (barHeight > 0) {
                    g.setColor(BRAND_RED);
                    g.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
                    String amount = formatAxisMoney(monthlyRevenue[month]);
                    g.setColor(TEXT_COLOR);
                    int labelX = xCenter - g.getFontMetrics().stringWidth(amount) / 2;
                    g.drawString(amount, labelX, Math.max(12, y - 5));
                }
                g.setColor(MUTED_COLOR);
                String monthLabel = "T" + (month + 1);
                int labelX = xCenter - g.getFontMetrics().stringWidth(monthLabel) / 2;
                g.drawString(monthLabel, labelX, top + plotHeight + 17);
            }
            g.setColor(new Color(230, 230, 230));
            g.drawLine(left, top + plotHeight, getWidth() - right, top + plotHeight);
            g.dispose();
        }

        private String formatAxisMoney(double value) {
            if (value >= 1_000_000) {
                return String.format(Locale.ROOT, "%.0fM", value / 1_000_000);
            }
            return NumberFormat.getIntegerInstance(Locale.ROOT).format(value);
        }
    }
}
