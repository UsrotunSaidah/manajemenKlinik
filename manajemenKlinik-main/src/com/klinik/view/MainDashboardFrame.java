package com.klinik.view;

import com.klinik.controller.MainController;
import com.klinik.main.MainApp;
import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.util.UIStyle;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MainDashboardFrame extends JFrame {
    public static final String PAGE_DASHBOARD = "DASHBOARD";
    public static final String PAGE_PASIEN = "PASIEN";
    public static final String PAGE_ANTREAN = "ANTREAN";
    public static final String PAGE_REKAM_MEDIS = "REKAM_MEDIS";
    public static final String PAGE_PEMBAYARAN = "PEMBAYARAN";
    public static final String PAGE_PENGATURAN = "PENGATURAN";

    private final String currentUser;
    private final PasienRepository pasienRepository;
    private final RekamMedisRepository rekamMedisRepository;
    private final MainController mainController;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final Map<String, JButton> sidebarButtons = new LinkedHashMap<>();

    // Label statistik dashboard
    private JLabel lblValTotalPasien;
    private JLabel lblValAntrean;
    private JLabel lblValRekamMedis;
    private JLabel lblValTransaksi;
    private JLabel lblNoteTransaksi;
    private String fullTransaksiNote = "Belum ada transaksi hari ini";
    private RegistrasiPasienPanel registrasiPasienPanel;

    public MainDashboardFrame(String currentUser,
        PasienRepository pasienRepository,
        RekamMedisRepository rekamMedisRepository) {
        this.currentUser = currentUser;
        this.pasienRepository = pasienRepository;
        this.rekamMedisRepository = rekamMedisRepository;
        this.mainController = new MainController(
                this, pasienRepository, rekamMedisRepository, MainApp.transaksiRepository);

        initComponents();
        mainController.navigateTo(PAGE_DASHBOARD);
    }

    private void initComponents() {
        setTitle("Klinik Sehat Bersama - Sistem Manajemen Rekam Medis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        JPanel rootContainer = new JPanel(new BorderLayout());
        rootContainer.setBackground(UIStyle.BACKGROUND_COLOR);

        // 1. Sidebar Kiri
        rootContainer.add(buildSidebarPanel(), BorderLayout.WEST);

        // 2. Area Konten Utama dengan CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIStyle.BACKGROUND_COLOR);

        // Daftarkan halaman bawaan & modul yang sudah selesai
        JPanel dashboardPage = buildDashboardHomePanel();
        addPage(PAGE_DASHBOARD, dashboardPage);
        dashboardPage.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                SwingUtilities.invokeLater(MainDashboardFrame.this::refreshStatistik);
            }
        });
        addPage(PAGE_REKAM_MEDIS, new PencatatanRekamMedisPanel(pasienRepository, rekamMedisRepository));

        // Placeholder untuk modul anggota kelompok lain (dapat diganti via addPage)
        registrasiPasienPanel = new RegistrasiPasienPanel(pasienRepository, rekamMedisRepository);
        addPage(PAGE_PASIEN, registrasiPasienPanel);


        addPage(PAGE_ANTREAN, new KelolaAntreanPanel(MainApp.antreanController, pasienRepository::findAll));

       
        addPage(PAGE_PEMBAYARAN, new PemrosesanResepPanel(currentUser));
        addPage(PAGE_PENGATURAN, createPlaceholderPanel(
                "Pengaturan Sistem Klinik",
                "Informasi akun aktif: " + currentUser + " | Klinik Sehat Bersama (Versi 1.0)."
        ));

        rootContainer.add(mainContentPanel, BorderLayout.CENTER);
        setContentPane(rootContainer);
    }

    private JPanel buildSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, UIStyle.BORDER_COLOR));

        // Header Logo Sidebar
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandPanel.setBackground(UIStyle.PRIMARY_COLOR);
        brandPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblBrand = new JLabel("Klinik Sehat Bersama");
        lblBrand.setFont(UIStyle.FONT_SUBTITLE);
        lblBrand.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel("Petugas: " + currentUser);
        lblRole.setFont(UIStyle.FONT_SMALL);
        lblRole.setForeground(UIStyle.SECONDARY_COLOR);

        brandPanel.add(lblBrand);
        brandPanel.add(lblRole);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Daftar Menu Navigasi
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 0, 6));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(new EmptyBorder(16, 12, 16, 12));

        addMenuButton(menuPanel, PAGE_DASHBOARD, "  Dashboard");
        addMenuButton(menuPanel, PAGE_PASIEN, "  Pasien");
        addMenuButton(menuPanel, PAGE_ANTREAN, "  Antrean");
        addMenuButton(menuPanel, PAGE_REKAM_MEDIS, "  Rekam Medis");
        addMenuButton(menuPanel, PAGE_PEMBAYARAN, "  Resep & Pembayaran");
        addMenuButton(menuPanel, PAGE_PENGATURAN, "  Pengaturan");

        JPanel menuWrapper = new JPanel(new BorderLayout());
        menuWrapper.setBackground(Color.WHITE);
        menuWrapper.add(menuPanel, BorderLayout.NORTH);
        sidebar.add(menuWrapper, BorderLayout.CENTER);

        // Tombol Logout di Bawah
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(12, 12, 16, 12));

        JButton btnLogout = UIStyle.createDangerButton("Logout");
        btnLogout.addActionListener(e -> mainController.handleLogout());
        bottomPanel.add(btnLogout, BorderLayout.CENTER);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addMenuButton(JPanel container, String pageKey, String label) {
        JButton btn = new JButton(label);
        btn.setFont(UIStyle.FONT_BODY_BOLD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(UIStyle.TEXT_PRIMARY);

        btn.addActionListener(e -> mainController.navigateTo(pageKey));
        sidebarButtons.put(pageKey, btn);
        container.add(btn);
    }

    private JPanel buildDashboardHomePanel() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(UIStyle.BACKGROUND_COLOR);
        page.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header Sambutan
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UIStyle.BACKGROUND_COLOR);

        JLabel lblWelcome = new JLabel("Selamat Datang di Sistem Manajemen Rekam Medis Klinik");
        lblWelcome.setFont(UIStyle.FONT_TITLE);
        lblWelcome.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Kelola data pasien, antrean, dan layanan kesehatan dengan mudah.");
        lblDesc.setFont(UIStyle.FONT_BODY);
        lblDesc.setForeground(UIStyle.TEXT_SECONDARY);

        JPanel descriptionRow = new JPanel(new BorderLayout(12, 0));
        descriptionRow.setBackground(UIStyle.BACKGROUND_COLOR);
        descriptionRow.add(lblDesc, BorderLayout.CENTER);

        headerPanel.add(lblWelcome);
        headerPanel.add(descriptionRow);
        page.add(headerPanel, BorderLayout.NORTH);

        // Bagian Tengah: 4 Card Statistik + 4 Card Shortcut
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UIStyle.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Baris 1: 4 Statistik Card
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setBackground(UIStyle.BACKGROUND_COLOR);

        lblValTotalPasien = new JLabel("0");
        lblValAntrean = new JLabel("0");
        lblValRekamMedis = new JLabel("0");
        lblValTransaksi = new JLabel("0");

        statsGrid.add(createStatCard("Total Pasien", lblValTotalPasien,
                new JLabel("Terdaftar")));
        statsGrid.add(createStatCard("Antrean Hari Ini", lblValAntrean,
                new JLabel("Menunggu & sedang diperiksa")));
        statsGrid.add(createStatCard("Rekam Medis Hari Ini", lblValRekamMedis,
                new JLabel("Tersimpan hari ini")));
        lblNoteTransaksi = new JLabel("Belum ada transaksi hari ini");
        lblNoteTransaksi.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fitTransaksiNote();
            }
        });
        statsGrid.add(createStatCard("Total Transaksi", lblValTransaksi, lblNoteTransaksi));

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        centerPanel.add(statsGrid, gbc);

        // Label Section Shortcut
        JLabel lblShortcutTitle = new JLabel("Akses Cepat Layanan Klinik");
        lblShortcutTitle.setFont(UIStyle.FONT_SUBTITLE);
        lblShortcutTitle.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        centerPanel.add(lblShortcutTitle, gbc);

        // Baris 2: 4 Shortcut Card
        JPanel shortcutGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        shortcutGrid.setBackground(UIStyle.BACKGROUND_COLOR);

        shortcutGrid.add(createShortcutCard(
                "Registrasi Pasien",
                "Daftarkan pasien baru atau periksa data identitas pasien klinik.",
                "Buka Halaman Pasien",
                () -> mainController.navigateTo(PAGE_PASIEN)
        ));
        shortcutGrid.add(createShortcutCard(
                "Kelola Antrean",
                "Atur nomor antrean pemeriksaan poli dan pemanggilan pasien.",
                "Buka Halaman Antrean",
                () -> mainController.navigateTo(PAGE_ANTREAN)
        ));
        shortcutGrid.add(createShortcutCard(
                "Pencatatan Rekam Medis",
                "Catat diagnosis dokter, tindakan medis, serta riwayat pemeriksaan.",
                "Catat Rekam Medis",
                () -> mainController.navigateTo(PAGE_REKAM_MEDIS)
        ));
        shortcutGrid.add(createShortcutCard(
                "Pemrosesan Resep & Pembayaran",
                "Kelola resep obat dari pemeriksaan dan selesaikan transaksi.",
                "Buka Pembayaran",
                () -> mainController.navigateTo(PAGE_PEMBAYARAN)
        ));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(shortcutGrid, gbc);

        JPanel topAlignWrapper = new JPanel(new BorderLayout());
        topAlignWrapper.setBackground(UIStyle.BACKGROUND_COLOR);
        topAlignWrapper.add(centerPanel, BorderLayout.NORTH);

        page.add(topAlignWrapper, BorderLayout.CENTER);
        return page;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, JLabel noteLabel) {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_BODY_BOLD);
        lblTitle.setForeground(UIStyle.TEXT_SECONDARY);

        valueLabel.setFont(UIStyle.FONT_STAT_NUMBER);
        valueLabel.setForeground(UIStyle.PRIMARY_COLOR);

        noteLabel.setFont(UIStyle.FONT_SMALL);
        noteLabel.setForeground(UIStyle.TEXT_SECONDARY);
        noteLabel.setToolTipText(noteLabel.getText());

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(noteLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createShortcutCard(String title, String desc, String btnText, Runnable action) {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setBackground(UIStyle.CARD_COLOR);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_SUBTITLE);
        lblTitle.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(UIStyle.FONT_BODY);
        lblDesc.setForeground(UIStyle.TEXT_SECONDARY);

        textPanel.add(lblTitle);
        textPanel.add(lblDesc);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrapper.setBackground(UIStyle.CARD_COLOR);
        JButton btnGo = UIStyle.createSecondaryButton(btnText);
        btnGo.addActionListener(e -> action.run());
        btnWrapper.add(btnGo);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(btnWrapper, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIStyle.BACKGROUND_COLOR);
        wrapper.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new GridLayout(3, 1, 0, 10));
        card.setPreferredSize(new Dimension(560, 180));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.PRIMARY_COLOR);

        JLabel lblDesc = new JLabel("<html><div style='text-align:center;'>" + description + "</div></html>", SwingConstants.CENTER);
        lblDesc.setFont(UIStyle.FONT_BODY);
        lblDesc.setForeground(UIStyle.TEXT_SECONDARY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UIStyle.CARD_COLOR);
        JButton btnBack = UIStyle.createPrimaryButton("Kembali ke Dashboard");
        btnBack.addActionListener(e -> mainController.navigateTo(PAGE_DASHBOARD));
        btnPanel.add(btnBack);

        card.add(lblTitle);
        card.add(lblDesc);
        card.add(btnPanel);

        wrapper.add(card);
        return wrapper;
    }

    /**
     * Method publik untuk integrasi panel anggota kelompok lain ke CardLayout.
     */
    public void addPage(String pageName, JPanel panel) {
        mainContentPanel.add(panel, pageName);
    }

    /**
     * Method publik untuk berpindah halaman dan menyorot menu sidebar aktif.
     */
    public void showPage(String pageName) {
        cardLayout.show(mainContentPanel, pageName);
        for (Map.Entry<String, JButton> entry : sidebarButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(pageName)) {
                btn.setBackground(UIStyle.SECONDARY_COLOR);
                btn.setForeground(UIStyle.PRIMARY_COLOR);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(UIStyle.TEXT_PRIMARY);
            }
        }
    }

    public void refreshStatistik() {
        mainController.refreshStatistikDashboard();
    }

    public void updateStatistikCards(
            String totalPasien,
            String antrean,
            String rekamMedis,
            int totalTransaksi,
            long pendapatanHariIni) {
        lblValTotalPasien.setText(totalPasien);
        lblValAntrean.setText(antrean);
        lblValRekamMedis.setText(rekamMedis);
        lblValTransaksi.setText(String.valueOf(totalTransaksi));
        String transaksiNote = totalTransaksi == 0
                ? "Belum ada transaksi hari ini"
                : "Pendapatan hari ini: " + formatRupiah(pendapatanHariIni);
        setTransaksiNote(transaksiNote, transaksiNote);
    }

    public void updateTransaksiStatistikError(String detail) {
        lblValTransaksi.setText("!");
        setTransaksiNote("Data transaksi tidak dapat dimuat", detail);
    }

    private void setTransaksiNote(String note, String tooltip) {
        fullTransaksiNote = note;
        lblNoteTransaksi.setToolTipText(tooltip);
        fitTransaksiNote();
    }

    private void fitTransaksiNote() {
        if (lblNoteTransaksi == null || lblNoteTransaksi.getWidth() <= 0) {
            return;
        }
        FontMetrics metrics = lblNoteTransaksi.getFontMetrics(lblNoteTransaksi.getFont());
        String displayText = fullTransaksiNote;
        while (displayText.length() > 3
                && metrics.stringWidth(displayText) > lblNoteTransaksi.getWidth()) {
            displayText = displayText.substring(0, displayText.length() - 4) + "...";
        }
        lblNoteTransaksi.setText(displayText);
    }

    private String formatRupiah(long value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        format.setGroupingUsed(true);
        return "Rp " + format.format(value);
    }
}
