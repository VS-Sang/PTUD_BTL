IF DB_ID('QuanLyBanHang') IS NOT NULL
    ALTER DATABASE QuanLyBanHang SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

IF DB_ID('QuanLyBanHang') IS NOT NULL
    DROP DATABASE QuanLyBanHang;
GO

CREATE DATABASE QuanLyBanHang;
GO

USE QuanLyBanHang;
GO

-- ============================================================
-- BẢNG 1: NhanVien (Employee)
-- Lưu trữ thông tin nhân viên và nhân viên quản lý
-- ============================================================
CREATE TABLE NhanVien (
                          maNhanVien      NVARCHAR(20)    NOT NULL,
                          hoTen           NVARCHAR(100)   NOT NULL,
                          gioiTinh        NVARCHAR(5)     NOT NULL CHECK (gioiTinh IN (N'Nam', N'Nữ')),
                          ngaySinh        DATE            NOT NULL,
                          soCMND          NVARCHAR(12)    NOT NULL UNIQUE,
                          soDienThoai     NVARCHAR(15)    NOT NULL UNIQUE,
                          email           NVARCHAR(100)   NULL UNIQUE,
                          diaChi          NVARCHAR(255)   NULL,
                          hinhAnh         NVARCHAR(255)   NULL,
                          chucVu          NVARCHAR(50)    NOT NULL DEFAULT N'Nhân viên',
    -- Ví dụ: 'Nhân viên', 'Quản lý', 'Thu ngân', 'Nhân viên kho'
                          phongBan        NVARCHAR(50)    NULL,
    -- Ví dụ: 'Bán hàng', 'Kho', 'Kế toán', 'Quản lý'
                          luongCoBan      DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (luongCoBan >= 0),
                          heSoLuong       FLOAT           NOT NULL DEFAULT 1.0 CHECK (heSoLuong > 0),
                          phuCap          DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (phuCap >= 0),
                          ngayVaoLam      DATE            NOT NULL DEFAULT GETDATE(),
                          ngayNghiViec    DATE            NULL,
                          trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Đang làm việc'
        CHECK (trangThai IN (N'Đang làm việc', N'Đã nghỉ việc', N'Tạm nghỉ')),
    -- Nhân viên quản lý trực tiếp (self-referencing FK)
                          maNguoiQuanLy   NVARCHAR(20)    NULL,
                          ghiChu          NVARCHAR(500)   NULL,
                          ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                          ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                          CONSTRAINT PK_NhanVien PRIMARY KEY (maNhanVien),
                          CONSTRAINT FK_NhanVien_QuanLy FOREIGN KEY (maNguoiQuanLy)
                              REFERENCES NhanVien(maNhanVien),
                          CONSTRAINT CK_NhanVien_NgaySinh CHECK (ngaySinh <= GETDATE()),
                          CONSTRAINT CK_NhanVien_NgayNghiViec CHECK (ngayNghiViec IS NULL OR ngayNghiViec >= ngayVaoLam)
);
GO

-- ============================================================
-- BẢNG 2: TaiKhoan (Account)
-- Lưu trữ thông tin đăng nhập hệ thống
-- Mỗi tài khoản gắn liền với một nhân viên
-- ============================================================
CREATE TABLE TaiKhoan (
                          maTaiKhoan      NVARCHAR(20)    NOT NULL,
                          tenDangNhap     NVARCHAR(50)    NOT NULL UNIQUE,
                          matKhau         NVARCHAR(255)   NOT NULL,
    -- Nên lưu dạng hash (SHA-256, BCrypt...)
                          vaiTro          NVARCHAR(30)    NOT NULL DEFAULT N'Nhân viên'
        CHECK (vaiTro IN (N'Admin', N'Quản lý', N'Nhân viên', N'Thu ngân', N'Nhân viên kho')),
                          maNhanVien      NVARCHAR(20)    NOT NULL UNIQUE,
    -- Mỗi nhân viên chỉ có 1 tài khoản
                          trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Hoạt động'
        CHECK (trangThai IN (N'Hoạt động', N'Bị khóa', N'Chưa kích hoạt')),
                          lanDangNhapCuoi DATETIME        NULL,
                          soLanDangNhapLoi INT            NOT NULL DEFAULT 0,
                          ngayDoiMatKhau  DATETIME        NULL,
                          ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                          ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                          CONSTRAINT PK_TaiKhoan PRIMARY KEY (maTaiKhoan),
                          CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien)
                              REFERENCES NhanVien(maNhanVien)
);
GO

-- ============================================================
-- BẢNG 3: QuyenTruyCap (Permission / Access Right)
-- Quản lý quyền truy cập theo từng chức năng của hệ thống
-- ============================================================
CREATE TABLE QuyenTruyCap (
                              maQuyen         NVARCHAR(20)    NOT NULL,
                              tenQuyen        NVARCHAR(100)   NOT NULL UNIQUE,
    -- Ví dụ: 'Quản lý sản phẩm', 'Xem báo cáo', 'Quản lý nhân viên'
                              moTa            NVARCHAR(255)   NULL,
                              nhomQuyen       NVARCHAR(50)    NOT NULL,
    -- Ví dụ: 'Sản phẩm', 'Đơn hàng', 'Nhân viên', 'Hệ thống', 'Báo cáo'
                              ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),

                              CONSTRAINT PK_QuyenTruyCap PRIMARY KEY (maQuyen)
);
GO

-- ============================================================
-- BẢNG 4: PhanQuyen (Role-Permission Assignment)
-- Gán quyền cho từng vai trò/tài khoản
-- ============================================================
CREATE TABLE PhanQuyen (
                           maTaiKhoan      NVARCHAR(20)    NOT NULL,
                           maQuyen         NVARCHAR(20)    NOT NULL,
                           coQuyenXem      BIT             NOT NULL DEFAULT 1,
                           coQuyenThem     BIT             NOT NULL DEFAULT 0,
                           coQuyenSua      BIT             NOT NULL DEFAULT 0,
                           coQuyenXoa      BIT             NOT NULL DEFAULT 0,
                           ngayGan         DATETIME        NOT NULL DEFAULT GETDATE(),
                           nguoiGan        NVARCHAR(20)    NULL,
    -- Tài khoản admin nào đã gán quyền này

                           CONSTRAINT PK_PhanQuyen PRIMARY KEY (maTaiKhoan, maQuyen),
                           CONSTRAINT FK_PhanQuyen_TaiKhoan FOREIGN KEY (maTaiKhoan)
                               REFERENCES TaiKhoan(maTaiKhoan),
                           CONSTRAINT FK_PhanQuyen_Quyen FOREIGN KEY (maQuyen)
                               REFERENCES QuyenTruyCap(maQuyen),
                           CONSTRAINT FK_PhanQuyen_NguoiGan FOREIGN KEY (nguoiGan)
                               REFERENCES TaiKhoan(maTaiKhoan)
);
GO

-- ============================================================
-- BẢNG 5: CaLamViec (Work Shift)
-- Định nghĩa các ca làm việc trong hệ thống
-- ============================================================
CREATE TABLE CaLamViec (
                           maCa            NVARCHAR(20)    NOT NULL,
                           tenCa           NVARCHAR(50)    NOT NULL,
    -- Ví dụ: 'Ca sáng', 'Ca chiều', 'Ca tối'
                           gioVao          TIME            NOT NULL,
                           gioRa           TIME            NOT NULL,
                           moTa            NVARCHAR(255)   NULL,
                           trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Hoạt động'
        CHECK (trangThai IN (N'Hoạt động', N'Ngừng sử dụng')),
                           ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),

                           CONSTRAINT PK_CaLamViec PRIMARY KEY (maCa)
);
GO

-- ============================================================
-- BẢNG 6: PhanCongCaLam (Shift Assignment)
-- Phân công nhân viên vào các ca làm việc
-- ============================================================
CREATE TABLE PhanCongCaLam (
                               maPhanCong      NVARCHAR(20)    NOT NULL,
                               maNhanVien      NVARCHAR(20)    NOT NULL,
                               maCa            NVARCHAR(20)    NOT NULL,
                               ngayLamViec     DATE            NOT NULL,
                               trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Đã lên lịch'
        CHECK (trangThai IN (N'Đã lên lịch', N'Đã điểm danh', N'Vắng mặt', N'Nghỉ phép', N'Đã hủy')),
                               gioDiemDanhVao  TIME            NULL,
                               gioDiemDanhRa   TIME            NULL,
                               soGioLamThem    FLOAT           NOT NULL DEFAULT 0 CHECK (soGioLamThem >= 0),
                               ghiChu          NVARCHAR(255)   NULL,
                               nguoiPhanCong   NVARCHAR(20)    NULL,
    -- Mã nhân viên quản lý đã phân công
                               ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                               ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                               CONSTRAINT PK_PhanCongCaLam PRIMARY KEY (maPhanCong),
                               CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (maNhanVien)
                                   REFERENCES NhanVien(maNhanVien),
                               CONSTRAINT FK_PhanCong_CaLam FOREIGN KEY (maCa)
                                   REFERENCES CaLamViec(maCa),
                               CONSTRAINT FK_PhanCong_NguoiPhanCong FOREIGN KEY (nguoiPhanCong)
                                   REFERENCES NhanVien(maNhanVien),
                               CONSTRAINT UQ_PhanCong_NV_Ca_Ngay UNIQUE (maNhanVien, maCa, ngayLamViec)
);
GO

-- ============================================================
-- BẢNG 7: DanhMuc (Category)
-- Phân loại và nhóm các sản phẩm
-- ============================================================
CREATE TABLE DanhMuc (
                         maDanhMuc       NVARCHAR(20)    NOT NULL,
                         tenDanhMuc      NVARCHAR(100)   NOT NULL UNIQUE,
                         moTa            NVARCHAR(255)   NULL,
                         hinhAnh         NVARCHAR(255)   NULL,
                         maDanhMucCha    NVARCHAR(20)    NULL,
    -- Cho phép cấu trúc danh mục đa cấp (self-referencing)
                         thuTuHienThi    INT             NOT NULL DEFAULT 0,
                         trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Hoạt động'
        CHECK (trangThai IN (N'Hoạt động', N'Ngừng hoạt động')),
                         ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                         ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                         CONSTRAINT PK_DanhMuc PRIMARY KEY (maDanhMuc),
                         CONSTRAINT FK_DanhMuc_DanhMucCha FOREIGN KEY (maDanhMucCha)
                             REFERENCES DanhMuc(maDanhMuc)
);
GO

-- ============================================================
-- BẢNG 8: SanPham (Product)
-- Lưu trữ thông tin chi tiết hàng hoá
-- ============================================================
CREATE TABLE SanPham (
                         maSanPham       NVARCHAR(20)    NOT NULL,
                         tenSanPham      NVARCHAR(200)   NOT NULL,
                         maDanhMuc       NVARCHAR(20)    NOT NULL,
                         moTa            NVARCHAR(500)   NULL,
                         thuongHieu      NVARCHAR(100)   NULL,
                         xuatXu          NVARCHAR(100)   NULL,
                         donViTinh       NVARCHAR(30)    NOT NULL DEFAULT N'Cái',
    -- Ví dụ: 'Cái', 'Kg', 'Lít', 'Hộp', 'Thùng'
                         giaNhap         DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (giaNhap >= 0),
                         giaBan          DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (giaBan >= 0),
                         soLuongTon      INT             NOT NULL DEFAULT 0 CHECK (soLuongTon >= 0),
                         soLuongToiThieu INT             NOT NULL DEFAULT 10 CHECK (soLuongToiThieu >= 0),
    -- Ngưỡng cảnh báo hết hàng
                         maSKU           NVARCHAR(50)    NULL UNIQUE,
    -- Stock Keeping Unit – mã quản lý kho
                         maVach          NVARCHAR(50)    NULL UNIQUE,
    -- Barcode
                         trongLuong      FLOAT           NULL CHECK (trongLuong IS NULL OR trongLuong >= 0),
                         kichThuoc       NVARCHAR(50)    NULL,
    -- Ví dụ: '10x20x5 cm'
                         hinhAnh         NVARCHAR(255)   NULL,
                         trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Đang kinh doanh'
        CHECK (trangThai IN (N'Đang kinh doanh', N'Ngừng kinh doanh', N'Hết hàng')),
                         ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                         ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                         CONSTRAINT PK_SanPham PRIMARY KEY (maSanPham),
                         CONSTRAINT FK_SanPham_DanhMuc FOREIGN KEY (maDanhMuc)
                             REFERENCES DanhMuc(maDanhMuc),
                         CONSTRAINT CK_SanPham_GiaBan CHECK (giaBan >= giaNhap)
);
GO

