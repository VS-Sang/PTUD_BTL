package entity;

import java.util.Date;

public class QuyenTruyCap {
    private String maQuyen;
    private String tenQuyen;
    private String moTa;
    private String nhomQuyen;
    private Date ngayTao;

    public QuyenTruyCap() {
    }

    public QuyenTruyCap(String maQuyen) {
        this.maQuyen = maQuyen;
    }

    public QuyenTruyCap(String maQuyen, String tenQuyen, String moTa, String nhomQuyen, Date ngayTao) {
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
        this.nhomQuyen = nhomQuyen;
        this.ngayTao = ngayTao;
    }

    public String getMaQuyen() { return maQuyen; }
    public void setMaQuyen(String maQuyen) { this.maQuyen = maQuyen; }

    public String getTenQuyen() { return tenQuyen; }
    public void setTenQuyen(String tenQuyen) { this.tenQuyen = tenQuyen; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getNhomQuyen() { return nhomQuyen; }
    public void setNhomQuyen(String nhomQuyen) { this.nhomQuyen = nhomQuyen; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
}
