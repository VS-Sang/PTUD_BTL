package entity;

import java.util.Date;

public class DanhMuc {
    private String maDanhMuc;
    private String tenDanhMuc;
    private String moTa;
    private String hinhAnh;
    private DanhMuc danhMucCha;
    private int thuTuHienThi;
    private String trangThai;
    private Date ngayTao;
    private Date ngayCapNhat;

    public DanhMuc() {
    }

    public DanhMuc(String maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public DanhMuc(String maDanhMuc, String tenDanhMuc, String moTa, String hinhAnh,
                   DanhMuc danhMucCha, int thuTuHienThi, String trangThai,
                   Date ngayTao, Date ngayCapNhat) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.danhMucCha = danhMucCha;
        this.thuTuHienThi = thuTuHienThi;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaDanhMuc() { return maDanhMuc; }
    public void setMaDanhMuc(String maDanhMuc) { this.maDanhMuc = maDanhMuc; }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public DanhMuc getDanhMucCha() { return danhMucCha; }
    public void setDanhMucCha(DanhMuc danhMucCha) { this.danhMucCha = danhMucCha; }

    public int getThuTuHienThi() { return thuTuHienThi; }
    public void setThuTuHienThi(int thuTuHienThi) { this.thuTuHienThi = thuTuHienThi; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public String toString() {
        return tenDanhMuc;
    }
}
