package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public AccountPanel() {
        setLayout(new BorderLayout(10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Employee Account Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(lblTitle, BorderLayout.NORTH);

        // Bảng
        model = new DefaultTableModel(
                new String[]{"ID", "Username", "Full Name", "Role", "Status"}, 0
        );
        table = new JTable(model);
        loadData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút
        JPanel panelButtons = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnDelete = new JButton("Delete");

        panelButtons.add(btnRefresh);
        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);

        add(panelButtons, BorderLayout.SOUTH);

        // Events
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> new AccountDialog(null, this));
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
    }

    public void loadData() {
        model.setRowCount(0);
        UserDAO dao = new UserDAO();
        List<User> list = dao.selectAll();

        for (User u : list) {
            String status = (u.getStatus() == 1) ? "Active" : "Locked";
            model.addRow(new Object[]{
                    u.getUserID(),
                    u.getUserName(),
                    u.getFullName(),
                    u.getRole(),
                    status
            });
        }
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        User user = new UserDAO().selectById(id);
        new AccountDialog(user, this);
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        int id = (int) model.getValueAt(row, 0);
        
       
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new UserDAO().delete(id);
            loadData();
        }
    }
}