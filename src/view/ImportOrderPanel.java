package view;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImportOrderPanel extends JPanel {

    // Tạo phiếu 
    private JComboBox<Supplier> cboSupplier;
    private JComboBox<Product> cboProduct;
    private JTextField txtQuantity, txtPrice;
    private JTable tableCreate;
    private DefaultTableModel modelCreate;
    private ArrayList<ImportDetail> details = new ArrayList<>();

    // Lịch sử 
    private JTable tableHistory;
    private DefaultTableModel modelHistory;

    public ImportOrderPanel() {
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tạo phiếu nhập
        JPanel pnlCreate = new JPanel(new BorderLayout(10, 10));

        // Phần nhập liệu 
        JPanel top = new JPanel(new GridLayout(2, 4, 10, 10));
        top.setBorder(BorderFactory.createTitledBorder("Import Order Information"));

        // Load danh sách Supplier
        cboSupplier = new JComboBox<>(new SupplierDAO().selectAll().toArray(new Supplier[0]));
        
        
        cboProduct = new JComboBox<>(); 
        
        txtQuantity = new JTextField();
        txtPrice = new JTextField();

        top.add(new JLabel("Supplier:")); top.add(cboSupplier);
        top.add(new JLabel("Product:")); top.add(cboProduct);
        top.add(new JLabel("Quantity:")); top.add(txtQuantity);
        top.add(new JLabel("Import Price:")); top.add(txtPrice);

        pnlCreate.add(top, BorderLayout.NORTH);

        // Bảng chi tiết đang nhập
        modelCreate = new DefaultTableModel(new String[]{"Product", "Quantity", "Price", "Total"}, 0);
        tableCreate = new JTable(modelCreate);
        pnlCreate.add(new JScrollPane(tableCreate), BorderLayout.CENTER);

        // Nút
        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Product");
        JButton btnRemove = new JButton("Remove");
        JButton btnSave = new JButton("Save Import Order");

        bottom.add(btnAdd);
        bottom.add(btnRemove);
        bottom.add(btnSave);
        pnlCreate.add(bottom, BorderLayout.SOUTH);

        // Sự kiện
        btnAdd.addActionListener(e -> addProduct());
        btnRemove.addActionListener(e -> removeProduct());
        btnSave.addActionListener(e -> saveOrder());
        cboSupplier.addActionListener(e -> loadProductBySupplier());

        // Lịch sử phiếu nhập
        JPanel pnlHistory = new JPanel(new BorderLayout(10, 10));
        
        modelHistory = new DefaultTableModel(new String[]{"Import ID", "Date", "Supplier", "Products", "Total Qty", "Status"}, 0);
        tableHistory = new JTable(modelHistory);
        
        tableHistory.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tableHistory.getColumnModel().getColumn(3).setPreferredWidth(300); // Products

        JPanel pnlHistoryControl = new JPanel();
        JButton btnRefreshHistory = new JButton("Refresh History");
        btnRefreshHistory.addActionListener(e -> loadHistoryData());
        pnlHistoryControl.add(btnRefreshHistory);

        pnlHistory.add(new JScrollPane(tableHistory), BorderLayout.CENTER);
        pnlHistory.add(pnlHistoryControl, BorderLayout.SOUTH);

        // Thêm các tab vào giao diện chính
        tabbedPane.addTab("Create New Order", pnlCreate);
        tabbedPane.addTab("Order History", pnlHistory);

        add(tabbedPane, BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadHistoryData();
        loadProductBySupplier(); 
    }

    // Load sản phẩm theo Nhà cung cấp đang chọn
    private void loadProductBySupplier() {
        Supplier selectedSupplier = (Supplier) cboSupplier.getSelectedItem();
        cboProduct.removeAllItems(); 

        if (selectedSupplier != null) {
            ProductDAO productDAO = new ProductDAO();
            ArrayList<Product> list = productDAO.selectBySupplierId(selectedSupplier.getSupplierID());
            for (Product p : list) {
                cboProduct.addItem(p);
            }
        }
    }

    private void addProduct() {
        try {
            Product p = (Product) cboProduct.getSelectedItem();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "No product selected!");
                return;
            }

            int qty = Integer.parseInt(txtQuantity.getText());
            double price = Double.parseDouble(txtPrice.getText());

            ImportDetail d = new ImportDetail(0, p.getProductID(), qty, price);
            details.add(d);
            
            modelCreate.addRow(new Object[]{
                    p.getProductName(), qty, price, qty * price
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid number!");
        }
    }

    private void removeProduct() {
        int row = tableCreate.getSelectedRow();
        if (row >= 0) {
            details.remove(row);
            modelCreate.removeRow(row);
        }
    }

    private void saveOrder() {
        if (details.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add products first!");
            return;
        }

        ImportOrder order = new ImportOrder();
        order.setImportDate(new Timestamp(System.currentTimeMillis()));
        order.setStatus(1); 
        order.setUserID(1); 
        order.setSupplierID(((Supplier) cboSupplier.getSelectedItem()).getSupplierID());

        ImportOrderDAO dao = new ImportOrderDAO();
        int result = dao.createImportOrder(order, details);

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Import order saved successfully");
            modelCreate.setRowCount(0);
            details.clear();
            loadHistoryData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save order");
        }
    }

    // Load dữ liệu lịch sử
    private void loadHistoryData() {
        modelHistory.setRowCount(0);
        ImportOrderDAO dao = new ImportOrderDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        ProductDAO productDAO = new ProductDAO(); 
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (ImportOrder order : dao.selectAll()) {
            Supplier s = supplierDAO.selectById(order.getSupplierID());
            String supplierName = (s != null) ? s.getSupplierName() : "Unknown";

            // Lấy danh sách chi tiết của đơn nhập
            ArrayList<ImportDetail> detailsList = dao.getImportDetails(order.getImportID());
            
            // Hiển thị: "Laptop (5), Iphone (10)"
            StringBuilder productInfo = new StringBuilder();
            int totalQty = 0;
            
            for (ImportDetail d : detailsList) {
                Product p = productDAO.selectById(d.getProductID());
                String pName = (p != null) ? p.getProductName() : "ID:" + d.getProductID();
                
                if (productInfo.length() > 0) productInfo.append(", ");
                productInfo.append(pName).append(" (").append(d.getQuantity()).append(")");
                
                totalQty += d.getQuantity();
            }

            modelHistory.addRow(new Object[]{
                    order.getImportID(),
                    sdf.format(order.getImportDate()),
                    supplierName,
                    productInfo.toString(), // Cột Sản phẩm
                    totalQty,               // Cột Tổng số lượng
                    order.getStatus() == 1 ? "Completed" : "Pending"
            });
        }
    }
}