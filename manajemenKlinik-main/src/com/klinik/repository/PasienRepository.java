package com.klinik.repository;

import com.klinik.exception.PasienTidakDitemukanException;
import com.klinik.model.Pasien;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PasienRepository {
    private static final String FILE_PATH = "data/pasien.csv";
    private static final String SEQUENCE_FILE_PATH = "data/pasien_sequence.txt";
    private static final String DELIMITER = ";";

    public PasienRepository() {
        inisialisasiFileDanDataContoh();
    }

    private void inisialisasiFileDanDataContoh() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            List<Pasien> dataContoh = new ArrayList<>();
            dataContoh.add(new Pasien("PSN-001", "Budi Santoso", "3372011505850001", "15-05-1985", "Laki-laki", "Jl. Slamet Riyadi No. 12, Surakarta"));
            dataContoh.add(new Pasien("PSN-002", "Siti Aminah", "3372024808920002", "08-08-1992", "Perempuan", "Jl. Adi Sucipto No. 45, Surakarta"));
            dataContoh.add(new Pasien("PSN-003", "Andi Pratama", "3372031211980003", "12-11-1998", "Laki-laki", "Jl. Dr. Moewardi No. 88, Surakarta"));
            dataContoh.add(new Pasien("PSN-004", "Dewi Lestari", "3372046503950004", "25-03-1995", "Perempuan", "Jl. Ir. Sutami No. 30, Surakarta"));

            for (Pasien p : dataContoh) {
                try {
                    save(p);
                } catch (IOException e) {
                    System.err.println("Gagal menulis data contoh pasien: " + e.getMessage());
                }
            }
        }
    }

    public synchronized void save(Pasien pasien) throws IOException {
        int nomorPasien = nomorDariId(pasien.getId());
        if (nomorPasien > readNomorTerakhir()) {
            simpanNomorTerakhir(nomorPasien);
        }

        File file = new File(FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            String line = String.join(DELIMITER,
                    bersihkanDelimiter(pasien.getId()),
                    bersihkanDelimiter(pasien.getNama()),
                    bersihkanDelimiter(pasien.getNik()),
                    bersihkanDelimiter(pasien.getTanggalLahir()),
                    bersihkanDelimiter(pasien.getJenisKelamin()),
                    bersihkanDelimiter(pasien.getAlamat())
            );
            writer.write(line);
            writer.newLine();
        }
    }

    public void deleteByNik(String nik) throws IOException, PasienTidakDitemukanException {
        List<Pasien> semuaPasien = findAll();
        Pasien pasienDihapus = null;

        for (Pasien pasien : semuaPasien) {
            if (pasien.getNik().equals(nik.trim())) {
                pasienDihapus = pasien;
                break;
            }
        }

        if (pasienDihapus == null) {
            throw new PasienTidakDitemukanException("Pasien dengan NIK '" + nik + "' tidak ditemukan.");
        }

        semuaPasien.remove(pasienDihapus);
        tulisSemua(semuaPasien);
    }

    public synchronized void deleteAll() throws IOException {
        tulisSemua(new ArrayList<>());
        simpanNomorTerakhir(0);
    }

    public synchronized String generateNextId() throws IOException {
        int nomorTerbesar = 0;
        for (Pasien pasien : findAll()) {
            nomorTerbesar = Math.max(nomorTerbesar, nomorDariId(pasien.getId()));
        }
        nomorTerbesar = Math.max(nomorTerbesar, readNomorTerakhir());
        return "PSN-" + String.format("%03d", nomorTerbesar + 1);
    }

    private int nomorDariId(String id) {
        if (id == null || !id.startsWith("PSN-")) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(4));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private int readNomorTerakhir() throws IOException {
        File file = new File(SEQUENCE_FILE_PATH);
        if (!file.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String value = reader.readLine();
            if (value == null || value.trim().isEmpty()) {
                return 0;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ex) {
                throw new IOException("Nomor urut pasien tidak valid.", ex);
            }
        }
    }

    private void simpanNomorTerakhir(int nomor) throws IOException {
        File file = new File(SEQUENCE_FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()
                && !file.getParentFile().mkdirs()) {
            throw new IOException("Folder data pasien tidak dapat dibuat.");
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            writer.write(String.valueOf(nomor));
            writer.newLine();
        }
    }

    private void tulisSemua(List<Pasien> daftarPasien) throws IOException {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            for (Pasien pasien : daftarPasien) {
                String line = String.join(DELIMITER,
                        bersihkanDelimiter(pasien.getId()),
                        bersihkanDelimiter(pasien.getNama()),
                        bersihkanDelimiter(pasien.getNik()),
                        bersihkanDelimiter(pasien.getTanggalLahir()),
                        bersihkanDelimiter(pasien.getJenisKelamin()),
                        bersihkanDelimiter(pasien.getAlamat())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public List<Pasien> findAll() {
        List<Pasien> daftarPasien = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return daftarPasien;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    Pasien p = new Pasien(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim()
                    );
                    daftarPasien.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Gagal membaca file pasien: " + e.getMessage());
        }

        return daftarPasien;
    }

    public Pasien findById(String id) throws PasienTidakDitemukanException {
        for (Pasien p : findAll()) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        throw new PasienTidakDitemukanException("Pasien dengan ID '" + id + "' tidak ditemukan.");
    }

    public Pasien findByNik(String nik) throws PasienTidakDitemukanException {
        for (Pasien p : findAll()) {
            if (p.getNik().equals(nik.trim())) {
                return p;
            }
        }
        throw new PasienTidakDitemukanException("Pasien dengan NIK '" + nik + "' tidak ditemukan.");
    }

    public List<Pasien> searchByNamaOrNik(String keyword) {
        List<Pasien> hasil = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String keyLower = keyword.trim().toLowerCase();
        for (Pasien p : findAll()) {
            if (p.getNama().toLowerCase().contains(keyLower) || p.getNik().contains(keyLower)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    public int countAll() {
        return findAll().size();
    }

    private String bersihkanDelimiter(String teks) {
        if (teks == null) return "";
        return teks.replace(DELIMITER, ",").replace("\n", " ").replace("\r", "");
    }
}
