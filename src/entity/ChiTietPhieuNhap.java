package entity;

import java.util.Date;

public class ChiTietPhieuNhap {
    private PhieuNhapKho phieuNhapKho;
    private SanPham sanPham;
    private int soLuongNhap;
    private double donGiaNhap;
    private Date ngaySanXuat;
    private Date hanSuDung;
    private String ghiChu;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(PhieuNhapKho phieuNhapKho, SanPham sanPham, int soLuongNhap, double donGiaNhap,
                            Date ngaySanXuat, Date hanSuDung, String ghiChu) {
        this.phieuNhapKho = phieuNhapKho;
        this.sanPham = sanPham;
        this.soLuongNhap = soLuongNhap;
        this.donGiaNhap = donGiaNhap;
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

    public double getThanhTien() { return soLuongNhap * donGiaNhap; }

    public Date getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(Date ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