-- ============================================================
-- BẢNG 9: KhuyenMai (Promotion)
-- Chứa dữ liệu chương trình giảm giá, khuyến mãi
-- ============================================================
CREATE TABLE KhuyenMai (
                           maKhuyenMai     NVARCHAR(20)    NOT NULL,
                           tenKhuyenMai    NVARCHAR(200)   NOT NULL,
                           moTa            NVARCHAR(500)   NULL,
                           loaiKhuyenMai   NVARCHAR(30)    NOT NULL
        CHECK (loaiKhuyenMai IN (N'Phần trăm', N'Số tiền', N'Mua X tặng Y', N'Miễn phí vận chuyển')),
                           giaTriGiam      DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (giaTriGiam >= 0),
    -- Nếu loại = 'Phần trăm': giá trị 0-100
    -- Nếu loại = 'Số tiền': số tiền giảm
                           giaTriGiamToiDa DECIMAL(15, 2)  NULL CHECK (giaTriGiamToiDa IS NULL OR giaTriGiamToiDa >= 0),
    -- Số tiền giảm tối đa (áp dụng khi loại = 'Phần trăm')
                           donHangToiThieu DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (donHangToiThieu >= 0),
    -- Giá trị đơn hàng tối thiểu để áp dụng
                           soLuongToiDa    INT             NULL CHECK (soLuongToiDa IS NULL OR soLuongToiDa > 0),
    -- Số lượt sử dụng tối đa
                           soLuongDaSuDung INT             NOT NULL DEFAULT 0 CHECK (soLuongDaSuDung >= 0),
                           ngayBatDau      DATETIME        NOT NULL,
                           ngayKetThuc     DATETIME        NOT NULL,
                           apDungChoTatCa  BIT             NOT NULL DEFAULT 1,
    -- 1: áp dụng cho tất cả sản phẩm, 0: chỉ một số sản phẩm
                           trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Chưa bắt đầu'
        CHECK (trangThai IN (N'Chưa bắt đầu', N'Đang diễn ra', N'Đã kết thúc', N'Đã hủy')),
                           nguoiTao        NVARCHAR(20)    NULL,
                           ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                           ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                           CONSTRAINT PK_KhuyenMai PRIMARY KEY (maKhuyenMai),
                           CONSTRAINT FK_KhuyenMai_NguoiTao FOREIGN KEY (nguoiTao)
                               REFERENCES NhanVien(maNhanVien),
                           CONSTRAINT CK_KhuyenMai_Ngay CHECK (ngayKetThuc > ngayBatDau)
);
GO

-- ============================================================
-- BẢNG 10: KhuyenMai_SanPham (Promotion-Product)
-- Bảng trung gian: chương trình KM áp dụng cho sản phẩm nào
-- ============================================================
CREATE TABLE KhuyenMai_SanPham (
                                   maKhuyenMai     NVARCHAR(20)    NOT NULL,
                                   maSanPham       NVARCHAR(20)    NOT NULL,
                                   giaTriGiamRieng DECIMAL(15, 2)  NULL,
    -- Giá trị giảm riêng cho sản phẩm này (nếu khác mặc định)
                                   ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),

                                   CONSTRAINT PK_KhuyenMai_SanPham PRIMARY KEY (maKhuyenMai, maSanPham),
                                   CONSTRAINT FK_KMSP_KhuyenMai FOREIGN KEY (maKhuyenMai)
                                       REFERENCES KhuyenMai(maKhuyenMai),
                                   CONSTRAINT FK_KMSP_SanPham FOREIGN KEY (maSanPham)
                                       REFERENCES SanPham(maSanPham)
);
GO

-- ============================================================
-- BẢNG 11: KhachHang (Customer)
-- Lưu trữ thông tin khách hàng và điểm thưởng
-- ============================================================
CREATE TABLE KhachHang (
                           maKhachHang     NVARCHAR(20)    NOT NULL,
                           hoTen           NVARCHAR(100)   NOT NULL,
                           gioiTinh        NVARCHAR(5)     NULL CHECK (gioiTinh IS NULL OR gioiTinh IN (N'Nam', N'Nữ')),
                           ngaySinh        DATE            NULL,
                           soDienThoai     NVARCHAR(15)    NOT NULL UNIQUE,
                           email           NVARCHAR(100)   NULL,
                           diaChi          NVARCHAR(255)   NULL,
                           loaiKhachHang   NVARCHAR(30)    NOT NULL DEFAULT N'Thường'
        CHECK (loaiKhachHang IN (N'Thường', N'Thành viên', N'VIP', N'Vàng', N'Bạch kim')),
                           diemThuong      INT             NOT NULL DEFAULT 0 CHECK (diemThuong >= 0),
                           tongDiemTichLuy INT             NOT NULL DEFAULT 0 CHECK (tongDiemTichLuy >= 0),
    -- Tổng điểm tích lũy từ trước tới nay (không bị trừ khi sử dụng)
                           tongChiTieu     DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongChiTieu >= 0),
    -- Tổng tiền đã chi tiêu
                           soLanMuaHang    INT             NOT NULL DEFAULT 0 CHECK (soLanMuaHang >= 0),
                           lanMuaGanNhat   DATETIME        NULL,
                           trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Hoạt động'
        CHECK (trangThai IN (N'Hoạt động', N'Ngừng hoạt động')),
                           ghiChu          NVARCHAR(500)   NULL,
                           ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                           ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                           CONSTRAINT PK_KhachHang PRIMARY KEY (maKhachHang)
);
GO

-- ============================================================
-- BẢNG 12: DonHang (Order)
-- Đại diện cho các đơn hàng được tạo ra
-- ============================================================
CREATE TABLE DonHang (
                         maDonHang       NVARCHAR(20)    NOT NULL,
                         maKhachHang     NVARCHAR(20)    NULL,
    -- Cho phép NULL cho khách vãng lai
                         maNhanVien      NVARCHAR(20)    NOT NULL,
    -- Nhân viên bán hàng tạo đơn
                         ngayDatHang     DATETIME        NOT NULL DEFAULT GETDATE(),
                         ngayGiaoHang    DATETIME        NULL,
                         diaChiGiaoHang  NVARCHAR(255)   NULL,
                         tongTienHang    DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongTienHang >= 0),
    -- Tổng tiền hàng trước giảm giá
                         tienGiamGia     DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienGiamGia >= 0),
    -- Tiền được giảm từ khuyến mãi
                         maKhuyenMai     NVARCHAR(20)    NULL,
    -- Mã khuyến mãi đã áp dụng (nếu có)
                         diemSuDung      INT             NOT NULL DEFAULT 0 CHECK (diemSuDung >= 0),
    -- Số điểm thưởng khách hàng sử dụng
                         tienDiem        DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienDiem >= 0),
    -- Số tiền quy đổi từ điểm
                         phiVanChuyen    DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (phiVanChuyen >= 0),
                         thueVAT         DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (thueVAT >= 0),
                         tongThanhToan   DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongThanhToan >= 0),
    -- = tongTienHang - tienGiamGia - tienDiem + phiVanChuyen + thueVAT
                         trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Mới tạo'
        CHECK (trangThai IN (N'Mới tạo', N'Đang xử lý', N'Đã xác nhận', N'Đang giao hàng',
                             N'Đã giao hàng', N'Đã hoàn thành', N'Đã hủy', N'Trả hàng')),
                         ghiChu          NVARCHAR(500)   NULL,
                         ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                         ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                         CONSTRAINT PK_DonHang PRIMARY KEY (maDonHang),
                         CONSTRAINT FK_DonHang_KhachHang FOREIGN KEY (maKhachHang)
                             REFERENCES KhachHang(maKhachHang),
                         CONSTRAINT FK_DonHang_NhanVien FOREIGN KEY (maNhanVien)
                             REFERENCES NhanVien(maNhanVien),
                         CONSTRAINT FK_DonHang_KhuyenMai FOREIGN KEY (maKhuyenMai)
                             REFERENCES KhuyenMai(maKhuyenMai)
);
GO

-- ============================================================
-- BẢNG 13: ChiTietDonHang (Order Detail)
-- Chi tiết từng sản phẩm trong đơn hàng
-- ============================================================
CREATE TABLE ChiTietDonHang (
                                maDonHang       NVARCHAR(20)    NOT NULL,
                                maSanPham       NVARCHAR(20)    NOT NULL,
                                soLuong         INT             NOT NULL CHECK (soLuong > 0),
                                donGia          DECIMAL(15, 2)  NOT NULL CHECK (donGia >= 0),
    -- Giá tại thời điểm bán
                                giamGia         DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (giamGia >= 0),
    -- Giảm giá riêng cho sản phẩm này trong đơn
                                thanhTien       DECIMAL(18, 2)  NOT NULL CHECK (thanhTien >= 0),
    -- = soLuong * donGia - giamGia
                                ghiChu          NVARCHAR(255)   NULL,

                                CONSTRAINT PK_ChiTietDonHang PRIMARY KEY (maDonHang, maSanPham),
                                CONSTRAINT FK_CTDH_DonHang FOREIGN KEY (maDonHang)
                                    REFERENCES DonHang(maDonHang),
                                CONSTRAINT FK_CTDH_SanPham FOREIGN KEY (maSanPham)
                                    REFERENCES SanPham(maSanPham)
);
GO

-- ============================================================
-- BẢNG 14: HoaDon (Invoice)
-- Lưu trữ thông tin hoá đơn thanh toán
-- ============================================================
CREATE TABLE HoaDon (
                        maHoaDon        NVARCHAR(20)    NOT NULL,
                        maDonHang       NVARCHAR(20)    NOT NULL,
                        maKhachHang     NVARCHAR(20)    NULL,
                        maNhanVien      NVARCHAR(20)    NOT NULL,
    -- Nhân viên thu ngân lập hoá đơn
                        ngayLapHoaDon   DATETIME        NOT NULL DEFAULT GETDATE(),
                        tongTienHang    DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongTienHang >= 0),
                        tienGiamGia     DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienGiamGia >= 0),
                        tienDiem        DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienDiem >= 0),
                        thueVAT         DECIMAL(15, 2)  NOT NULL DEFAULT 0 CHECK (thueVAT >= 0),
                        tongThanhToan   DECIMAL(18, 2)  NOT NULL CHECK (tongThanhToan >= 0),
                        tienKhachDua    DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienKhachDua >= 0),
                        tienTraLai      DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tienTraLai >= 0),
                        phuongThucThanhToan NVARCHAR(30) NOT NULL DEFAULT N'Tiền mặt'
        CHECK (phuongThucThanhToan IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ tín dụng',
                                        N'Ví điện tử', N'Kết hợp')),
                        soTaiKhoanNH    NVARCHAR(30)    NULL,
    -- Số tài khoản ngân hàng (nếu thanh toán chuyển khoản)
                        tenNganHang     NVARCHAR(100)   NULL,
                        maGiaoDich      NVARCHAR(50)    NULL,
    -- Mã giao dịch từ ngân hàng/ví điện tử
                        diemCongThem    INT             NOT NULL DEFAULT 0 CHECK (diemCongThem >= 0),
    -- Điểm tích lũy được cộng cho khách hàng
                        trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Đã thanh toán'
        CHECK (trangThai IN (N'Đã thanh toán', N'Chờ thanh toán', N'Đã hủy', N'Hoàn tiền')),
                        ghiChu          NVARCHAR(500)   NULL,
                        ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                        ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                        CONSTRAINT PK_HoaDon PRIMARY KEY (maHoaDon),
                        CONSTRAINT FK_HoaDon_DonHang FOREIGN KEY (maDonHang)
                            REFERENCES DonHang(maDonHang),
                        CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKhachHang)
                            REFERENCES KhachHang(maKhachHang),
                        CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNhanVien)
                            REFERENCES NhanVien(maNhanVien)
);
GO

-- ============================================================
-- BẢNG 15: NhaCungCap (Supplier)
-- Thông tin nhà cung cấp cho phiếu nhập kho
-- ============================================================
CREATE TABLE NhaCungCap (
                            maNhaCungCap    NVARCHAR(20)    NOT NULL,
                            tenNhaCungCap   NVARCHAR(200)   NOT NULL,
                            soDienThoai     NVARCHAR(15)    NOT NULL,
                            email           NVARCHAR(100)   NULL,
                            diaChi          NVARCHAR(255)   NULL,
                            nguoiLienHe     NVARCHAR(100)   NULL,
                            maSoThue        NVARCHAR(20)    NULL,
                            soTaiKhoanNH    NVARCHAR(30)    NULL,
                            tenNganHang     NVARCHAR(100)   NULL,
                            congNo          DECIMAL(18, 2)  NOT NULL DEFAULT 0,
    -- Tổng công nợ hiện tại
                            trangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Hoạt động'
        CHECK (trangThai IN (N'Hoạt động', N'Ngừng hợp tác')),
                            ghiChu          NVARCHAR(500)   NULL,
                            ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                            ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                            CONSTRAINT PK_NhaCungCap PRIMARY KEY (maNhaCungCap)
);
GO

-- ============================================================
-- BẢNG 16: PhieuNhapKho (Import Receipt)
-- Lưu trữ dữ liệu về các lần nhập hàng vào kho
-- ============================================================
CREATE TABLE PhieuNhapKho (
                              maPhieuNhap     NVARCHAR(20)    NOT NULL,
                              maNhaCungCap    NVARCHAR(20)    NOT NULL,
                              maNhanVien      NVARCHAR(20)    NOT NULL,
    -- Nhân viên kho thực hiện nhập
                              ngayNhap        DATETIME        NOT NULL DEFAULT GETDATE(),
                              tongSoLuong     INT             NOT NULL DEFAULT 0 CHECK (tongSoLuong >= 0),
                              tongTienNhap    DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongTienNhap >= 0),
                              trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Mới tạo'
        CHECK (trangThai IN (N'Mới tạo', N'Đang nhập', N'Đã nhập', N'Đã hủy')),
                              ghiChu          NVARCHAR(500)   NULL,
                              ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                              ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                              CONSTRAINT PK_PhieuNhapKho PRIMARY KEY (maPhieuNhap),
                              CONSTRAINT FK_PNK_NhaCungCap FOREIGN KEY (maNhaCungCap)
                                  REFERENCES NhaCungCap(maNhaCungCap),
                              CONSTRAINT FK_PNK_NhanVien FOREIGN KEY (maNhanVien)
                                  REFERENCES NhanVien(maNhanVien)
);
GO

