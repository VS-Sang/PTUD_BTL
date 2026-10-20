package entity;

import java.util.Date;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private Date gioVao;
    private Date gioRa;
    private String moTa;
    private String trangThai;
    private Date ngayTao;

    public CaLamViec() {
    }

    public CaLamViec(String maCa) {
        this.maCa = maCa;
    }

    public CaLamViec(String maCa, String tenCa, Date gioVao, Date gioRa, String moTa, String trangThai, Date ngayTao) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public Date getGioVao() { return gioVao; }
    public void setGioVao(Date gioVao) { this.gioVao = gioVao; }

    public Date getGioRa() { return gioRa; }
    public void setGioRa(Date gioRa) { this.gioRa = gioRa; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    @Override
    public String toString() {
        return tenCa;
    }
}
