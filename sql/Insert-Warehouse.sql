USE WarehouseManagement;
GO

-- =============================================
-- PHẦN 1: LÀM SẠCH DỮ LIỆU CŨ
-- =============================================
DELETE FROM ExportDetails;
DELETE FROM ImportDetails;
DELETE FROM ExportOrders;
DELETE FROM ImportOrders;
DELETE FROM Products;
DELETE FROM Users;
DELETE FROM Customers;
DELETE FROM Suppliers;
DELETE FROM Categories;
DELETE FROM Warehouse;

DBCC CHECKIDENT ('Warehouse', RESEED, 0);
DBCC CHECKIDENT ('Categories', RESEED, 0);
DBCC CHECKIDENT ('Suppliers', RESEED, 0);
DBCC CHECKIDENT ('Customers', RESEED, 0);
DBCC CHECKIDENT ('Users', RESEED, 0);
DBCC CHECKIDENT ('Products', RESEED, 0);
DBCC CHECKIDENT ('ImportOrders', RESEED, 0);
DBCC CHECKIDENT ('ExportOrders', RESEED, 0);
GO

-- =============================================
-- PHẦN 2: DANH MỤC & ĐỐI TƯỢNG ĐỘC LẬP
-- =============================================

-- 1. Kho
INSERT INTO Warehouse (WarehouseName, Address)
VALUES 
(N'Kho Sách Chính', N'123 Đường Sách, Đà Nẵng'),
(N'Kho Văn Phòng Phẩm', N'456 Đường Giấy, Đà Nẵng');
GO

-- 2. Danh mục sản phẩm
INSERT INTO Categories (CategoryName) 
VALUES 
(N'Sách'),
(N'Truyện'),
(N'Tiểu thuyết'),
(N'Truyện tranh'),
(N'Văn phòng phẩm');
GO

-- 3. Nhà cung cấp
INSERT INTO Suppliers (SupplierName, Phone, Address) 
VALUES 
(N'NXB Trẻ', '02838220000', N'TP. Hồ Chí Minh'),
(N'NXB Kim Đồng', '02838221111', N'TP. Hồ Chí Minh'),
(N'Công ty TNHH TT-VH AZ Books', '02839230000', N'TP. Hồ Chí Minh'),
(N'Công ty Văn Phòng Phẩm Thiên Long', '02837540000', N'TP. Hồ Chí Minh');
GO

-- 4. Khách hàng
INSERT INTO Customers (CustomerName, Phone, Address) 
VALUES 
(N'Nhà sách ABC', '0901112222', N'Hải Châu, Đà Nẵng'),
(N'Cửa hàng Văn phòng phẩm HSSV', '0903334444', N'Liên Chiểu, Đà Nẵng'),
(N'Văn phòng công ty B', '02363557777', N'Sơn Trà, Đà Nẵng');
GO

-- =============================================
-- PHẦN 3: USERS & PRODUCTS
-- =============================================

-- 5. Người dùng
INSERT INTO Users (UserName, Password, FullName, Role, Status, WarehouseID) 
VALUES 
('admin', '123456', N'Quản Trị Viên', 'Admin', 1, 1),
('staff', '123456', N'Nhân Viên Kho', 'Staff', 1, 1);
GO

-- 6. Sản phẩm
INSERT INTO Products (ProductName, CategoryID, Unit, Price, Quantity, WarehouseID, SupplierID) 
VALUES 
-- ===== SÁCH - NXB TRẺ =====
(N'Tuyển tập Nguyễn Nhật Ánh', 1, N'Cuốn', 120000, 20, 1, 1),
(N'Truyện ngắn Nguyễn Ngọc Tư', 1, N'Cuốn', 110000, 15, 1, 1),

-- ===== TRUYỆN TRANH - NXB KIM ĐỒNG =====
(N'Doraemon Tập 1', 4, N'Cuốn', 25000, 40, 1, 2),
(N'Thám Tử Lừng Danh Conan Tập 1', 4, N'Cuốn', 30000, 35, 1, 2),

-- ===== TRUYỆN TRANH - AZ BOOKS =====
(N'Tổng hợp Truyện Tranh 2025', 2, N'Cuốn', 95000, 25, 1, 3),

-- ===== VĂN PHÒNG PHẨM - THIÊN LONG =====
(N'Bút bi Thiên Long', 5, N'Hộp', 45000, 12, 2, 4),
(N'Sổ tay A5', 5, N'Cuốn', 30000, 8, 2, 4),
(N'Giấy A4 Double A', 5, N'Ream', 68000, 6, 2, 4);
GO

-- =============================================
-- PHẦN 4: GIAO DỊCH NHẬP / XUẤT
-- =============================================

-- 7. Đơn nhập kho từ NXB Trẻ
INSERT INTO ImportOrders (ImportDate, Note, Status, UserID, SupplierID) 
VALUES 
(GETDATE(), N'Nhập sách văn học từ NXB Trẻ', 1, 1, 1);
GO

INSERT INTO ImportDetails (ImportID, ProductID, Quantity, ImportPrice) 
VALUES 
(1, 1, 10, 100000),
(1, 2, 8, 90000);
GO

-- 8. Đơn nhập kho từ NXB Kim Đồng
INSERT INTO ImportOrders (ImportDate, Note, Status, UserID, SupplierID) 
VALUES 
(GETDATE(), N'Nhập truyện tranh thiếu nhi từ NXB Kim Đồng', 1, 1, 2);
GO

INSERT INTO ImportDetails (ImportID, ProductID, Quantity, ImportPrice) 
VALUES 
(2, 3, 15, 20000),
(2, 4, 12, 25000);
GO

-- 9. Đơn xuất kho cho Nhà sách ABC
INSERT INTO ExportOrders (ExportDate, Note, Status, UserID, CustomerID) 
VALUES 
(GETDATE(), N'Xuất sách và truyện tranh bán lẻ', 1, 2, 1);
GO

INSERT INTO ExportDetails (ExportID, ProductID, Quantity, ExportPrice) 
VALUES 
(1, 1, 2, 120000),
(1, 3, 5, 25000),
(1, 4, 3, 30000);
GO

-- =============================================
-- KIỂM TRA KẾT QUẢ
-- =============================================
SELECT * FROM Products;
SELECT * FROM ImportOrders;
SELECT * FROM ImportDetails;
SELECT * FROM ExportOrders;
SELECT * FROM ExportDetails;