-- ============================================================
-- BẢNG 17: ChiTietPhieuNhap (Import Receipt Detail)
-- Chi tiết từng sản phẩm trong phiếu nhập kho
-- ============================================================
CREATE TABLE ChiTietPhieuNhap (
                                  maPhieuNhap     NVARCHAR(20)    NOT NULL,
                                  maSanPham       NVARCHAR(20)    NOT NULL,
                                  soLuongNhap     INT             NOT NULL CHECK (soLuongNhap > 0),
                                  donGiaNhap      DECIMAL(15, 2)  NOT NULL CHECK (donGiaNhap >= 0),
                                  thanhTien       DECIMAL(18, 2)  NOT NULL CHECK (thanhTien >= 0),
    -- = soLuongNhap * donGiaNhap
                                  ngaySanXuat     DATE            NULL,
                                  hanSuDung       DATE            NULL,
                                  ghiChu          NVARCHAR(255)   NULL,

                                  CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (maPhieuNhap, maSanPham),
                                  CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (maPhieuNhap)
                                      REFERENCES PhieuNhapKho(maPhieuNhap),
                                  CONSTRAINT FK_CTPN_SanPham FOREIGN KEY (maSanPham)
                                      REFERENCES SanPham(maSanPham),
                                  CONSTRAINT CK_CTPN_HanSuDung CHECK (hanSuDung IS NULL OR ngaySanXuat IS NULL OR hanSuDung > ngaySanXuat)
);
GO

-- ============================================================
-- BẢNG 18: PhieuXuatKho (Export Receipt)
-- Lưu trữ dữ liệu về các lần xuất hàng khỏi kho
-- ============================================================
CREATE TABLE PhieuXuatKho (
                              maPhieuXuat     NVARCHAR(20)    NOT NULL,
                              maNhanVien      NVARCHAR(20)    NOT NULL,
    -- Nhân viên kho thực hiện xuất
                              maDonHang       NVARCHAR(20)    NULL,
    -- Liên kết với đơn hàng (nếu xuất cho bán hàng)
                              lyDoXuat        NVARCHAR(50)    NOT NULL DEFAULT N'Bán hàng'
        CHECK (lyDoXuat IN (N'Bán hàng', N'Trả hàng NCC', N'Hàng hỏng', N'Điều chỉnh', N'Khác')),
                              ngayXuat        DATETIME        NOT NULL DEFAULT GETDATE(),
                              tongSoLuong     INT             NOT NULL DEFAULT 0 CHECK (tongSoLuong >= 0),
                              tongTienXuat    DECIMAL(18, 2)  NOT NULL DEFAULT 0 CHECK (tongTienXuat >= 0),
                              trangThai       NVARCHAR(30)    NOT NULL DEFAULT N'Mới tạo'
        CHECK (trangThai IN (N'Mới tạo', N'Đang xuất', N'Đã xuất', N'Đã hủy')),
                              ghiChu          NVARCHAR(500)   NULL,
                              ngayTao         DATETIME        NOT NULL DEFAULT GETDATE(),
                              ngayCapNhat     DATETIME        NOT NULL DEFAULT GETDATE(),

                              CONSTRAINT PK_PhieuXuatKho PRIMARY KEY (maPhieuXuat),
                              CONSTRAINT FK_PXK_NhanVien FOREIGN KEY (maNhanVien)
                                  REFERENCES NhanVien(maNhanVien),
                              CONSTRAINT FK_PXK_DonHang FOREIGN KEY (maDonHang)
                                  REFERENCES DonHang(maDonHang)
);
GO

-- ============================================================
-- BẢNG 19: ChiTietPhieuXuat (Export Receipt Detail)
-- Chi tiết từng sản phẩm trong phiếu xuất kho
-- ============================================================
CREATE TABLE ChiTietPhieuXuat (
                                  maPhieuXuat     NVARCHAR(20)    NOT NULL,
                                  maSanPham       NVARCHAR(20)    NOT NULL,
                                  soLuongXuat     INT             NOT NULL CHECK (soLuongXuat > 0),
                                  donGiaXuat      DECIMAL(15, 2)  NOT NULL CHECK (donGiaXuat >= 0),
                                  thanhTien       DECIMAL(18, 2)  NOT NULL CHECK (thanhTien >= 0),
    -- = soLuongXuat * donGiaXuat
                                  ghiChu          NVARCHAR(255)   NULL,

                                  CONSTRAINT PK_ChiTietPhieuXuat PRIMARY KEY (maPhieuXuat, maSanPham),
                                  CONSTRAINT FK_CTPX_PhieuXuat FOREIGN KEY (maPhieuXuat)
                                      REFERENCES PhieuXuatKho(maPhieuXuat),
                                  CONSTRAINT FK_CTPX_SanPham FOREIGN KEY (maSanPham)
                                      REFERENCES SanPham(maSanPham)
);
GO

-- ============================================================
-- BẢNG 20: PhieuTraHang (Return Receipt)
-- Lưu phiếu xử lý trả hàng/đổi hàng từ hóa đơn gốc
-- ============================================================
CREATE TABLE PhieuTraHang (
                             maPhieuTra      INT IDENTITY(1,1) NOT NULL,
                             maHoaDon        NVARCHAR(20)    NOT NULL,
                             maNhanVien      NVARCHAR(20)    NOT NULL,
                             ngayTra         DATETIME        NOT NULL DEFAULT GETDATE(),
                             loaiXuLy        NVARCHAR(30)    NOT NULL
        CHECK (loaiXuLy IN (N'Trả hàng & hoàn tiền', N'Đổi hàng')),
                             tongGiaTriHangTra DECIMAL(18, 2) NOT NULL DEFAULT 0
        CHECK (tongGiaTriHangTra >= 0),
                             tongGiaTriHangDoi DECIMAL(18, 2) NOT NULL DEFAULT 0
        CHECK (tongGiaTriHangDoi >= 0),
                             tienHoan        DECIMAL(18, 2)  NOT NULL DEFAULT 0
        CHECK (tienHoan >= 0),
                             chenhLechThuThem DECIMAL(18, 2) NOT NULL DEFAULT 0,
                             ghiChu          NVARCHAR(500)   NULL,
                             CONSTRAINT PK_PhieuTraHang PRIMARY KEY (maPhieuTra),
                             CONSTRAINT FK_PhieuTraHang_HoaDon FOREIGN KEY (maHoaDon)
                                 REFERENCES HoaDon(maHoaDon),
                             CONSTRAINT FK_PhieuTraHang_NhanVien FOREIGN KEY (maNhanVien)
                                 REFERENCES NhanVien(maNhanVien)
);
GO

-- ============================================================
-- BẢNG 21: ChiTietPhieuTraHang (Return Receipt Detail)
-- ============================================================
CREATE TABLE ChiTietPhieuTraHang (
                                     maPhieuTra      INT             NOT NULL,
                                     maSanPham       NVARCHAR(20)    NOT NULL,
                                     soLuongTra      INT             NOT NULL CHECK (soLuongTra > 0),
                                     giaTriHoanMoiDonVi DECIMAL(18, 4) NOT NULL DEFAULT 0
        CHECK (giaTriHoanMoiDonVi >= 0),
                                     thanhTien       DECIMAL(18, 2)  NOT NULL DEFAULT 0
        CHECK (thanhTien >= 0),
                                     CONSTRAINT PK_ChiTietPhieuTraHang PRIMARY KEY (maPhieuTra, maSanPham),
                                     CONSTRAINT FK_CTPTH_PhieuTraHang FOREIGN KEY (maPhieuTra)
                                         REFERENCES PhieuTraHang(maPhieuTra),
                                     CONSTRAINT FK_CTPTH_SanPham FOREIGN KEY (maSanPham)
                                         REFERENCES SanPham(maSanPham)
);
GO

-- ============================================================
-- BẢNG 22: ChiTietDoiHang (Exchange Receipt Detail)
-- ============================================================
CREATE TABLE ChiTietDoiHang (
                                maPhieuTra      INT             NOT NULL,
                                maSanPham       NVARCHAR(20)    NOT NULL,
                                soLuongDoi      INT             NOT NULL CHECK (soLuongDoi > 0),
                                donGiaDoi       DECIMAL(15, 2)  NOT NULL CHECK (donGiaDoi >= 0),
                                thanhTien       DECIMAL(18, 2)  NOT NULL CHECK (thanhTien >= 0),
                                CONSTRAINT PK_ChiTietDoiHang PRIMARY KEY (maPhieuTra, maSanPham),
                                CONSTRAINT FK_CTDOI_PhieuTraHang FOREIGN KEY (maPhieuTra)
                                    REFERENCES PhieuTraHang(maPhieuTra),
                                CONSTRAINT FK_CTDOI_SanPham FOREIGN KEY (maSanPham)
                                    REFERENCES SanPham(maSanPham)
);
GO

-- ============================================================
-- BẢNG 23: LichSuDiemThuong (Loyalty Point History)
-- Lịch sử tích lũy và sử dụng điểm thưởng
-- ============================================================
CREATE TABLE LichSuDiemThuong (
                                  maLichSu        NVARCHAR(20)    NOT NULL,
                                  maKhachHang     NVARCHAR(20)    NOT NULL,
                                  maDonHang       NVARCHAR(20)    NULL,
                                  maHoaDon        NVARCHAR(20)    NULL,
                                  loaiGiaoDich    NVARCHAR(20)    NOT NULL
        CHECK (loaiGiaoDich IN (N'Tích điểm', N'Sử dụng', N'Hoàn điểm', N'Điều chỉnh')),
                                  soDiem          INT             NOT NULL,
    -- Dương: tích điểm, Âm: sử dụng
                                  diemTruocGD     INT             NOT NULL CHECK (diemTruocGD >= 0),
                                  diemSauGD       INT             NOT NULL CHECK (diemSauGD >= 0),
                                  moTa            NVARCHAR(255)   NULL,
                                  ngayGiaoDich    DATETIME        NOT NULL DEFAULT GETDATE(),

                                  CONSTRAINT PK_LichSuDiemThuong PRIMARY KEY (maLichSu),
                                  CONSTRAINT FK_LSDT_KhachHang FOREIGN KEY (maKhachHang)
                                      REFERENCES KhachHang(maKhachHang),
                                  CONSTRAINT FK_LSDT_DonHang FOREIGN KEY (maDonHang)
                                      REFERENCES DonHang(maDonHang),
                                  CONSTRAINT FK_LSDT_HoaDon FOREIGN KEY (maHoaDon)
                                      REFERENCES HoaDon(maHoaDon)
);
GO

-- ============================================================
-- BẢNG 24: LichSuDangNhap (Login History)
-- Ghi lại lịch sử đăng nhập hệ thống
-- ============================================================
CREATE TABLE LichSuDangNhap (
                                maLichSu        INT IDENTITY(1,1) NOT NULL,
                                maTaiKhoan      NVARCHAR(20)    NOT NULL,
                                thoiGianDangNhap DATETIME       NOT NULL DEFAULT GETDATE(),
                                thoiGianDangXuat DATETIME       NULL,
                                diaChiIP        NVARCHAR(50)    NULL,
                                tenMay          NVARCHAR(100)   NULL,
                                ketQua          NVARCHAR(20)    NOT NULL
        CHECK (ketQua IN (N'Thành công', N'Thất bại')),
                                lyDoThatBai     NVARCHAR(255)   NULL,

                                CONSTRAINT PK_LichSuDangNhap PRIMARY KEY (maLichSu),
                                CONSTRAINT FK_LSDN_TaiKhoan FOREIGN KEY (maTaiKhoan)
                                    REFERENCES TaiKhoan(maTaiKhoan)
);
GO


-- ============================================================
-- TẠO INDEXES để tối ưu hiệu suất truy vấn
-- ============================================================

-- NhanVien
CREATE INDEX IX_NhanVien_HoTen ON NhanVien(hoTen);
CREATE INDEX IX_NhanVien_ChucVu ON NhanVien(chucVu);
CREATE INDEX IX_NhanVien_TrangThai ON NhanVien(trangThai);
CREATE INDEX IX_NhanVien_PhongBan ON NhanVien(phongBan);

-- TaiKhoan
CREATE INDEX IX_TaiKhoan_VaiTro ON TaiKhoan(vaiTro);
CREATE INDEX IX_TaiKhoan_TrangThai ON TaiKhoan(trangThai);

-- SanPham
CREATE INDEX IX_SanPham_TenSanPham ON SanPham(tenSanPham);
CREATE INDEX IX_SanPham_MaDanhMuc ON SanPham(maDanhMuc);
CREATE INDEX IX_SanPham_TrangThai ON SanPham(trangThai);
CREATE INDEX IX_SanPham_GiaBan ON SanPham(giaBan);
CREATE INDEX IX_SanPham_ThuongHieu ON SanPham(thuongHieu);

-- KhachHang
CREATE INDEX IX_KhachHang_HoTen ON KhachHang(hoTen);
CREATE INDEX IX_KhachHang_LoaiKH ON KhachHang(loaiKhachHang);
CREATE INDEX IX_KhachHang_DiemThuong ON KhachHang(diemThuong);

-- DonHang
CREATE INDEX IX_DonHang_MaKhachHang ON DonHang(maKhachHang);
CREATE INDEX IX_DonHang_MaNhanVien ON DonHang(maNhanVien);
CREATE INDEX IX_DonHang_NgayDatHang ON DonHang(ngayDatHang);
CREATE INDEX IX_DonHang_TrangThai ON DonHang(trangThai);

