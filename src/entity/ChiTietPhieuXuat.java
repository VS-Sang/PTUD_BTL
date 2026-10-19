package entity;

public class ChiTietPhieuXuat {
    private PhieuXuatKho phieuXuatKho;
    private SanPham sanPham;
    private int soLuongXuat;
    private double donGiaXuat;
    private double thanhTien;
    private String ghiChu;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(PhieuXuatKho phieuXuatKho, SanPham sanPham, int soLuongXuat,
                            double donGiaXuat, double thanhTien, String ghiChu) {
        this.phieuXuatKho = phieuXuatKho;
        this.sanPham = sanPham;
        this.soLuongXuat = soLuongXuat;
        this.donGiaXuat = donGiaXuat;
        this.thanhTien = thanhTien;
        this.ghiChu = ghiChu;
    }

    public PhieuXuatKho getPhieuXuatKho() { return phieuXuatKho; }
    public void setPhieuXuatKho(PhieuXuatKho phieuXuatKho) { this.phieuXuatKho = phieuXuatKho; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public int getSoLuongXuat() { return soLuongXuat; }
    public void setSoLuongXuat(int soLuongXuat) { this.soLuongXuat = soLuongXuat; }

    public double getDonGiaXuat() { return donGiaXuat; }
    public void setDonGiaXuat(double donGiaXuat) { this.donGiaXuat = donGiaXuat; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
