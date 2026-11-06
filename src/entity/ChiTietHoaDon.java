package entity;

public class ChiTietHoaDon extends ChiTietDonHang {
    private String maHoaDon;
    private HoaDon hoaDon;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHoaDon, DonHang donHang, SanPham sanPham,
                         int soLuong, double donGia, double giamGia,
                         double thanhTien, String ghiChu) {
        super(donHang, sanPham, soLuong, donGia, giamGia, thanhTien, ghiChu);
        setMaHoaDon(maHoaDon);
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
        if (maHoaDon == null) {
            this.hoaDon = null;
        } else if (this.hoaDon == null) {
            this.hoaDon = new HoaDon(maHoaDon);
        } else {
            this.hoaDon.setMaHoaDon(maHoaDon);
        }
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        this.maHoaDon = hoaDon == null ? null : hoaDon.getMaHoaDon();
    }
}
