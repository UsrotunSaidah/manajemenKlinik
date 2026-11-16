package com.klinik.controller;

import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.view.LoginFrame;
import com.klinik.view.MainDashboardFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    private final LoginFrame loginFrame;
    private final Map<String, String> demoAccounts;

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.demoAccounts = new HashMap<>();
        // Akun demo terpisah dari komponen View
        this.demoAccounts.put("admin", "admin123");
    }

    public void handleLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    loginFrame,
                    "Username tidak boleh kosong.",
                    "Validasi Login",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    loginFrame,
                    "Password tidak boleh kosong.",
                    "Validasi Login",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String cleanUsername = username.trim();
        if (demoAccounts.containsKey(cleanUsername) && demoAccounts.get(cleanUsername).equals(password)) {
            PasienRepository pasienRepository = new PasienRepository();
            RekamMedisRepository rekamMedisRepository = new RekamMedisRepository();

            MainDashboardFrame dashboardFrame = new MainDashboardFrame(cleanUsername, pasienRepository, rekamMedisRepository);
            dashboardFrame.setVisible(true);
            loginFrame.dispose();
        } else {
            // Jangan tampilkan password dalam pesan error
            JOptionPane.showMessageDialog(
                    loginFrame,
                    "Username atau password yang Anda masukkan salah.",
                    "Autentikasi Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
