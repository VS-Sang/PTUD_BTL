package entity;

import java.util.Date;

public class PhanQuyen {
    private TaiKhoan taiKhoan;
    private QuyenTruyCap quyen;
    private boolean coQuyenXem;
    private boolean coQuyenThem;
    private boolean coQuyenSua;
    private boolean coQuyenXoa;
    private Date ngayGan;
    private TaiKhoan nguoiGan;

    public PhanQuyen() {
    }

    public PhanQuyen(TaiKhoan taiKhoan, QuyenTruyCap quyen, boolean coQuyenXem, boolean coQuyenThem,
                     boolean coQuyenSua, boolean coQuyenXoa, Date ngayGan, TaiKhoan nguoiGan) {
        this.taiKhoan = taiKhoan;
        this.quyen = quyen;
        this.coQuyenXem = coQuyenXem;
        this.coQuyenThem = coQuyenThem;
        this.coQuyenSua = coQuyenSua;
        this.coQuyenXoa = coQuyenXoa;
        this.ngayGan = ngayGan;
        this.nguoiGan = nguoiGan;
    }

    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    public QuyenTruyCap getQuyen() { return quyen; }
    public void setQuyen(QuyenTruyCap quyen) { this.quyen = quyen; }

    public boolean isCoQuyenXem() { return coQuyenXem; }
    public void setCoQuyenXem(boolean coQuyenXem) { this.coQuyenXem = coQuyenXem; }

    public boolean isCoQuyenThem() { return coQuyenThem; }
    public void setCoQuyenThem(boolean coQuyenThem) { this.coQuyenThem = coQuyenThem; }

    public boolean isCoQuyenSua() { return coQuyenSua; }
    public void setCoQuyenSua(boolean coQuyenSua) { this.coQuyenSua = coQuyenSua; }

    public boolean isCoQuyenXoa() { return coQuyenXoa; }
    public void setCoQuyenXoa(boolean coQuyenXoa) { this.coQuyenXoa = coQuyenXoa; }

    public Date getNgayGan() { return ngayGan; }
    public void setNgayGan(Date ngayGan) { this.ngayGan = ngayGan; }

    public TaiKhoan getNguoiGan() { return nguoiGan; }
    public void setNguoiGan(TaiKhoan nguoiGan) { this.nguoiGan = nguoiGan; }
}
