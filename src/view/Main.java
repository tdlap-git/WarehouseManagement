package view;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện
        try {
            
            FlatLightLaf.setup();
            
            
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo FlatLaf. Đang dùng giao diện mặc định.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame(); // Mở màn hình đăng nhập
        });
    }
}