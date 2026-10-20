package entity;

import java.util.Date;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private DanhMuc danhMuc;
    private String moTa;
    private String thuongHieu;
    private String xuatXu;
    private String donViTinh;
    private double giaNhap;
    private double giaBan;
    private int soLuongTon;
    private int soLuongToiThieu;
    private String maSKU;
    private String maVach;
    private double trongLuong;
    private String kichThuoc;
    private String hinhAnh;
    private String trangThai;
    private Date ngayTao;
    private Date ngayCapNhat;

    public SanPham() {
    }

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public SanPham(String maSanPham, String tenSanPham, DanhMuc danhMuc, String moTa,
                   String thuongHieu, String xuatXu, String donViTinh, double giaNhap,
                   double giaBan, int soLuongTon, int soLuongToiThieu, String maSKU,
                   String maVach, double trongLuong, String kichThuoc, String hinhAnh,
                   String trangThai, Date ngayTao, Date ngayCapNhat) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.danhMuc = danhMuc;
        this.moTa = moTa;
        this.thuongHieu = thuongHieu;
        this.xuatXu = xuatXu;
        this.donViTinh = donViTinh;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.soLuongToiThieu = soLuongToiThieu;
        this.maSKU = maSKU;
        this.maVach = maVach;
        this.trongLuong = trongLuong;
        this.kichThuoc = kichThuoc;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public DanhMuc getDanhMuc() { return danhMuc; }
    public void setDanhMuc(DanhMuc danhMuc) { this.danhMuc = danhMuc; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(String thuongHieu) { this.thuongHieu = thuongHieu; }

    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public int getSoLuongToiThieu() { return soLuongToiThieu; }
    public void setSoLuongToiThieu(int soLuongToiThieu) { this.soLuongToiThieu = soLuongToiThieu; }

    public String getMaSKU() { return maSKU; }
    public void setMaSKU(String maSKU) { this.maSKU = maSKU; }

    public String getMaVach() { return maVach; }
    public void setMaVach(String maVach) { this.maVach = maVach; }

    public double getTrongLuong() { return trongLuong; }
    public void setTrongLuong(double trongLuong) { this.trongLuong = trongLuong; }

    public String getKichThuoc() { return kichThuoc; }
    public void setKichThuoc(String kichThuoc) { this.kichThuoc = kichThuoc; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public String toString() {
        return tenSanPham;
    }
}
