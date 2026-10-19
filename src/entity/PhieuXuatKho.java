package entity;

import java.util.Date;

public class PhieuXuatKho {
    private String maPhieuXuat;
    private NhanVien nhanVien;
    private DonHang donHang;
    private String lyDoXuat;
    private Date ngayXuat;
    private int tongSoLuong;
    private double tongTienXuat;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public PhieuXuatKho() {
    }

    public PhieuXuatKho(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public PhieuXuatKho(String maPhieuXuat, NhanVien nhanVien, DonHang donHang, String lyDoXuat,
                        Date ngayXuat, int tongSoLuong, double tongTienXuat, String trangThai,
                        String ghiChu, Date ngayTao, Date ngayCapNhat) {
        this.maPhieuXuat = maPhieuXuat;
        this.nhanVien = nhanVien;
        this.donHang = donHang;
        this.lyDoXuat = lyDoXuat;
        this.ngayXuat = ngayXuat;
        this.tongSoLuong = tongSoLuong;
        this.tongTienXuat = tongTienXuat;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaPhieuXuat() { return maPhieuXuat; }
    public void setMaPhieuXuat(String maPhieuXuat) { this.maPhieuXuat = maPhieuXuat; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public String getLyDoXuat() { return lyDoXuat; }
    public void setLyDoXuat(String lyDoXuat) { this.lyDoXuat = lyDoXuat; }

    public Date getNgayXuat() { return ngayXuat; }
    public void setNgayXuat(Date ngayXuat) { this.ngayXuat = ngayXuat; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int tongSoLuong) { this.tongSoLuong = tongSoLuong; }

    public double getTongTienXuat() { return tongTienXuat; }
    public void setTongTienXuat(double tongTienXuat) { this.tongTienXuat = tongTienXuat; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
