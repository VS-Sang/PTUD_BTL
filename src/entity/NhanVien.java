package entity;

import java.util.Date;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String gioiTinh;
    private Date ngaySinh;
    private String soCMND;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String hinhAnh;
    private String chucVu;
    private String phongBan;
    private double luongCoBan;
    private double heSoLuong;
    private double phuCap;
    private Date ngayVaoLam;
    private Date ngayNghiViec;
    private String trangThai;
    private NhanVien nguoiQuanLy;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String hoTen, String gioiTinh, Date ngaySinh, String soCMND,
                    String soDienThoai, String email, String diaChi, String chucVu, String phongBan,
                    double luongCoBan, double heSoLuong, double phuCap, Date ngayVaoLam, String trangThai) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soCMND = soCMND;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.phongBan = phongBan;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
        this.phuCap = phuCap;
        this.ngayVaoLam = ngayVaoLam;
        this.trangThai = trangThai;
    }

    public NhanVien(String maNhanVien, String hoTen, String gioiTinh, Date ngaySinh, String soCMND,
                    String soDienThoai, String email, String diaChi, String hinhAnh, String chucVu,
                    String phongBan, double luongCoBan, double heSoLuong, double phuCap, Date ngayVaoLam,
                    Date ngayNghiViec, String trangThai, NhanVien nguoiQuanLy, String ghiChu,
                    Date ngayTao, Date ngayCapNhat) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soCMND = soCMND;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.hinhAnh = hinhAnh;
        this.chucVu = chucVu;
        this.phongBan = phongBan;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
        this.phuCap = phuCap;
        this.ngayVaoLam = ngayVaoLam;
        this.ngayNghiViec = ngayNghiViec;
        this.trangThai = trangThai;
        this.nguoiQuanLy = nguoiQuanLy;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSoCMND() { return soCMND; }
    public void setSoCMND(String soCMND) { this.soCMND = soCMND; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getPhongBan() { return phongBan; }
    public void setPhongBan(String phongBan) { this.phongBan = phongBan; }

    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }

    public double getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(double heSoLuong) { this.heSoLuong = heSoLuong; }

    public double getPhuCap() { return phuCap; }
    public void setPhuCap(double phuCap) { this.phuCap = phuCap; }

    public Date getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Date ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public Date getNgayNghiViec() { return ngayNghiViec; }
    public void setNgayNghiViec(Date ngayNghiViec) { this.ngayNghiViec = ngayNghiViec; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public NhanVien getNguoiQuanLy() { return nguoiQuanLy; }
    public void setNguoiQuanLy(NhanVien nguoiQuanLy) { this.nguoiQuanLy = nguoiQuanLy; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public String toString() {
        return "NhanVien [maNhanVien=" + maNhanVien + ", hoTen=" + hoTen + ", chucVu=" + chucVu + "]";
    }
}
