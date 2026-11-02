package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.DanhMuc;
import entity.SanPham;

public class SanPham_DAO {

    private SanPham docSanPhamTuResultSet(ResultSet rs) throws SQLException {
        DanhMuc dm = new DanhMuc(rs.getString("maDanhMuc"));
        try {
            dm.setTenDanhMuc(rs.getString("tenDanhMuc"));
        } catch (Exception ignored) {}

        return new SanPham(
                rs.getString("maSanPham"),
                rs.getString("tenSanPham"),
                dm,
                rs.getString("moTa"),
                rs.getString("thuongHieu"),
                rs.getString("xuatXu"),
                rs.getString("donViTinh"),
                rs.getDouble("giaNhap"),
                rs.getDouble("giaBan"),
                rs.getInt("soLuongTon"),
                rs.getInt("soLuongToiThieu"),
                rs.getString("maSKU"),
                rs.getString("maVach"),
                rs.getDouble("trongLuong"),
                rs.getString("kichThuoc"),
                rs.getString("hinhAnh"),
                rs.getString("trangThai"),
                rs.getTimestamp("ngayTao"),
                rs.getTimestamp("ngayCapNhat")
        );
    }

    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc "
                + "ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(docSanPhamTuResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<SanPham> timKiemSanPham(String tuKhoa) {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.*, dm.tenDanhMuc FROM SanPham sp "
                + "JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc "
                + "WHERE sp.maSanPham LIKE ? OR sp.tenSanPham LIKE ? OR sp.thuongHieu LIKE ? "
                + "ORDER BY sp.maSanPham";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(docSanPhamTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ArrayList<DanhMuc> getAllDanhMuc() throws SQLException {
        ArrayList<DanhMuc> danhMucList = new ArrayList<>();
        String sql = "SELECT maDanhMuc, tenDanhMuc FROM DanhMuc "
                + "WHERE trangThai = N'Hoạt động' ORDER BY tenDanhMuc";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DanhMuc danhMuc = new DanhMuc(rs.getString("maDanhMuc"));
                danhMuc.setTenDanhMuc(rs.getString("tenDanhMuc"));
                danhMucList.add(danhMuc);
            }
        }
        return danhMucList;
    }

    public void capNhatTrangThai(String maSanPham, String trangThai) throws SQLException {
        if (!"Đang kinh doanh".equals(trangThai)
                && !"Ngừng kinh doanh".equals(trangThai)
                && !"Hết hàng".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái sản phẩm không hợp lệ.");
        }

        String sql = "UPDATE SanPham SET trangThai = ?, ngayCapNhat = GETDATE() WHERE maSanPham = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            stmt.setString(2, maSanPham);
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Không tìm thấy sản phẩm " + maSanPham + ".");
            }
        }
    }

    public String themSanPham(SanPham sanPham) throws SQLException {
        if (sanPham == null || sanPham.getDanhMuc() == null) {
            throw new IllegalArgumentException("Thông tin sản phẩm không hợp lệ.");
        }

        String sqlMa = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maSanPham, 3, 18))), 0) + 1 "
                + "FROM SanPham WITH (TABLOCKX, HOLDLOCK)";
        String sqlInsert = "INSERT INTO SanPham "
                + "(maSanPham, tenSanPham, maDanhMuc, moTa, thuongHieu, donViTinh, "
                + "giaNhap, giaBan, soLuongTon, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                String maSanPham;
                try (PreparedStatement stmt = con.prepareStatement(sqlMa);
                     ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Không thể tạo mã sản phẩm.");
                    }
                    maSanPham = "SP" + String.format("%03d", rs.getInt(1));
                }

                try (PreparedStatement stmt = con.prepareStatement(sqlInsert)) {
                    stmt.setString(1, maSanPham);
                    stmt.setString(2, sanPham.getTenSanPham());
                    stmt.setString(3, sanPham.getDanhMuc().getMaDanhMuc());
                    stmt.setString(4, sanPham.getMoTa());
                    stmt.setString(5, sanPham.getThuongHieu());
                    stmt.setString(6, sanPham.getDonViTinh());
                    stmt.setDouble(7, sanPham.getGiaNhap());
                    stmt.setDouble(8, sanPham.getGiaBan());
                    stmt.setInt(9, sanPham.getSoLuongTon());
                    stmt.setString(10, sanPham.getTrangThai());
                    stmt.executeUpdate();
                }

                con.commit();
                return maSanPham;
            } catch (SQLException | RuntimeException exception) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }
}
