package view;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExportOrderPanel extends JPanel {

    // Tạo
    private JComboBox<Customer> cboCustomer;
    private JComboBox<Product> cboProduct;
    private JTextField txtQuantity, txtPrice;
    private JTable tableCreate; 
    private DefaultTableModel modelCreate;
    private ArrayList<ExportDetail> details = new ArrayList<>();

    // Lịch sử 
    private JTable tableHistory;
    private DefaultTableModel modelHistory;

    public ExportOrderPanel() {
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();

        // ================== TAB 1: CREATE NEW ORDER ==================
        JPanel pnlCreate = new JPanel(new BorderLayout(10, 10));
        
        JPanel top = new JPanel(new GridLayout(2, 4, 10, 10));
        top.setBorder(BorderFactory.createTitledBorder("Export Order Information"));

        cboCustomer = new JComboBox<>(new CustomerDAO().selectAll().toArray(new Customer[0]));
        cboProduct = new JComboBox<>(new ProductDAO().selectAll().toArray(new Product[0]));
        txtQuantity = new JTextField();
        txtPrice = new JTextField();

        top.add(new JLabel("Customer:")); top.add(cboCustomer);
        top.add(new JLabel("Product:")); top.add(cboProduct);
        top.add(new JLabel("Quantity:")); top.add(txtQuantity);
        top.add(new JLabel("Export Price:")); top.add(txtPrice);

        pnlCreate.add(top, BorderLayout.NORTH);

        // Bảng chi tiết
        modelCreate = new DefaultTableModel(new String[]{"Product", "Quantity", "Price", "Total"}, 0);
        tableCreate = new JTable(modelCreate);
        pnlCreate.add(new JScrollPane(tableCreate), BorderLayout.CENTER);

        // Nút bấm
        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Product");
        JButton btnRemove = new JButton("Remove");
        JButton btnSave = new JButton("Save Export Order");

        bottom.add(btnAdd);
        bottom.add(btnRemove);
        bottom.add(btnSave);
        pnlCreate.add(bottom, BorderLayout.SOUTH);

        // Event
        btnAdd.addActionListener(e -> addProduct());
        btnRemove.addActionListener(e -> removeProduct());
        btnSave.addActionListener(e -> saveOrder());

        // ================== TAB 2: ORDER HISTORY (UPDATED) ==================
        JPanel pnlHistory = new JPanel(new BorderLayout(10, 10));
        
        // [SỬA] Thêm cột "Products" và "Total Qty"
        modelHistory = new DefaultTableModel(new String[]{"Export ID", "Date", "Customer", "Products", "Total Qty", "Total Amount", "Status"}, 0);
        tableHistory = new JTable(modelHistory);
        
        // Chỉnh độ rộng cột cho dễ nhìn
        tableHistory.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tableHistory.getColumnModel().getColumn(3).setPreferredWidth(300); // Products

        JPanel pnlHistoryControl = new JPanel();
        JButton btnRefreshHistory = new JButton("Refresh History");
        btnRefreshHistory.addActionListener(e -> loadHistoryData());
        pnlHistoryControl.add(btnRefreshHistory);

        pnlHistory.add(new JScrollPane(tableHistory), BorderLayout.CENTER);
        pnlHistory.add(pnlHistoryControl, BorderLayout.SOUTH);

        tabbedPane.addTab("Create New Order", pnlCreate);
        tabbedPane.addTab("Order History", pnlHistory);

        add(tabbedPane, BorderLayout.CENTER);
        
        // Tải dữ liệu lịch sử lần đầu
        loadHistoryData();
    }

    
    private void addProduct() {
        try {
            Product p = (Product) cboProduct.getSelectedItem();
            if (p == null) return;
            
            int qty = Integer.parseInt(txtQuantity.getText());
            double price = Double.parseDouble(txtPrice.getText());

            ExportDetail d = new ExportDetail(0, p.getProductID(), qty, price);
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
        
        ExportOrder order = new ExportOrder();
        order.setExportDate(new Timestamp(System.currentTimeMillis()));
        order.setStatus(1);
        order.setUserID(1); 
        order.setCustomerID(((Customer) cboCustomer.getSelectedItem()).getCustomerID());

        ExportOrderDAO dao = new ExportOrderDAO();
        int result = dao.createExportOrder(order, details); 

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Export order saved successfully!");
            modelCreate.setRowCount(0);
            details.clear();
            // Tự động tải lại lịch sử sau khi lưu thành công
            loadHistoryData(); 
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save order (Check Stock?)");
        }
    }

    // [SỬA] Tải data có hiển thị chi tiết sản phẩm
    private void loadHistoryData() {
        modelHistory.setRowCount(0);
        ExportOrderDAO dao = new ExportOrderDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        ProductDAO productDAO = new ProductDAO(); // [MỚI] Cần để lấy tên sản phẩm
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (ExportOrder order : dao.selectAll()) { 
            // Lấy tên khách hàng
            Customer c = customerDAO.selectById(order.getCustomerID()); 
            String customerName = (c != null) ? c.getCustomerName() : "Unknown";

            // Tính tổng tiền
            double total = dao.getTotalAmount(order.getExportID()); 

            // 1. Lấy danh sách chi tiết (Hàm này đã có sẵn trong ExportOrderDAO)
            ArrayList<ExportDetail> detailsList = dao.getExportDetails(order.getExportID());

            // 2. Xây dựng chuỗi hiển thị sản phẩm và tính tổng số lượng
            StringBuilder productInfo = new StringBuilder();
            int totalQty = 0;

            for (ExportDetail d : detailsList) {
                Product p = productDAO.selectById(d.getProductID());
                String pName = (p != null) ? p.getProductName() : "ID:" + d.getProductID();

                if (productInfo.length() > 0) productInfo.append(", ");
                productInfo.append(pName).append(" (").append(d.getQuantity()).append(")");

                totalQty += d.getQuantity();
            }

            modelHistory.addRow(new Object[]{
                    order.getExportID(),
                    sdf.format(order.getExportDate()),
                    customerName,
                    productInfo.toString(), // Cột Sản phẩm
                    totalQty,               // Cột Tổng số lượng
                    String.format("%,.0f", total),
                    order.getStatus() == 1 ? "Completed" : "Pending"
            });
        }
    }
}