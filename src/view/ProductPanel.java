package view;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    
   
    private JTextField txtSearch;
    private JButton btnSearch;

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));

        // Thanh tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSearch.add(new JLabel("Search Product:"));
        
        txtSearch = new JTextField(20); // Ô nhập liệu dài 20 ký tự
        pnlSearch.add(txtSearch);
        
        btnSearch = new JButton("Search");
        pnlSearch.add(btnSearch);
        
        add(pnlSearch, BorderLayout.NORTH); 

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Unit", "Price", "Quantity"}, 0
        );

        table = new JTable(model);
        loadData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        buttons.add(btnRefresh);
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);

        add(buttons, BorderLayout.SOUTH);

        // Refresh thì xóa ô tìm kiếm và load lại tất cả
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        
        btnAdd.addActionListener(e -> new ProductDialog(null, this));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        
        // Bấm nút Tìm kiếm
        btnSearch.addActionListener(e -> searchData());
    }

    void loadData() {
        model.setRowCount(0);
        ProductDAO dao = new ProductDAO();
        DecimalFormat df = new DecimalFormat("#,### VND");

        for (Product p : dao.selectAll()) {
            model.addRow(new Object[]{
                    p.getProductID(), 
                    p.getProductName(),
                    p.getUnit(), 
                    df.format(p.getPrice()), 
                    p.getQuantity()
            });
        }
    }
    
    // Xử lý tìm kiếm
    private void searchData() {
        String keyword = txtSearch.getText().trim();
        // Nếu ô tìm kiếm rỗng thì load lại tất cả
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        model.setRowCount(0);
        ProductDAO dao = new ProductDAO();
        DecimalFormat df = new DecimalFormat("#,### VND");

        
        for (Product p : dao.searchByKeyword(keyword)) {
            model.addRow(new Object[]{
                    p.getProductID(), 
                    p.getProductName(),
                    p.getUnit(), 
                    df.format(p.getPrice()), 
                    p.getQuantity()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        int id = (int) model.getValueAt(row, 0);
        Product p = new ProductDAO().selectById(id);
        new ProductDialog(p, this);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this,
                "Delete this product?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new ProductDAO().delete(id);
            loadData();
        }
    }
}