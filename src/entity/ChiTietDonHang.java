package entity;

public class ChiTietDonHang {
    private DonHang donHang;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private double giamGia;
    private String ghiChu;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, int soLuong, double donGia,
                          double giamGia, String ghiChu) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giamGia = giamGia;
        this.ghiChu = ghiChu;
    }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }

    public double getThanhTien() { return soLuong * donGia - giamGia; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
