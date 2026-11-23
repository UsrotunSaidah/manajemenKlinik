package com.klinik.view;

import com.klinik.controller.AntreanController;
import com.klinik.exception.AntreanKosongException;
import com.klinik.model.Antrean;
import com.klinik.model.Pasien;
import com.klinik.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class KelolaAntreanPanel extends JPanel {

    private final AntreanController controller;
    private final Supplier<List<Pasien>> sumberPasien;

    private JLabel lblNomorSaatIni;
    private JLabel lblNamaSaatIni;
    private JLabel lblJumlahMenunggu;
    private final AntreanTableModel tableModel = new AntreanTableModel();
    private JTable tblAntrean;

    public KelolaAntreanPanel(AntreanController controller, Supplier<List<Pasien>> sumberPasien) {
        this.controller = controller;
        this.sumberPasien = sumberPasien;
        initComponents();

        controller.tambahListener(this::refresh); // View ikut update otomatis
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(UIStyle.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header Halaman
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UIStyle.BACKGROUND_COLOR);

        JLabel lblTitle = new JLabel("Kelola Antrean Pasien");
        lblTitle.setFont(UIStyle.FONT_TITLE);
        lblTitle.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Tambahkan pasien terdaftar ke antrean dan panggil pasien berikutnya untuk diperiksa.");
        lblSubtitle.setFont(UIStyle.FONT_BODY);
        lblSubtitle.setForeground(UIStyle.TEXT_SECONDARY);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Kiri: kontrol antrean, Kanan: daftar antrean
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(UIStyle.BACKGROUND_COLOR);

        JPanel left = buildLeftControlPanel();
        left.setPreferredSize(new Dimension(340, 0));
        content.add(left, BorderLayout.WEST);
        content.add(buildRightListPanel(), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildLeftControlPanel() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 14));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(UIStyle.CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul section
        JLabel lblSection = new JLabel("Nomor Antrean Saat Ini");
        lblSection.setFont(UIStyle.FONT_SUBTITLE);
        lblSection.setForeground(UIStyle.PRIMARY_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        top.add(lblSection, gbc);

        // Box nomor saat ini
        lblNomorSaatIni = new JLabel("-", SwingConstants.CENTER);
        lblNomorSaatIni.setFont(UIStyle.FONT_STAT_NUMBER.deriveFont(Font.BOLD, 54f));
        lblNomorSaatIni.setForeground(UIStyle.PRIMARY_COLOR);

        lblNamaSaatIni = new JLabel(" ", SwingConstants.CENTER);
        lblNamaSaatIni.setFont(UIStyle.FONT_BODY_BOLD);
        lblNamaSaatIni.setForeground(UIStyle.TEXT_PRIMARY);

        lblJumlahMenunggu = new JLabel(" ", SwingConstants.CENTER);
        lblJumlahMenunggu.setFont(UIStyle.FONT_BODY);
        lblJumlahMenunggu.setForeground(UIStyle.TEXT_SECONDARY);

        JPanel infoBox = new JPanel(new GridLayout(3, 1, 0, 4));
        infoBox.setBackground(UIStyle.SECONDARY_COLOR);
        infoBox.setBorder(new EmptyBorder(16, 14, 16, 14));
        infoBox.add(lblNomorSaatIni);
        infoBox.add(lblNamaSaatIni);
        infoBox.add(lblJumlahMenunggu);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        top.add(infoBox, gbc);

        JButton btnPanggil = UIStyle.createPrimaryButton("Panggil Berikutnya");
        btnPanggil.addActionListener(e -> panggilBerikutnya());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        top.add(btnPanggil, gbc);

        JButton btnSelesai = UIStyle.createSecondaryButton("Selesai Diperiksa");
        btnSelesai.addActionListener(e -> selesaikanPemeriksaan());
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 22, 0);
        top.add(btnSelesai, gbc);

        // Section tambah pasien
        JLabel lblTambah = new JLabel("Pasien Baru");
        lblTambah.setFont(UIStyle.FONT_SECTION);
        lblTambah.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        top.add(lblTambah, gbc);

        JButton btnTambah = UIStyle.createSecondaryButton("+ Tambah ke Antrean");
        btnTambah.addActionListener(e -> tambahPasien());
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        top.add(btnTambah, gbc);

        card.add(top, BorderLayout.NORTH);
        return card;
    }

    private JPanel buildRightListPanel() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JLabel lblDaftar = new JLabel("Daftar Antrean");
        lblDaftar.setFont(UIStyle.FONT_SUBTITLE);
        lblDaftar.setForeground(UIStyle.PRIMARY_COLOR);
        card.add(lblDaftar, BorderLayout.NORTH);

        tblAntrean = new JTable(tableModel);
        UIStyle.styleTable(tblAntrean);

        // Bungkus renderer bawaan UIStyle: hanya menambah sorotan baris yang sedang diperiksa
        TableCellRenderer dasar = tblAntrean.getDefaultRenderer(Object.class);
        tblAntrean.setDefaultRenderer(Object.class, (t, v, selected, focus, row, col) -> {
            Component c = dasar.getTableCellRendererComponent(t, v, selected, focus, row, col);
            boolean aktif = tableModel.getAt(row).getStatus() == Antrean.Status.DALAM_PEMERIKSAAN;
            if (aktif && !selected) {
                c.setBackground(UIStyle.SECONDARY_COLOR);
                if (col == 2) {
                    c.setForeground(UIStyle.PRIMARY_COLOR);
                    c.setFont(UIStyle.FONT_BODY_BOLD);
                }
            }
            return c;
        });

        JScrollPane scroll = new JScrollPane(tblAntrean);
        scroll.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void tambahPasien() {
        List<Pasien> daftar = sumberPasien.get();
        if (daftar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada pasien terdaftar.",
                    "Tambah ke Antrean", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Pasien[] pilihan = daftar.toArray(new Pasien[0]);
        Pasien dipilih = (Pasien) JOptionPane.showInputDialog(
                this, "Pilih pasien yang akan masuk antrean:", "Tambah ke Antrean",
                JOptionPane.QUESTION_MESSAGE, null, pilihan, pilihan[0]);
        if (dipilih != null) {
            controller.tambahKeAntrean(dipilih);
        }
    }

    private void panggilBerikutnya() {
        try {
            Antrean dipanggil = controller.panggilBerikutnya();
            new PemanggilanAntreanDialog(SwingUtilities.getWindowAncestor(this), dipanggil).setVisible(true);
        } catch (AntreanKosongException ex) {
            String pesan = ex.getMessage();
            if (controller.getSedangDiperiksa() != null) {
                pesan += "\nKlik \"Selesai Diperiksa\" untuk menyelesaikan pasien saat ini.";
            }
            JOptionPane.showMessageDialog(this, pesan,
                    "Antrean Kosong", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void selesaikanPemeriksaan() {
        if (!controller.selesaikanPemeriksaan()) {
            JOptionPane.showMessageDialog(this, "Tidak ada pasien yang sedang diperiksa.",
                    "Selesai Diperiksa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refresh() {
        Antrean saatIni = controller.getSedangDiperiksa();
        lblNomorSaatIni.setText(controller.getNomorSaatIni());
        lblNamaSaatIni.setText(saatIni == null ? " " : saatIni.getPasien().getNama());
        lblJumlahMenunggu.setText(controller.getJumlahMenunggu() + " pasien menunggu");
        tableModel.setData(controller.getDaftarAktif());
    }

    // ---- TableModel ----
    private static class AntreanTableModel extends AbstractTableModel {
        private final String[] kolom = {"No Antrean", "Nama Pasien", "Status"};
        private List<Antrean> data = List.of();

        void setData(List<Antrean> d) { data = d; fireTableDataChanged(); }
        Antrean getAt(int row) { return data.get(row); }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return kolom.length; }
        @Override public String getColumnName(int c) { return kolom[c]; }
        @Override public Object getValueAt(int r, int c) {
            Antrean a = data.get(r);
            return switch (c) {
                case 0 -> a.getNomor();
                case 1 -> a.getPasien().getNama();
                default -> a.getStatus();
            };
        }
    }
}
