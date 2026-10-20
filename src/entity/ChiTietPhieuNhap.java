package entity;

import java.util.Date;

public class ChiTietPhieuNhap {
    private PhieuNhapKho phieuNhapKho;
    private SanPham sanPham;
    private int soLuongNhap;
    private double donGiaNhap;
    private double thanhTien;
    private Date ngaySanXuat;
    private Date hanSuDung;
    private String ghiChu;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(PhieuNhapKho phieuNhapKho, SanPham sanPham, int soLuongNhap, double donGiaNhap,
                            double thanhTien, Date ngaySanXuat, Date hanSuDung, String ghiChu) {
        this.phieuNhapKho = phieuNhapKho;
        this.sanPham = sanPham;
        this.soLuongNhap = soLuongNhap;
        this.donGiaNhap = donGiaNhap;
        this.thanhTien = thanhTien;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
        this.ghiChu = ghiChu;
    }

    public PhieuNhapKho getPhieuNhapKho() { return phieuNhapKho; }
    public void setPhieuNhapKho(PhieuNhapKho phieuNhapKho) { this.phieuNhapKho = phieuNhapKho; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public double getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public Date getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(Date ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
