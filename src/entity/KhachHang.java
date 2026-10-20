package entity;

import java.util.Date;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String gioiTinh;
    private Date ngaySinh;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String loaiKhachHang;
    private int diemThuong;
    private int tongDiemTichLuy;
    private double tongChiTieu;
    private int soLanMuaHang;
    private Date lanMuaGanNhat;
    private String trangThai;
    private String ghiChu;
    private Date ngayTao;
    private Date ngayCapNhat;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public KhachHang(String maKhachHang, String hoTen, String gioiTinh, Date ngaySinh,
                     String soDienThoai, String email, String diaChi, String loaiKhachHang,
                     int diemThuong, int tongDiemTichLuy, double tongChiTieu, int soLanMuaHang,
                     Date lanMuaGanNhat, String trangThai, String ghiChu,
                     Date ngayTao, Date ngayCapNhat) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.loaiKhachHang = loaiKhachHang;
        this.diemThuong = diemThuong;
        this.tongDiemTichLuy = tongDiemTichLuy;
        this.tongChiTieu = tongChiTieu;
        this.soLanMuaHang = soLanMuaHang;
        this.lanMuaGanNhat = lanMuaGanNhat;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getLoaiKhachHang() { return loaiKhachHang; }
    public void setLoaiKhachHang(String loaiKhachHang) { this.loaiKhachHang = loaiKhachHang; }

    public int getDiemThuong() { return diemThuong; }
    public void setDiemThuong(int diemThuong) { this.diemThuong = diemThuong; }

    public int getTongDiemTichLuy() { return tongDiemTichLuy; }
    public void setTongDiemTichLuy(int tongDiemTichLuy) { this.tongDiemTichLuy = tongDiemTichLuy; }

    public double getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(double tongChiTieu) { this.tongChiTieu = tongChiTieu; }

    public int getSoLanMuaHang() { return soLanMuaHang; }
    public void setSoLanMuaHang(int soLanMuaHang) { this.soLanMuaHang = soLanMuaHang; }

    public Date getLanMuaGanNhat() { return lanMuaGanNhat; }
    public void setLanMuaGanNhat(Date lanMuaGanNhat) { this.lanMuaGanNhat = lanMuaGanNhat; }

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
        return hoTen;
    }
}
