USE WarehouseManagement;
GO

DROP TABLE IF EXISTS ImportDetails;
DROP TABLE IF EXISTS ExportDetails;
DROP TABLE IF EXISTS ImportOrders;
DROP TABLE IF EXISTS ExportOrders;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Suppliers;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Warehouse;

-- Kho
CREATE TABLE Warehouse (
    WarehouseID int PRIMARY KEY IDENTITY(1,1),
    Address nvarchar(100) NOT NULL,
    WarehouseName nvarchar(50) NOT NULL
);

-- Danh mục hàng
CREATE TABLE Categories (
    CategoryID int PRIMARY KEY IDENTITY(1,1),
    CategoryName nvarchar(50) NOT NULL
);

-- Nhà cung cấp
CREATE TABLE Suppliers (
    SupplierID int PRIMARY KEY IDENTITY(1,1),
    SupplierName nvarchar(100) NOT NULL,
    Phone varchar(20),
    Address nvarchar(200)
);

-- Khách hàng
CREATE TABLE Customers (
    CustomerID int PRIMARY KEY IDENTITY(1,1),
    CustomerName nvarchar(100) NOT NULL,
    Phone varchar(20),
    Address nvarchar(200)
);

-- Người dùng
CREATE TABLE Users (
    UserID int PRIMARY KEY IDENTITY(1,1),
    UserName nvarchar(50) NOT NULL UNIQUE,
    Password varchar(100) NOT NULL,
    FullName nvarchar(100) NOT NULL,
    Role nvarchar(20) DEFAULT 'Staff',
    Status int DEFAULT 1, -- 1: Đang làm, 0: Nghỉ việc
    WarehouseID int,
    FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID)
);

-- Sản phẩm
CREATE TABLE Products (
    ProductID int PRIMARY KEY IDENTITY(1,1),
    ProductName nvarchar(100) NOT NULL,
    CategoryID int FOREIGN KEY REFERENCES Categories(CategoryID),
    Unit nvarchar(20),
    Price decimal(18, 2) DEFAULT 0,
    Quantity int DEFAULT 0,
    WarehouseID int FOREIGN KEY REFERENCES Warehouse(WarehouseID),
    SupplierID int FOREIGN KEY REFERENCES Suppliers(SupplierID) 
);

-- Nhập kho
CREATE TABLE ImportOrders (
    ImportID int PRIMARY KEY IDENTITY(1,1),
    ImportDate datetime DEFAULT GETDATE(),
    Note nvarchar(200),
    Status int DEFAULT 0,
    UserID int FOREIGN KEY REFERENCES Users(UserID),
    SupplierID int FOREIGN KEY REFERENCES Suppliers(SupplierID)
);

-- Chi tiết nhập kho
CREATE TABLE ImportDetails (
    ImportID int FOREIGN KEY REFERENCES ImportOrders(ImportID),
    ProductID int FOREIGN KEY REFERENCES Products(ProductID),
    Quantity int CHECK (Quantity > 0),
    ImportPrice decimal(18, 2) CHECK (ImportPrice >= 0),
    PRIMARY KEY (ImportID, ProductID)
);

-- Xuất kho
CREATE TABLE ExportOrders (
    ExportID int PRIMARY KEY IDENTITY(1,1),
    ExportDate datetime DEFAULT GETDATE(),
    Note nvarchar(200),
    Status int DEFAULT 0,
    UserID int FOREIGN KEY REFERENCES Users(UserID),
    CustomerID int FOREIGN KEY REFERENCES Customers(CustomerID)
);

-- Chi tiết xuất kho
CREATE TABLE ExportDetails (
    ExportID int FOREIGN KEY REFERENCES ExportOrders(ExportID),
    ProductID int FOREIGN KEY REFERENCES Products(ProductID),
    Quantity int CHECK (Quantity > 0),
    ExportPrice decimal(18, 2) CHECK (ExportPrice >= 0),
    PRIMARY KEY (ExportID, ProductID)
);