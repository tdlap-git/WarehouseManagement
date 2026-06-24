package view;

import dao.CategoryDAO;   
import dao.ProductDAO;
import dao.SupplierDAO;
import dao.WarehouseDAO;
import model.Category; 
import model.Product;
import model.Supplier;
import model.Warehouse; 

import javax.swing.*;
import java.awt.*;

public class ProductDialog extends JDialog {

    private JTextField txtName, txtUnit, txtPrice, txtQuantity;
    private JComboBox<Supplier> cboSupplier;
    private JComboBox<Category> cboCategory;  
    private JComboBox<Warehouse> cboWarehouse;
    private Product product;
    private ProductPanel parent;

    public ProductDialog(Product p, ProductPanel parent) {
        this.product = p;
        this.parent = parent;

        setTitle(p == null ? "Add Product" : "Edit Product");
        setSize(400, 500); 
        setLocationRelativeTo(parent);
        setModal(true);

        
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tên sản phẩm
        panel.add(new JLabel("Name:"));
        txtName = new JTextField(); 
        panel.add(txtName);

        // Danh mục 
        panel.add(new JLabel("Category:"));
        cboCategory = new JComboBox<>(new CategoryDAO().selectAll().toArray(new Category[0]));
        panel.add(cboCategory);

        // Nhà cung cấp
        panel.add(new JLabel("Supplier:"));
        cboSupplier = new JComboBox<>(new SupplierDAO().selectAll().toArray(new Supplier[0]));
        panel.add(cboSupplier);

        // Kho hàng 
        panel.add(new JLabel("Warehouse:"));
        cboWarehouse = new JComboBox<>(new WarehouseDAO().selectAll().toArray(new Warehouse[0]));
        panel.add(cboWarehouse);

        // Đơn vị tính
        panel.add(new JLabel("Unit:"));
        txtUnit = new JTextField(); 
        panel.add(txtUnit);

        // Giá bán
        panel.add(new JLabel("Price:"));
        txtPrice = new JTextField(); 
        panel.add(txtPrice);

        // Số lượng tồn đầu
        panel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        panel.add(txtQuantity);

        // Nút
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        panel.add(btnSave);
        panel.add(btnCancel);

        add(panel);

        // Load dữ liệu cũ nếu edit
        if (p != null) {
            txtName.setText(p.getProductName());
            txtUnit.setText(p.getUnit());
            txtPrice.setText(String.valueOf(p.getPrice()));
            txtQuantity.setText(String.valueOf(p.getQuantity()));

            // Danh mục
            for (int i = 0; i < cboCategory.getItemCount(); i++) {
                if (cboCategory.getItemAt(i).getCategoryID() == p.getCategoryID()) {
                    cboCategory.setSelectedIndex(i);
                    break;
                }
            }

            // Nhà cung cấp
            for (int i = 0; i < cboSupplier.getItemCount(); i++) {
                if (cboSupplier.getItemAt(i).getSupplierID() == p.getSupplierID()) {
                    cboSupplier.setSelectedIndex(i);
                    break;
                }
            }

            // Kho
            for (int i = 0; i < cboWarehouse.getItemCount(); i++) {
                if (cboWarehouse.getItemAt(i).getWarehouseID() == p.getWarehouseID()) {
                    cboWarehouse.setSelectedIndex(i);
                    break;
                }
            }
        }

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void save() {
        ProductDAO dao = new ProductDAO();
        if (product == null) product = new Product();

       
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name is required!");
            return;
        }

        product.setProductName(txtName.getText());
        product.setUnit(txtUnit.getText());

        try {
            product.setPrice(Double.parseDouble(txtPrice.getText()));
            product.setQuantity(Integer.parseInt(txtQuantity.getText()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers!");
            return;
        }

        
        Category selectedCat = (Category) cboCategory.getSelectedItem();
        if (selectedCat != null) {
            product.setCategoryID(selectedCat.getCategoryID());
        }

        
        Supplier selectedSup = (Supplier) cboSupplier.getSelectedItem();
        if (selectedSup != null) {
            product.setSupplierID(selectedSup.getSupplierID());
        }

        
        Warehouse selectedWh = (Warehouse) cboWarehouse.getSelectedItem();
        if (selectedWh != null) {
            product.setWarehouseID(selectedWh.getWarehouseID());
        }

        // Gọi DAO để lưu
        if (product.getProductID() == 0) {
            dao.insert(product);
        } else {
            dao.update(product);
        }

        parent.loadData();
        dispose();
    }
}