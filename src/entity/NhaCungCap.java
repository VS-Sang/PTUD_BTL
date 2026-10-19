package entity;

import java.util.Date;

public class NhaCungCap {
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String nguoiLienHe;
    private String maSoThue;
    private String soTaiKhoanNH;
    private String tenNganHang;
    private double congNo;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    public NhaCungCap(String maNhaCungCap, String tenNhaCungCap, String soDienThoai, String email,
                      String diaChi, String nguoiLienHe, String maSoThue, String soTaiKhoanNH,
                      String tenNganHang, double congNo, String trangThai, String ghiChu,
                      Date ngayTao, Date ngayCapNhat) {
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.nguoiLienHe = nguoiLienHe;
        this.maSoThue = maSoThue;
        this.soTaiKhoanNH = soTaiKhoanNH;
        this.tenNganHang = tenNganHang;
        this.congNo = congNo;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(String maNhaCungCap) { this.maNhaCungCap = maNhaCungCap; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getNguoiLienHe() { return nguoiLienHe; }
    public void setNguoiLienHe(String nguoiLienHe) { this.nguoiLienHe = nguoiLienHe; }

    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }

    public String getSoTaiKhoanNH() { return soTaiKhoanNH; }
    public void setSoTaiKhoanNH(String soTaiKhoanNH) { this.soTaiKhoanNH = soTaiKhoanNH; }

    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }

    public double getCongNo() { return congNo; }
    public void setCongNo(double congNo) { this.congNo = congNo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public String toString() {
        return tenNhaCungCap;
    }
}
