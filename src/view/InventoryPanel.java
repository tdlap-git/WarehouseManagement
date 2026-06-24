package view;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class InventoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    
    // Hiển thị thống kê
    private JLabel lblTotalProducts;
    private JLabel lblTotalQuantity;
    private JLabel lblTotalInventoryValue;
    private JLabel lblLowStockWarning;

    public InventoryPanel() {
        setLayout(new BorderLayout(10, 10));

        // Phần thống kê
        JPanel pnlStats = new JPanel(new GridLayout(1, 4, 15, 0)); 
        pnlStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        
        lblTotalProducts = createStatCard("Total Products", Color.BLUE);
        lblTotalQuantity = createStatCard("Total Quantity", new Color(0, 100, 0)); 
        lblTotalInventoryValue = createStatCard("Inventory Value", new Color(102, 0, 102)); 
        lblLowStockWarning = createStatCard("Low Stock Items", Color.RED);

        pnlStats.add(lblTotalProducts);
        pnlStats.add(lblTotalQuantity);
        pnlStats.add(lblTotalInventoryValue);
        pnlStats.add(lblLowStockWarning);

        add(pnlStats, BorderLayout.NORTH);

        // Bảng
        
        model = new DefaultTableModel(
                new String[]{"ID", "Product Name", "Unit", "Qty", "Price", "Total Value", "Status"}, 0
        ) {
            // Không cho sửa trực tiếp trên bảng
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("LOW STOCK".equals(status)) {
                    setForeground(Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(0, 100, 0)); // Green
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút refresh
        JPanel pnlBottom = new JPanel();
        JButton btnRefresh = new JButton("Refresh Statistics");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> loadData());
        pnlBottom.add(btnRefresh);
        add(pnlBottom, BorderLayout.SOUTH);

        // Tải dữ liệu lần đầu
        loadData();
    }

    
    private JLabel createStatCard(String title, Color color) {
        JLabel lbl = new JLabel("<html><center>" + title + "<br/><font size=5>0</font></center></html>", SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        lbl.setForeground(color);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        return lbl;
    }

    // Tính toán và hiển thị
    private void loadData() {
        model.setRowCount(0);
        ProductDAO dao = new ProductDAO();
        ArrayList<Product> list = dao.selectAll();

        int totalProds = 0;
        int totalQty = 0;
        double totalVal = 0;
        int lowStockCount = 0;

        DecimalFormat df = new DecimalFormat("#,### VND");

        for (Product p : list) {
            // Tính toán thống kê
            totalProds++;
            totalQty += p.getQuantity();
            double rowValue = p.getQuantity() * p.getPrice(); // Giá trị = Số lượng x Đơn giá
            totalVal += rowValue;

            String status = "OK";
            if (p.getQuantity() < 10) {
                status = "LOW STOCK";
                lowStockCount++;
            }

            // Thêm vào bảng
            model.addRow(new Object[]{
                    p.getProductID(),
                    p.getProductName(),
                    p.getUnit(),
                    p.getQuantity(),
                    df.format(p.getPrice()),
                    df.format(rowValue), 
                    status
            });
        }

        // Cập nhật các thẻ thống kê phía trên
        updateStatLabel(lblTotalProducts, "Total Products", totalProds);
        updateStatLabel(lblTotalQuantity, "Total Quantity", totalQty);
        
        
        lblTotalInventoryValue.setText("<html><center>Inventory Value<br/><font size=5>" + df.format(totalVal) + "</font></center></html>");
        
        lblLowStockWarning.setText("<html><center>Low Stock Items<br/><font size=5>" + lowStockCount + "</font></center></html>");
        if (lowStockCount > 0) {
            lblLowStockWarning.setBackground(new Color(255, 230, 230)); 
        } else {
            lblLowStockWarning.setBackground(Color.WHITE);
        }
    }

    private void updateStatLabel(JLabel lbl, String title, int value) {
        lbl.setText("<html><center>" + title + "<br/><font size=5>" + value + "</font></center></html>");
    }
}