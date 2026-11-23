package com.klinik.main;

import com.klinik.controller.AntreanController;
import com.klinik.repository.PasienRepository;
import com.klinik.repository.TransaksiRepository;
import com.klinik.view.KelolaAntreanPanel;
import com.klinik.view.LoginFrame;

import javax.swing.*;

public class MainApp {

    /** Dipakai bersama oleh semua panel supaya data konsisten. */
    public static final PasienRepository pasienRepository = new PasienRepository();
    public static final AntreanController antreanController = new AntreanController();
    public static final TransaksiRepository transaksiRepository = new TransaksiRepository();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    /** Panggil dari LoginFrame (setelah login berhasil) atau dari MainDashboardFrame. */
    public static void bukaKelolaAntrean() {
        JFrame f = new JFrame("Kelola Antrean Pasien");
        f.setContentPane(new KelolaAntreanPanel(antreanController, pasienRepository::findAll));
        f.setSize(800, 380);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }
}
