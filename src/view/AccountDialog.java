package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class AccountDialog extends JDialog {

    private JTextField txtUser, txtFullname;
    private JPasswordField txtPass;
    private JComboBox<String> cboRole, cboStatus;
    private User user;
    private AccountPanel parent;

    public AccountDialog(User user, AccountPanel parent) {
        this.user = user;
        this.parent = parent;

        setTitle(user == null ? "Add New User" : "Edit User");
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setModal(true);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        panel.add(new JLabel("Username:"));
        txtUser = new JTextField();
        panel.add(txtUser);

        panel.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        panel.add(txtPass);

        panel.add(new JLabel("Full Name:"));
        txtFullname = new JTextField();
        panel.add(txtFullname);

        panel.add(new JLabel("Role:"));
        cboRole = new JComboBox<>(new String[]{"Staff", "Admin"});
        panel.add(cboRole);

        panel.add(new JLabel("Status:"));
        cboStatus = new JComboBox<>(new String[]{"Active", "Locked"});
        panel.add(cboStatus);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        panel.add(btnSave);
        panel.add(btnCancel);

        add(panel);

        // Tải dữ liệu nếu trong edit mode
        if (user != null) {
            txtUser.setText(user.getUserName());
            txtUser.setEditable(false); // Không cho sửa Username
            txtPass.setText(user.getPassword());
            txtFullname.setText(user.getFullName());
            cboRole.setSelectedItem(user.getRole());
            cboStatus.setSelectedIndex(user.getStatus() == 1 ? 0 : 1);
        }

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void save() {
    	
        if (txtUser.getText().isEmpty() || txtPass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Username and Password are required!");
            return;
        }

        UserDAO dao = new UserDAO();
        if (user == null) user = new User();

        user.setUserName(txtUser.getText());
        user.setPassword(new String(txtPass.getPassword()));
        user.setFullName(txtFullname.getText());
        user.setRole(cboRole.getSelectedItem().toString());
        
        // Trạng thái: Active=1, Locked=0
        user.setStatus(cboStatus.getSelectedItem().equals("Active") ? 1 : 0);
        user.setWarehouseID(1); 

        if (user.getUserID() == 0) {
            dao.insert(user);
        } else {
            dao.update(user);
        }

        parent.loadData();
        dispose();
    }
}