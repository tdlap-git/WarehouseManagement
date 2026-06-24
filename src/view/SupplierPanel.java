package view;

import dao.SupplierDAO;
import model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierPanel extends JPanel {

    public SupplierPanel() {
        setLayout(new BorderLayout(10,10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID","Name","Phone","Address"},0
        );
        JTable table = new JTable(model);

        for (Supplier s : new SupplierDAO().selectAll()) {
            model.addRow(new Object[]{
                    s.getSupplierID(), s.getSupplierName(),
                    s.getPhone(), s.getAddress()
            });
        }

        add(new JLabel("Supplier Management", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}