-- HoaDon
CREATE INDEX IX_HoaDon_MaDonHang ON HoaDon(maDonHang);
CREATE INDEX IX_HoaDon_NgayLap ON HoaDon(ngayLapHoaDon);
CREATE INDEX IX_HoaDon_TrangThai ON HoaDon(trangThai);

-- PhieuNhapKho
CREATE INDEX IX_PNK_MaNCC ON PhieuNhapKho(maNhaCungCap);
CREATE INDEX IX_PNK_NgayNhap ON PhieuNhapKho(ngayNhap);

-- PhieuXuatKho
CREATE INDEX IX_PXK_NgayXuat ON PhieuXuatKho(ngayXuat);
CREATE INDEX IX_PXK_MaDonHang ON PhieuXuatKho(maDonHang);

-- PhanCongCaLam
CREATE INDEX IX_PhanCong_NgayLamViec ON PhanCongCaLam(ngayLamViec);
CREATE INDEX IX_PhanCong_MaNhanVien ON PhanCongCaLam(maNhanVien);

-- KhuyenMai
CREATE INDEX IX_KhuyenMai_TrangThai ON KhuyenMai(trangThai);
CREATE INDEX IX_KhuyenMai_NgayBatDau ON KhuyenMai(ngayBatDau);
CREATE INDEX IX_KhuyenMai_NgayKetThuc ON KhuyenMai(ngayKetThuc);
GO


-- ============================================================
-- TRIGGER: Tự động cập nhật ngayCapNhat khi sửa bản ghi
-- ============================================================

CREATE TRIGGER trg_NhanVien_Update ON NhanVien
    AFTER UPDATE AS
BEGIN
UPDATE NhanVien SET ngayCapNhat = GETDATE()
WHERE maNhanVien IN (SELECT maNhanVien FROM inserted);
END;
GO

CREATE TRIGGER trg_SanPham_Update ON SanPham
    AFTER UPDATE AS
BEGIN
UPDATE SanPham SET ngayCapNhat = GETDATE()
WHERE maSanPham IN (SELECT maSanPham FROM inserted);
END;
GO

CREATE TRIGGER trg_KhachHang_Update ON KhachHang
    AFTER UPDATE AS
BEGIN
UPDATE KhachHang SET ngayCapNhat = GETDATE()
WHERE maKhachHang IN (SELECT maKhachHang FROM inserted);
END;
GO

CREATE TRIGGER trg_DonHang_Update ON DonHang
    AFTER UPDATE AS
BEGIN
UPDATE DonHang SET ngayCapNhat = GETDATE()
WHERE maDonHang IN (SELECT maDonHang FROM inserted);
END;
GO

CREATE TRIGGER trg_HoaDon_Update ON HoaDon
    AFTER UPDATE AS
BEGIN
UPDATE HoaDon SET ngayCapNhat = GETDATE()
WHERE maHoaDon IN (SELECT maHoaDon FROM inserted);
END;
GO


-- ============================================================
-- TRIGGER: Cập nhật tồn kho khi nhập/xuất kho
-- ============================================================

-- Khi thêm chi tiết phiếu nhập → tăng tồn kho
CREATE TRIGGER trg_ChiTietPhieuNhap_Insert ON ChiTietPhieuNhap
    AFTER INSERT AS
BEGIN
UPDATE sp
SET sp.soLuongTon = sp.soLuongTon + i.soLuongNhap
    FROM SanPham sp
    INNER JOIN inserted i ON sp.maSanPham = i.maSanPham;
END;
GO

-- Khi thêm chi tiết phiếu xuất → giảm tồn kho
CREATE TRIGGER trg_ChiTietPhieuXuat_Insert ON ChiTietPhieuXuat
    AFTER INSERT AS
BEGIN
    -- Kiểm tra tồn kho đủ không
    IF EXISTS (
        SELECT 1
        FROM inserted i
        INNER JOIN SanPham sp ON sp.maSanPham = i.maSanPham
        WHERE sp.soLuongTon < i.soLuongXuat
    )
BEGIN
        RAISERROR(N'Số lượng tồn kho không đủ để xuất!', 16, 1);
ROLLBACK TRANSACTION;
RETURN;
END

UPDATE sp
SET sp.soLuongTon = sp.soLuongTon - i.soLuongXuat
    FROM SanPham sp
    INNER JOIN inserted i ON sp.maSanPham = i.maSanPham;
END;
GO


-- ============================================================
-- DỮ LIỆU MẪU (Sample Data cho đầy đủ 21 bảng)
-- ============================================================

-- 1. Nhân viên (BẢNG 1)
INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, ngaySinh, soCMND, soDienThoai, email, diaChi, chucVu, phongBan, luongCoBan, heSoLuong, phuCap, ngayVaoLam, ngayNghiViec, trangThai, ghiChu)
VALUES
    (N'NV001', N'Nguyễn Văn An',     N'Nam', '1990-05-15', '079190001234', '0901234567', 'an.nv@company.com',     N'123 Lê Lợi, Q.1, TP.HCM',        N'Quản lý',       N'Quản lý',  15000000, 2.0, 3000000, '2020-01-15', NULL,         N'Đang làm việc', N'Quản lý cửa hàng trưởng'),
    (N'NV002', N'Trần Thị Bình',     N'Nữ',  '1995-08-20', '079195002345', '0912345678', 'binh.tt@company.com',   N'456 Nguyễn Huệ, Q.1, TP.HCM',    N'Thu ngân',      N'Bán hàng', 8000000,  1.2, 1000000, '2021-03-10', NULL,         N'Đang làm việc', N'Thu ngân ca sáng'),
    (N'NV003', N'Phạm Văn Cường',    N'Nam', '1993-12-01', '079193003456', '0923456789', 'cuong.pv@company.com',  N'789 CMT8, Q.10, TP.HCM',          N'Nhân viên kho', N'Kho',      7500000,  1.0, 500000,  '2021-06-01', NULL,         N'Đang làm việc', N'Phụ trách nhập xuất kho'),
    (N'NV004', N'Lê Thị Dung',       N'Nữ',  '1998-03-25', '079198004567', '0934567890', 'dung.lt@company.com',   N'101 Hai Bà Trưng, Q.3, TP.HCM',   N'Nhân viên',     N'Bán hàng', 7000000,  1.0, 500000,  '2022-01-05', NULL,         N'Tạm nghỉ',      N'Nghỉ phép cá nhân'),
    (N'NV005', N'Hoàng Minh Đức',    N'Nam', '1994-07-10', '079194005678', '0945678901', 'duc.hm@company.com',    N'202 Điện Biên Phủ, Bình Thạnh',   N'Quản lý',       N'Kế toán',  12000000, 1.5, 2000000, '2021-02-15', NULL,         N'Đang làm việc', N'Phụ trách kế toán - tài chính'),
    (N'NV006', N'Ngô Thị Phượng',    N'Nữ',  '1996-11-30', '079196006789', '0956789012', 'phuong.nt@company.com', N'303 Võ Văn Tần, Q.3, TP.HCM',     N'Thu ngân',      N'Bán hàng', 8000000,  1.2, 1000000, '2021-09-20', NULL,         N'Đang làm việc', N'Thu ngân ca chiều'),
    (N'NV007', N'Đặng Quốc Giang',   N'Nam', '1991-02-14', '079191007890', '0967890123', 'giang.dq@company.com',  N'404 Cách Mạng T8, Q.Tân Bình',    N'Nhân viên kho', N'Kho',      7500000,  1.0, 500000,  '2020-11-01', NULL,         N'Đang làm việc', N'Kiểm kê hàng hóa'),
    (N'NV008', N'Bùi Thị Hạnh',      N'Nữ',  '1999-06-18', '079199008901', '0978901234', 'hanh.bt@company.com',   N'505 Nguyễn Trãi, Q.5, TP.HCM',    N'Nhân viên',     N'Bán hàng', 7000000,  1.0, 500000,  '2023-02-01', NULL,         N'Đang làm việc', N'Tư vấn viên điện thoại'),
    (N'NV009', N'Vũ Hoàng Khôi',     N'Nam', '1997-04-22', '079197009012', '0989012345', 'khoi.vh@company.com',   N'606 Phan Văn Trị, Q.Gò Vấp',      N'Nhân viên',     N'Bán hàng', 7000000,  1.1, 600000,  '2022-08-10', NULL,         N'Đang làm việc', N'Tư vấn viên laptop'),
    (N'NV010', N'Đinh Thị Lan',      N'Nữ',  '1995-09-09', '079195010123', '0990123456', 'lan.dt@company.com',    N'707 Hoàng Văn Thụ, Q.Phú Nhuận',  N'Nhân viên',     N'Kế toán',  8500000,  1.2, 800000,  '2021-05-12', '2024-12-31', N'Đã nghỉ việc',  N'Đã bàn giao công việc');
GO

-- Gán người quản lý trực tiếp
UPDATE NhanVien SET maNguoiQuanLy = N'NV001' WHERE maNhanVien IN (N'NV002', N'NV003', N'NV004', N'NV005', N'NV006', N'NV007', N'NV008', N'NV009', N'NV010');
GO

