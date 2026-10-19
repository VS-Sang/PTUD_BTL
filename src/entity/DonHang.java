package entity;

import java.util.Date;

public class DonHang {
    private String maDonHang;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Date ngayDatHang;
    private Date ngayGiaoHang;
    private String diaChiGiaoHang;
    private double tongTienHang;
    private double tienGiamGia;
    private KhuyenMai khuyenMai;
    private int diemSuDung;
    private double tienDiem;
    private double phiVanChuyen;
    private double thueVAT;
    private double tongThanhToan;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public DonHang() {
    }

    public DonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public DonHang(String maDonHang, KhachHang khachHang, NhanVien nhanVien, Date ngayDatHang,
                   Date ngayGiaoHang, String diaChiGiaoHang, double tongTienHang,
                   double tienGiamGia, KhuyenMai khuyenMai, int diemSuDung, double tienDiem,
                   double phiVanChuyen, double thueVAT, double tongThanhToan, String trangThai,
                   String ghiChu, Date ngayTao, Date ngayCapNhat) {
        this.maDonHang = maDonHang;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.ngayDatHang = ngayDatHang;
        this.ngayGiaoHang = ngayGiaoHang;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.tongTienHang = tongTienHang;
        this.tienGiamGia = tienGiamGia;
        this.khuyenMai = khuyenMai;
        this.diemSuDung = diemSuDung;
        this.tienDiem = tienDiem;
        this.phiVanChuyen = phiVanChuyen;
        this.thueVAT = thueVAT;
        this.tongThanhToan = tongThanhToan;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public Date getNgayDatHang() { return ngayDatHang; }
    public void setNgayDatHang(Date ngayDatHang) { this.ngayDatHang = ngayDatHang; }

    public Date getNgayGiaoHang() { return ngayGiaoHang; }
    public void setNgayGiaoHang(Date ngayGiaoHang) { this.ngayGiaoHang = ngayGiaoHang; }

    public String getDiaChiGiaoHang() { return diaChiGiaoHang; }
    public void setDiaChiGiaoHang(String diaChiGiaoHang) { this.diaChiGiaoHang = diaChiGiaoHang; }

    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }

    public double getTienGiamGia() { return tienGiamGia; }
    public void setTienGiamGia(double tienGiamGia) { this.tienGiamGia = tienGiamGia; }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }

    public int getDiemSuDung() { return diemSuDung; }
    public void setDiemSuDung(int diemSuDung) { this.diemSuDung = diemSuDung; }

    public double getTienDiem() { return tienDiem; }
    public void setTienDiem(double tienDiem) { this.tienDiem = tienDiem; }

    public double getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(double phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }

    public double getThueVAT() { return thueVAT; }
    public void setThueVAT(double thueVAT) { this.thueVAT = thueVAT; }

    public double getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(double tongThanhToan) { this.tongThanhToan = tongThanhToan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
