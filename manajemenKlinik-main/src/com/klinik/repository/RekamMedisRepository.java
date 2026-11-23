package com.klinik.repository;

import com.klinik.model.RekamMedis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RekamMedisRepository {
    private static final String FILE_PATH = "data/rekam_medis.csv";
    private static final String DELIMITER = ";";

    public RekamMedisRepository() {
        inisialisasiFile();
    }

    private void inisialisasiFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Gagal membuat file rekam_medis.csv: " + e.getMessage());
            }
        }
    }

    public void save(RekamMedis rm) throws IOException {
        inisialisasiFile();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH, true), StandardCharsets.UTF_8))) {
            String line = String.join(DELIMITER,
                    bersihkanDelimiter(rm.getIdRekamMedis()),
                    bersihkanDelimiter(rm.getIdPasien()),
                    bersihkanDelimiter(rm.getTanggalPemeriksaan()),
                    bersihkanDelimiter(rm.getDiagnosis()),
                    bersihkanDelimiter(rm.getTindakan()),
                    bersihkanDelimiter(rm.getCatatan())
            );
            writer.write(line);
            writer.newLine();
        }
    }

    public List<RekamMedis> findAll() {
        List<RekamMedis> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    RekamMedis rm = new RekamMedis(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            kembalikanBarisBaru(parts[3].trim()),
                            parts[4].trim(),
                            kembalikanBarisBaru(parts[5].trim())
                    );
                    list.add(rm);
                }
            }
        } catch (IOException e) {
            System.err.println("Gagal membaca file rekam_medis.csv: " + e.getMessage());
        }
        return list;
    }

    public List<RekamMedis> findByPasienId(String idPasien) {
        List<RekamMedis> hasil = new ArrayList<>();
        if (idPasien == null) return hasil;

        for (RekamMedis rm : findAll()) {
            if (rm.getIdPasien().equalsIgnoreCase(idPasien.trim())) {
                hasil.add(rm);
            }
        }
        return hasil;
    }

    public void deleteByPasienId(String idPasien) throws IOException {
        List<RekamMedis> riwayatTetap = new ArrayList<>();
        for (RekamMedis rm : findAll()) {
            if (!rm.getIdPasien().equalsIgnoreCase(idPasien.trim())) {
                riwayatTetap.add(rm);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH, false), StandardCharsets.UTF_8))) {
            for (RekamMedis rm : riwayatTetap) {
                String line = String.join(DELIMITER,
                        bersihkanDelimiter(rm.getIdRekamMedis()),
                        bersihkanDelimiter(rm.getIdPasien()),
                        bersihkanDelimiter(rm.getTanggalPemeriksaan()),
                        bersihkanDelimiter(rm.getDiagnosis()),
                        bersihkanDelimiter(rm.getTindakan()),
                        bersihkanDelimiter(rm.getCatatan())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void deleteAll() throws IOException {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Folder data tidak dapat dibuat.");
        }
        try (FileOutputStream output = new FileOutputStream(file, false)) {
            output.getFD().sync();
        }
    }

    public int countHariIni() {
        String tanggalHariIni = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        int count = 0;
        for (RekamMedis rm : findAll()) {
            if (rm.getTanggalPemeriksaan().startsWith(tanggalHariIni)) {
                count++;
            }
        }
        return count;
    }

    public String generateNextId() {
        List<RekamMedis> semua = findAll();
        int nomor = semua.size() + 1;
        return String.format("RM-%04d", nomor);
    }

    private String bersihkanDelimiter(String teks) {
        if (teks == null) return "";
        return teks.replace(DELIMITER, ",")
                   .replace("\r\n", "\\n")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }

    private String kembalikanBarisBaru(String teks) {
        if (teks == null) return "";
        return teks.replace("\\n", "\n");
    }
}
