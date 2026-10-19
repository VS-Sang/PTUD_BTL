package entity;

import java.util.Date;

public class KhuyenMai_SanPham {
    private KhuyenMai khuyenMai;
    private SanPham sanPham;
    private double giaTriGiamRieng;
    private Date ngayTao;

    public KhuyenMai_SanPham() {
    }

    public KhuyenMai_SanPham(KhuyenMai khuyenMai, SanPham sanPham, double giaTriGiamRieng, Date ngayTao) {
        this.khuyenMai = khuyenMai;
        this.sanPham = sanPham;
        this.giaTriGiamRieng = giaTriGiamRieng;
        this.ngayTao = ngayTao;
    }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public double getGiaTriGiamRieng() { return giaTriGiamRieng; }
    public void setGiaTriGiamRieng(double giaTriGiamRieng) { this.giaTriGiamRieng = giaTriGiamRieng; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
}
