package com.klinik.controller;

import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.repository.TransaksiRepository;
import com.klinik.view.LoginFrame;
import com.klinik.main.MainApp;
import com.klinik.view.MainDashboardFrame;

import java.io.IOException;
import javax.swing.*;

public class MainController {
    private final MainDashboardFrame dashboardFrame;
    private final PasienRepository pasienRepository;
    private final RekamMedisRepository rekamMedisRepository;
    private final TransaksiRepository transaksiRepository;

    public MainController(MainDashboardFrame dashboardFrame,
        PasienRepository pasienRepository,
        RekamMedisRepository rekamMedisRepository,
        TransaksiRepository transaksiRepository) {
        this.dashboardFrame = dashboardFrame;
        this.pasienRepository = pasienRepository;
        this.rekamMedisRepository = rekamMedisRepository;
        this.transaksiRepository = transaksiRepository;
    }

    public void navigateTo(String pageName) {
        if (MainDashboardFrame.PAGE_DASHBOARD.equals(pageName)) {
            dashboardFrame.refreshStatistik();
        }
        dashboardFrame.showPage(pageName);
    }

    public void refreshStatistikDashboard() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshStatistikDashboard);
            return;
        }

        int totalPasien = pasienRepository.countAll();
        int rekamMedisHariIni = rekamMedisRepository.countHariIni();

        // Antrean diambil dari modul antrean (AntreanController milik MainApp)
        int antreanHariIni = MainApp.antreanController.getDaftarAktif().size();
        try {
            int totalTransaksi = transaksiRepository.hitungTransaksiHariIni();
            long pendapatanHariIni = transaksiRepository.hitungPendapatanHariIni();
            dashboardFrame.updateStatistikCards(
                    String.valueOf(totalPasien),
                    String.valueOf(antreanHariIni),
                    String.valueOf(rekamMedisHariIni),
                    totalTransaksi,
                    pendapatanHariIni
            );
        } catch (IOException ex) {
            dashboardFrame.updateStatistikCards(
                    String.valueOf(totalPasien),
                    String.valueOf(antreanHariIni),
                    String.valueOf(rekamMedisHariIni),
                    0,
                    0
            );
            dashboardFrame.updateTransaksiStatistikError(ex.getMessage());
        }
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
