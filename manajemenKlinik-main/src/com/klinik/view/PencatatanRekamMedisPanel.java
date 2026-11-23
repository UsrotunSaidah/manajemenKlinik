package com.klinik.view;

import com.klinik.controller.RekamMedisController;
import com.klinik.model.Pasien;
import com.klinik.model.RekamMedis;
import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.util.UIStyle;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class PencatatanRekamMedisPanel extends JPanel {
    private final RekamMedisController controller;

    // Komponen Pencarian & Identitas Pasien
    private JTextField txtCariPasien;
    private JButton btnCariPasien;
    private JLabel lblIdPasienVal;
    private JLabel lblNamaPasienVal;
    private JLabel lblNikPasienVal;
    private JLabel lblJkTglLahirVal;
    private DefaultTableModel riwayatTableModel;
    private JTable tblRiwayatMedis;

    // Komponen Form Rekam Medis
    private JTextArea txtDiagnosis;
    private JComboBox<String> cmbTindakan;
    private JTextArea txtCatatan;
    private JButton btnSimpan;
    private JButton btnReset;

    public PencatatanRekamMedisPanel(PasienRepository pasienRepository, RekamMedisRepository rekamMedisRepository) {
        this.controller = new RekamMedisController(this, pasienRepository, rekamMedisRepository);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(UIStyle.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UIStyle.BACKGROUND_COLOR);

        JLabel lblTitle = new JLabel("Pencatatan Rekam Medis Pasien");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Cari pasien terdaftar, tinjau riwayat pemeriksaan, dan catat hasil pemeriksaan medis baru.");
        lblSubtitle.setFont(UIStyle.FONT_BODY);
        lblSubtitle.setForeground(UIStyle.TEXT_SECONDARY);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        JPanel splitContent = new JPanel(new GridLayout(1, 2, 20, 0));
        splitContent.setBackground(UIStyle.BACKGROUND_COLOR);
        splitContent.add(buildLeftPasienPanel());
        splitContent.add(buildRightFormPanel());
        add(splitContent, BorderLayout.CENTER);
    }

    private JPanel buildLeftPasienPanel() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 14));

        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBackground(UIStyle.CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSection = new JLabel("Data & Identitas Pasien");
        lblSection.setFont(UIStyle.FONT_SUBTITLE);
        lblSection.setForeground(UIStyle.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        topSection.add(lblSection, gbc);

        txtCariPasien = new JTextField();
        txtCariPasien.setToolTipText("Masukkan Nama atau NIK pasien (contoh: Budi atau 337201...)");
        UIStyle.styleTextField(txtCariPasien);
        txtCariPasien.addActionListener(e -> controller.cariPasien(txtCariPasien.getText()));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 8);
        topSection.add(txtCariPasien, gbc);

        btnCariPasien = UIStyle.createPrimaryButton("Cari Pasien");
        btnCariPasien.addActionListener(e -> controller.cariPasien(txtCariPasien.getText()));
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 14, 0);
        topSection.add(btnCariPasien, gbc);

        lblIdPasienVal = createValueLabel("-");
        lblNamaPasienVal = createValueLabel("-");
        lblNikPasienVal = createValueLabel("-");
        lblJkTglLahirVal = createValueLabel("-");

        JPanel infoBox = new JPanel(new GridLayout(4, 2, 8, 6));
        infoBox.setBackground(UIStyle.SECONDARY_COLOR);
        infoBox.setBorder(new EmptyBorder(12, 14, 12, 14));
        infoBox.add(createKeyLabel("ID Pasien:"));
        infoBox.add(lblIdPasienVal);
        infoBox.add(createKeyLabel("Nama Lengkap:"));
        infoBox.add(lblNamaPasienVal);
        infoBox.add(createKeyLabel("NIK:"));
        infoBox.add(lblNikPasienVal);
        infoBox.add(createKeyLabel("Gender / Tgl Lahir:"));
        infoBox.add(lblJkTglLahirVal);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 4, 0);
        topSection.add(infoBox, gbc);
        card.add(topSection, BorderLayout.NORTH);

        JPanel historySection = new JPanel(new BorderLayout(0, 8));
        historySection.setBackground(UIStyle.CARD_COLOR);
        JLabel lblRiwayat = new JLabel("Riwayat Pemeriksaan Medis");
        lblRiwayat.setFont(UIStyle.FONT_SECTION);
        lblRiwayat.setForeground(UIStyle.TEXT_PRIMARY);
        historySection.add(lblRiwayat, BorderLayout.NORTH);

        String[] kolom = {"ID RM", "Tanggal", "Diagnosis", "Tindakan", "Catatan"};
        riwayatTableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRiwayatMedis = new JTable(riwayatTableModel);
        UIStyle.styleTable(tblRiwayatMedis);

        JScrollPane scrollTable = new JScrollPane(tblRiwayatMedis);
        scrollTable.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        historySection.add(scrollTable, BorderLayout.CENTER);
        card.add(historySection, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRightFormPanel() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 16));

        JLabel lblFormTitle = new JLabel("Form Input Rekam Medis Baru");
        lblFormTitle.setFont(UIStyle.FONT_SUBTITLE);
        lblFormTitle.setForeground(UIStyle.PRIMARY_COLOR);
        card.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(UIStyle.CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Diagnosis
        JLabel lblDiagnosis = new JLabel("Diagnosis Dokter *");
        lblDiagnosis.setFont(UIStyle.FONT_BODY_BOLD);
        lblDiagnosis.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        formGrid.add(lblDiagnosis, gbc);

        txtDiagnosis = new JTextArea(4, 20);
        UIStyle.styleTextArea(txtDiagnosis);
        JScrollPane scrollDiag = new JScrollPane(txtDiagnosis);
        scrollDiag.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        gbc.gridy = 1;
        gbc.weighty = 0.35;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 14, 0);
        formGrid.add(scrollDiag, gbc);

        // 2. Tindakan Medis
        JLabel lblTindakan = new JLabel("Tindakan Medis *");
        lblTindakan.setFont(UIStyle.FONT_BODY_BOLD);
        lblTindakan.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        formGrid.add(lblTindakan, gbc);

        String[] daftarTindakan = {
                "-- Pilih Tindakan Medis --",
                "Pemeriksaan Umum",
                "Pemberian Obat",
                "Istirahat Total",
                "Pemeriksaan Laboratorium",
                "Rujukan"
        };
        cmbTindakan = new JComboBox<>(daftarTindakan);
        cmbTindakan.setFont(UIStyle.FONT_BODY);
        cmbTindakan.setBackground(Color.WHITE);
        cmbTindakan.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 14, 0);
        formGrid.add(cmbTindakan, gbc);

        // 3. Catatan Tambahan
        JLabel lblCatatan = new JLabel("Catatan & Anjuran Medis *");
        lblCatatan.setFont(UIStyle.FONT_BODY_BOLD);
        lblCatatan.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        formGrid.add(lblCatatan, gbc);

        txtCatatan = new JTextArea(5, 20);
        UIStyle.styleTextArea(txtCatatan);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        gbc.gridy = 5;
        gbc.weighty = 0.45;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        formGrid.add(scrollCatatan, gbc);

        card.add(formGrid, BorderLayout.CENTER);

        // Tombol Aksi
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonBar.setBackground(UIStyle.CARD_COLOR);

        btnReset = UIStyle.createSecondaryButton("Reset Form");
        btnReset.addActionListener(e -> resetFormInput());

        btnSimpan = UIStyle.createPrimaryButton("Simpan Rekam Medis");
        btnSimpan.addActionListener(e -> controller.simpanRekamMedis(
                txtDiagnosis.getText(),
                (String) cmbTindakan.getSelectedItem(),
                txtCatatan.getText()
        ));

        buttonBar.add(btnReset);
        buttonBar.add(btnSimpan);
        card.add(buttonBar, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createKeyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIStyle.FONT_BODY);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIStyle.FONT_BODY_BOLD);
        lbl.setForeground(UIStyle.TEXT_PRIMARY);
        return lbl;
    }

    public void tampilkanIdentitasPasien(Pasien pasien) {
        lblIdPasienVal.setText(pasien.getId());
        lblNamaPasienVal.setText(pasien.getNama());
        lblNikPasienVal.setText(pasien.getNik());
        lblJkTglLahirVal.setText(pasien.getJenisKelamin() + " / " + pasien.getTanggalLahir());
    }

    public void clearIdentitasPasien() {
        lblIdPasienVal.setText("-");
        lblNamaPasienVal.setText("-");
        lblNikPasienVal.setText("-");
        lblJkTglLahirVal.setText("-");
        riwayatTableModel.setRowCount(0);
    }

    public void tampilkanRiwayatMedis(List<RekamMedis> daftarRiwayat) {
        riwayatTableModel.setRowCount(0);
        for (RekamMedis rm : daftarRiwayat) {
            riwayatTableModel.addRow(new Object[]{
                    rm.getIdRekamMedis(),
                    rm.getTanggalPemeriksaan(),
                    rm.getDiagnosis(),
                    rm.getTindakan(),
                    rm.getCatatan()
            });
        }
    }

    public void resetFormInput() {
        txtDiagnosis.setText("");
        cmbTindakan.setSelectedIndex(0);
        txtCatatan.setText("");
    }
}
