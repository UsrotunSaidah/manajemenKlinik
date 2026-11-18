package com.klinik.controller;

import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.view.LoginFrame;
import com.klinik.main.MainApp;
import com.klinik.view.MainDashboardFrame;

import javax.swing.*;

public class MainController {
    private final MainDashboardFrame dashboardFrame;
    private final PasienRepository pasienRepository;
    private final RekamMedisRepository rekamMedisRepository;

    public MainController(MainDashboardFrame dashboardFrame,
        PasienRepository pasienRepository,
        RekamMedisRepository rekamMedisRepository) {
        this.dashboardFrame = dashboardFrame;
        this.pasienRepository = pasienRepository;
        this.rekamMedisRepository = rekamMedisRepository;
    }

    public void navigateTo(String pageName) {
        if (MainDashboardFrame.PAGE_DASHBOARD.equals(pageName)) {
            refreshStatistikDashboard();
        }
        dashboardFrame.showPage(pageName);
    }

    public void refreshStatistikDashboard() {
        int totalPasien = pasienRepository.countAll();
        int rekamMedisHariIni = rekamMedisRepository.countHariIni();

        // Antrean diambil dari modul antrean (AntreanController milik MainApp)
        int antreanHariIni = MainApp.antreanController.getDaftarAktif().size();

        // Modul pembayaran dikerjakan anggota kelompok lain (nilai awal sementara)
        String totalTransaksi = "0 (Belum aktif)";

        dashboardFrame.updateStatistikCards(
                String.valueOf(totalPasien),
                String.valueOf(antreanHariIni),
                String.valueOf(rekamMedisHariIni),
                totalTransaksi
        );
    }

    public void handleLogout() {
        int konfirmasi = JOptionPane.showConfirmDialog(
                dashboardFrame,
                "Apakah Anda yakin ingin keluar dari sesi ini?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dashboardFrame.dispose();
        }
    }
}
