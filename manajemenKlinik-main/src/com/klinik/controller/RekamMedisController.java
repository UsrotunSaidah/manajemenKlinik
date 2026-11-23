package com.klinik.controller;

import com.klinik.exception.PasienTidakDitemukanException;
import com.klinik.model.Pasien;
import com.klinik.model.RekamMedis;
import com.klinik.repository.PasienRepository;
import com.klinik.repository.RekamMedisRepository;
import com.klinik.view.PencatatanRekamMedisPanel;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RekamMedisController {
    private final PencatatanRekamMedisPanel view;
    private final PasienRepository pasienRepository;
    private final RekamMedisRepository rekamMedisRepository;
    private Pasien pasienTerpilih;

    public RekamMedisController(PencatatanRekamMedisPanel view,
                                PasienRepository pasienRepository,
                                RekamMedisRepository rekamMedisRepository) {
        this.view = view;
        this.pasienRepository = pasienRepository;
        this.rekamMedisRepository = rekamMedisRepository;
    }

    public void cariPasien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Masukkan Nama atau NIK pasien terlebih dahulu.",
                    "Pencarian Pasien",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        List<Pasien> hasil = pasienRepository.searchByNamaOrNik(keyword.trim());
        if (hasil.isEmpty()) {
            pasienTerpilih = null;
            view.clearIdentitasPasien();
            JOptionPane.showMessageDialog(
                    view,
                    "Pasien dengan kata kunci '" + keyword.trim() + "' tidak ditemukan.",
                    "Pasien Tidak Ditemukan",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (hasil.size() == 1) {
            pilihPasien(hasil.get(0));
        } else {
            Pasien[] pilihan = hasil.toArray(new Pasien[0]);
            Pasien dipilih = (Pasien) JOptionPane.showInputDialog(
                    view,
                    "Ditemukan " + hasil.size() + " pasien. Pilih pasien:",
                    "Pilih Pasien",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    pilihan,
                    pilihan[0]
            );
            if (dipilih != null) {
                pilihPasien(dipilih);
            }
        }
    }

    public void pilihPasien(Pasien pasien) {
        this.pasienTerpilih = pasien;
        view.tampilkanIdentitasPasien(pasien);
        muatRiwayatPasien(pasien.getId());
    }

    public void muatRiwayatPasien(String idPasien) {
        List<RekamMedis> riwayat = rekamMedisRepository.findByPasienId(idPasien);
        view.tampilkanRiwayatMedis(riwayat);
    }

    public void simpanRekamMedis(String diagnosis, String tindakan, String catatan) {
        // Validasi pasien sudah dipilih & terdaftar di repository
        if (pasienTerpilih == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Silakan cari dan pilih pasien terdaftar terlebih dahulu.",
                    "Validasi Data Pasien",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            pasienRepository.findById(pasienTerpilih.getId());
        } catch (PasienTidakDitemukanException e) {
            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Pasien Tidak Valid",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Diagnosis wajib diisi.",
                    "Validasi Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (tindakan == null || tindakan.trim().isEmpty() || tindakan.startsWith("--")) {
            JOptionPane.showMessageDialog(
                    view,
                    "Silakan pilih tindakan medis.",
                    "Validasi Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (catatan == null || catatan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Catatan pemeriksaan wajib diisi.",
                    "Validasi Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idRm = rekamMedisRepository.generateNextId();
        String waktuSekarang = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        RekamMedis rekamMedisBaru = new RekamMedis(
                idRm,
                pasienTerpilih.getId(),
                waktuSekarang,
                diagnosis.trim(),
                tindakan.trim(),
                catatan.trim()
        );

        try {
            rekamMedisRepository.save(rekamMedisBaru);
            JOptionPane.showMessageDialog(
                    view,
                    "Rekam medis (" + idRm + ") untuk pasien " + pasienTerpilih.getNama() + " berhasil disimpan.",
                    "Penyimpanan Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );
            view.resetFormInput();
            muatRiwayatPasien(pasienTerpilih.getId());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Terjadi kesalahan saat menyimpan data ke file: " + e.getMessage(),
                    "Gagal Menyimpan",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public Pasien getPasienTerpilih() {
        return pasienTerpilih;
    }
}
