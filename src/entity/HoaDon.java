package entity;

import java.util.Date;

public class HoaDon {
    private String maHoaDon;
    private DonHang donHang;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Date ngayLapHoaDon;
    private double tongTienHang;
    private double tienGiamGia;
    private double tienDiem;
    private double thueVAT;
    private double tongThanhToan;
    private double tienKhachDua;
    private double tienTraLai;
    private String phuongThucThanhToan;
    private String soTaiKhoanNH;
    private String tenNganHang;
    private String maGiaoDich;
    private int diemCongThem;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, DonHang donHang, KhachHang khachHang, NhanVien nhanVien,
                  Date ngayLapHoaDon, double tongTienHang, double tienGiamGia, double tienDiem,
                  double thueVAT, double tongThanhToan, double tienKhachDua, double tienTraLai,
                  String phuongThucThanhToan, String soTaiKhoanNH, String tenNganHang,
                  String maGiaoDich, int diemCongThem, String trangThai, String ghiChu,
                  Date ngayTao, Date ngayCapNhat) {
        this.maHoaDon = maHoaDon;
        this.donHang = donHang;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.ngayLapHoaDon = ngayLapHoaDon;
        this.tongTienHang = tongTienHang;
        this.tienGiamGia = tienGiamGia;
        this.tienDiem = tienDiem;
        this.thueVAT = thueVAT;
        this.tongThanhToan = tongThanhToan;
        this.tienKhachDua = tienKhachDua;
        this.tienTraLai = tienTraLai;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.soTaiKhoanNH = soTaiKhoanNH;
        this.tenNganHang = tenNganHang;
        this.maGiaoDich = maGiaoDich;
        this.diemCongThem = diemCongThem;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public Date getNgayLapHoaDon() { return ngayLapHoaDon; }
    public void setNgayLapHoaDon(Date ngayLapHoaDon) { this.ngayLapHoaDon = ngayLapHoaDon; }

    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }

    public double getTienGiamGia() { return tienGiamGia; }
    public void setTienGiamGia(double tienGiamGia) { this.tienGiamGia = tienGiamGia; }

    public double getTienDiem() { return tienDiem; }
    public void setTienDiem(double tienDiem) { this.tienDiem = tienDiem; }

    public double getThueVAT() { return thueVAT; }
    public void setThueVAT(double thueVAT) { this.thueVAT = thueVAT; }

    public double getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(double tongThanhToan) { this.tongThanhToan = tongThanhToan; }

    public double getTienKhachDua() { return tienKhachDua; }
    public void setTienKhachDua(double tienKhachDua) { this.tienKhachDua = tienKhachDua; }

    public double getTienTraLai() { return tienTraLai; }
    public void setTienTraLai(double tienTraLai) { this.tienTraLai = tienTraLai; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public String getSoTaiKhoanNH() { return soTaiKhoanNH; }
    public void setSoTaiKhoanNH(String soTaiKhoanNH) { this.soTaiKhoanNH = soTaiKhoanNH; }

    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public int getDiemCongThem() { return diemCongThem; }
    public void setDiemCongThem(int diemCongThem) { this.diemCongThem = diemCongThem; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
