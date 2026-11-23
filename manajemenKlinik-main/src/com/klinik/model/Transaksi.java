package com.klinik.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Transaksi {
    private final String noTransaksi;
    private final String idKunjungan;
    private final String idPasien;
    private final long total;
    private final String metode;
    private final long jumlahDibayar;
    private final long kembalian;
    private final LocalDateTime tanggalWaktu;
    private final String status;
    private final String petugas;

    public Transaksi(
            String noTransaksi,
            String idKunjungan,
            String idPasien,
            long total,
            String metode,
            long jumlahDibayar,
            long kembalian,
            LocalDateTime tanggalWaktu,
            String status,
            String petugas) {
        this.noTransaksi = Objects.requireNonNull(noTransaksi, "noTransaksi");
        this.idKunjungan = Objects.requireNonNull(idKunjungan, "idKunjungan");
        this.idPasien = Objects.requireNonNull(idPasien, "idPasien");
        this.total = total;
        this.metode = Objects.requireNonNull(metode, "metode");
        this.jumlahDibayar = jumlahDibayar;
        this.kembalian = kembalian;
        this.tanggalWaktu = Objects.requireNonNull(tanggalWaktu, "tanggalWaktu");
        this.status = Objects.requireNonNull(status, "status");
        this.petugas = Objects.requireNonNull(petugas, "petugas");
    }

    public String getNoTransaksi() {
        return noTransaksi;
    }

    public String getIdKunjungan() {
        return idKunjungan;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public long getTotal() {
        return total;
    }

    public String getMetode() {
        return metode;
    }

    public long getJumlahDibayar() {
        return jumlahDibayar;
    }

    public long getKembalian() {
        return kembalian;
    }

    public LocalDateTime getTanggalWaktu() {
        return tanggalWaktu;
    }

    public String getStatus() {
        return status;
    }

    public String getPetugas() {
        return petugas;
    }
}