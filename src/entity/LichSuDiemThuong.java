package entity;

import java.util.Date;

public class LichSuDiemThuong {
    private String maLichSu;
    private KhachHang khachHang;
    private DonHang donHang;
    private HoaDon hoaDon;
    private String loaiGiaoDich;
    private int soDiem;
    private int diemTruocGD;
    private int diemSauGD;
    private String moTa;
    private Date ngayGiaoDich;

    public LichSuDiemThuong() {
    }

    public LichSuDiemThuong(String maLichSu) {
        this.maLichSu = maLichSu;
    }

    public LichSuDiemThuong(String maLichSu, KhachHang khachHang, DonHang donHang, HoaDon hoaDon,
                            String loaiGiaoDich, int soDiem, int diemTruocGD, int diemSauGD,
                            String moTa, Date ngayGiaoDich) {
        this.maLichSu = maLichSu;
        this.khachHang = khachHang;
        this.donHang = donHang;
        this.hoaDon = hoaDon;
        this.loaiGiaoDich = loaiGiaoDich;
        this.soDiem = soDiem;
        this.diemTruocGD = diemTruocGD;
        this.diemSauGD = diemSauGD;
        this.moTa = moTa;
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public String getMaLichSu() { return maLichSu; }
    public void setMaLichSu(String maLichSu) { this.maLichSu = maLichSu; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }

    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }

    public int getSoDiem() { return soDiem; }
    public void setSoDiem(int soDiem) { this.soDiem = soDiem; }

    public int getDiemTruocGD() { return diemTruocGD; }
    public void setDiemTruocGD(int diemTruocGD) { this.diemTruocGD = diemTruocGD; }

    public int getDiemSauGD() { return diemSauGD; }
    public void setDiemSauGD(int diemSauGD) { this.diemSauGD = diemSauGD; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Date getNgayGiaoDich() { return ngayGiaoDich; }
    public void setNgayGiaoDich(Date ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }
}
