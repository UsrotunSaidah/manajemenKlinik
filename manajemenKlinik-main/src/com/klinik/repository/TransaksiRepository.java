package com.klinik.repository;

import com.klinik.model.Transaksi;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransaksiRepository {
    private static final Path FILE_PATH = Paths.get("data", "transaksi.csv");
    private static final String DELIMITER = ";";
    private static final DateTimeFormatter ID_DATE_FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE;

    public synchronized Transaksi simpanPembayaranLunas(
            String idKunjungan,
            String idPasien,
            long total,
            String metode,
            long jumlahDibayar,
            long kembalian,
            String petugas) throws IOException {
        if (total < 0 || jumlahDibayar < 0 || kembalian < 0) {
            throw new IllegalArgumentException("Nilai transaksi tidak boleh negatif.");
        }

        LocalDateTime tanggalWaktu = LocalDateTime.now();
        String noTransaksi = generateNoTransaksi(tanggalWaktu.toLocalDate());
        Transaksi transaksi = new Transaksi(
                noTransaksi,
                idKunjungan,
                idPasien,
                total,
                metode,
                jumlahDibayar,
                kembalian,
                tanggalWaktu,
                "Lunas",
                petugas
        );
        append(transaksi);
        return transaksi;
    }

    public synchronized List<Transaksi> findAll() throws IOException {
        List<Transaksi> transaksi = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return transaksi;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                transaksi.add(parseLine(line, lineNumber));
            }
        }
        return transaksi;
    }

    public synchronized void deleteAll() throws IOException {
        Path parent = FILE_PATH.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(
                FILE_PATH,
                new byte[0],
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public synchronized int hitungTransaksiHariIni() throws IOException {
        LocalDate hariIni = LocalDate.now();
        int jumlah = 0;
        for (Transaksi transaksi : findAll()) {
            if (isLunasHariIni(transaksi, hariIni)) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public synchronized long hitungPendapatanHariIni() throws IOException {
        LocalDate hariIni = LocalDate.now();
        long pendapatan = 0;
        for (Transaksi transaksi : findAll()) {
            if (isLunasHariIni(transaksi, hariIni)) {
                pendapatan = Math.addExact(pendapatan, transaksi.getTotal());
            }
        }
        return pendapatan;
    }

    private boolean isLunasHariIni(Transaksi transaksi, LocalDate hariIni) {
        return "Lunas".equalsIgnoreCase(transaksi.getStatus())
                && transaksi.getTanggalWaktu().toLocalDate().equals(hariIni);
    }

    private String generateNoTransaksi(LocalDate tanggal) throws IOException {
        String prefix = "TRX-" + tanggal.format(ID_DATE_FORMAT) + "-";
        int nomorBerikutnya = 1;
        for (Transaksi transaksi : findAll()) {
            String noTransaksi = transaksi.getNoTransaksi();
            if (noTransaksi.startsWith(prefix)) {
                String suffix = noTransaksi.substring(prefix.length());
                try {
                    nomorBerikutnya = Math.max(
                            nomorBerikutnya, Integer.parseInt(suffix) + 1);
                } catch (NumberFormatException ex) {
                    throw new IOException(
                            "Nomor transaksi tidak valid: " + noTransaksi, ex);
                }
            }
        }
        return prefix + String.format("%03d", nomorBerikutnya);
    }

    private void append(Transaksi transaksi) throws IOException {
        Path parent = FILE_PATH.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(String.join(DELIMITER,
                    escape(transaksi.getNoTransaksi()),
                    escape(transaksi.getIdKunjungan()),
                    escape(transaksi.getIdPasien()),
                    String.valueOf(transaksi.getTotal()),
                    escape(transaksi.getMetode()),
                    String.valueOf(transaksi.getJumlahDibayar()),
                    String.valueOf(transaksi.getKembalian()),
                    transaksi.getTanggalWaktu().toString(),
                    escape(transaksi.getStatus()),
                    escape(transaksi.getPetugas())
            ));
            writer.newLine();
        }
    }

    private Transaksi parseLine(String line, int lineNumber) throws IOException {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 10) {
            throw new IOException("Format data transaksi rusak pada baris " + lineNumber + ".");
        }
        try {
            return new Transaksi(
                    parts[0],
                    parts[1],
                    parts[2],
                    Long.parseLong(parts[3]),
                    parts[4],
                    Long.parseLong(parts[5]),
                    Long.parseLong(parts[6]),
                    LocalDateTime.parse(parts[7]),
                    parts[8],
                    parts[9]
            );
        } catch (IllegalArgumentException ex) {
            throw new IOException("Data transaksi tidak valid pada baris " + lineNumber + ".", ex);
        }
    }

    private String escape(String value) {
        return value.replace(DELIMITER, ",")
                .replace("\r", " ")
                .replace("\n", " ");
    }
}