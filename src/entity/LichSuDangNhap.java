package entity;

import java.util.Date;

public class LichSuDangNhap {
    private int maLichSu;
    private TaiKhoan taiKhoan;
    private Date thoiGianDangNhap;
    private Date thoiGianDangXuat;
    private String diaChiIP;
    private String tenMay;
    private String ketQua;
    private String lyDoThatBai;

    public LichSuDangNhap() {
    }

    public LichSuDangNhap(int maLichSu) {
        this.maLichSu = maLichSu;
    }

    public LichSuDangNhap(int maLichSu, TaiKhoan taiKhoan, Date thoiGianDangNhap,
                          Date thoiGianDangXuat, String diaChiIP, String tenMay,
                          String ketQua, String lyDoThatBai) {
        this.maLichSu = maLichSu;
        this.taiKhoan = taiKhoan;
        this.thoiGianDangNhap = thoiGianDangNhap;
        this.thoiGianDangXuat = thoiGianDangXuat;
        this.diaChiIP = diaChiIP;
        this.tenMay = tenMay;
        this.ketQua = ketQua;
        this.lyDoThatBai = lyDoThatBai;
    }

    public int getMaLichSu() { return maLichSu; }
    public void setMaLichSu(int maLichSu) { this.maLichSu = maLichSu; }

    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    public Date getThoiGianDangNhap() { return thoiGianDangNhap; }
    public void setThoiGianDangNhap(Date thoiGianDangNhap) { this.thoiGianDangNhap = thoiGianDangNhap; }

    public Date getThoiGianDangXuat() { return thoiGianDangXuat; }
    public void setThoiGianDangXuat(Date thoiGianDangXuat) { this.thoiGianDangXuat = thoiGianDangXuat; }

    public String getDiaChiIP() { return diaChiIP; }
    public void setDiaChiIP(String diaChiIP) { this.diaChiIP = diaChiIP; }

    public String getTenMay() { return tenMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }

    public String getKetQua() { return ketQua; }
    public void setKetQua(String ketQua) { this.ketQua = ketQua; }

    public String getLyDoThatBai() { return lyDoThatBai; }
    public void setLyDoThatBai(String lyDoThatBai) { this.lyDoThatBai = lyDoThatBai; }
}
