package entity;

import java.util.Date;

public class PhanCongCaLam {
    private String maPhanCong;
    private NhanVien nhanVien;
    private CaLamViec caLamViec;
    private Date ngayLamViec;
    private String trangThai;
    private String ghiChu;
    private NhanVien nguoiPhanCong;

    public PhanCongCaLam() {
    }

    public PhanCongCaLam(String maPhanCong, NhanVien nhanVien, CaLamViec caLamViec,
                         Date ngayLamViec, String trangThai, String ghiChu, NhanVien nguoiPhanCong) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = nhanVien;
        this.caLamViec = caLamViec;
        this.ngayLamViec = ngayLamViec;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.nguoiPhanCong = nguoiPhanCong;
    }

    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) { this.maPhanCong = maPhanCong; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public CaLamViec getCaLamViec() { return caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) { this.caLamViec = caLamViec; }

    public Date getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(Date ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public NhanVien getNguoiPhanCong() { return nguoiPhanCong; }
    public void setNguoiPhanCong(NhanVien nguoiPhanCong) { this.nguoiPhanCong = nguoiPhanCong; }
}
