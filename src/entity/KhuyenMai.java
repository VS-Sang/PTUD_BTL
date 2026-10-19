package entity;

import java.util.Date;

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private String loaiKhuyenMai;
    private double giaTriGiam;
    private double giaTriGiamToiDa;
    private double donHangToiThieu;
    private int soLuongToiDa;
    private int soLuongDaSuDung;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private boolean apDungChoTatCa;
    private String trangThai;
    private NhanVien nguoiTao;
    private Date ngayTao;
    private Date ngayCapNhat;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, String moTa, String loaiKhuyenMai,
                     double giaTriGiam, double giaTriGiamToiDa, double donHangToiThieu,
                     int soLuongToiDa, int soLuongDaSuDung, Date ngayBatDau,
                     Date ngayKetThuc, boolean apDungChoTatCa, String trangThai,
                     NhanVien nguoiTao, Date ngayTao, Date ngayCapNhat) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.moTa = moTa;
        this.loaiKhuyenMai = loaiKhuyenMai;
        this.giaTriGiam = giaTriGiam;
        this.giaTriGiamToiDa = giaTriGiamToiDa;
        this.donHangToiThieu = donHangToiThieu;
        this.soLuongToiDa = soLuongToiDa;
        this.soLuongDaSuDung = soLuongDaSuDung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.apDungChoTatCa = apDungChoTatCa;
        this.trangThai = trangThai;
        this.nguoiTao = nguoiTao;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getLoaiKhuyenMai() { return loaiKhuyenMai; }
    public void setLoaiKhuyenMai(String loaiKhuyenMai) { this.loaiKhuyenMai = loaiKhuyenMai; }

    public double getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(double giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public double getGiaTriGiamToiDa() { return giaTriGiamToiDa; }
    public void setGiaTriGiamToiDa(double giaTriGiamToiDa) { this.giaTriGiamToiDa = giaTriGiamToiDa; }

    public double getDonHangToiThieu() { return donHangToiThieu; }
    public void setDonHangToiThieu(double donHangToiThieu) { this.donHangToiThieu = donHangToiThieu; }

    public int getSoLuongToiDa() { return soLuongToiDa; }
    public void setSoLuongToiDa(int soLuongToiDa) { this.soLuongToiDa = soLuongToiDa; }

    public int getSoLuongDaSuDung() { return soLuongDaSuDung; }
    public void setSoLuongDaSuDung(int soLuongDaSuDung) { this.soLuongDaSuDung = soLuongDaSuDung; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public boolean isApDungChoTatCa() { return apDungChoTatCa; }
    public void setApDungChoTatCa(boolean apDungChoTatCa) { this.apDungChoTatCa = apDungChoTatCa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public NhanVien getNguoiTao() { return nguoiTao; }
    public void setNguoiTao(NhanVien nguoiTao) { this.nguoiTao = nguoiTao; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public String toString() {
        return tenKhuyenMai;
    }
}
