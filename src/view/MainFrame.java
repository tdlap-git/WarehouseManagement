package view;

import model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(User user) {
        setTitle("Warehouse Management System - " + user.getFullName());
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tab = new JTabbedPane();

        // Các tab chức năng
        tab.add("Inventory", new InventoryPanel());
        tab.add("Products", new ProductPanel());
        tab.add("Import Orders", new ImportOrderPanel()); 
        tab.add("Export Orders", new ExportOrderPanel());
        tab.add("Customers", new CustomerPanel());
        tab.add("Suppliers", new SupplierPanel());

        // Chỉ Admin mới thấy tab Quản lý tài khoản
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("Admin")) {
            tab.add("Accounts", new AccountPanel());
        }

        add(tab, BorderLayout.CENTER);
        
        setVisible(true);
    }
}