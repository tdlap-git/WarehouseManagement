package view;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Customer Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Bảng
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Address"}, 0
        );
        table = new JTable(model);
        loadData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút
        JPanel panelButtons = new JPanel();

        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        panelButtons.add(btnRefresh);
        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);

        add(panelButtons, BorderLayout.SOUTH);

        // Sự kiện
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Add Customer dialog can be implemented later")
        );
        btnEdit.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Edit Customer dialog can be implemented later")
        );
        btnDelete.addActionListener(e -> deleteCustomer());
    }

    // Tải dữ liệu từ database
    private void loadData() {
        model.setRowCount(0);
        CustomerDAO dao = new CustomerDAO();
        List<Customer> list = dao.selectAll();

        for (Customer c : list) {
            model.addRow(new Object[]{
                    c.getCustomerID(),
                    c.getCustomerName(),
                    c.getPhone(),
                    c.getAddress()
            });
        }
    }

    // Xóa
    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this customer?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new CustomerDAO().delete(id);
            loadData();
        }
    }
}
