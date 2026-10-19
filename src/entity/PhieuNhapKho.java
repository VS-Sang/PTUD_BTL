package entity;

import java.util.Date;

public class PhieuNhapKho {
    private String maPhieuNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nhanVien;
    private Date ngayNhap;
    private int tongSoLuong;
    private double tongTienNhap;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public PhieuNhapKho() {
    }

    public PhieuNhapKho(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public PhieuNhapKho(String maPhieuNhap, NhaCungCap nhaCungCap, NhanVien nhanVien, Date ngayNhap,
                        int tongSoLuong, double tongTienNhap, String trangThai, String ghiChu,
                        Date ngayTao, Date ngayCapNhat) {
        this.maPhieuNhap = maPhieuNhap;
        this.nhaCungCap = nhaCungCap;
        this.nhanVien = nhanVien;
        this.ngayNhap = ngayNhap;
        this.tongSoLuong = tongSoLuong;
        this.tongTienNhap = tongTienNhap;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }

    public NhaCungCap getNhaCungCap() { return nhaCungCap; }
    public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int tongSoLuong) { this.tongSoLuong = tongSoLuong; }

    public double getTongTienNhap() { return tongTienNhap; }
    public void setTongTienNhap(double tongTienNhap) { this.tongTienNhap = tongTienNhap; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
