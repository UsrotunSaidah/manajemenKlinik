package com.klinik.view;

import com.klinik.model.Pasien;
import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.exception.PasienTidakDitemukanException;
import com.klinik.util.UIStyle;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class RegistrasiPasienPanel extends JPanel {
    private final PasienRepository repository;
    private final RekamMedisRepository rekamMedisRepository;
    private final JTextField txtNama = new JTextField();
    private final JTextField txtNik = new JTextField();
    private final JTextField txtTanggal = new JTextField();
    private final JTextField txtAlamat = new JTextField();
    private final JTextField txtCari = new JTextField();
    private final JRadioButton rbLaki = new JRadioButton("Laki-laki", true);
    private final JRadioButton rbPerempuan = new JRadioButton("Perempuan");
    private final DefaultTableModel tableModel;
    private JTable table;

    public RegistrasiPasienPanel(PasienRepository repository, RekamMedisRepository rekamMedisRepository) {
        this.repository = repository;
        this.rekamMedisRepository = rekamMedisRepository;
        tableModel = new DefaultTableModel(
                new Object[]{"No", "Nama", "NIK", "Tanggal Lahir", "Jenis Kelamin", "Alamat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initComponents();
        muatPasien(repository.findAll());
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIStyle.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.BACKGROUND_COLOR);
        JLabel title = new JLabel("Registrasi Pasien");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 16));
        content.setBackground(UIStyle.BACKGROUND_COLOR);
        content.add(buildFormCard());
        content.add(buildDataCard());
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildFormCard() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 8);

        txtNama.setText("Budi Santoso");
        txtNik.setText("3375031234560001");
        txtTanggal.setText("15-05-2000");

        UIStyle.styleTextField(txtNama);
        UIStyle.styleTextField(txtNik);
        UIStyle.styleTextField(txtTanggal);
        UIStyle.styleTextField(txtAlamat);

        addFormRow(form, gbc, 0, "Nama Lengkap", txtNama);
        addFormRow(form, gbc, 1, "NIK", txtNik);
        addFormRow(form, gbc, 2, "Tanggal Lahir", txtTanggal);

        JLabel lblKelamin = new JLabel("Jenis Kelamin");
        lblKelamin.setFont(UIStyle.FONT_BODY);
        lblKelamin.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.35;
        form.add(lblKelamin, gbc);

        ButtonGroup gender = new ButtonGroup();
        gender.add(rbLaki);
        gender.add(rbPerempuan);
        rbLaki.setFont(UIStyle.FONT_BODY);
        rbPerempuan.setFont(UIStyle.FONT_BODY);
        rbLaki.setBackground(UIStyle.CARD_COLOR);
        rbPerempuan.setBackground(UIStyle.CARD_COLOR);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        genderPanel.setBackground(UIStyle.CARD_COLOR);
        genderPanel.add(rbLaki);
        genderPanel.add(rbPerempuan);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.65;
        form.add(genderPanel, gbc);
        addFormRow(form, gbc, 4, "Alamat", txtAlamat);

        card.add(form, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(UIStyle.CARD_COLOR);
        JButton btnSimpan = UIStyle.createPrimaryButton("Simpan");
        JButton btnReset = UIStyle.createSecondaryButton("Reset");
        btnSimpan.addActionListener(e -> simpanPasien());
        btnReset.addActionListener(e -> resetForm());
        actions.add(btnSimpan);
        actions.add(btnReset);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        JLabel key = new JLabel(label);
        key.setFont(UIStyle.FONT_BODY);
        key.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        form.add(key, gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private JPanel buildDataCard() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(8, 10));

        JLabel title = new JLabel("Data Pasien");
        title.setFont(UIStyle.FONT_SUBTITLE);
        title.setForeground(UIStyle.PRIMARY_COLOR);
        card.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(UIStyle.CARD_COLOR);
        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setBackground(UIStyle.CARD_COLOR);
        UIStyle.styleTextField(txtCari);
        txtCari.setToolTipText("Cari nama atau NIK...");
        JButton btnCari = UIStyle.createPrimaryButton("Cari");
        btnCari.addActionListener(e -> muatPasien(repository.searchByNamaOrNik(txtCari.getText())));
        txtCari.addActionListener(e -> muatPasien(repository.searchByNamaOrNik(txtCari.getText())));
        search.add(txtCari, BorderLayout.CENTER);
        search.add(btnCari, BorderLayout.EAST);
        center.add(search, BorderLayout.NORTH);

        table = new JTable(tableModel);
        UIStyle.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setBackground(UIStyle.CARD_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(155);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(220);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        scroll.getViewport().setBackground(UIStyle.CARD_COLOR);
        center.add(scroll, BorderLayout.CENTER);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableActions.setBackground(UIStyle.CARD_COLOR);
        JButton btnHapus = UIStyle.createDangerButton("Hapus Pasien");
        btnHapus.addActionListener(e -> hapusPasienTerpilih());
        tableActions.add(btnHapus);
        center.add(tableActions, BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private void muatPasien(List<Pasien> daftar) {
        tableModel.setRowCount(0);
        int no = 1;
        for (Pasien pasien : daftar) {
            tableModel.addRow(new Object[]{
                    no++, pasien.getNama(), pasien.getNik(), pasien.getTanggalLahir(),
                    pasien.getJenisKelamin(), pasien.getAlamat()
            });
        }
    }

    public void refreshDataPasien() {
        muatPasien(repository.findAll());
    }

    private void simpanPasien() {
        if (txtNama.getText().trim().isEmpty() || txtNik.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan NIK wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String id = repository.generateNextId();
            Pasien pasien = new Pasien(
                    id,
                    txtNama.getText().trim(),
                    txtNik.getText().trim(),
                    txtTanggal.getText().trim(),
                    rbLaki.isSelected() ? "Laki-laki" : "Perempuan",
                    txtAlamat.getText().trim());
            repository.save(pasien);
            muatPasien(repository.findAll());
            JOptionPane.showMessageDialog(this, "Data pasien berhasil disimpan.");
            resetForm();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusPasienTerpilih() {
        int baris = table.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih pasien pada tabel yang ingin dihapus.",
                    "Pilihan Tidak Ada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = String.valueOf(tableModel.getValueAt(baris, 1));
        String nik = String.valueOf(tableModel.getValueAt(baris, 2));

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Hapus pasien " + nama + " (NIK: " + nik + ")?\n"
                        + "Riwayat rekam medis pasien ini juga akan dihapus.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Pasien pasien = repository.findByNik(nik);
            rekamMedisRepository.deleteByPasienId(pasien.getId());
            repository.deleteByNik(nik);
            muatPasien(repository.findAll());
            JOptionPane.showMessageDialog(this,
                    "Data pasien dan riwayat rekam medis berhasil dihapus.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (PasienTidakDitemukanException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Data Tidak Ditemukan",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtNik.setText("");
        txtTanggal.setText("");
        txtAlamat.setText("");
        rbLaki.setSelected(true);
        txtNama.requestFocus();
    }
}