-- 2. Tài khoản (BẢNG 2)
INSERT INTO TaiKhoan (maTaiKhoan, tenDangNhap, matKhau, vaiTro, maNhanVien, trangThai, lanDangNhapCuoi, soLanDangNhapLoi)
VALUES
    (N'TK001', N'admin',       N'21232f297a57a5a743894a0e4a801fc3', N'Admin',         N'NV001', N'Hoạt động',      '2026-10-19 08:00:00', 0), -- admin (MD5)
    (N'TK002', N'thungan01',   N'e10adc3949ba59abbe56e057f20f883e', N'Thu ngân',       N'NV002', N'Hoạt động',      '2026-10-19 07:55:00', 0),
    (N'TK003', N'khonv01',     N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên kho',  N'NV003', N'Hoạt động',      '2026-10-18 17:30:00', 0),
    (N'TK004', N'nhanvien01',  N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên',      N'NV004', N'Hoạt động',      '2026-10-15 14:20:00', 0),
    (N'TK005', N'quanly02',    N'e10adc3949ba59abbe56e057f20f883e', N'Quản lý',        N'NV005', N'Hoạt động',      '2026-10-19 08:10:00', 0),
    (N'TK006', N'thungan02',   N'e10adc3949ba59abbe56e057f20f883e', N'Thu ngân',       N'NV006', N'Hoạt động',      '2026-10-18 21:45:00', 0),
    (N'TK007', N'khonv02',     N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên kho',  N'NV007', N'Hoạt động',      '2026-10-18 16:00:00', 0),
    (N'TK008', N'nhanvien02',  N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên',      N'NV008', N'Hoạt động',      '2026-10-19 08:05:00', 0),
    (N'TK009', N'nhanvien03',  N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên',      N'NV009', N'Chưa kích hoạt', NULL,                  0),
    (N'TK010', N'ketoan_cu',   N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên',      N'NV010', N'Bị khóa',        '2024-12-30 17:00:00', 5);
GO

-- 3. Quyền truy cập (BẢNG 3)
INSERT INTO QuyenTruyCap (maQuyen, tenQuyen, moTa, nhomQuyen)
VALUES
    (N'Q001', N'Quản lý sản phẩm',    N'Thêm, sửa, xóa, xem sản phẩm',          N'Sản phẩm'),
    (N'Q002', N'Quản lý danh mục',     N'Thêm, sửa, xóa, xem danh mục',          N'Sản phẩm'),
    (N'Q003', N'Quản lý khuyến mãi',   N'Tạo và quản lý chương trình khuyến mãi', N'Sản phẩm'),
    (N'Q004', N'Quản lý kho',          N'Nhập xuất kho, kiểm kê',                 N'Kho'),
    (N'Q005', N'Quản lý khách hàng',   N'Thêm, sửa, xóa, xem khách hàng',        N'Khách hàng'),
    (N'Q006', N'Quản lý đơn hàng',     N'Tạo và quản lý đơn hàng',                N'Đơn hàng'),
    (N'Q007', N'Quản lý hóa đơn',      N'Lập và quản lý hóa đơn',                 N'Đơn hàng'),
    (N'Q008', N'Quản lý nhân viên',    N'Thêm, sửa, xóa, xem nhân viên',         N'Nhân viên'),
    (N'Q009', N'Quản lý ca làm',       N'Tạo ca và phân công nhân viên',          N'Nhân viên'),
    (N'Q010', N'Quản lý tài khoản',    N'Tạo, khóa, phân quyền tài khoản',       N'Hệ thống'),
    (N'Q011', N'Xem báo cáo',          N'Xem các báo cáo thống kê',              N'Báo cáo'),
    (N'Q012', N'Quản lý NCC',          N'Thêm, sửa, xóa nhà cung cấp',          N'Kho');
GO

-- 4. Phân quyền (BẢNG 4)
-- Phân quyền cho Admin (toàn quyền)
INSERT INTO PhanQuyen (maTaiKhoan, maQuyen, coQuyenXem, coQuyenThem, coQuyenSua, coQuyenXoa, nguoiGan)
SELECT N'TK001', maQuyen, 1, 1, 1, 1, N'TK001' FROM QuyenTruyCap;
GO

-- Phân quyền cho Quản lý kế toán (TK005)
INSERT INTO PhanQuyen (maTaiKhoan, maQuyen, coQuyenXem, coQuyenThem, coQuyenSua, coQuyenXoa, nguoiGan)
VALUES
    (N'TK005', N'Q006', 1, 0, 0, 0, N'TK001'),
    (N'TK005', N'Q007', 1, 1, 1, 0, N'TK001'),
    (N'TK005', N'Q008', 1, 0, 0, 0, N'TK001'),
    (N'TK005', N'Q009', 1, 1, 1, 0, N'TK001'),
    (N'TK005', N'Q011', 1, 1, 0, 0, N'TK001');
GO

-- Phân quyền cho Thu ngân (TK002, TK006)
INSERT INTO PhanQuyen (maTaiKhoan, maQuyen, coQuyenXem, coQuyenThem, coQuyenSua, coQuyenXoa, nguoiGan)
VALUES
    (N'TK002', N'Q005', 1, 1, 1, 0, N'TK001'),  -- Khách hàng: xem, thêm, sửa
    (N'TK002', N'Q006', 1, 1, 1, 0, N'TK001'),  -- Đơn hàng: xem, thêm, sửa
    (N'TK002', N'Q007', 1, 1, 0, 0, N'TK001'),  -- Hóa đơn: xem, thêm
    (N'TK002', N'Q001', 1, 0, 0, 0, N'TK001'),  -- Sản phẩm: chỉ xem
    (N'TK006', N'Q005', 1, 1, 1, 0, N'TK001'),
    (N'TK006', N'Q006', 1, 1, 1, 0, N'TK001'),
    (N'TK006', N'Q007', 1, 1, 0, 0, N'TK001'),
    (N'TK006', N'Q001', 1, 0, 0, 0, N'TK001');
GO

-- Phân quyền cho Nhân viên kho (TK003, TK007)
INSERT INTO PhanQuyen (maTaiKhoan, maQuyen, coQuyenXem, coQuyenThem, coQuyenSua, coQuyenXoa, nguoiGan)
VALUES
    (N'TK003', N'Q001', 1, 0, 1, 0, N'TK001'),  -- Sản phẩm: xem, sửa
    (N'TK003', N'Q004', 1, 1, 1, 0, N'TK001'),  -- Kho: xem, thêm, sửa
    (N'TK003', N'Q012', 1, 1, 1, 0, N'TK001'),  -- NCC: xem, thêm, sửa
    (N'TK007', N'Q001', 1, 0, 1, 0, N'TK001'),
    (N'TK007', N'Q004', 1, 1, 1, 0, N'TK001'),
    (N'TK007', N'Q012', 1, 0, 0, 0, N'TK001');
GO

-- 5. Ca làm việc (BẢNG 5)
INSERT INTO CaLamViec (maCa, tenCa, gioVao, gioRa, moTa, trangThai)
VALUES
    (N'CA001', N'Ca sáng',               '08:00', '12:00', N'Ca làm việc buổi sáng (8h - 12h)',       N'Hoạt động'),
    (N'CA002', N'Ca chiều',              '13:00', '17:00', N'Ca làm việc buổi chiều (13h - 17h)',     N'Hoạt động'),
    (N'CA003', N'Ca tối',                '18:00', '22:00', N'Ca làm việc buổi tối (18h - 22h)',       N'Hoạt động'),
    (N'CA004', N'Ca hành chính',         '08:00', '17:00', N'Ca hành chính giờ văn phòng (8h - 17h)', N'Hoạt động'),
    (N'CA005', N'Ca gãy (Sáng - Tối)',   '08:00', '21:00', N'Ca tăng cường dịp lễ / khuyến mãi',     N'Hoạt động'),
    (N'CA006', N'Ca đêm kiểm kho',       '22:00', '06:00', N'Ca kiểm kê kho định kỳ ban đêm',         N'Ngừng sử dụng');
GO

-- 6. Phân công ca làm (BẢNG 6)
INSERT INTO PhanCongCaLam (maPhanCong, maNhanVien, maCa, ngayLamViec, trangThai, gioDiemDanhVao, gioDiemDanhRa, soGioLamThem, ghiChu, nguoiPhanCong)
VALUES
    (N'PC001', N'NV001', N'CA004', '2026-10-16', N'Đã điểm danh', '07:55', '17:05', 0.0, N'Quản lý trực hành chính',     N'NV001'),
    (N'PC002', N'NV002', N'CA001', '2026-10-16', N'Đã điểm danh', '07:58', '12:02', 0.0, N'Trực thu ngân quầy 1',        N'NV001'),
    (N'PC003', N'NV006', N'CA002', '2026-10-16', N'Đã điểm danh', '12:55', '17:10', 0.5, N'Trực thu ngân quầy 1',        N'NV001'),
    (N'PC004', N'NV003', N'CA001', '2026-10-16', N'Đã điểm danh', '08:00', '12:00', 0.0, N'Nhận hàng từ nhà cung cấp',   N'NV001'),
    (N'PC005', N'NV004', N'CA002', '2026-10-17', N'Nghỉ phép',    NULL,    NULL,    0.0, N'Xin nghỉ phép có báo trước',   N'NV001'),
    (N'PC006', N'NV008', N'CA003', '2026-10-17', N'Đã điểm danh', '17:50', '22:15', 1.0, N'Tăng ca hỗ trợ khách tối',    N'NV001'),
    (N'PC007', N'NV007', N'CA002', '2026-10-18', N'Đã điểm danh', '13:00', '17:00', 0.0, N'Soạn hàng xuất kho',          N'NV001'),
    (N'PC008', N'NV009', N'CA001', '2026-10-18', N'Vắng mặt',     NULL,    NULL,    0.0, N'Đi trễ quá giờ quy định',     N'NV001'),
    (N'PC009', N'NV002', N'CA001', '2026-10-19', N'Đã lên lịch',  NULL,    NULL,    0.0, N'Trực quầy chính đầu tuần',    N'NV001'),
    (N'PC010', N'NV005', N'CA004', '2026-10-19', N'Đã lên lịch',  NULL,    NULL,    0.0, N'Tổng kết doanh thu tuần',     N'NV001'),
    (N'PC011', N'NV006', N'CA002', '2026-10-19', N'Đã lên lịch',  NULL,    NULL,    0.0, N'Trực thu ngân chiều',         N'NV001'),
    (N'PC012', N'NV008', N'CA003', '2026-10-19', N'Đã hủy',       NULL,    NULL,    0.0, N'Đổi ca sang ngày hôm sau',    N'NV001');
GO

-- 7. Danh mục (BẢNG 7)
INSERT INTO DanhMuc (maDanhMuc, tenDanhMuc, moTa, thuTuHienThi, trangThai)
VALUES
    (N'DM001', N'Điện thoại',          N'Điện thoại di động các loại',            1, N'Hoạt động'),
    (N'DM002', N'Laptop',              N'Máy tính xách tay',                      2, N'Hoạt động'),
    (N'DM003', N'Phụ kiện điện thoại', N'Ốp lưng, sạc, cáp, kính cường lực...',   3, N'Hoạt động'),
    (N'DM004', N'Phụ kiện laptop',     N'Chuột, bàn phím, túi chống sốc, hub...', 4, N'Hoạt động'),
    (N'DM005', N'Máy tính bảng',       N'Tablet các loại',                        5, N'Hoạt động'),
    (N'DM006', N'Đồng hồ thông minh',  N'Smartwatch các loại',                    6, N'Hoạt động'),
    (N'DM007', N'Tai nghe',            N'Tai nghe có dây và không dây',           7, N'Hoạt động'),
    (N'DM008', N'Loa',                 N'Loa bluetooth, loa kéo, soundbar',       8, N'Hoạt động'),
    (N'DM009', N'Tivi & Màn hình',     N'Smart TV và màn hình máy tính',          9, N'Hoạt động'),
    (N'DM010', N'Máy nghe nhạc MP3',   N'Danh mục thiết bị cũ ngừng kinh doanh',  10, N'Ngừng hoạt động');
GO

-- Danh mục con
INSERT INTO DanhMuc (maDanhMuc, tenDanhMuc, moTa, maDanhMucCha, thuTuHienThi, trangThai)
VALUES
    (N'DM011', N'Điện thoại Android', N'Điện thoại hệ điều hành Android',  N'DM001', 1, N'Hoạt động'),
    (N'DM012', N'iPhone',             N'Điện thoại Apple iPhone',           N'DM001', 2, N'Hoạt động'),
    (N'DM021', N'Laptop Gaming',      N'Laptop cấu hình cao cho gaming',    N'DM002', 1, N'Hoạt động'),
    (N'DM022', N'Laptop Văn phòng',   N'Laptop mỏng nhẹ dùng cho văn phòng',N'DM002', 2, N'Hoạt động');
GO

-- 8. Sản phẩm (BẢNG 8)
INSERT INTO SanPham (maSanPham, tenSanPham, maDanhMuc, moTa, thuongHieu, xuatXu, donViTinh, giaNhap, giaBan, soLuongTon, soLuongToiThieu, maSKU, maVach, trongLuong, kichThuoc, trangThai)
VALUES
    (N'SP001', N'iPhone 15 Pro Max 256GB',          N'DM012', N'iPhone 15 Pro Max, bộ nhớ 256GB, chip A17 Pro', N'Apple',    N'Mỹ',         N'Cái', 28000000, 34990000, 50,  5,  N'SKU-IP15PM-256', N'8901234567890', 0.22, N'15.9x7.6x0.8 cm', N'Đang kinh doanh'),
    (N'SP002', N'iPhone 15 128GB',                  N'DM012', N'iPhone 15, bộ nhớ 128GB, chip A16 Bionic',     N'Apple',    N'Mỹ',         N'Cái', 18000000, 22990000, 80,  10, N'SKU-IP15-128',   N'8901234567891', 0.17, N'14.7x7.1x0.7 cm', N'Đang kinh doanh'),
    (N'SP003', N'Samsung Galaxy S24 Ultra 256GB',    N'DM011', N'Galaxy S24 Ultra, bộ nhớ 256GB, S-Pen',        N'Samsung',  N'Hàn Quốc',   N'Cái', 25000000, 31990000, 45,  5,  N'SKU-SS24U-256',  N'8901234567892', 0.23, N'16.2x7.9x0.8 cm', N'Đang kinh doanh'),
    (N'SP004', N'Samsung Galaxy A55 128GB',          N'DM011', N'Galaxy A55, bộ nhớ 128GB, camera 50MP',        N'Samsung',  N'Hàn Quốc',   N'Cái', 7500000,  9990000,  120, 15, N'SKU-SSA55-128',  N'8901234567893', 0.21, N'16.1x7.7x0.8 cm', N'Đang kinh doanh'),
    (N'SP005', N'Xiaomi 14 Ultra 512GB',             N'DM011', N'Xiaomi 14 Ultra, camera Leica, 512GB',         N'Xiaomi',   N'Trung Quốc', N'Cái', 20000000, 24990000, 30,  5,  N'SKU-XM14U-512',  N'8901234567894', 0.22, N'16.1x7.5x0.9 cm', N'Đang kinh doanh'),
    (N'SP006', N'OPPO Find X7 Ultra 256GB',          N'DM011', N'Find X7 Ultra, camera Hasselblad',             N'OPPO',     N'Trung Quốc', N'Cái', 18000000, 22990000, 25,  5,  N'SKU-OPX7U-256',  N'8901234567895', 0.22, N'16.4x7.6x0.9 cm', N'Đang kinh doanh'),
    (N'SP007', N'Laptop ASUS ROG Strix G16',         N'DM021', N'ROG Strix G16, RTX 4060, i7-13650HX',         N'ASUS',     N'Đài Loan',   N'Cái', 28000000, 33990000, 20,  3,  N'SKU-ASROG-G16',  N'8901234567896', 2.50, N'35.4x26.4x2.2 cm',N'Đang kinh doanh'),
    (N'SP008', N'Laptop Dell XPS 15',                N'DM022', N'Dell XPS 15, i7-13700H, 16GB RAM',             N'Dell',     N'Mỹ',         N'Cái', 32000000, 38990000, 15,  3,  N'SKU-DXPS15',     N'8901234567897', 1.86, N'34.4x23.0x1.8 cm',N'Đang kinh doanh'),
    (N'SP009', N'Laptop HP Pavilion 15',             N'DM022', N'HP Pavilion 15, Ryzen 7, 16GB RAM',            N'HP',       N'Mỹ',         N'Cái', 14000000, 17990000, 35,  5,  N'SKU-HPP15',      N'8901234567898', 1.75, N'36.0x23.4x1.7 cm',N'Đang kinh doanh'),
    (N'SP010', N'MacBook Air M3 256GB',              N'DM022', N'MacBook Air M3, 8GB RAM, 256GB SSD',           N'Apple',    N'Mỹ',         N'Cái', 24000000, 28990000, 25,  5,  N'SKU-MBA-M3-256', N'8901234567899', 1.24, N'30.4x21.5x1.1 cm',N'Đang kinh doanh'),
    (N'SP011', N'iPad Pro M4 11 inch 256GB',         N'DM005', N'iPad Pro M4, màn OLED, Apple Pencil Pro',      N'Apple',    N'Mỹ',         N'Cái', 22000000, 27990000, 20,  3,  N'SKU-IPDP-M4',    N'8901234567900', 0.44, N'24.9x17.7x0.5 cm',N'Đang kinh doanh'),
    (N'SP012', N'Apple Watch Series 10 GPS',         N'DM006', N'Apple Watch S10, GPS, viền nhôm',              N'Apple',    N'Mỹ',         N'Cái', 9000000,  11990000, 40,  5,  N'SKU-AWS10-GPS',  N'8901234567901', 0.04, N'4.6x3.9x0.9 cm',  N'Đang kinh doanh'),
    (N'SP013', N'AirPods Pro 2 USB-C',               N'DM007', N'AirPods Pro thế hệ 2, cổng USB-C, ANC',       N'Apple',    N'Mỹ',         N'Cái', 4500000,  6790000,  60,  10, N'SKU-APP2-USBC',  N'8901234567902', 0.06, N'4.5x6.0x2.1 cm',  N'Đang kinh doanh'),
    (N'SP014', N'Sony WH-1000XM5',                   N'DM007', N'Tai nghe chụp tai, chống ồn cao cấp',          N'Sony',     N'Nhật Bản',   N'Cái', 6000000,  8490000,  30,  5,  N'SKU-SXWH5',      N'8901234567903', 0.25, N'20.0x18.0x8.0 cm',N'Đang kinh doanh'),
    (N'SP015', N'JBL Charge 5',                      N'DM008', N'Loa Bluetooth chống nước IP67',                N'JBL',      N'Mỹ',         N'Cái', 2500000,  3690000,  50,  10, N'SKU-JBLC5',      N'8901234567904', 0.96, N'22.3x9.6x9.4 cm', N'Đang kinh doanh'),
    (N'SP016', N'Ốp lưng iPhone 15 Pro Max MagSafe', N'DM003', N'Ốp lưng silicon chính hãng Apple MagSafe',    N'Apple',    N'Mỹ',         N'Cái', 500000,   1290000,  200, 20, N'SKU-OL-IP15PM',  N'8901234567905', 0.03, N'16.2x8.0x1.0 cm', N'Đang kinh doanh'),
    (N'SP017', N'Sạc nhanh Samsung 25W',             N'DM003', N'Củ sạc nhanh Samsung 25W USB-C',              N'Samsung',  N'Hàn Quốc',   N'Cái', 150000,   350000,   300, 30, N'SKU-SC-SS25W',   N'8901234567906', 0.05, N'4.7x7.8x2.6 cm',  N'Đang kinh doanh'),
    (N'SP018', N'Chuột Logitech MX Master 3S',      N'DM004', N'Chuột không dây, cảm biến 8000 DPI',          N'Logitech', N'Thụy Sĩ',    N'Cái', 1800000,  2490000,  40,  5,  N'SKU-LG-MXM3S',   N'8901234567907', 0.14, N'12.4x8.4x5.1 cm', N'Đang kinh doanh'),
    (N'SP019', N'Bàn phím cơ Keychron K8 Pro',      N'DM004', N'Bàn phím cơ TKL, kết nối BT 5.1 & USB-C',    N'Keychron', N'Trung Quốc', N'Cái', 1500000,  2190000,  35,  5,  N'SKU-KC-K8P',     N'8901234567908', 0.99, N'35.5x12.3x4.2 cm',N'Đang kinh doanh'),
    (N'SP020', N'Samsung Galaxy Watch 7 44mm',       N'DM006', N'Đồng hồ thông minh, BIA sensor, GPS',         N'Samsung',  N'Hàn Quốc',   N'Cái', 6000000,  7990000,  30,  5,  N'SKU-SSGW7-44',   N'8901234567909', 0.03, N'4.4x4.4x0.9 cm',  N'Đang kinh doanh');
GO

-- 9. Khuyến mãi (BẢNG 9)
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, moTa, loaiKhuyenMai, giaTriGiam, giaTriGiamToiDa, donHangToiThieu, soLuongToiDa, soLuongDaSuDung, ngayBatDau, ngayKetThuc, apDungChoTatCa, trangThai, nguoiTao)
VALUES
    (N'KM001', N'Giảm 10% mừng khai trương',        N'Chương trình giảm giá 10% cho tất cả sản phẩm',   N'Phần trăm',            10,     5000000, 0,        NULL, 5, '2026-10-01', '2026-11-15', 1, N'Đang diễn ra', N'NV001'),
    (N'KM002', N'Giảm 500K cho đơn từ 5 triệu',     N'Giảm trực tiếp 500,000đ cho đơn từ 5,000,000đ',  N'Số tiền',              500000, NULL,    5000000,  100,  1, '2026-10-10', '2026-10-31', 1, N'Đang diễn ra', N'NV001'),
    (N'KM003', N'Black Friday – Giảm 20% Phụ kiện', N'Giảm 20% phụ kiện điện thoại & laptop',           N'Phần trăm',            20,     2000000, 0,        200,  0, '2026-11-20', '2026-11-30', 0, N'Chưa bắt đầu', N'NV001'),
    (N'KM004', N'Mua điện thoại tặng ốp lưng',      N'Mua bất kỳ điện thoại nào tặng 1 ốp lưng',       N'Mua X tặng Y',         0,      NULL,    10000000, 50,   0, '2026-10-20', '2026-12-31', 0, N'Chưa bắt đầu', N'NV001'),
    (N'KM005', N'Miễn phí giao hàng nội thành',     N'Miễn phí vận chuyển cho đơn hàng từ 2 triệu',     N'Miễn phí vận chuyển',  50000,  50000,   2000000,  500,  2, '2026-09-01', '2026-12-31', 1, N'Đang diễn ra', N'NV001');
GO

-- 10. Khuyến mãi - Sản phẩm (BẢNG 10)
INSERT INTO KhuyenMai_SanPham (maKhuyenMai, maSanPham, giaTriGiamRieng)
VALUES
    (N'KM003', N'SP016', 250000),
    (N'KM003', N'SP017', 70000),
    (N'KM003', N'SP018', 500000),
    (N'KM003', N'SP019', 400000),
    (N'KM004', N'SP001', NULL),
    (N'KM004', N'SP002', NULL),
    (N'KM004', N'SP003', NULL);
GO

-- 11. Khách hàng (BẢNG 11)
INSERT INTO KhachHang (maKhachHang, hoTen, gioiTinh, ngaySinh, soDienThoai, email, diaChi, loaiKhachHang, diemThuong, tongDiemTichLuy, tongChiTieu, soLanMuaHang, lanMuaGanNhat, trangThai, ghiChu)
VALUES
    (N'KH001', N'Nguyễn Thị Mai',     N'Nữ',  '1992-04-12', '0801234567', 'mai.nt@gmail.com',    N'12 Trần Hưng Đạo, Q.5, TP.HCM',      N'VIP',        1576, 3576, 92641000,  13, '2026-10-17 15:50:00', N'Hoạt động',       N'Khách hàng thân thiết lâu năm'),
    (N'KH002', N'Trần Văn Hùng',      N'Nam', '1988-09-05', '0812345678', 'hung.tv@gmail.com',   N'45 Lý Thường Kiệt, Q.Tân Bình',      N'Vàng',       800,  1800, 52000000,  8,  '2026-10-16 10:20:00', N'Hoạt động',       NULL),
    (N'KH003', N'Lê Thị Ngọc Ánh',    N'Nữ',  '1995-01-20', '0823456789', 'anh.ltn@gmail.com',   N'78 Nguyễn Đình Chiểu, Q.3, TP.HCM',  N'Thành viên', 290,  590,  43990000,  4,  '2026-10-17 14:30:00', N'Hoạt động',       NULL),
    (N'KH004', N'Phạm Quốc Đạt',      N'Nam', '1990-11-15', '0834567890', 'dat.pq@gmail.com',    N'101 Cộng Hòa, Q.Tân Bình',            N'Thường',     225,  225,  22490000,  1,  '2026-10-18 08:00:00', N'Hoạt động',       NULL),
    (N'KH005', N'Hoàng Thị Linh',     N'Nữ',  '1997-06-30', '0845678901', 'linh.ht@gmail.com',   N'234 Điện Biên Phủ, Q.Bình Thạnh',     N'Thành viên', 350,  700,  22000000,  5,  '2026-10-18 10:30:00', N'Hoạt động',       NULL),
    (N'KH006', N'Võ Minh Tuấn',       N'Nam', '1985-03-08', '0856789012', 'tuan.vm@gmail.com',   N'567 Nguyễn Kiệm, Q.Gò Vấp',          N'Bạch kim',   3200, 6500, 185000000, 24, '2026-10-18 10:00:00', N'Hoạt động',       N'Khách hàng doanh nghiệp VIP'),
    (N'KH007', N'Đỗ Thị Hồng Nhung',  N'Nữ',  '2000-12-25', '0867890123', 'nhung.dth@gmail.com', N'890 Quang Trung, Q.Gò Vấp',           N'Thường',     0,    0,    0,         0,  NULL,                  N'Hoạt động',       N'Mới đăng ký thẻ thành viên'),
    (N'KH008', N'Bùi Đức Anh',        N'Nam', '1993-08-18', '0878901234', 'anh.bd@gmail.com',    N'123 Phan Xích Long, Q.Phú Nhuận',     N'Vàng',       900,  2100, 65000000,  10, '2026-10-10 16:00:00', N'Hoạt động',       NULL),
    (N'KH009', N'Đặng Thảo Vy',       N'Nữ',  '1999-07-19', '0889012345', 'vy.dt@gmail.com',     N'321 Sư Vạn Hạnh, Q.10, TP.HCM',       N'Thành viên', 150,  400,  18500000,  3,  '2026-10-18 15:20:00', N'Hoạt động',       NULL),
    (N'KH010', N'Trịnh Công Sơn',     N'Nam', '1982-02-28', '0890123456', 'son.tc@gmail.com',    N'55 Nguyễn Thị Minh Khai, Q.1',        N'Thường',     0,    120,  12000000,  2,  '2025-05-10 11:00:00', N'Ngừng hoạt động', N'Đổi số điện thoại');
GO

-- 12. Đơn hàng (BẢNG 12)
INSERT INTO DonHang (maDonHang, maKhachHang, maNhanVien, ngayDatHang, ngayGiaoHang, diaChiGiaoHang, tongTienHang, tienGiamGia, maKhuyenMai, diemSuDung, tienDiem, phiVanChuyen, thueVAT, tongThanhToan, trangThai, ghiChu)
VALUES
    (N'DH001', N'KH001', N'NV002', '2026-10-16 09:30:00', '2026-10-16 09:35:00', N'Mua trực tiếp tại cửa hàng',          34990000, 3499000, N'KM001', 0,   0,      0,     0, 31491000, N'Đã hoàn thành', N'Thanh toán tại quầy'),
    (N'DH002', N'KH002', N'NV002', '2026-10-16 10:15:00', '2026-10-16 10:20:00', N'Mua trực tiếp tại cửa hàng',          9990000,  999000,  N'KM001', 0,   0,      0,     0, 8991000,  N'Đã hoàn thành', N'Thanh toán tại quầy'),
    (N'DH003', N'KH003', N'NV006', '2026-10-17 14:20:00', '2026-10-17 14:30:00', N'78 Nguyễn Đình Chiểu, Q.3, TP.HCM',   28990000, 0,       NULL,     0,   0,      0,     0, 28990000, N'Đã hoàn thành', N'Khách nhận tại quầy'),
    (N'DH004', N'KH001', N'NV004', '2026-10-17 15:45:00', '2026-10-17 15:50:00', N'12 Trần Hưng Đạo, Q.5, TP.HCM',       8490000,  849000,  N'KM001', 0,   0,      0,     0, 7641000,  N'Đã hoàn thành', NULL),
    (N'DH005', N'KH004', N'NV006', '2026-10-18 08:00:00', '2026-10-18 14:00:00', N'101 Cộng Hòa, Q.Tân Bình',            22990000, 500000,  N'KM002', 0,   0,      0,     0, 22490000, N'Đã hoàn thành', N'Giao nhanh nội thành'),
    (N'DH006', NULL,     N'NV002', '2026-10-18 09:10:00', '2026-10-18 09:15:00', N'Khách vãng lai mua tại quầy',         3690000,  369000,  N'KM001', 0,   0,      0,     0, 3321000,  N'Đã hoàn thành', N'Khách lẻ không tích điểm'),
    (N'DH007', N'KH006', N'NV008', '2026-10-18 10:00:00', NULL,                  N'567 Nguyễn Kiệm, Q.Gò Vấp',           38990000, 3899000, N'KM001', 100, 100000, 0,     0, 34991000, N'Đang giao hàng',N'Giao giờ hành chính'),
    (N'DH008', N'KH005', N'NV002', '2026-10-18 10:30:00', NULL,                  N'234 Điện Biên Phủ, Q.Bình Thạnh',     6790000,  679000,  N'KM001', 0,   0,      30000, 0, 6141000,  N'Đã xác nhận',   N'Chờ đơn vị vận chuyển'),
    (N'DH009', N'KH009', N'NV009', '2026-10-19 08:45:00', NULL,                  N'321 Sư Vạn Hạnh, Q.10, TP.HCM',       20480000, 500000,  N'KM002', 0,   0,      0,     0, 19980000, N'Mới tạo',       N'Khách đặt online chờ gọi xác nhận'),
    (N'DH010', N'KH008', N'NV006', '2026-10-19 09:15:00', NULL,                  N'123 Phan Xích Long, Q.Phú Nhuận',     11990000, 0,       NULL,     0,   0,      0,     0, 11990000, N'Đã hủy',        N'Khách đổi ý muốn mua mẫu khác');
GO

-- 13. Chi tiết đơn hàng (BẢNG 13)
INSERT INTO ChiTietDonHang (maDonHang, maSanPham, soLuong, donGia, giamGia, thanhTien, ghiChu)
VALUES
    (N'DH001', N'SP001', 1, 34990000, 3499000, 31491000, N'Bảo hành 12 tháng chính hãng'),
    (N'DH002', N'SP004', 1, 9990000,  999000,  8991000,  N'Tặng dán màn hình'),
    (N'DH003', N'SP010', 1, 28990000, 0,       28990000, N'Bảo hành Apple Việt Nam'),
    (N'DH004', N'SP014', 1, 8490000,  849000,  7641000,  NULL),
    (N'DH005', N'SP002', 1, 22990000, 500000,  22490000, NULL),
    (N'DH006', N'SP015', 1, 3690000,  369000,  3321000,  NULL),
    (N'DH007', N'SP008', 1, 38990000, 3899000, 35091000, N'Cài sẵn Windows 11 & Office'),
    (N'DH008', N'SP013', 1, 6790000,  679000,  6111000,  NULL),
    (N'DH009', N'SP009', 1, 17990000, 500000,  17490000, NULL),
    (N'DH009', N'SP018', 1, 2490000,  0,       2490000,  N'Mua kèm laptop'),
    (N'DH010', N'SP012', 1, 11990000, 0,       11990000, N'Đơn đã hủy');
GO

-- 14. Hóa đơn (BẢNG 14)
INSERT INTO HoaDon (maHoaDon, maDonHang, maKhachHang, maNhanVien, ngayLapHoaDon, tongTienHang, tienGiamGia, tienDiem, thueVAT, tongThanhToan, tienKhachDua, tienTraLai, phuongThucThanhToan, soTaiKhoanNH, tenNganHang, maGiaoDich, diemCongThem, trangThai, ghiChu)
VALUES
    (N'HD001', N'DH001', N'KH001', N'NV002', '2026-10-16 09:35:00', 34990000, 3499000, 0,      0, 31491000, 32000000, 509000, N'Tiền mặt',     NULL,           NULL,          NULL,            315, N'Đã thanh toán', NULL),
    (N'HD002', N'DH002', N'KH002', N'NV002', '2026-10-16 10:20:00', 9990000,  999000,  0,      0, 8991000,  9000000,  9000,   N'Tiền mặt',     NULL,           NULL,          NULL,            90,  N'Đã thanh toán', NULL),
    (N'HD003', N'DH003', N'KH003', N'NV006', '2026-10-17 14:30:00', 28990000, 0,       0,      0, 28990000, 28990000, 0,      N'Chuyển khoản', N'19036889999', N'Techcombank',N'FT2629012345', 290, N'Đã thanh toán', N'Chuyển khoản QR'),
    (N'HD004', N'DH004', N'KH001', N'NV002', '2026-10-17 15:50:00', 8490000,  849000,  0,      0, 7641000,  8000000,  359000, N'Tiền mặt',     NULL,           NULL,          NULL,            76,  N'Đã thanh toán', NULL),
    (N'HD005', N'DH005', N'KH004', N'NV006', '2026-10-18 14:10:00', 22990000, 500000,  0,      0, 22490000, 22490000, 0,      N'Thẻ tín dụng', N'4111****8899',N'Vietcombank',N'POS261018005', 225, N'Đã thanh toán', N'Quẹt thẻ Visa'),
    (N'HD006', N'DH006', NULL,     N'NV002', '2026-10-18 09:15:00', 3690000,  369000,  0,      0, 3321000,  3321000,  0,      N'Ví điện tử',   NULL,           N'MoMo',       N'MOMO99887766', 0,   N'Đã thanh toán', N'Khách lẻ thanh toán MoMo'),
    (N'HD007', N'DH007', N'KH006', N'NV002', '2026-10-18 10:05:00', 38990000, 3899000, 100000, 0, 34991000, 0,        0,      N'Chuyển khoản', N'00710008888', N'Vietcombank',NULL,            350, N'Chờ thanh toán',N'Thanh toán khi nhận hàng');
GO

-- 15. Nhà cung cấp (BẢNG 15)
INSERT INTO NhaCungCap (maNhaCungCap, tenNhaCungCap, soDienThoai, email, diaChi, nguoiLienHe, maSoThue, soTaiKhoanNH, tenNganHang, congNo, trangThai, ghiChu)
VALUES
    (N'NCC001', N'Công ty TNHH Apple Việt Nam',             '02838001111', 'apple.vn@supplier.com',   N'Tòa nhà Bitexco, Q.1, TP.HCM',       N'Nguyễn Minh Tâm', N'0312345678', N'0071001112223', N'Vietcombank', 150000000, N'Hoạt động',     N'Nhà phân phối ủy quyền Apple'),
    (N'NCC002', N'Samsung Electronics Việt Nam',            '02438002222', 'samsung.vn@supplier.com', N'KCN Yên Phong, Bắc Ninh',             N'Trần Quốc Bảo',   N'0312345679', N'1903444555666', N'Techcombank', 80000000,  N'Hoạt động',     N'Đối tác chiến lược Samsung'),
    (N'NCC003', N'Công ty CP Phân phối FPT (FPT Trading)',  '02838003333', 'fpt@supplier.com',        N'Lô L29B-31B-33B, KCX Tân Thuận, Q.7', N'Lê Hoàng Nam',    N'0312345680', N'1122334455667', N'MB Bank',     0,         N'Hoạt động',     N'Phân phối Laptop & Phụ kiện'),
    (N'NCC004', N'Công ty TNHH Synnex FPT',                 '02838004444', 'synnex@supplier.com',     N'Tầng 12, Tòa nhà TTC, Q.1, TP.HCM',  N'Phạm Anh Tuấn',   N'0312345681', N'8899001122334', N'ACB',         45000000,  N'Hoạt động',     NULL),
    (N'NCC005', N'Xiaomi Technology Việt Nam',              '02838005555', 'xiaomi.vn@supplier.com',  N'Tầng 8, Lim Tower, Q.1, TP.HCM',     N'Vũ Thị Hương',    N'0312345682', N'5566778899001', N'VPBank',      0,         N'Hoạt động',     NULL),
    (N'NCC006', N'Công ty TNHH Phụ kiện Số Sài Gòn',        '02838006666', 'phukienso@supplier.com',  N'99 Nguyễn Thị Nhỏ, Q.11, TP.HCM',    N'Đặng Văn Hòa',    N'0312345683', NULL,             NULL,           0,         N'Ngừng hợp tác', N'Ngừng nhập từ đầu năm 2026');
GO

-- 16. Phiếu nhập kho (BẢNG 16)
INSERT INTO PhieuNhapKho (maPhieuNhap, maNhaCungCap, maNhanVien, ngayNhap, tongSoLuong, tongTienNhap, trangThai, ghiChu)
VALUES
    (N'PN001', N'NCC001', N'NV003', '2026-10-10 08:00:00', 330, 2940000000, N'Đã nhập',   N'Nhập lô iPhone 15 và ốp lưng chính hãng'),
    (N'PN002', N'NCC002', N'NV003', '2026-10-12 09:00:00', 495, 2250000000, N'Đã nhập',   N'Nhập điện thoại và đồng hồ Samsung'),
    (N'PN003', N'NCC003', N'NV007', '2026-10-14 08:30:00', 145, 1654500000, N'Đã nhập',   N'Nhập Laptop ASUS, Dell, HP và phụ kiện'),
    (N'PN004', N'NCC005', N'NV007', '2026-10-15 10:00:00', 30,  600000000,  N'Đã nhập',   N'Nhập Xiaomi 14 Ultra'),
    (N'PN005', N'NCC004', N'NV003', '2026-10-19 08:30:00', 50,  255000000,  N'Đang nhập', N'Đang kiểm đếm hàng tai nghe & loa');
GO

-- 17. Chi tiết phiếu nhập (BẢNG 17 - Disable trigger tạm để insert dữ liệu mẫu không bị cộng dồn tồn kho)
DISABLE TRIGGER trg_ChiTietPhieuNhap_Insert ON ChiTietPhieuNhap;
GO

INSERT INTO ChiTietPhieuNhap (maPhieuNhap, maSanPham, soLuongNhap, donGiaNhap, thanhTien, ngaySanXuat, hanSuDung, ghiChu)
VALUES
    -- PN001: Nhập từ Apple
    (N'PN001', N'SP001', 50,  28000000, 1400000000, '2026-06-01', NULL, N'Nguyên seal Apple'),
    (N'PN001', N'SP002', 80,  18000000, 1440000000, '2026-06-15', NULL, N'Nguyên seal Apple'),
    (N'PN001', N'SP016', 200, 500000,   100000000,  '2026-05-01', NULL, NULL),
    -- PN002: Nhập từ Samsung
    (N'PN002', N'SP003', 45,  25000000, 1125000000, '2026-07-01', NULL, NULL),
    (N'PN002', N'SP004', 120, 7500000,  900000000,  '2026-07-10', NULL, NULL),
    (N'PN002', N'SP017', 300, 150000,   45000000,   '2026-04-01', NULL, NULL),
    (N'PN002', N'SP020', 30,  6000000,  180000000,  '2026-08-01', NULL, NULL),
    -- PN003: Nhập từ FPT Trading
    (N'PN003', N'SP007', 20,  28000000, 560000000,  '2026-05-20', NULL, NULL),
    (N'PN003', N'SP008', 15,  32000000, 480000000,  '2026-06-10', NULL, NULL),
    (N'PN003', N'SP009', 35,  14000000, 490000000,  '2026-06-25', NULL, NULL),
    (N'PN003', N'SP018', 40,  1800000,  72000000,   '2026-03-15', NULL, NULL),
    (N'PN003', N'SP019', 35,  1500000,  52500000,   '2026-03-20', NULL, NULL),
    -- PN004: Nhập từ Xiaomi
    (N'PN004', N'SP005', 30,  20000000, 600000000,  '2026-08-10', NULL, NULL),
    -- PN005: Nhập từ Synnex FPT
    (N'PN005', N'SP013', 30,  4500000,  135000000,  '2026-07-05', NULL, NULL),
    (N'PN005', N'SP014', 20,  6000000,  120000000,  '2026-07-12', NULL, NULL);
GO

ENABLE TRIGGER trg_ChiTietPhieuNhap_Insert ON ChiTietPhieuNhap;
GO

-- 18. Phiếu xuất kho (BẢNG 18)
INSERT INTO PhieuXuatKho (maPhieuXuat, maNhanVien, maDonHang, lyDoXuat, ngayXuat, tongSoLuong, tongTienXuat, trangThai, ghiChu)
VALUES
    (N'PX001', N'NV003', N'DH001', N'Bán hàng',     '2026-10-16 09:32:00', 1, 34990000, N'Đã xuất',   N'Xuất bán cho đơn DH001'),
    (N'PX002', N'NV003', N'DH002', N'Bán hàng',     '2026-10-16 10:17:00', 1, 9990000,  N'Đã xuất',   N'Xuất bán cho đơn DH002'),
    (N'PX003', N'NV007', N'DH003', N'Bán hàng',     '2026-10-17 14:25:00', 1, 28990000, N'Đã xuất',   N'Xuất bán cho đơn DH003'),
    (N'PX004', N'NV007', N'DH004', N'Bán hàng',     '2026-10-17 15:48:00', 1, 8490000,  N'Đã xuất',   N'Xuất bán cho đơn DH004'),
    (N'PX005', N'NV003', N'DH005', N'Bán hàng',     '2026-10-18 08:15:00', 1, 22990000, N'Đã xuất',   N'Xuất giao hàng đơn DH005'),
    (N'PX006', N'NV007', NULL,     N'Trả hàng NCC', '2026-10-18 16:30:00', 2, 1000000,  N'Đã xuất',   N'Trả 2 ốp lưng bị lỗi ngoại quan'),
    (N'PX007', N'NV003', N'DH007', N'Bán hàng',     '2026-10-18 10:10:00', 1, 38990000, N'Đang xuất', N'Đang đóng gói giao cho KH006');
GO

-- 19. Chi tiết phiếu xuất (BẢNG 19 - Disable trigger tạm)
DISABLE TRIGGER trg_ChiTietPhieuXuat_Insert ON ChiTietPhieuXuat;
GO

INSERT INTO ChiTietPhieuXuat (maPhieuXuat, maSanPham, soLuongXuat, donGiaXuat, thanhTien, ghiChu)
VALUES
    (N'PX001', N'SP001', 1, 34990000, 34990000, NULL),
    (N'PX002', N'SP004', 1, 9990000,  9990000,  NULL),
    (N'PX003', N'SP010', 1, 28990000, 28990000, NULL),
    (N'PX004', N'SP014', 1, 8490000,  8490000,  NULL),
    (N'PX005', N'SP002', 1, 22990000, 22990000, NULL),
    (N'PX006', N'SP016', 2, 500000,   1000000,  N'Lỗi trầy xước vỏ hộp'),
    (N'PX007', N'SP008', 1, 38990000, 38990000, NULL);
GO

ENABLE TRIGGER trg_ChiTietPhieuXuat_Insert ON ChiTietPhieuXuat;
GO

-- 20. Lịch sử điểm thưởng (BẢNG 20)
INSERT INTO LichSuDiemThuong (maLichSu, maKhachHang, maDonHang, maHoaDon, loaiGiaoDich, soDiem, diemTruocGD, diemSauGD, moTa, ngayGiaoDich)
VALUES
    (N'LS001', N'KH001', N'DH001', N'HD001', N'Tích điểm',  315,  1185, 1500, N'Tích điểm từ đơn DH001',              '2026-10-16 09:35:00'),
    (N'LS002', N'KH002', N'DH002', N'HD002', N'Tích điểm',  90,   710,  800,  N'Tích điểm từ đơn DH002',              '2026-10-16 10:20:00'),
    (N'LS003', N'KH003', N'DH003', N'HD003', N'Tích điểm',  290,  0,    290,  N'Tích điểm từ đơn DH003 (khách mới)',  '2026-10-17 14:30:00'),
    (N'LS004', N'KH001', N'DH004', N'HD004', N'Tích điểm',  76,   1500, 1576, N'Tích điểm từ đơn DH004',              '2026-10-17 15:50:00'),
    (N'LS005', N'KH004', N'DH005', N'HD005', N'Tích điểm',  225,  0,    225,  N'Tích điểm từ đơn DH005',              '2026-10-18 14:10:00'),
    (N'LS006', N'KH006', N'DH007', N'HD007', N'Sử dụng',   -100,  3300, 3200, N'Trừ 100 điểm giảm 100,000đ đơn DH007','2026-10-18 10:00:00');
GO

-- 21. Lịch sử đăng nhập (BẢNG 21)
INSERT INTO LichSuDangNhap (maTaiKhoan, thoiGianDangNhap, thoiGianDangXuat, diaChiIP, tenMay, ketQua, lyDoThatBai)
VALUES
    (N'TK001', '2026-10-18 07:50:00', '2026-10-18 17:15:00', N'192.168.1.10', N'PC-QUANLY-01',  N'Thành công', NULL),
    (N'TK002', '2026-10-18 07:55:00', '2026-10-18 12:05:00', N'192.168.1.21', N'POS-THUNGAN-01',N'Thành công', NULL),
    (N'TK006', '2026-10-18 12:55:00', '2026-10-18 17:10:00', N'192.168.1.21', N'POS-THUNGAN-01',N'Thành công', NULL),
    (N'TK003', '2026-10-18 08:00:00', '2026-10-18 17:00:00', N'192.168.1.30', N'PC-KHO-01',     N'Thành công', NULL),
    (N'TK008', '2026-10-18 17:48:00', NULL,                  N'192.168.1.25', N'PC-BANHANG-02', N'Thất bại',   N'Sai mật khẩu'),
    (N'TK008', '2026-10-18 17:50:00', '2026-10-18 22:15:00', N'192.168.1.25', N'PC-BANHANG-02', N'Thành công', NULL),
    (N'TK001', '2026-10-19 08:00:00', NULL,                  N'192.168.1.10', N'PC-QUANLY-01',  N'Thành công', NULL),
    (N'TK002', '2026-10-19 07:55:00', NULL,                  N'192.168.1.21', N'POS-THUNGAN-01',N'Thành công', NULL);
GO



-- ============================================================
-- STORED PROCEDURES HỮU ÍCH
-- ============================================================

-- SP: Tìm kiếm sản phẩm
CREATE PROCEDURE sp_TimKiemSanPham
    @tuKhoa NVARCHAR(100) = NULL,
    @maDanhMuc NVARCHAR(20) = NULL,
    @giaMin DECIMAL(15,2) = NULL,
    @giaMax DECIMAL(15,2) = NULL,
    @trangThai NVARCHAR(30) = NULL
AS
BEGIN
SELECT sp.maSanPham, sp.tenSanPham, dm.tenDanhMuc, sp.thuongHieu,
       sp.giaBan, sp.soLuongTon, sp.trangThai
FROM SanPham sp
         INNER JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc
WHERE (@tuKhoa IS NULL OR sp.tenSanPham LIKE '%' + @tuKhoa + '%' OR sp.thuongHieu LIKE '%' + @tuKhoa + '%')
  AND (@maDanhMuc IS NULL OR sp.maDanhMuc = @maDanhMuc)
  AND (@giaMin IS NULL OR sp.giaBan >= @giaMin)
  AND (@giaMax IS NULL OR sp.giaBan <= @giaMax)
  AND (@trangThai IS NULL OR sp.trangThai = @trangThai)
ORDER BY sp.tenSanPham;
END;
GO

-- SP: Báo cáo doanh thu theo ngày
CREATE PROCEDURE sp_BaoCaoDoanhThuTheoNgay
    @tuNgay DATE,
    @denNgay DATE
AS
BEGIN
SELECT CAST(hd.ngayLapHoaDon AS DATE) AS ngay,
       COUNT(hd.maHoaDon) AS soHoaDon,
       SUM(hd.tongThanhToan) AS doanhThu,
       SUM(hd.tienGiamGia) AS tongGiamGia
FROM HoaDon hd
WHERE hd.trangThai = N'Đã thanh toán'
  AND CAST(hd.ngayLapHoaDon AS DATE) BETWEEN @tuNgay AND @denNgay
GROUP BY CAST(hd.ngayLapHoaDon AS DATE)
ORDER BY ngay;
END;
GO

-- SP: Báo cáo sản phẩm bán chạy
CREATE PROCEDURE sp_SanPhamBanChay
    @tuNgay DATE,
    @denNgay DATE,
    @top INT = 10
AS
BEGIN
SELECT TOP (@top)
    sp.maSanPham, sp.tenSanPham, sp.thuongHieu,
       SUM(ct.soLuong) AS tongSoLuong,
       SUM(ct.thanhTien) AS tongDoanhThu
FROM ChiTietDonHang ct
         INNER JOIN SanPham sp ON ct.maSanPham = sp.maSanPham
         INNER JOIN DonHang dh ON ct.maDonHang = dh.maDonHang
WHERE dh.trangThai = N'Đã hoàn thành'
  AND CAST(dh.ngayDatHang AS DATE) BETWEEN @tuNgay AND @denNgay
GROUP BY sp.maSanPham, sp.tenSanPham, sp.thuongHieu
ORDER BY tongSoLuong DESC;
END;
GO

-- SP: Báo cáo tồn kho
CREATE PROCEDURE sp_BaoCaoTonKho
    @sapHet BIT = 0
AS
BEGIN
SELECT sp.maSanPham, sp.tenSanPham, dm.tenDanhMuc, sp.thuongHieu,
       sp.donViTinh, sp.soLuongTon, sp.soLuongToiThieu,
       sp.giaNhap, sp.giaBan,
       IIF(sp.soLuongTon <= sp.soLuongToiThieu, N'Sắp hết', N'Đủ hàng') AS tinhTrang
FROM SanPham sp
         INNER JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc
WHERE sp.trangThai = N'Đang kinh doanh'
  AND (@sapHet = 0 OR sp.soLuongTon <= sp.soLuongToiThieu)
ORDER BY sp.soLuongTon ASC;
END;
GO

-- SP: Đăng nhập
CREATE PROCEDURE sp_DangNhap
    @tenDangNhap NVARCHAR(50),
    @matKhau NVARCHAR(255)
AS
BEGIN
    DECLARE @maTK NVARCHAR(20), @trangThai NVARCHAR(20);

SELECT @maTK = maTaiKhoan, @trangThai = trangThai
FROM TaiKhoan
WHERE tenDangNhap = @tenDangNhap AND matKhau = @matKhau;

IF @maTK IS NULL
BEGIN
        -- Ghi log thất bại
        DECLARE @maTKFail NVARCHAR(20);
SELECT @maTKFail = maTaiKhoan FROM TaiKhoan WHERE tenDangNhap = @tenDangNhap;
IF @maTKFail IS NOT NULL
BEGIN
UPDATE TaiKhoan SET soLanDangNhapLoi = soLanDangNhapLoi + 1 WHERE maTaiKhoan = @maTKFail;
-- Khóa tài khoản nếu sai quá 5 lần
UPDATE TaiKhoan SET trangThai = N'Bị khóa'
WHERE maTaiKhoan = @maTKFail AND soLanDangNhapLoi >= 5;

INSERT INTO LichSuDangNhap (maTaiKhoan, ketQua, lyDoThatBai)
VALUES (@maTKFail, N'Thất bại', N'Sai mật khẩu');
END
SELECT 0 AS ketQua, N'Sai tên đăng nhập hoặc mật khẩu' AS thongBao;
RETURN;
END

    IF @trangThai = N'Bị khóa'
BEGIN
INSERT INTO LichSuDangNhap (maTaiKhoan, ketQua, lyDoThatBai)
VALUES (@maTK, N'Thất bại', N'Tài khoản bị khóa');
SELECT 0 AS ketQua, N'Tài khoản đã bị khóa' AS thongBao;
RETURN;
END

    -- Đăng nhập thành công
UPDATE TaiKhoan
SET lanDangNhapCuoi = GETDATE(), soLanDangNhapLoi = 0
WHERE maTaiKhoan = @maTK;

INSERT INTO LichSuDangNhap (maTaiKhoan, ketQua)
VALUES (@maTK, N'Thành công');

SELECT 1 AS ketQua, N'Đăng nhập thành công' AS thongBao,
       tk.maTaiKhoan, tk.tenDangNhap, tk.vaiTro,
       nv.maNhanVien, nv.hoTen, nv.chucVu, nv.hinhAnh
FROM TaiKhoan tk
         INNER JOIN NhanVien nv ON tk.maNhanVien = nv.maNhanVien
WHERE tk.maTaiKhoan = @maTK;
END;
GO

-- SP: Lấy quyền của tài khoản
CREATE PROCEDURE sp_LayQuyenTaiKhoan
    @maTaiKhoan NVARCHAR(20)
AS
BEGIN
SELECT pq.maTaiKhoan, q.maQuyen, q.tenQuyen, q.nhomQuyen,
       pq.coQuyenXem, pq.coQuyenThem, pq.coQuyenSua, pq.coQuyenXoa
FROM PhanQuyen pq
         INNER JOIN QuyenTruyCap q ON pq.maQuyen = q.maQuyen
WHERE pq.maTaiKhoan = @maTaiKhoan
ORDER BY q.nhomQuyen, q.tenQuyen;
END;
GO


-- ============================================================
-- VIEWS HỮU ÍCH
-- ============================================================

-- View: Thông tin đầy đủ sản phẩm + danh mục
CREATE VIEW vw_SanPham_DanhMuc AS
SELECT sp.maSanPham, sp.tenSanPham, sp.moTa, sp.thuongHieu, sp.xuatXu,
       sp.donViTinh, sp.giaNhap, sp.giaBan, sp.soLuongTon, sp.soLuongToiThieu,
       sp.maSKU, sp.maVach, sp.trangThai AS trangThaiSP,
       dm.maDanhMuc, dm.tenDanhMuc,
       dmc.maDanhMuc AS maDanhMucCha, dmc.tenDanhMuc AS tenDanhMucCha
FROM SanPham sp
         INNER JOIN DanhMuc dm ON sp.maDanhMuc = dm.maDanhMuc
         LEFT JOIN DanhMuc dmc ON dm.maDanhMucCha = dmc.maDanhMuc;
GO

-- View: Đơn hàng chi tiết
CREATE VIEW vw_DonHang_ChiTiet AS
SELECT dh.maDonHang, dh.ngayDatHang, dh.tongTienHang, dh.tienGiamGia,
       dh.tongThanhToan, dh.trangThai AS trangThaiDH,
       kh.maKhachHang, kh.hoTen AS tenKhachHang, kh.soDienThoai AS sdtKhachHang,
       nv.maNhanVien, nv.hoTen AS tenNhanVien,
       km.maKhuyenMai, km.tenKhuyenMai
FROM DonHang dh
         LEFT JOIN KhachHang kh ON dh.maKhachHang = kh.maKhachHang
         INNER JOIN NhanVien nv ON dh.maNhanVien = nv.maNhanVien
         LEFT JOIN KhuyenMai km ON dh.maKhuyenMai = km.maKhuyenMai;
GO

-- View: Hóa đơn đầy đủ
CREATE VIEW vw_HoaDon_DayDu AS
SELECT hd.maHoaDon, hd.ngayLapHoaDon, hd.tongTienHang, hd.tienGiamGia,
       hd.thueVAT, hd.tongThanhToan, hd.tienKhachDua, hd.tienTraLai,
       hd.phuongThucThanhToan, hd.trangThai AS trangThaiHD,
       dh.maDonHang, dh.ngayDatHang,
       kh.maKhachHang, kh.hoTen AS tenKhachHang,
       nv.maNhanVien, nv.hoTen AS tenNhanVienThuNgan
FROM HoaDon hd
         INNER JOIN DonHang dh ON hd.maDonHang = dh.maDonHang
         LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang
         INNER JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien;
GO

-- View: Nhân viên & tài khoản
CREATE VIEW vw_NhanVien_TaiKhoan AS
SELECT nv.maNhanVien, nv.hoTen, nv.gioiTinh, nv.ngaySinh, nv.soDienThoai,
       nv.email, nv.diaChi, nv.chucVu, nv.phongBan, nv.luongCoBan,
       nv.heSoLuong, nv.phuCap, nv.ngayVaoLam, nv.trangThai AS trangThaiNV,
       nql.hoTen AS tenNguoiQuanLy,
       tk.maTaiKhoan, tk.tenDangNhap, tk.vaiTro, tk.trangThai AS trangThaiTK
FROM NhanVien nv
         LEFT JOIN NhanVien nql ON nv.maNguoiQuanLy = nql.maNhanVien
         LEFT JOIN TaiKhoan tk ON nv.maNhanVien = tk.maNhanVien;
GO


PRINT N'============================================================';
PRINT N'  CƠ SỞ DỮ LIỆU QuanLyBanHang ĐÃ ĐƯỢC TẠO THÀNH CÔNG!';
PRINT N'============================================================';
PRINT N'  Tổng số bảng: 21';
PRINT N'  Tổng số Trigger: 5';
PRINT N'  Tổng số Stored Procedure: 5';
PRINT N'  Tổng số View: 4';
PRINT N'  Dữ liệu mẫu: Đã có';
PRINT N'============================================================';
GO