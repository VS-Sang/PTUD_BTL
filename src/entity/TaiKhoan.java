package entity;

import java.util.Date;

public class TaiKhoan {
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private NhanVien nhanVien;
    private String trangThai;
    private Date lanDangNhapCuoi;
    private int soLanDangNhapLoi;
    private Date ngayDoiMatKhau;
    private Date ngayTao;
    private Date ngayCapNhat;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, String vaiTro,
                    NhanVien nhanVien, String trangThai, Date lanDangNhapCuoi,
                    int soLanDangNhapLoi, Date ngayDoiMatKhau, Date ngayTao, Date ngayCapNhat) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.nhanVien = nhanVien;
        this.trangThai = trangThai;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
        this.soLanDangNhapLoi = soLanDangNhapLoi;
        this.ngayDoiMatKhau = ngayDoiMatKhau;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getLanDangNhapCuoi() { return lanDangNhapCuoi; }
    public void setLanDangNhapCuoi(Date lanDangNhapCuoi) { this.lanDangNhapCuoi = lanDangNhapCuoi; }

    public int getSoLanDangNhapLoi() { return soLanDangNhapLoi; }
    public void setSoLanDangNhapLoi(int soLanDangNhapLoi) { this.soLanDangNhapLoi = soLanDangNhapLoi; }

    public Date getNgayDoiMatKhau() { return ngayDoiMatKhau; }
    public void setNgayDoiMatKhau(Date ngayDoiMatKhau) { this.ngayDoiMatKhau = ngayDoiMatKhau; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
