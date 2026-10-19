package entity;

import java.util.Date;

public class PhanCongCaLam {
    private String maPhanCong;
    private NhanVien nhanVien;
    private CaLamViec caLamViec;
    private Date ngayLamViec;
    private String trangThai;
    private Date gioDiemDanhVao;
    private Date gioDiemDanhRa;
    private double soGioLamThem;
    private String ghiChu;
    private NhanVien nguoiPhanCong;
    private Date ngayTao;
    private Date ngayCapNhat;

    public PhanCongCaLam() {
    }

    public PhanCongCaLam(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public PhanCongCaLam(String maPhanCong, NhanVien nhanVien, CaLamViec caLamViec, Date ngayLamViec,
                         String trangThai, Date gioDiemDanhVao, Date gioDiemDanhRa,
                         double soGioLamThem, String ghiChu, NhanVien nguoiPhanCong,
                         Date ngayTao, Date ngayCapNhat) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = nhanVien;
        this.caLamViec = caLamViec;
        this.ngayLamViec = ngayLamViec;
        this.trangThai = trangThai;
        this.gioDiemDanhVao = gioDiemDanhVao;
        this.gioDiemDanhRa = gioDiemDanhRa;
        this.soGioLamThem = soGioLamThem;
        this.ghiChu = ghiChu;
        this.nguoiPhanCong = nguoiPhanCong;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
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

    public Date getGioDiemDanhVao() { return gioDiemDanhVao; }
    public void setGioDiemDanhVao(Date gioDiemDanhVao) { this.gioDiemDanhVao = gioDiemDanhVao; }

    public Date getGioDiemDanhRa() { return gioDiemDanhRa; }
    public void setGioDiemDanhRa(Date gioDiemDanhRa) { this.gioDiemDanhRa = gioDiemDanhRa; }

    public double getSoGioLamThem() { return soGioLamThem; }
    public void setSoGioLamThem(double soGioLamThem) { this.soGioLamThem = soGioLamThem; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public NhanVien getNguoiPhanCong() { return nguoiPhanCong; }
    public void setNguoiPhanCong(NhanVien nguoiPhanCong) { this.nguoiPhanCong = nguoiPhanCong; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
